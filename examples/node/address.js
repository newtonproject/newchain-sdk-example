const bip39 = require("bip39")
const HDKey = require("newchain-hdkey")
const newchainAccount = require('newchain-web3-accounts');
const DevRpc = "https://rpc1.newchain.newtonproject.org";
const base58check = require("base58check");

const account = new newchainAccount.Accounts(DevRpc);
// config chain ID
const devChainId = 1002;
const testChainId = 1007;
const mainChainId = 1012;

const mnemonic = "turn dentist tomato cause answer orbit harbor version luxury purpose broom rubber"


test()


function test() {
	var privateKey = getPrivateFromMnemonic(mnemonic)
	var wallet = getAccountFromPrivateKey(privateKey)
	var hexAddress = wallet.address
	var newAddress = hexAddress2NewAddress(hexAddress, testChainId)
	console.log(newAddress)
}


function getAccountFromPrivateKey(privateKey) {
	return account.privateKeyToAccount(privateKey);
}

function getPrivateFromMnemonic(mnemonic) {
	var seed = bip39.mnemonicToSeedSync(mnemonic)
	var hdKey = HDKey.fromMasterSeed(Buffer.from(seed.toString('hex'), 'hex'))
	var path = "m/44'/1642'/0'/0/0"
	var childKey = hdKey.derive(path)
	const privBuffer = childKey.privateKey
	const privateKey = "0x" + privBuffer.toString("hex")
	return privateKey;
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