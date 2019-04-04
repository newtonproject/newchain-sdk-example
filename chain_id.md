# NewChain IDs for Different Environments

| Environment | Chain ID | String in NEW Address |
| --- | --- | --- |
| MainNet | 1012 | 182 |
| TestNet | 1007 | 17z |
| BetaNet | 1007 | 17z |
| DevNet | 1002 | 17x |

## NewAddress Creation Rules

1. Get hex string of the NewChain ID above
2. Combine the hex string and the eth address (without '0x')
3. Get the Base58Check-encoded string from the string you created in step 2
4. Combine the string ```NEW``` and the Base58Check-encoded string from step 3

Since the chain ID is different in different environments, we can identify the environment of a NEW address based on the specific part of string in the NEW address which is created from the chain ID.

```
/// ios Code Sample to create NEW Address
let chainID = currentChainID.getHexData().toHexString()
let encodeString = chainID + addressData.substring(from: 2)
let newAddress = Data(hexString: encodeString)?.base58CheckEncodedString()
return "NEW" + newAddress!

```
