
### Boilerplate

Simple code to initialize everything

```javascript
var EC = require('elliptic').ec;
var web3 = require('web3');
var ec = new EC('p256');
var base58check = require('base58check');
```

### key.getPrivate('hex')

```javascript
var key = ec.genKeyPair();
var privateKey = key.getPrivate('hex');
````

Generates a private key

console.log('private key:' + privateKey);
var publicKey = key.getPublic(false, 'hex').slice(2);
console.log('uncompressed public key:' + publicKey)

var hashKey = web3.utils.sha3('0x' + publicKey);
console.log("hashKey:" + hashKey);

var address_data = hashKey.slice(-40);
console.log("address_data:" + address_data);
```

### Generating a NewChain Address

```javascript
var chainID = 16888;
var PREFIX = 'NEW';
var data = chainID.toString(16).slice(-8) + address_data;
console.log("data:" + data);
// encode
var newChainAddress = PREFIX + base58check.encode(data);
console.log("newChainAddress:" + newChainAddress + " length:" + newChainAddress.length);
// decode
console.log("decode:" + base58check.decode(newChainAddress.slice(3), 'hex').data);
```
