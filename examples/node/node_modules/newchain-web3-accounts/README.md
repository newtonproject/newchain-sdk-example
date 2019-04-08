# newchain-web3-accounts

This is a sub package of [newchain-web3.js][repo]

This is the accounts package to be used in the `web3-eth` package.
Please read the [documentation][docs] for more.

## Installation

### Node.js

```bash
npm install newchain-web3-accounts
```

## Usage

```js
import {Accounts} from 'newchain-web3-accounts';

const accounts = new Accounts(
    'http://127.0.0.1:4546',
    options
);
```

## Types 

All the typescript typings are placed in the types folder. 

[docs]: http://web3js.readthedocs.io/en/1.0/
[repo]: https://github.com/xiawu/newchain-web3.js
