const newchainWeb3 = require("newchain-web3");
const newTx = require("newchainjs-tx");

// config rpc url
const DevRpc = "https://devnet.newchain.cloud.diynova.com";
const testRpc = "https://devnet.newchain.cloud.diynova.com";
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

console.log(account.address);
console.log(account.privateKey);

/**
 * define the address and private Key
 */
const address = "0x32eebc8fd8cb9353eeb5e0ea4ee124dd66ee6a37";
const privateKey = "0xe4dc3fddabf68b36aa61af08e0e0f8c06801e262faec95abf2c67c309ae5d42d";
const toAddress = "0x9d851444143ee6fb8c535b183c3ee191e79666f5";
const privBuffer = Buffer.from(privateKey.replace("0x",""), 'hex');

signUseTx();

function signUseTx() {
    var value = 1100200;
    web3.eth.getBalance(address).then(console.log);
    web3.eth.getTransactionCount(address).then(
        nonce => {
            console.log("Nonce:" + nonce);
            web3.eth.getGasPrice().then(gasPrice => {
                console.log("Gas price:" + gasPrice);
                web3.eth.estimateGas(
                    {
                        to: toAddress,
                        data: ""
                    }
                ).then(gasLimit => {
                    console.log("Gas limit:" + gasLimit);
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
                    console.log(raw);
                    web3.eth.sendSignedTransaction(raw).on("receipt", console.log);
                });
            });
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
