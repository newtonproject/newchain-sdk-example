const newchainWeb3 = require("newchain-web3");
const newchainAccount = require('newchain-web3-accounts');
const newTx = require("newchainjs-tx");
const base58check = require("base58check");
const HDKey = require('newchain-hdkey')

// config rpc url
const DevRpc = "https://rpc1.newchain.newtonproject.org";
const testRpc = "https://rpc1.newchain.newtonproject.org";
const mainRpc = "tbd";

// config chain ID
const devChainId = 1002;
const testChainId = 1007;
const mainChainId = 1012;


/**
 * generate the account, you can get your hex address, and your privateKey.
 */
const web3 = new newchainWeb3(DevRpc);
const account = new newchainAccount.Accounts(DevRpc);

const accountObj = account.create() 
console.log("Account address: " + accountObj.address);
console.log("Account private key: " + accountObj.privateKey);

/**
 * define the address and private Key
 */
const address = "0x6cCD72b70926f5794B0B6FBD2e19c9e3F105044F";
const privateKey = "0x4befb65242ca03dc31cc446c6e17a91f40db9ad0fc03e16264103e8e7fae32fc";
const toAddress = "0x9d851444143ee6fb8c535b183c3ee191e79666f5";
const privBuffer = Buffer.from(privateKey.replace("0x",""), 'hex');
console.log("address:" + account.privateKeyToAccount(privateKey).address);

signUseTx();
// testConvertAddress();

function testConvertAddress() {
    var newAddress = "NEW17zGziJeWdpN8YTQ94kDrAqC8rDaTmw27yMK";
    console.log("NEW Address is: " + hexAddress2NewAddress(address, testChainId));
    console.log("Hex Address is: " + newAddress2HexAddress(newAddress));
}

function signUseTx() {
    var value = 1100200;
    web3.eth.getBalance(address).then(balance => { console.log("Balance is:" + balance); }).catch(new Function());

    web3.eth.getTransactionCount(address).then(
        nonce => {
            console.log("Nonce: " + nonce);
            web3.eth.getGasPrice().then(gasPrice => {
                console.log("Gas price: " + gasPrice);
                web3.eth.estimateGas(
                    {
                        to: toAddress,
                        data: ""
                    }
                ).then(gasLimit => {
                    console.log("Gas limit: " + gasLimit);
                    const txParams = {
                        nonce: convertHexString(nonce),
                        gasPrice: convertHexString(gasPrice), 
                        gasLimit: convertHexString(gasLimit),
                        to: toAddress, 
                        value: convertHexString(value), 
                        data: '',
                        chainId:testChainId 
                    };
                    const tx = new newTx(txParams);
                    tx.sign(privBuffer);
                    const serializedTx = tx.serialize();
                    const raw = "0x" + serializedTx.toString("hex");
                    console.log("Raw serialized transaction: " + raw);

                    web3.eth.sendSignedTransaction(raw)
                    .on('receipt', receipt => {
                        console.log("Receipt: "+ receipt)
                        console.log(receipt)
                    })
                    // .on('confirmation', (confirmationNumber, receipt) => {
                    //     console.log("confirmation: " + confirmationNumber)
                    //     console.log(receipt)
                    // })
                    .on('transactionHash', transactionHash => {
                        console.log("transactionHash:")
                        console.log(transactionHash)
                    })
                    .on('error', error => {
                        console.log("error:")
                        console.log(error)
                    })
                    .catch((error) => {
                        console.log("catch:error:");
                        console.log(error);
                    });

                }).catch(new Function());
            }).catch(new Function());
        }
    );
}

/**
 * 
 * @param {number, string} input convert input to hex string.
 */
function convertHexString(input){
    var res;
    if(typeof(input) == "number") {
        res = "0x" + input.toString(16);
    }else if(input != "undefine" && typeof(input) == "string" && input.startsWith("0x")){
        res = input;
    }  else {
        res = "0x" + parseInt(input).toString("16");
    }
    return res;
}

/**
 * convert hex address to new address.
 * @param {String} hexAddress 
 * @param {int} chainId 
 */
function hexAddress2NewAddress(hexAddress, chainId) {
    if(hexAddress.startsWith("0x")) {
        hexAddress = hexAddress.slice(2);
    }
    var PREFIX = "NEW";
    var data = chainId.toString(16).slice(-8) + hexAddress;
    if(data.length % 2 != 0) {
        data = "0" + data;
    }
    return PREFIX + base58check.encode(data);
}

/**
 * convert new address to hex address.
 * @param {String} newAddress 
 */
function newAddress2HexAddress(newAddress) {
    if(typeof(newAddress) == "string" && newAddress.startsWith("NEW")) {
        return "0x" + base58check.decode(newAddress.slice(3), "hex").data.slice(4);
    } else {
        return newAddress;
    }
}
