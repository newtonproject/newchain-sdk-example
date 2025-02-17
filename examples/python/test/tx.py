#!/usr/bin/env python
# encoding: utf-8

import sys
import os
import errno
import datetime
import json
import base58
import binascii
import click
import eth_utils

# add newchain_keys_constants to sys.modules
# newchain_keys_constants must be imported before web3
sys.modules['eth_keys.constants'] = __import__('newchain_keys_constants')
from web3 import Web3, HTTPProvider, Account
from web3.middleware import geth_poa_middleware

MAINNET_CHAIN_ID = 1012
TESTNET_CHAIN_ID = 1007
MAINNET_JSONRPC_URL = "https://global.rpc.mainnet.newtonproject.org"
TESTNET_JSONRPC_URL = "https://rpc1.newchain.newtonproject.org"


@click.group()
def main():
    """Simple program that tests newchain web3.py"""


def key_file_name(key_address):
    address = key_address.lower()
    if address.startswith('0x'):
        address = address[2:]
    ts = datetime.datetime.utcnow()
    return "UTC--{}--{}".format(ts.strftime('%Y-%m-%dT%H-%M-%S.%f000Z'), address)


@main.command()
@click.option('--path', default="wallet", help='Path to store the keystore file.')
@click.option('--password', prompt='Enter a password to encrypt the keystore file',
              hide_input=True,
              confirmation_prompt=True,
              help='The password to encrypt the keystore file.')
def create(path, password, chain_id=MAINNET_CHAIN_ID):
    """Create new address"""
    a = Account.create()
    keystore = a.encrypt(password)
    filename = "{}/{}".format(path, key_file_name(a.address))
    if not os.path.exists(os.path.dirname(filename)):
        try:
            os.makedirs(os.path.dirname(filename))
        except OSError as exc:  # Guard against race condition
            if exc.errno != errno.EEXIST:
                raise

    with open(filename, "w") as f:
        f.write(json.dumps(keystore))

    print("Create new address: {} ({})".format(a.address, hex_to_new(a.address, chain_id)))


@main.command()
@click.argument('address')
@click.option('--path', default="wallet", help='Path to store the keystore file.')
@click.option('--rpc', help='The RPC URL Of the NewChain node.')
@click.option('--password', hide_input=True, help='The password to encrypt the keystore file.')
def wallet(address, path, rpc, password):
    """Get address from wallet"""
    json_value = get_address_from_wallet(address, path)
    if json_value is None:
        print("Address not found")
        return
    
    if password is None:
        password = click.prompt("Enter the password of the keystore", hide_input=True)

    private = Account.decrypt(json_value, password.encode('utf-8'))
    a = Account.privateKeyToAccount(private)

    rpc = check_rpc_url(rpc)
    web3 = Web3(HTTPProvider(rpc))
    nonce = web3.eth.getTransactionCount(address)
    chain_id = int(web3.net.version)
    print("Rpc: %s" % rpc)
    print("Chain ID: %s" % chain_id)
    print("Private Key: %s" % private.hex())
    print("Address: %s %s" % (address, hex_to_new(address, chain_id)))
    print("Balance: %s" % web3.fromWei(web3.eth.getBalance(address), 'ether'))
    print("Nonce: %s" % nonce)
    
    

def get_address_from_wallet(address, path):
    address = address.lower()
    if address.startswith('0x'):
        address = address[2:]
    for filename in os.listdir(path):
        with open(path + "/" + filename) as load_f:
            json_value = json.load(load_f)
            if address == json_value['address'].lower():
                return json_value


def check_rpc_url(rpc):
    if rpc is None or rpc == "" or rpc == "mainnet":
        rpc = MAINNET_JSONRPC_URL
    if rpc == "testnet":
        rpc = TESTNET_JSONRPC_URL
    return rpc


@main.command()
@click.argument('address')
@click.option('--rpc', help='NewChain node RPC URL', required=False)
def balance(address, rpc):
    rpc = check_rpc_url(rpc)
    web3 = Web3(HTTPProvider(rpc))
    a = web3.toChecksumAddress(address)
    balance_wei = web3.eth.getBalance(a)
    b = web3.fromWei(balance_wei, 'ether')
    print("The balance of {} is {} NEW.".format(a, b))



