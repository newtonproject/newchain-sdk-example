# NewChain SDK

This document describes how to get started with Newton's NewChain SDK. At present we support NodeJS, Python, and Java. Example transaction code for each language can be found in the [examples](./examples) folder

For more detailed reference information, please check:

* [RPC and API reference](RPC_API_reference.md)
* [Chain IDs](chain_id.md) for devnet, testnet, and mainnet ID codes
* [Address standards](address_standards.md) for NewChain address standards

## Running Example Code

All example code is stored in the [examples](examples) folder.

### Node.js

```sh
cd examples/node
npm install --save
node index.js
```

### Python

```bash
cd examples/python/test2
pip install -r requirements.txt
```

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
### Java
