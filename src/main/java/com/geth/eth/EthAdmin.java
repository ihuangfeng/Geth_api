package com.geth.eth;

import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.core.Request;

import java.io.IOException;

public class EthAdmin {

    /**
     * 输入密码创建地址
     *
     * @param password 密码（建议同一个平台的地址使用一个相同的，且复杂度较高的密码）
     * @return 地址hash
     * @throws IOException
     */
    public static String newAccount(String password) throws IOException {
        Admin admin = EthService.initAdmin();
        Request<?, NewAccountIdentifier> request = admin.personalNewAccount(password);
        NewAccountIdentifier result = request.send();
        return result.getAccountId();
    }

}
