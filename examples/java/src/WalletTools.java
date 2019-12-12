import service.NewKeystoreImpl;
import service.StringUtil;

import java.io.File;

/**
 * @author weixuefeng@lubangame.com
 * @version $Rev$
 * @time: 2019/12/12--1:33 PM
 * @description
 * @copyright (c) 2019 Newton Foundation. All rights reserved.
 */
public class WalletTools {
    /**
     * 不同环境的keystore，助记词需要切换 {@link service.NewAddressUtils} 中的 ChainID, dev 1002, testnet 1007, release 1012.
     * @param args
     */
    public static void main(String args[]) {
        String path = "";
        String mnemonic = "1 2 ... 12"; //12 个助记词
        NewKeystoreImpl keystore = new NewKeystoreImpl(new File(path));
//        keystore.createAccountMnemonic().subscribe(next-> {
//            StringBuilder builder = new StringBuilder();
//            for(String s : next) {
//                builder.append(s).append(" ");
//            }
//            System.out.println(builder.toString());
//        });
//
        keystore.importMnemonic(StringUtil.getMnemonicFromString(mnemonic), "123456")
                .subscribe(next-> {
                    System.out.println(next.address);
                });
    }
}
