#!/usr/bin/env python
# encoding: utf-8

import os
import errno
import datetime
import json

import base58
import binascii
import click

from newchain_web3 import Web3, HTTPProvider, Account
import eth_utils as utils


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
def create(path, password):
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

    print("Create new address: {} ({})".format(a.address, a.new_address))


@main.command()
@click.argument('address')
@click.option('--rpc', help='NewChain node RPC URL', required=True)
def balance(address, rpc):
    """Get the balance of the address"""
    web3 = Web3(HTTPProvider(rpc))
    a = web3.toChecksumAddress(address)
    balance_wei = web3.eth.getBalance(a)
    b = web3.fromWei(balance_wei, 'ether')
    print("The balance of {} is {} NEW.".format(a, b))


def get_address_from_wallet(address, path):
    address = address.lower()
    if address.startswith('0x'):
        address = address[2:]
    for filename in os.listdir(path):
        with open(path + "/" + filename) as load_f:
            json_value = json.load(load_f)
            if address == json_value['address'].lower():
                return json_value


@main.command()
@click.option('--path', default="wallet", help='Path to store keystore file.')
@click.option('--src', required=True, help='Path to store keystore file.')
@click.option('--dest', required=True, help='Path to store keystore file.')
@click.option('--value', required=True, help='Path to store keystore file.')
@click.option('--data', help='Path to store keystore file.')
@click.option('--rpc', help='The RPC URL Of the NewChain node.')
@click.option('--password', hide_input=True, help='The password to encrypt the keystore file.')
def pay(path, src, dest, value, data, rpc, password):
    """Pay NEW to address"""
    web3 = Web3(HTTPProvider(rpc))
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

    if password is None:
        password = click.prompt("Enter the password of the keystore", hide_input=True)

    Account.chain_id = chain_id

    try:
        a = Account.privateKeyToAccount(Account.decrypt(json_value, password.encode('utf-8')))

        sign_tx = a.signTransaction(tx)
        tx_hash = web3.eth.sendRawTransaction(sign_tx.rawTransaction)
        print(tx_hash.hex())
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
@click.option('--rpc', help='NewChain node RPC URL', required=True)
def convert(address, rpc):
    """Convert address of NEW"""
    web3 = Web3(HTTPProvider(rpc))
    chain_id = int(web3.net.version)
    if utils.is_address(address):
        print(hex_to_new(address, chain_id), address)
    elif is_new_address(address):
        print(address, new_to_hex(address, chain_id))
    else:
        print("Address invalid")


if __name__ == '__main__':
    main()
