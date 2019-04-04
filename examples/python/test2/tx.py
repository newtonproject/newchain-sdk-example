#!/usr/bin/env python
# encoding: utf-8

from newchain_web3 import Web3, HTTPProvider, Account
import os
import errno
import json
import click
import datetime

@click.group()
def main():
    """Simple program that test newchain web3.py"""


def keyFileName(keyAddr):
    address = keyAddr.lower()
    if address.startswith('0x'):
        address = address[2:]
    ts = datetime.datetime.utcnow()
    return "UTC--{}--{}".format(ts.strftime('%Y-%m-%dT%H-%M-%S.%f000Z'), address)


@main.command()
@click.option('--path', default="wallet", help='Path to store keystore file.')
@click.option('--password', prompt='Enter the password to encrypt the keystore file',
              hide_input=True,
              confirmation_prompt=True,
              help='The password to encrypt the keystore file.')
def create(path, password):
    """Create new address"""
    a = Account.create()
    keystore = a.encrypt(password)
    filename = "{}/{}".format(path, keyFileName(a.address))
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
@click.option('--rpc', help='The rpc url of the NewChain node.', required=True)
def balance(address, rpc):
    """Get the balance of the address"""
    web3 = Web3(HTTPProvider(rpc))
    a = web3.toChecksumAddress(address)
    balance = web3.eth.getBalance(a)
    b = web3.fromWei(balance, 'ether')
    print("The balance of {} is {} NEW.".format(a, b))


def get_address_from_wallet(address, path):
    address = address.lower()
    if address.startswith('0x'):
        address = address[2:]
    for filename in os.listdir(path):
        with open(path + "/" + filename) as load_f:
            jsonvalue = json.load(load_f)
            if address == jsonvalue['address'].lower():
                return jsonvalue


@main.command()
@click.option('--path', default="wallet", help='Path to store keystore file.')
@click.option('--src', required=True, help='Path to store keystore file.')
@click.option('--dest', required=True, help='Path to store keystore file.')
@click.option('--value', required=True, help='Path to store keystore file.')
@click.option('--data', help='Path to store keystore file.')
@click.option('--rpc', help='The rpc url of the NewChain node.')
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
        hash = web3.eth.sendRawTransaction(sign_tx.rawTransaction)
        print(hash.hex())
    except ValueError:
        print("error")
        pass


if __name__ == '__main__':
    main()
