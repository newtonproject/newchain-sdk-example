const newchainWeb3 = require("newchain-web3");
const newTx = require("newchainjs-tx");
const base58check = require("base58check");

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
const account = new web3.eth.accounts.create();

console.log("Account address: " + account.address);
console.log("Account private key: " + account.privateKey);

/**
 * define the address and private Key
 */
const address = "0x32eebc8fd8cb9353eeb5e0ea4ee124dd66ee6a37";
const privateKey = "0xe4dc3fddabf68b36aa61af08e0e0f8c06801e262faec95abf2c67c309ae5d42d";
const toAddress = "0x9d851444143ee6fb8c535b183c3ee191e79666f5";
const privBuffer = Buffer.from(privateKey.replace("0x",""), 'hex');


signUseTx();
testConvertAddress();

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
                        chainId: devChainId
                    };
                    const tx = new newTx(txParams);
                    tx.sign(privBuffer);
                    const serializedTx = tx.serialize();
                    const raw = "0x" + serializedTx.toString("hex");
                    console.log("Raw serialized transaction: " + raw);
                    web3.eth.sendSignedTransaction(raw).on('receipt', receipt => console.log("Receipt: "+ receipt)).catch(new Function());
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