@main.command()
@click.option('--path', default="wallet", help='Path to store keystore file.')
@click.option('--src', required=True, help='')
@click.option('--dest', required=True, help='Destination address')
@click.option('--value', required=True, help='The amount of NEW to transfer.')
@click.option('--data', help='The data to send with the transaction.')
@click.option('--rpc', help='The RPC URL Of the NewChain node.')
@click.option('--password', hide_input=True, help='The password to encrypt the keystore file.')
def pay(path, src, dest, value, data, rpc, password):
    """Pay NEW to address"""
    rpc = check_rpc_url(rpc)
    # sys.modules['eth_keys.constants'] = __import__('newchain_keys_constants')
    web3 = Web3(HTTPProvider(rpc))
    # inject the poa compatibility middleware to the innermost layer
    web3.middleware_onion.inject(geth_poa_middleware, layer=0)
    src = web3.toChecksumAddress(src)
    dest = web3.toChecksumAddress(dest)
    value = web3.toWei(value, "ether")
    if data is not None:
        data = data.encode('utf-8')
    else:
        data = b''

    json_value = get_address_from_wallet(src, path)

    if json_value == "":
        print("No from address")
        return

    nonce = web3.eth.getTransactionCount(src)
    chain_id = int(web3.net.version)
    gas_price = web3.eth.gasPrice
    tx = {
        'to': dest,
        'value': value,
        'gas': 0,
        'gasPrice': gas_price,
        'nonce': nonce,
        'chainId': chain_id,
        'data': data,
    }
    gas = web3.eth.estimateGas(tx)
    tx['gas'] = gas

    print("Rpc: %s" % rpc)
    print("Chain ID: %s" % chain_id)
    print("From: %s (Balance: %s )" % (src, web3.fromWei(web3.eth.getBalance(src), 'ether')))
    print("To: %s (Balance: %s )" % (dest, web3.fromWei(web3.eth.getBalance(dest), 'ether')))
    print("Value: %s " % web3.fromWei(value, 'ether'))
    print("Gas: %s" % gas)
    print("Nonce: %s" % nonce)
    

    if password is None:
        password = click.prompt("Enter the password of the keystore", hide_input=True)

    Account.chain_id = chain_id

    try:
        a = Account.privateKeyToAccount(Account.decrypt(json_value, password.encode('utf-8')))
        sign_tx = a.signTransaction(tx)
        tx_hash = web3.eth.sendRawTransaction(sign_tx.rawTransaction)
        print("Tx Hash: %s" % tx_hash.hex())
        receipt = web3.eth.waitForTransactionReceipt(tx_hash)
        print("Tx Receipt: %s" % receipt)
    except ValueError:
        print("error")
        pass


def hex_to_new(address, chain_id):
    return encode_new_address(address, chain_id)


def is_new_address(address):
    return len(address) == 39 and address[:3] == "NEW"


def new_to_hex(address, chain_id):
    return decode_new_address(address, chain_id)


def encode_new_address(address, chain_id):
    address_data = address
    if address_data.startswith('0x'):
        address_data = address_data[2:]
    hex_chain_id = hex(chain_id)[2:][-8:]
    if (len(hex_chain_id) % 2) == 1:
        hex_chain_id = '0' + hex_chain_id
    num_sum = hex_chain_id + address_data
    data = base58.b58encode_check(b'\0' + binascii.a2b_hex(num_sum))
    new_address = 'NEW' + data.decode()
    return new_address


def decode_new_address(address, chain_id):
    address_data = address[3:]
    hex_address = base58.b58decode_check(address_data)
    return hex_address.hex()[-40:]


@main.command()
@click.argument('address')
@click.option('--rpc', help='NewChain node RPC URL', required=False)
def convert(address, rpc=MAINNET_JSONRPC_URL):
    """Convert address of NEW"""
    rpc = check_rpc_url(rpc)
    web3 = Web3(HTTPProvider(rpc))
    chain_id = int(web3.net.version)
    if eth_utils.is_address(address):
        print(hex_to_new(address, chain_id), address)
    elif is_new_address(address):
        print(address, new_to_hex(address, chain_id))
    else:
        print("Address invalid")


if __name__ == '__main__':
    main()
