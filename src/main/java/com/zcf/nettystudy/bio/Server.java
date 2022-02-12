package com.zcf.nettystudy.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-09-20:53
 * @Description: com.zcf.nettystudy.bio
 * @version: 1.0
 */
public class Server {
    public static void main(String[] args)throws IOException {
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress("127.0.0.1",8889));
        while (true){
            Socket s = ss.accept();//阻塞方法
            new Thread(()->{
                handle(s);
            }).start();
            /**
             * BIO这种做法线程数太多，handle中每个线程都等待读取内容写内容 无法及时释放 线程数就很多了
             */
        }
    }

    private static void handle(Socket s) {
        try{
            byte[] bytes = new byte[1024];
            int len = s.getInputStream().read(bytes);
            System.out.println(new String(bytes,0,len));
            s.getOutputStream().write(bytes,0,len);
            s.getOutputStream().flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
