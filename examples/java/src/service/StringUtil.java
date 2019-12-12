package service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weixuefeng@lubangame.com
 * @version $Rev$
 * @time: 2019/12/12--1:23 PM
 * @description
 * @copyright (c) 2019 Newton Foundation. All rights reserved.
 */
public class StringUtil {

    public static ArrayList<String> getMnemonicFromString(String mnemonic) {
        ArrayList<String> list = new ArrayList<>();
        if (null == mnemonic || mnemonic.length() == 0) {
            return null;
        }
        mnemonic = mnemonic.replace("\n", " ").replace("\r"," ");
        String[] strArray = mnemonic.split(" ");
        for (String str : strArray) {
            if(str != null) {
                list.add(str.trim());
            }
        }
        return list;
    }

    public static String getMnemonicFromList(List<String> mnemonic) {
        StringBuilder builder = new StringBuilder();
        for(String str : mnemonic) {
            builder.append(str + " ");
        }
        return builder.toString().trim();
    }

}
