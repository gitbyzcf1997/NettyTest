package com.zcf.nettystudy.bio;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-09-20:32
 * @Description: com.zcf.nettystudy.bio
 * @version: 1.0
 */

import java.io.IOException;
import java.net.Socket;

/**
 * BIO的Client客户端
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("127.0.0.1", 8888);
        s.getOutputStream().write("HelloServer".getBytes());
        s.getOutputStream().flush();
        //s.getOutputStream().close();//关闭Socket的输出流
        System.out.println("write over,waiting for msg back..");
        byte[]bytes=new  byte[1024];
        int len=s.getInputStream().read(bytes);
        System.out.println(new String(bytes,0,len));
        s.close();//关闭Socket
    }
    //半双工：Server往外写的时候不能同时读，读的时候不能同时写
}
