#!/usr/bin/env python
# encoding: utf-8

"""
@author: Tang Smith
@software: PyCharm
@time: 2019-04-01 21:51
@copyright: 2018-2019 Newton Foundation. All rights reserved.
"""

import sys 
import base58
import json

# add newchain_keys_constants to sys.modules
# newchain_keys_constants must be imported before web3
sys.modules['eth_keys.constants'] = __import__('newchain_keys_constants')
from web3 import Web3, HTTPProvider, Account
from web3.middleware import geth_poa_middleware

TESTNET_JSONRPC_URL = "https://rpc1.newchain.newtonproject.org"

w3 = Web3(HTTPProvider(TESTNET_JSONRPC_URL))

ClientVersion = int(w3.net.version)
print("Client Version: %s" % ClientVersion)
print("Rpc Url: %s " % TESTNET_JSONRPC_URL)

w3.middleware_onion.inject(geth_poa_middleware, layer=0)

def NewToEth(str):
	print("%s  0x%s" % (str, base58.b58decode_check(str[3:]).hex().lower()[6:]))
	return "0x%s" % base58.b58decode_check(str[3:]).hex().lower()[6:]

From = w3.toChecksumAddress(NewToEth("NEW17zWzuY5etuEjM2eSAkXTtfEoRNxdY66aVUa")) # ~/keystore_test
To = w3.toChecksumAddress(NewToEth("NEW17zNNCR9ouLnVMktE8iHBwaPZJh14iK3ghGU"))

print("Gas Price: %s" % w3.eth.gasPrice)
print("From Balance: %s" %  w3.fromWei(w3.eth.getBalance(From), 'ether'))
print("To Balance: %s" % w3.fromWei(w3.eth.getBalance(To), 'ether'))

with open("./keystore_test") as load_f:
    jsonvalue = json.load(load_f)

w3.eth.account.chain_id=ClientVersion
 
Account = w3.eth.account.privateKeyToAccount(w3.eth.account.decrypt(jsonvalue,"12345678"))
print("getTransactionCount of [%s] is: %s" % (From,w3.eth.getTransactionCount(From)))

value = w3.toWei(1, 'ether')
Gas = w3.eth.estimateGas({'to': To, 'from': From, 'value': value})
print("Gas: %s" % Gas)

nonce = w3.eth.getTransactionCount(From)

signed_txn = Account.signTransaction(
    dict(
    nonce=nonce,
    gasPrice=w3.eth.gasPrice,
    gas=Gas,
    to=To,
    value=value,
    chainId=ClientVersion)
)

tx_hash = w3.eth.sendRawTransaction(signed_txn.rawTransaction)
print("Tx Hash: %s" % tx_hash.hex())
receipt = w3.eth.waitForTransactionReceipt(tx_hash)
print("Tx Receipt: %s" % receipt)

print("From Balance: %s" %  w3.fromWei(w3.eth.getBalance(From), 'ether'))
print("To Balance: %s" % w3.fromWei(w3.eth.getBalance(To), 'ether'))
