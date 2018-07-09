package com.geth.eth;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlockNumber;

import java.io.IOException;
import java.math.BigInteger;

public class EthWeb3j {

    /**
     * 获得当前区块高度
     *
     * @return 当前区块高度
     * @throws IOException
     */
    public static BigInteger getCurrentBlockNumber() throws IOException {
        Web3j web3j = EthService.initWeb3j();
        Request<?, EthBlockNumber> request = web3j.ethBlockNumber();
        return request.send().getBlockNumber();
    }
}
