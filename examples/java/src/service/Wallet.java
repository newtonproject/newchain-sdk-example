package service;

/**
 * @author weixuefeng@lubangame.com
 * @version $Rev$
 * @time: 2019/12/12--1:15 PM
 * @description
 * @copyright (c) 2019 Newton Foundation. All rights reserved.
 */
public class Wallet{
    public final String address;
    public String type;

    public Wallet(String address) {
        this.address = address;
    }

    public Wallet(String address, String type) {
        this.address = address;
        this.type = type;
    }
}