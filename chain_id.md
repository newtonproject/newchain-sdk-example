# NewChain IDs for Different Environments

| Environment | Chain ID | String in NEW Address | accelerate endpoint | allow cros RPC|
| --- | --- | --- | --- | --- |
| MainNet | 1012 | 182 | https://global.rpc.mainnet.newtonproject.org | https://cors.rpc.mainnet.newtonproject.org |
| TestNet | 1007 | 17z | https://rpc1.newchain.newtonproject.org/ | https://cors.rpc.testnet.newtonproject.org |
| DevNet | 1002 | 17x | https://devnet.newchain.cloud.diynova.com/ |--|

Since the chain ID is different in different environments, we can identify the environment of a NEW address based on the specific part of string in the NEW address which is created from the chain ID.

