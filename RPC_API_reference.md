(1), get_block_height
```
API: eth_blockNumber

Parameter: none

Result: an integer of the current block number where the client is located
```
(2), get_block_by_height
```
API: eth_getBlockByNumber

Parameter: two arguments, the first being a block-numbered integer (such as 0x1b4), or the string "earliest", "latest" or "pending".
The second one is the boolean type. If true, it returns all the complete transaction objects. If it is false, it only returns the hash value of the transaction object.

Result:
Returns a block object, if it is not found to be null.
Number: QUANTITY - the block number. null when its pending block.
Hash: DATA, 32 Bytes - hash of the block. null when its pending block.
parentHash: DATA, 32 Bytes - hash of the parent block.
Nonce: DATA, 8 Bytes - hash of the generated proof-of-work. null when its pending block.
sha3Uncles: DATA, 32 Bytes - SHA3 of the uncles data in the block.
logsBloom: DATA, 256 Bytes - the bloom filter for the logs of the block. null when its pending block.
transactionsRoot: DATA, 32 Bytes - the root of the transaction trie of the block.
stateRoot: DATA, 32 Bytes - the root of the final state trie of the block.
receiptsRoot: DATA, 32 Bytes - the root of the receipts trie of the block.
Miner: DATA, 20 Bytes - the address of the beneficiary to whom the mining rewards were given.
Difficulty: QUANTITY - integer of the difficulty for this block.
totalDifficulty: QUANTITY - integer of the total difficulty of the chain until this block.
extraData: DATA - the "extra data" field of this block.
Size: QUANTITY - integer the size of this block in bytes.
gasLimit: QUANTITY - the maximum gas allowed in this block.
gasUsed: QUANTITY - the total used gas by all transactions in this block.
Timestamp: QUANTITY - the unix timestamp for when the block was collated.
Transactions: Array - Array of transaction objects, or 32 Bytes transaction hashes depending on the last given parameter.
Uncles: Array - Array of uncle hashes.
```
(3), get_block_by_hash
```
API: eth_getBlockByHash

Parameter: two parameters, the first one is the hash value of the block (32Bytes).
The second one is the boolean type. If true, it returns all the complete transaction objects. If it is false, it only returns the hash value of the transaction object.

Result:
Returns a block object, if it is not found to be null.
Number: QUANTITY - the block number. null when its pending block.
Hash: DATA, 32 Bytes - hash of the block. null when its pending block.
parentHash: DATA, 32 Bytes - hash of the parent block.
Nonce: DATA, 8 Bytes - hash of the generated proof-of-work. null when its pending block.
sha3Uncles: DATA, 32 Bytes - SHA3 of the uncles data in the block.
logsBloom: DATA, 256 Bytes - the bloom filter for the logs of the block. null when its pending block.
transactionsRoot: DATA, 32 Bytes - the root of the transaction trie of the block.
stateRoot: DATA, 32 Bytes - the root of the final state trie of the block.
receiptsRoot: DATA, 32 Bytes - the root of the receipts trie of the block.
Miner: DATA, 20 Bytes - the address of the beneficiary to whom the mining rewards were given.
Difficulty: QUANTITY - integer of the difficulty for this block.
totalDifficulty: QUANTITY - integer of the total difficulty of the chain until this block.
extraData: DATA - the "extra data" field of this block.
Size: QUANTITY - integer the size of this block in bytes.
gasLimit: QUANTITY - the maximum gas allowed in this block.
gasUsed: QUANTITY - the total used gas by all transactions in this block.
Timestamp: QUANTITY - the unix timestamp for when the block was collated.
Transactions: Array - Array of transaction objects, or 32 Bytes transaction hashes depending on the last given parameter.
Uncles: Array - Array of uncle hashes.
```
(4), get_transaction_count_by_height
```
API: eth_getBlockTransactionCountByNumberParameter: the integer of the block number (such as 0x1b4), or the string "earliest", "latest" or "pending".

Result: number of transactions, hexadecimal integer
```
(5), get_transaction_by_height_and_index
```
API: eth_getTransactionByBlockNumberAndIndex

Parameter: two arguments, the first being a block-numbered integer (such as 0x1b4), or the string "earliest", "latest" or "pending".
The second is the index position of the transaction (for example: 0x0)

Result:
a transaction object, or null if there is no transaction
Hash: DATA, 32 Bytes - hash of the transaction.
Nonce: QUANTITY - the number of transactions made by the sender prior to this one.
blockHash: DATA, 32 Bytes - hash of the block where this transaction was in. null when its pending.
blockNumber: QUANTITY - block number where this transaction was in. null when its pending.
transactionIndex: QUANTITY - integer of the transactions index position in the block. null when its pending.
From: DATA, 20 Bytes - address of the sender.
To: DATA, 20 Bytes - address of the receiver. null when its a contract creation transaction.
Value: QUANTITY - value transferred in Wei.
gasPrice: QUANTITY - gas price provided by the sender in Wei.
Gas: QUANTITY - gas provided by the sender.
Input: DATA - the data send along with the transaction.
```
(6), get_transaction_by_hash
```
API: eth_getTransactionByHash

Parameter: The hash value of the block (32Bytes).

Result:
a transaction object, or null if there is no transaction
Hash: DATA, 32 Bytes - hash of the transaction.
Nonce: QUANTITY - the number of transactions made by the sender prior to this one.
blockHash: DATA, 32 Bytes - hash of the block where this transaction was in. null when its pending.
blockNumber: QUANTITY - block number where this transaction was in. null when its pending.
transactionIndex: QUANTITY - integer of the transactions index position in the block. null when its pending.
From: DATA, 20 Bytes - address of the sender.
To: DATA, 20 Bytes - address of the receiver. null when its a contract creation transaction.
Value: QUANTITY - value transferred in Wei.
gasPrice: QUANTITY - gas price provided by the sender in Wei.
Gas: QUANTITY - gas provided by the sender.
Input: DATA - the data send along with the transaction.
```
(7), get_balance_by_address
```
API: eth_getBalance

Parameter: The first parameter is: 20Bytes address
The second parameter is: block-numbered integer (such as 0x1b4), or the string "earliest", "latest" or "pending".

Result: an integer of the current balance in wei
```
(8), send_transaction
```
API: eth_sendRawTransaction

Parameter: signed transaction data

Result: returns the transaction hash value, if the transaction is not available, the hash is 0.
```