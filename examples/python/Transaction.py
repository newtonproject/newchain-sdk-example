#!/usr/bin/env python
# encoding: utf-8

"""
@author: Tang Smith
@software: PyCharm
@time: 2019-04-01 21:51
@copyright: 2018-2019 Newton Foundation. All rights reserved.
"""

from newchain_web3 import Web3, HTTPProvider
import base58
import json

w3 = Web3(HTTPProvider('https://rpc1.newchain.newtonproject.org/'))

ClientVersion = int(w3.net.version)
print("Client Version: %s" % ClientVersion)

from web3.middleware import geth_poa_middleware
w3.middleware_onion.inject(geth_poa_middleware, layer=0)

def NewToEth(str):
	print("Str: 0x%s" % base58.b58decode_check(str[3:]).hex().lower()[6:])
	return "0x%s" % base58.b58decode_check(str[3:]).hex().lower()[6:]

From = w3.toChecksumAddress(NewToEth("NEW17zDT6sQYPCATtJZ3QpPoR2yW8aGBc2hS3MT")) #/tmp/wallet
TO = w3.toChecksumAddress(NewToEth("NEW17zNNCR9ouLnVMktE8iHBwaPZJh14iK3ghGU"))
print("Gas Price: %s" % w3.eth.gasPrice)

print("From Balance: %s" % w3.eth.getBalance(From))
print("To Balance: %s" % w3.eth.getBalance(TO))

with open("/tmp/UTC--2019-04-01T15-45-13.671000000Z--0c0be2750b4cf6664efb7da885507654f70f38b5.json") as load_f:
    jsonvalue = json.load(load_f)

w3.eth.account.chain_id=ClientVersion
 
Account = w3.eth.account.privateKeyToAccount(w3.eth.account.decrypt(jsonvalue,"123qwe"))
print("getTransactionCount of [%s] is: %s" % (From,w3.eth.getTransactionCount(From)))

GAS = w3.eth.estimateGas({'to': TO, 'from': From, 'value': 1})
print("GAS: %s" % GAS)

signed_txn = Account.signTransaction(
    dict(
    nonce=0,
    gasPrice=w3.eth.gasPrice,
    gas=GAS,
    to=TO,
    value=1,
    # data=b'',
    chainId=ClientVersion)
    # private_key=w3.eth.account.decrypt(jsonvalue,"123qwe")
)

w3.eth.sendRawTransaction(signed_txn.rawTransaction)
