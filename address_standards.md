# NewChain Address Identifier (NAI)

This documentation explains the address identification system used by NewChain, the blockchain behind Newton's ecosystem.

## Background

NewChain lives in a multi-chain world. For end users, it's perfectly common to transfer value from an address on network A to an address on network B.

The Bitcoin protocol uses Base58Check encoding to prevent users sending value off-network, while Ethereum uses a raw hex version of the address.
Qtum, NEO and Ontology use similar encoding to Bitcoin. EOS is slightly different, but almost similar to Bitcoin.

## NewChain Addresses

NewChain specifies a network ID (also known as a chain ID). Programmers can extract the network part and a visual indicator of subchain and main chain, as well as other networks like Bitcoin and Ethereum.

An example NewChain address would be:

```
NEW132AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9UK6K
```

The following items are encoded:

3 character prefix:

*NEW*132AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9UK6K

Last 4 bytes of the network ID: 123A

NEW*132A*WeEvHXTLWbTGVwvXpCjCcfv1uoiF9UK6K

Actual address data: 

NEW132A*WeEvHXTLWbTGVwvXpCjCcfv1uoiF9*UK6K

Four bytes (32 bits) of SHA3-based error checking code: UK6K

NEW132AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9*UK6K*

At present this system works with NewChain, but will also work with other blockchains in the future.

### Easy to Implement

Since NewChain is based on the go-Ethereum codebase, generating NewChain addresses is very simple. The last four bytes of the SHA3-based error checking code reduce the accidental address errors which occur in Ethereum.

## NewChain MainChain Examples

### 256-bit private key

```
eec7d48ec12e2f8f6da7683eb202529d35cd814875d23b0eba127ab01f73239b
```
`
### Uncompressed public key with secp256r1
`
```
e19e548c22765859940e854f863a2932e637415b9efaf32cbd21452369cc8e764c57e0a89a0b0c64c6cebd70385c03d8f570b6baf8dcd75502404719aee84461
```
`
### Keccak-256 hash of the public key
`
```
c5592e6aab131b88a728868154db4d39cedeaf78af1d15aa540570ca168fe303
```
`
### Last 40 characters/20 bytes of this hash key (Keccak-256)
`
```
54db4d39cedeaf78af1d15aa540570ca168fe303
```

### Combined with Base58Check
`
```
NEW132AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9UK6K
```
