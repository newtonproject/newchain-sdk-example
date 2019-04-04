
# Chain ID for different environment

| Environment | Chain ID | String in NEW Address
| --- | --- | --- |
| MainNet | 1012 | 182 |
| TestNet | 1007 | 17z |
| BetaNet | 1007 | 17z |
| DevNet | 1002 | 17x |


### NewAddress Creation Rules:

1. Get hex string of Chain ID above
2. Combine the hex string and the eth address (without '0x')
3. Get the Base58Check-encoded string from the step 2 string
4. Combine NEW and the Base58Check-encoded string

Since the chain ID is different in different environments, we can identify the environment of a NEW address based on the specific part of string in the new address which is created from the chain id.



```
/// ios Code Sample to create NEW Address
let chainID = currentChainID.getHexData().toHexString()
let encodeString = chainID + addressData.substring(from: 2)
let newAddress = Data(hexString: encodeString)?.base58CheckEncodedString()
return "NEW" + newAddress!

```
