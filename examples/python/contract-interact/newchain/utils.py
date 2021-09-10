# -*- coding: utf-8 -*-
"""

"""

__copyright__ = """ Copyright (c) 2021 Newton Foundation. All rights reserved."""
__author__ = 'weixuefeng@diynova.com'
__version__ = '1.0'

import json

from web3 import Web3

import newchain.config as config
from newchain.contracts import constant


def get_contract_abi(path):
    f = open(path)
    abi = json.load(f)
    f.close()
    return abi


swap_factory_abi = get_contract_abi('newchain/contracts/UniswapV2Factory.abi')
swap_pair_abi = get_contract_abi('newchain/contracts/UniswapV2Pair.abi')
swap_router_abi = get_contract_abi('newchain/contracts/UniswapV2Router.abi')
sushi_maker_abi = get_contract_abi('newchain/contracts/SushiMaker.abi')
erc20_abi = get_contract_abi('newchain/contracts/Erc20.abi')
zombie_abi = get_contract_abi('newchain/contracts/Zombie.abi')


def get_router_contract(w3, router_address):
    return w3.eth.contract(address=w3.toChecksumAddress(router_address), abi=swap_router_abi)


def get_factory_contract(w3, factory_address):
    return w3.eth.contract(address=w3.toChecksumAddress(factory_address), abi=swap_factory_abi)


def get_pair_contract(w3, pair_address):
    return w3.eth.contract(address=pair_address, abi=swap_pair_abi)


def get_swap_contract(w3, pair_address):
    return w3.eth.contract(address=w3.toChecksumAddress(pair_address), abi=swap_pair_abi)


def get_sushi_maker_contract(w3, address):
    return w3.eth.contract(address=w3.toChecksumAddress(address), abi=sushi_maker_abi)


def get_erc20_contract(w3, address):
    return w3.eth.contract(address=w3.toChecksumAddress(address), abi=erc20_abi)


def get_new_usdt_path():
    return [Web3.toChecksumAddress(config.WETH_ADDRESS), Web3.toChecksumAddress(config.USDT_CONTRACT_ADDRESS)]


def get_zombie_contract(w3):
    return w3.eth.contract(abi=zombie_abi,
                           bytecode=constant.BYTE_CODE_ZOMBIE)


def get_contract(w3, address, abi):
    return w3.eth.contract(address=w3.toChecksumAddress(address), abi=abi)


def get_new_usdt_pair_address(w3):
    factory_contract = get_factory_contract(w3, config.FACTORY_ADDRESS)
    res = factory_contract.functions.getPair(config.USDT_CONTRACT_ADDRESS, config.WETH_ADDRESS).call()
    return res
