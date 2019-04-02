# NewChain RPC API Reference

## get_block_height

Gets the current block number

**Ethereum equivalent:** eth_blockNumber

### Parameters

None

### Return Values

An integer of the current block number where the client is located
 
## get_block_by_height
 
Gets the block number of the specified block

**Ethereum equivalent:** eth_getBlockByNumber

### Parameters

Two parameters:

* A block-numbered integer (such as 0x1b4), or the string "earliest", "latest" or "pending".
* The boolean type. If true, it returns all the complete transaction objects. If it is false, it only returns the hash value of the transaction object.

### Return Values

Returns a block object, if it is not found to be null.

|Value | Type | Returns |
| ---	| ---	| ---	|
|Number | QUANTITY | The block number. null when its pending block.|
|Hash| DATA, 32 Bytes | Hash of the block. null when its pending block.|
|parentHash| DATA, 32 Bytes | Hash of the parent block.|
|Nonce| DATA, 8 Bytes | Hash of the generated proof-of-work. null when its pending block.|
|sha3Uncles| DATA, 32 Bytes | SHA3 of the uncles data in the block.|
|logsBloom| DATA, 256 Bytes | The bloom filter for the logs of the block. null when its pending block.|
|transactionsRoot| DATA, 32 Bytes | The root of the transaction trie of the block.|
|stateRoot| DATA, 32 Bytes | The root of the final state trie of the block.|
|receiptsRoot| DATA, 32 Bytes | The root of the receipts trie of the block.|
|Miner| DATA, 20 Bytes | The address of the beneficiary to whom the mining rewards were given.|
|Difficulty| QUANTITY | Integer of the difficulty for this block.|
|totalDifficulty| QUANTITY | Integer of the total difficulty of the chain until this block.|
|extraData| DATA | The "extra data" field of this block.|
|Size| QUANTITY | Integer the size of this block in bytes.|
|gasLimit| QUANTITY | The maximum gas allowed in this block.|
|gasUsed| QUANTITY | The total used gas by all transactions in this block.|
|Timestamp| QUANTITY | The unix timestamp for when the block was collated.|
|Transactions| Array | Array of transaction objects, or 32 Bytes transaction hashes depending on the last given parameter.|
|Uncles| Array | Array of uncle hashes.|
 
## get_block_by_hash

Gets the block with the specified hash 
 
**Ethereum equivalent:** eth_getBlockByHash

### Parameters

Two parameters:

* The hash value of the block (32 bytes)
* The boolean type. If true, it returns all the complete transaction objects. If false, it only returns the hash value of the transaction object.

### Return Values

Returns a block object, if it is not found to be null.

|Value | Type | Returns |
| ---	| ---	| ---	|
|Number| QUANTITY | The block number. null when its pending block.|
|Hash| DATA, 32 Bytes | Hash of the block. null when its pending block.|
|parentHash| DATA, 32 Bytes | Hash of the parent block.|
|Nonce| DATA, 8 Bytes | Hash of the generated proof-of-work. null when its pending block.|
|sha3Uncles| DATA, 32 Bytes | SHA3 of the uncles data in the block.|
|logsBloom| DATA, 256 Bytes | The bloom filter for the logs of the block. null when its pending block.|
|transactionsRoot| DATA, 32 Bytes | The root of the transaction trie of the block.|
|stateRoot| DATA, 32 Bytes | The root of the final state trie of the block.|
|receiptsRoot| DATA, 32 Bytes | The root of the receipts trie of the block.|
|Miner| DATA, 20 Bytes | The address of the beneficiary to whom the mining rewards were given.|
|Difficulty| QUANTITY | Integer of the difficulty for this block.|
|totalDifficulty| QUANTITY | Integer of the total difficulty of the chain until this block.|
|extraData| DATA | The "extra data" field of this block.|
|Size| QUANTITY | Integer the size of this block in bytes.|
|gasLimit| QUANTITY | The maximum gas allowed in this block.|
|gasUsed| QUANTITY | The total used gas by all transactions in this block.|
|Timestamp| QUANTITY | The unix timestamp for when the block was collated.|
|Transactions| Array | Array of transaction objects, or 32 Bytes transaction hashes depending on the last given parameter.|
|Uncles| Array | Array of uncle hashes.|

## get_transaction_count_by_height
 
**Ethereum equivalent:** eth_getBlockTransactionCountByNumberParameter: 

### Parameters

The integer of the block number (such as 0x1b4), or the string "earliest", "latest" or "pending".

### Return Values

Number of transactions, hexadecimal integer
 
## get_transaction_by_height_and_index
 
**Ethereum equivalent:** eth_getTransactionByBlockNumberAndIndex

### Parameters

Two arguments:

* A block-numbered integer (such as 0x1b4), or the string "earliest", "latest" or "pending". 
* The index position of the transaction (for example: 0x0)

### Return Values

A transaction object, or null if there is no transaction

| Value | Type | Returns |
| ---	| ---	| ---	|
|Hash| DATA, 32 Bytes | Hash of the transaction.|
|Nonce| QUANTITY | The number of transactions made by the sender prior to this one.|
|blockHash| DATA, 32 Bytes | Hash of the block where this transaction was in. null when its pending.|
|blockNumber| QUANTITY | Block number where this transaction was in. null when its pending.|
|transactionIndex| QUANTITY | Integer of the transactions index position in the block. null when its pending.|
|From| DATA, 20 Bytes | Address of the sender.|
|To| DATA, 20 Bytes | Address of the receiver. null when its a contract creation transaction.|
|Value| QUANTITY | Value transferred in Wei.|
|gasPrice| QUANTITY | Gas price provided by the sender in Wei.|
|Gas| QUANTITY | Gas provided by the sender.|
|Input| DATA | The data send along with the transaction.|
 
## get_transaction_by_hash
 
**Ethereum equivalent:** eth_getTransactionByHash

### Parameters

The hash value of the block (32 bytes).

### Return Values

A transaction object, or null if there is no transaction

|Value | Type | Returns |
| ---	| ---	| ---	|
|Hash| DATA, 32 Bytes | Hash of the transaction.|
|Nonce| QUANTITY | The number of transactions made by the sender prior to this one.|
|blockHash| DATA, 32 Bytes | Hash of the block where this transaction was in. null when its pending.|
|blockNumber| QUANTITY | Block number where this transaction was in. null when its pending.|
|transactionIndex| QUANTITY | Integer of the transactions index position in the block. null when its pending.|
|From| DATA, 20 Bytes | Address of the sender.|
|To| DATA, 20 Bytes | Address of the receiver. null when its a contract creation transaction.|
|Value| QUANTITY | Value transferred in Wei.|
|gasPrice| QUANTITY | Gas price provided by the sender in Wei.|
|Gas| QUANTITY | Gas provided by the sender.|
|Input| DATA | The data send along with the transaction.|
 
## get_balance_by_address
 
**Ethereum equivalent:** eth_getBalance

### Parameters

Two parameters:
* A 20-byte address
* A block-numbered integer (such as 0x1b4), or the string "earliest", "latest" or "pending".

### Return Values

An integer of the current balance in wei
 
## send_transaction
 
**Ethereum equivalent:** eth_sendRawTransaction

### Parameters

Signed transaction data

### Return Values

The transaction hash value, if the transaction is not available, the hash is 0.
 
