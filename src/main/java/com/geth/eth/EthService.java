package com.geth.eth;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.BooleanResponse;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.geth.Geth;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

public class EthService {
    //这里值得一提的是这里采用的是 HttpService 来初始化一个与 geth 通信的 http 连接，
    // 在真实生产中可以采用 OkHttpClient 来对此链接进行设置超时时间等参数。
    private static final String URL = "http://192.168.1.70:18545/";


    //在使用上面的接口调用时，request.send() 方法是同步返回结果，
    // 在某些情况下可能会导致响应比较慢，因此此框架提供了 request.sendAsync() 异步操作
    // 当进行异步操作时只是将操作的执行发送出去，并没有获得相应的操作结果
    // 需要通过监听器获取结果的通知。


    //在真实生产环境使用时需要进行相应的优化处理，
    // 比如网络优化、初始化链接优化、超时时间设定、冷钱包设置、
    // 私钥与 Geth 节点分离、系统安全考虑等。



    /**
     * 初始化web3j普通api调用
     * @return web3j
     */
    public static Web3j initWeb3j() {
        return Web3j.build(getService());
    }

    /**
     * 初始化personal级别的操作对象
     * @return Geth
     */
    public static Geth initGeth(){
        return Geth.build(getService());
    }

    /**
     * 初始化admin级别操作的对象
     * @return Admin
     */
    public static Admin initAdmin(){
        return Admin.build(getService());
    }

    /**
     * 通过http连接到geth节点
     * @return
     */
    private static HttpService getService(){
        return new HttpService(URL);
    }

    /**
     * 输入密码创建地址
     *
     * @param password 密码（建议同一个平台的地址使用一个相同的，且复杂度较高的密码）
     * @return 地址hash
     * @throws IOException
     */
    public static String newAccount(String password) throws IOException {
        Admin admin = initAdmin();
        Request<?, NewAccountIdentifier> request = admin.personalNewAccount(password);
        NewAccountIdentifier result = request.send();
        return result.getAccountId();
    }

    /**
     * 获得当前区块高度
     *
     * @return 当前区块高度
     * @throws IOException
     */
    public static BigInteger getCurrentBlockNumber() throws IOException {
        Web3j web3j = initWeb3j();
        Request<?, EthBlockNumber> request = web3j.ethBlockNumber();
        return request.send().getBlockNumber();
    }

    /**
     * 解锁账户，发送交易前需要对账户进行解锁
     *
     * @param address  地址
     * @param password 密码
     * @param duration 解锁有效时间，单位秒
     * @return
     * @throws IOException
     */
    public static Boolean unlockAccount(String address, String password, BigInteger duration) throws IOException {
        Admin admin = initAdmin();
        Request<?, PersonalUnlockAccount> request = admin.personalUnlockAccount(address, password, duration);
        PersonalUnlockAccount account = request.send();
        return account.accountUnlocked();

    }

    /**
     * 账户解锁，使用完成之后需要锁定
     *
     * @param address
     * @return
     * @throws IOException
     */
    public static Boolean lockAccount(String address) throws IOException {
        Geth geth = initGeth();
        Request<?, BooleanResponse> request = geth.personalLockAccount(address);
        BooleanResponse response = request.send();
        return response.success();
    }

    /**
     * 根据hash值获取交易
     *
     * @param hash
     * @return
     * @throws IOException
     */
    public static EthTransaction getTransactionByHash(String hash) throws IOException {
        Web3j web3j = initWeb3j();
        Request<?, EthTransaction> request = web3j.ethGetTransactionByHash(hash);
        return request.send();
    }

    /**
     * 获得ethblock
     *
     * @param blockNumber 根据区块编号
     * @return
     * @throws IOException
     */
    public static EthBlock getBlockEthBlock(Integer blockNumber) throws IOException {
        Web3j web3j = initWeb3j();

        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
        Request<?, EthBlock> request = web3j.ethGetBlockByNumber(defaultBlockParameter, true);
        EthBlock ethBlock = request.send();

        return ethBlock;
    }
    /**
     * 发送交易并获得交易hash值
     *
     * @param transaction
     * @param password
     * @return
     * @throws IOException
     */
    public static String sendTransaction(Transaction transaction, String password) throws IOException {
        Admin admin = initAdmin();
        Request<?, EthSendTransaction> request = admin.personalSendTransaction(transaction, password);
        EthSendTransaction ethSendTransaction = request.send();
        return ethSendTransaction.getTransactionHash();

    }

    /**
     * 指定地址发送交易所需nonce获取
     *
     * @param address 待发送交易地址
     * @return
     * @throws IOException
     */
    public static BigInteger getNonce(String address) throws IOException {
        Web3j web3j = initWeb3j();
        Request<?, EthGetTransactionCount> request = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST);
        return request.send().getTransactionCount();
    }


}
