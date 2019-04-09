# Web3.py

A Python implementation of [NewChain web3.js](https://github.com/newtonproject/newchain-web3.js)

* Python 3.6+ support

## Developer Setup

```bash
cd examples/python/test/
```

Then run these install commands:

```bash
virtualenv venv
source venv/bin/activate
pip install -r requirements.txt
```

## Usage

#### Create account
```bash
python tx.py create
```

#### Check balance
```bash
python tx.py balance 0x0286a28b49b3462252e3162a982ef28a8a8611dc
```

#### Pay
```bash
python tx.py pay --src 0x0286a28b49b3462252e3162A982EF28a8A8611dc --dest 0x97549E368AcaFdCAE786BB93D98379f1D1561a29 --value 1 --rpc https://devnet.newchain.cloud.diynova.com --value 100
```