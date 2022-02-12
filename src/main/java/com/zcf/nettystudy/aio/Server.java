package com.zcf.nettystudy.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-09-22:04
 * @Description: com.zcf.nettystudy.aio
 * @version: 1.0
 */
public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        final AsynchronousServerSocketChannel serverChannel=AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress(8888));
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment) {
                    serverChannel.accept(null,this);
                    try{
                        System.out.println(client.getRemoteAddress());
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                            @Override
                            public void completed(Integer result, ByteBuffer attachment) {
                                attachment.flip();
                                System.out.println(new String(attachment.array(),0,result));
                                client.write(ByteBuffer.wrap("HelloClient".getBytes()));
                            }

                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {
                                    exc.printStackTrace();
                            }
                        });
                    }catch (IOException e){
                        e.printStackTrace();
                    }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                    exc.printStackTrace();
            }
        });
        while (true){
            Thread.sleep(1000);
        }
    }
}
