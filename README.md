# NewChain SDK

**Note: This document can be found on both our [documentation website](http://docs.newtonproject.org/en/NewChain/) and [GitHub repository](https://github.com/newtonproject/newchain-sdk-example)**

This document describes how to get started with Newton's NewChain SDK. At present we support NodeJS, Python, and Java. Example transaction code for each language can be found in the [examples](./examples) folder

For more detailed reference information, please check:

* [RPC and API reference](RPC_API_reference.md)
* [Chain IDs](chain_id.md) for devnet, testnet, and mainnet ID codes
* [Address standards](address_standards.md) for NewChain address standards

## Running Example Code

All example code is stored in the [examples](examples) folder.

### Node.js

NewChain's Javascript library can be downloaded from [NPM](https://www.npmjs.com/package/newchain-web3)

#### Install

```bash
npm install newchain-web3
```

#### Run Example Code

```sh
cd examples/node
npm install --save
node index.js
```

### Python

NewChain's Python library can be downloaded from [PyPI](https://pypi.org/project/newchain-web3/)

#### Install

```bash
cd examples/python/test/
virtualenv venv
source venv/bin/activate
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

#### Initiate payment

```bash
python tx.py pay --src 0x0286a28b49b3462252e3162A982EF28a8A8611dc --dest 0x97549E368AcaFdCAE786BB93D98379f1D1561a29 --value 1 --rpc https://rpc1.newchain.newtonproject.org -value 100
```
### Java

NewChain's Java library can be downloaded [HERE](examples/java/web3j-4.1.1.zip).  
Download it directly and import as libs:

```java
import org.web3j.*
```
