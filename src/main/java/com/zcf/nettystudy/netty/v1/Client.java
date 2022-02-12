package com.zcf.nettystudy.netty.v1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;


/**
 * @Auther:ZhenCF
 * @Date: 2022-02-09-22:40
 * @Description: com.zcf.nettystudy.netty
 * @version: 1.0
 */
public class Client {
    public static void main(String[] args) throws Exception{
        //netty自动是多线程的  线程池封装在这
        EventLoopGroup group=new NioEventLoopGroup(2);

        Bootstrap b=new Bootstrap();
        try {
            ChannelFuture f = b.group(group)//设置线程池
                    .channel(NioSocketChannel.class)//SocketChannel类型
                    .handler(new ClientChannelInitializer())//处理
                    .connect("localhost", 8888);//连接地址和端口
                //netty大多方法是异步的
                f.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(!future.isSuccess()){
                            System.out.println("not connected!");
                        }else {
                            System.out.println("connected!");
                        }
                    }
                });
                f.sync();//让netty同步
            System.out.println("...");
            //让程序阻塞住  不让他往下执行
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }
}
class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ClientChildHandler());
    }
}
class ClientChildHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //channel 第一次连上可用，写出一个字符串  Direct Memory
        ByteBuf buf= Unpooled.copiedBuffer("Hello".getBytes());
        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("read:");
        ByteBuf buf=null;
        try{
        buf=(ByteBuf)msg;
        byte[] bytes=new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(),bytes);

        System.out.println(new String(bytes));
        }finally {
            if(buf!=null)ReferenceCountUtil.release(msg);
        }

    }
}
