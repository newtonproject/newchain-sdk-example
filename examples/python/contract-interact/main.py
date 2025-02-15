# -*- coding: utf-8 -*-
"""

"""

__copyright__ = """ Copyright (c) 2021 Newton Foundation. All rights reserved."""
__author__ = 'weixuefeng@diynova.com'
__version__ = '1.0'
import sys
sys.modules['eth_keys.constants'] = __import__('newchain_keys_constants')

from newchain import zombie

if __name__ == "__main__":

    # deploy contract
    # zombie.deploy()

    # mint nft
    zombie.only_mint()
