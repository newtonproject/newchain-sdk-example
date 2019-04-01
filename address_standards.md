# NewChain Address Identifier (NAI)

This documentation explains the address identification system used by NewChain, the blockchain behind Newton's ecosystem.

## Background

NewChain lives in a multi-chain world. For end users, it's perfectly common to transfer value from an address on network A to an address on network B.

The Bitcoin protocol uses Base58Check encoding to prevent users sending value off-network, while Ethereum uses a raw hex version of the address.
Qtum, NEO and Ontology use similar encoding to Bitcoin. EOS is slightly different, but almost similar to Bitcoin.

At present this system works with NewChain, but will also work with other blockchains in the future.

## NewChain Addresses

NewChain specifies a network ID (also known as a chain ID). Programmers can extract the network part and a visual indicator of subchain and main chain, as well as other networks like Bitcoin and Ethereum.

An example NewChain address is:

```
NEW132AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9UK6K
```

The following items are encoded:

3 character [network ID](network_id.md):

<b>NEW</b>132AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9UK6K

4-byte [chain ID](chain_id.md):

NEW<b>132</b>AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9UK6K

Actual address: 

NEW132A<b>WeEvHXTLWbTGVwvXpCjCcfv1uoiF9</b>UK6K

Four bytes (32 bits) of SHA3-based checksum: 

NEW132AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9<b>UK6K</b>

(The last four bytes of the SHA3-based error checking code reduce the accidental address errors which occur in Ethereum.)

## How Do We Build a NewChain Address?

We start with a 256-bit private key:

```
eec7d48ec12e2f8f6da7683eb202529d35cd814875d23b0eba127ab01f73239b
```

The public key is created by running the private key through secp256r1:

```
e19e548c22765859940e854f863a2932e637415b9efaf32cbd21452369cc8e764c57e0a89a0b0c64c6cebd70385c03d8f570b6baf8dcd75502404719aee84461
```

The uncompressed public key is hashed with the Keccak-256 algorithm:

```
c5592e6aab131b88a728868154db4d39cedeaf78af1d15aa540570ca168fe303
```

We take the last 40 characters for the hashed public key...:

```
54db4d39cedeaf78af1d15aa540570ca168fe303
```

...and run it through the Base58Check algorithm:

```
AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9UK6K
```

We then combine this with our network ID (NEW) and chain ID (132) to get the final NewChain address:

```
NEW123AWeEvHXTLWbTGVwvXpCjCcfv1uoiF9UK6K
```
