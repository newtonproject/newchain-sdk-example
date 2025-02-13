package main

import (
	"bytes"
	"context"
	"errors"
	"fmt"
	"log"
	"math/big"
	"net/http"
	"os"
	"time"

	"github.com/btcsuite/btcutil/base58"
	"github.com/ethereum/go-ethereum/accounts"
	"github.com/ethereum/go-ethereum/accounts/keystore"
	"github.com/ethereum/go-ethereum/common"
	"github.com/ethereum/go-ethereum/core/types"
	"github.com/ethereum/go-ethereum/ethclient"
	"github.com/shopspring/decimal"
)

const (
	Decimals = uint8(18)
	Unit     = "AB" // NEW

	MainnetJSONRpcURL = "https://global.rpc.mainnet.newtonproject.org"
	TestnetJSONRpcURL = "https://rpc1.newchain.newtonproject.org"
)

var (
	WalletDir  = "./wallet"
	ToAddress  = "NEW17zackKVVm9wSGNjmQgpqcAjbmLVchHhGt1N"
	Passphrase = "ab"

	big10          = big.NewInt(10)
	big1NEWInISAAC = new(big.Int).Exp(big10, big.NewInt(18), nil)
)

func main() {
	// create keystore
	wallet := keystore.NewKeyStore(WalletDir, keystore.StandardScryptN, keystore.StandardScryptP)

	// get default account, if not, create one
	var (
		err     error
		account accounts.Account
	)
	if len(wallet.Accounts()) == 0 {
		// create new address
		account, err = wallet.NewAccount(Passphrase)
		if err != nil {
			log.Fatal(err)
		}
	} else {
		account = wallet.Accounts()[0]
	}

	address := account.Address
	fmt.Println("Hex Address:", address.String())

	client, err := ethclient.Dial(TestnetJSONRpcURL)
	if err != nil {
		log.Fatal(err)
	}
	defer client.Close()
	ctx := context.Background()

	// get chainId
	chainId, err := client.ChainID(ctx)
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println("ChainId:", chainId.String())

	// get NEW address
	newAddress := AddressToNew(chainId.Bytes(), address)
	fmt.Println("New Address:", newAddress)

	// faucet testnet coin
	GetFaucet(TestnetJSONRpcURL, address.String())
	time.Sleep(6 * time.Second) // just wait 2 blocks

	// show balance
	balance, err := client.BalanceAt(ctx, address, nil)
	if err != nil {
		log.Fatal(err)
	}
	balanceDecimal := decimal.NewFromBigInt(balance, -1*int32(Decimals))
	fmt.Printf("Balance: %s %s\n", balanceDecimal.String(), Unit)

	// sweep balance
	{
		// get nonce
		nonce, err := client.PendingNonceAt(ctx, address)
		if err != nil {
			log.Fatal(err)
		}

		to, err := NewToAddress(chainId.Bytes(), ToAddress)
		if err != nil {
			log.Fatal(err)
		}
		fmt.Printf("To Address is %s (%s)\n", ToAddress, to.String())

		// get gasLimit, for native coin, use 21000
		// for token, call client.EstimateGas
		gasLimit := uint64(21000)

		// gas price
		gasPrice, err := client.SuggestGasPrice(ctx)
		if err != nil {
			log.Fatal(err)
		}

		// fee = gasPrice * gasLimit
		fee := big.NewInt(0).Mul(big.NewInt(0).SetUint64(gasLimit), gasPrice)

		// amount to send
		amount := new(big.Int).Sub(balance, fee)
		if amount.Cmp(balance) >= 0 {
			log.Fatal("Not enough funds")
		}

		// create tx, for native coin transfer, data is nil
		tx := types.NewTransaction(nonce, to, amount, gasLimit, gasPrice, nil)

		// unlock account
		err = wallet.Unlock(account, Passphrase)
		if err != nil {
			log.Fatal(err)
		}

		// sign tx
		signedTx, err := wallet.SignTx(account, tx, chainId)
		if err != nil {
			log.Fatal(err)
		}

		// get hash
		fmt.Printf("Hash: %v\n", signedTx.Hash().String())

		// broadcast tx to blockchain
		err = client.SendTransaction(ctx, signedTx)
		if err != nil {
			log.Fatal(err)
		}

		fmt.Printf("Send %s %s from %s to %s.\n",
			decimal.NewFromBigInt(amount, -1*int32(Decimals)).String(),
			Unit,
			AddressToNew(chainId.Bytes(), address),
			AddressToNew(chainId.Bytes(), to))
	}

}

// GetFaucet only for work testnet
func GetFaucet(rpcURL, address string) {
	url := fmt.Sprintf("%s/faucet?address=%s", rpcURL, address)
	resp, err := http.Get(url)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Get error: %v\n", err)
		return
	}
	if resp.StatusCode == 200 {
		fmt.Printf("Get faucet for %s\n", address)
	}
}

// AddressToNew covert hex address to NEW address
func AddressToNew(chainID []byte, address common.Address) string {
	input := append(chainID, address.Bytes()...)
	return "NEW" + base58.CheckEncode(input, 0)
}

// NewToAddress convert NEW address to hex address
func NewToAddress(chainID []byte, newAddress string) (common.Address, error) {
	if newAddress[:3] != "NEW" {
		return common.Address{}, errors.New("not NEW address")
	}

	decoded, version, err := base58.CheckDecode(newAddress[3:])
	if err != nil {
		return common.Address{}, err
	}
	if version != 0 {
		return common.Address{}, errors.New("illegal version")
	}
	if len(decoded) < 20 {
		return common.Address{}, errors.New("illegal decoded length")
	}
	if !bytes.Equal(decoded[:len(decoded)-20], chainID) {
		return common.Address{}, errors.New("illegal ChainID")
	}

	address := common.BytesToAddress(decoded[len(decoded)-20:])

	return address, nil
}
