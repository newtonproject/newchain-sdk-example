# -*- coding: utf-8 -*-
"""

"""

__copyright__ = """ Copyright (c) 2021 Newton Foundation. All rights reserved."""
__author__ = 'weixuefeng@diynova.com'
__version__ = '1.0'

import datetime

from web3.auto import Web3
from web3.middleware import geth_poa_middleware

import newchain.config as config
import newchain.utils as utils
from newchain.contracts import constant

# 构造 web3
w3 = Web3(Web3.HTTPProvider(config.RPC))
w3.middleware_onion.inject(geth_poa_middleware, layer=0)

# 获取账户信息
account = w3.eth.account.privateKeyToAccount(config.PRIVATE_KEY)
chain_id = int(w3.net.version)
gas_price = w3.eth.gasPrice

# 获取合约信息
zombie = utils.get_zombie_contract(w3)
zero_value_new = Web3.toWei("0", 'ether')


def mint(contract):
    """
    mint zombie nft
    """
    transaction = pre_transaction(account.address)
    reward_address = Web3.toChecksumAddress("0xff827d59f973bc57eec55dce2955e03e6c81db30")
    token_uri = "https://raw.githubusercontent.com/weixuefeng/tokeuri/main/zombie.png"
    gas = contract.functions.awardItem(reward_address, token_uri).estimateGas(transaction)
    transaction['gas'] = gas
    tx = contract.functions.awardItem(reward_address, token_uri).buildTransaction(transaction)
    signed_txn = account.sign_transaction(tx)
    tx_hash = w3.toHex(w3.eth.sendRawTransaction(signed_txn.rawTransaction))
    print("mint txid: %s" % tx_hash)


def __get_nonce(address):
    """
    获取地址的 nonce
    """
    nonce = w3.eth.getTransactionCount(address)
    return nonce


def __get_deadline():
    """
    设置 合约 deadline 参数
    """
    return int((datetime.datetime.now() + datetime.timedelta(minutes=30)).timestamp())


def pre_transaction(address):
    """
    拼接交易信息
    """
    print(address)
    nonce = __get_nonce(address)
    return {
        "from": address,
        "gasPrice": gas_price,
        "chainId": chain_id,
        "nonce": nonce,
        "value": zero_value_new
    }


def deploy():
    """
    部署合约信息
    """
    transaction = pre_transaction(address=account.address)
    gas = zombie.constructor().estimateGas(transaction)
    transaction['gas'] = gas
    tx = zombie.constructor().buildTransaction(transaction)
    signed_txn = account.sign_transaction(tx)
    tx_hash = w3.toHex(w3.eth.sendRawTransaction(signed_txn.rawTransaction))
    txn_receipt = w3.eth.waitForTransactionReceipt(tx_hash)
    print(txn_receipt['contractAddress'])
    return txn_receipt['contractAddress']


def deploy_dev():
    contract_address = deploy()
    contract = utils.get_contract(w3, contract_address, constant.ABI_ZOMBIE)
    mint(contract)


def only_mint():
    address = "0x57b98Fed6Ab31c21704b851dFe879fAC9798f14B"
    contract = utils.get_contract(w3, address, constant.ABI_ZOMBIE)
    mint(contract)


if __name__ == "__main__":
    pass

