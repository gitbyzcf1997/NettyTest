package com.zcf.nettystudy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-09-21:08
 * @Description: com.zcf.nettystudy.nio
 * @version: 1.0
 */
public class Server {
    public static void main(String[] args) throws IOException {
        //全双工
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("127.0.0.1",8888));
        //设置阻塞态  默认true为阻塞
        ssc.configureBlocking(false);
        System.out.println("server started,listening on :"+ssc.getLocalAddress());
        //选择器(大管家)  不断的循环 看看是否有事件发生
        Selector selector = Selector.open();
        //将chanel注册到选择器中 注册的是连接事件
        ssc.register(selector,SelectionKey.OP_ACCEPT);
        while (true){
            selector.select();//阻塞方法，选择器监听的事件发生时
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                it.remove();
                handle(key);
            }
        }
    }

    private static void handle(SelectionKey key) {
        //判断是否可以连接的事件
        if(key.isAcceptable()){
            try {
                //拿到channel
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                //从通道中产生新的SocketChannel与Client连接
                SocketChannel sc = ssc.accept();
                //将通道设置为非阻塞态
                sc.configureBlocking(false);
                sc.register(key.selector(),SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(key.isReadable()){
            SocketChannel sc=null;
            try{
                sc=(SocketChannel)key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(512);
                buffer.clear();
                int len=sc.read(buffer);
                if(len!=-1){
                    System.out.println(new String(buffer.array(),0,len));
                }
                ByteBuffer bufferToWrite = ByteBuffer.wrap("HelloClient".getBytes());
                sc.write(bufferToWrite);
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(sc!=null){
                    try{
                        sc.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
