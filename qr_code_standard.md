

# Newton QR Code Standard


## Simpler Syntax

[foo] means optional, < bar > are placeholders

`<coin>:<address>[?code=<code>][?amount=<amount>][?unit=<unit>][?label=<label>][?msg=<notes>][?msg_param=param>]`

Items with TBD means this field needs To Be Determined in future versions. Should not be used in current production.

## Description

| Item           | Description       |
| -------------- | ----------------- |
| coin           | Coin. E.g. newton, bitcoin etc. |
| address        | Address to receiver. E.g. NEW182.....1234, JIs...i2x etc. |
| code           | Symbol Code, Optional. E.g. NEW, BTC etc. |
| amount         | Amount to send. Optional. E.g. 12345678.12345678 |
| unit           | Unit to the Amount. Optional. E.g. NEW, CNY, USD etc. |
| label          | Label Message. Optional. E.g. Vending Machine No.123 |
| msg            | Notes in transaction. Optional. |
| msg_param      | Parameters for notes. values: lock, hidden.   |

## Examples

### Just the address

`NEW182cJEKxVB5dX4K2g5qcC1Zb6o64V17NNZCa`

### Prefix and the address

`newton:NEW182cJEKxVB5dX4K2g5qcC1Zb6o64V17NNZCa`

`bitcoin:1J1CwuZHrhYS4nB1g3osdKZtSrGWYFaT3E`

### Address With name

`newton:NEW182cJEKxVB5dX4K2g5qcC1Zb6o64V17NNZCa?label=Sir-Newton`

### Request 123,456.12345678 NEW to "Someone Newton"

`newton:NEW182cJEKxVB5dX4K2g5qcC1Zb6o64V17NNZCa?amount=123456.12345678&label="Someone Newton"`

### Request equivalence amount of 100 CNY to NEW Address

`newton:NEW182cJEKxVB5dX4K2g5qcC1Zb6o64V17NNZCa?amount=100&unit=CNY`

### Request 123,456.12345678 NEW with Message

`newton:NEW182cJEKxVB5dX4K2g5qcC1Zb6o64V17NNZCa?amount=123456.12345678&msg=thisIsMyMessage`

### Request 123,456.12345678 NEW with Label and Message

`newton:NEW182cJEKxVB5dX4K2g5qcC1Zb6o64V17NNZCa?amount=123456.12345678&label="New Vending Machine"&msg=thisIsMyMessage`

### Request 123,456.12345678 NEW with Message not editable by user

`newton:NEW182cJEKxVB5dX4K2g5qcC1Zb6o64V17NNZCa?amount=123456.12345678&msg=thisIsMyMessage&msg_param=-l`
