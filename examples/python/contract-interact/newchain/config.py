# -*- coding: utf-8 -*-
"""

"""

__copyright__ = """ Copyright (c) 2021 Newton Foundation. All rights reserved."""
__author__ = 'weixuefeng@diynova.com'
__version__ = '1.0'

import sys
sys.modules['eth_keys.constants'] = __import__('newchain_keys_constants')

from web3 import Web3


# test config

# RPC = "https://rpc1.newchain.newtonproject.org/"
PRIVATE_KEY = "0x38f720bfe3110ec031eeba5b03b5181372cbf25b88a2269bd1b0539cf2ef42ba"
# NEW17zMmLhW2qE9sK5EvKpc75MbSyhmcV5obXhB 0x673fEc17549fB1F9870F49fb0EE73F70c90Af381


KEYSTORE = """
{"address":"673fec17549fb1f9870f49fb0ee73f70c90af381","crypto":{"cipher":"aes-128-ctr","ciphertext":"77e4a5daaf33c016bd0b2332a1f85ad864245b7e1fbf0bd20bdc26711870eac3","cipherparams":{"iv":"29f615a6e65e97e2ec345bbd2d3137bd"},"kdf":"scrypt","kdfparams":{"dklen":32,"n":4096,"p":6,"r":8,"salt":"edb52989bbf985e3f44d7900eb19fe1e82a0d76c8665cb4714bdee593bde0f53"},"mac":"a45929038dea45c818478fcfcdfcd20cd7044a2ceb645ec09d09c925e8270531"},"id":"5272354b-7387-47a7-ae57-6939f268327c","version":3}
"""
KEYSTORE_PASSWORD = ""
USDT_CONTRACT_ADDRESS = Web3.toChecksumAddress("0x20f12218281f9ca566b5c41f17c6c19050125cd3")
FACTORY_ADDRESS = Web3.toChecksumAddress("0x723913136a42684B5e3657e3cD2f67ee3e83A82D")
WETH_ADDRESS = Web3.toChecksumAddress("0xf4905b9bc02Ce21C98Eac1803693A9357D5253bf")
ROUTER_ADDRESS = Web3.toChecksumAddress("0x9e5A14dd31D1e7120eC45E6E4EfA287c94bCcf2f")
NEW_USDT_PAIR_ADDRESS = Web3.toChecksumAddress("0x56ae975581a382193ff36579c81281e179486c43")
ZOMBIE_CONTRACT_ADDRESS = Web3.toChecksumAddress("0x57b98Fed6Ab31c21704b851dFe879fAC9798f14B")

# test config
RPC = "https://rpc1.newchain.newtonproject.org/"
