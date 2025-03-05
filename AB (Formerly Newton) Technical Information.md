# AB (Formerly Newton) Technical Information

## Basic Information

- **Official Website**:  
  [https://ab.org/](https://ab.org/)

- **Blockchain Explorer**:  
  - **Mainnet**: [https://explorer.ab.org/](https://explorer.ab.org/)  
  - **Testnet**: [https://explorer.testnet.ab.org/](https://explorer.testnet.ab.org/)

- **Blockchain Source Code**:  
  [https://github.com/newtonproject/newchain/](https://github.com/newtonproject/newchain/)

- **Circulation and Total Supply**:  
  [https://ab.org/en/getab/](https://ab.org/en/getab/)

---

## Key Differences Between NewChain and Ethereum

1. **Signature Algorithm**:  
   - Ethereum uses `secp256k1`, while NewChain uses `secp256r1`.

2. **Address Representation**:  
   - Addresses are transformed before being displayed to users.  
   - Users see addresses starting with `NEW` (e.g., `NEW182F3f3q8CFM3od4RkjWPPEx6vAcBJpVdurC`).  
   - The SDK provides code for two-way address conversion.

3. **Address Validation**:  
   - Ensure that the ChainID parsed from the user's submitted address matches the current network’s ChainID.  
   - Failing to do so may result in transaction failures or asset loss.  
   - For specific address parsing methods, refer to the example code.

---

## Blockchain-Related Information

- **Token Precision**:  
  The smallest unit of AB is `ISSAC`.  
  **1 AB = 1,000,000,000,000,000,000 ISSAC.**

- **Block Interval**:  
  3 seconds.

- **Number of Confirmations for Transaction Security**:  
  A transaction is considered secure after 20 block confirmations.

- **Node Deployment**:  
  [https://github.com/newtonproject/newchain-deploy](https://github.com/newtonproject/newchain-deploy)

---

## API

- **RPC Service Endpoints**:
  - **Mainnet (MainNet)**:  
    [https://global.rpc.mainnet.newtonproject.org](https://global.rpc.mainnet.newtonproject.org)
  - **Testnet (TestNet)**:  
    [https://rpc1.newchain.newtonproject.org](https://rpc1.newchain.newtonproject.org)

- **ChainID**:
  - **Mainnet (MainNet)**: `1012`
  - **Testnet (TestNet)**: `1007`

- **Sample Code**:  
  [https://github.com/newtonproject/newchain-sdk-example](https://github.com/newtonproject/newchain-sdk-example)

- **Available RPC APIs**:  
  Currently, Newton provides the following APIs for open use. For additional APIs, deploy your own node:

```plaintext
"net_version",
"net_peerCount",
"net_listening",
"eth_protocolVersion",
"eth_syncing",
"eth_mining",
"eth_hashrate",
"eth_gasPrice",
"eth_blockNumber",
"eth_getBalance",
"eth_getStorageAt",
"eth_getTransactionCount",
"eth_getBlockTransactionCountByHash",
"eth_getBlockTransactionCountByNumber",
"eth_getUncleCountByBlockHash",
"eth_getUncleCountByBlockNumber",
"eth_getCode",
"eth_sendRawTransaction",
"eth_call",
"eth_estimateGas",
"eth_getBlockByHash",
"eth_getBlockByNumber",
"eth_getTransactionByHash",
"eth_getTransactionByBlockHashAndIndex",
"eth_getTransactionByBlockNumberAndIndex",
"eth_getTransactionReceipt",
"eth_getUncleByBlockHashAndIndex",
"eth_getUncleByBlockNumberAndIndex",
"eth_getCompilers",
"eth_compileLLL",
"eth_compileSolidity",
"eth_compileSerpent",
"eth_newFilter",
"eth_newBlockFilter",
"eth_newPendingTransactionFilter",
"eth_uninstallFilter",
"eth_getFilterChanges",
"eth_getFilterLogs",
"eth_getLogs",
"eth_chainId",
"eth_getProof"
