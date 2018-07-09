package com.geth.eth;

import org.junit.Test;

import static org.junit.Assert.*;

public class EthServiceTest {
    @Test
    public void newAccount() throws Exception {


        System.out.println(EthService.newAccount("password"));
        //0xf2c60e9588c81b941a549888590e143c8efbac71
    }

}