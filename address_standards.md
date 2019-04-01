# NewChain Address Identifier (NAI)
NewChain is a multi-chain world. For end users, it's perfectly common to transfer value from an address on network A to an address on network B.

The Bitcoin protocol uses Base58Check encoding to prevent users sending value off-network, while Ethereum uses a raw hex version of the address.
Qtum, NEO and Ontology use similar encoding to Bitcoin. EOS is slightly different, but almost similar to Bitcoin.

## Extensible Encoding

Inspired by Base58Check encoding and EIP77, Newton specifies network ID (also chain ID) which lets programmers extract the network part as well as provide a visual indicator of subchain and main chain, as well as other networks like Bitcoin and Ethereum.

The following items are encoded:

* 3 character prefix: NEW
Similar to EOS's encoding, indicator 'NEW' is good signal.
* Last 4 bytes of network ID
* Actual address data
* Four bytes (32 bits) of SHA3-based error checking code

### Benefits
The scheme not only works with NewChain, but also with other blockchains in future.

### Easy to Implement

Since NewChain is based on the go-Ethereum codebase, generating NewChain addresses is very simple, only filling the original Ethereum address into actual address data. The last four bytes of the SHA3-based error checking code reduce the accidental address errors which occur in Ethereum.

## NewChain MainChain Examples
1. Generate 256-bit private key
`
eec7d48ec12e2f8f6da7683eb202529d35cd814875d23b0eba127ab01f73239b
`

2. Generate uncompressed public key with secp256r1
`
e19e548c22765859940e854f863a2932e637415b9efaf32cbd21452369cc8e764c57e0a89a0b0c64c6cebd70385c03d8f570b6baf8dcd75502404719aee84461
`

3. Take the Keccak-256 hash of the public key
`
c5592e6aab131b88a728868154db4d39cedeaf78af1d15aa540570ca168fe303
`

4. Take the last 40 characters/20 bytes of this hash key (Keccak-256)
`
54db4d39cedeaf78af1d15aa540570ca168fe303
`

5. Combined with Base58Check
`
NEW132AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9UK6K
`

## Code

```javascript
var EC = require('elliptic').ec;
var web3 = require('web3');
var ec = new EC('p256');
var base58check = require('base58check');
// Generate keys
var key = ec.genKeyPair();

var privateKey = key.getPrivate('hex');
console.log('private key:' + privateKey);
var publicKey = key.getPublic(false, 'hex').slice(2);
console.log('uncompressed public key:' + publicKey)

var hashKey = web3.utils.sha3('0x' + publicKey);
console.log("hashKey:" + hashKey);

var address_data = hashKey.slice(-40);
console.log("address_data:" + address_data);

// Generate the newchain address
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

## References
