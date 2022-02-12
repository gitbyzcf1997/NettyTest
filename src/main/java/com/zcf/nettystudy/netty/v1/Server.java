package com.zcf.nettystudy.netty.v1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-09-22:53
 * @Description: com.zcf.nettystudy.netty
 * @version: 1.0
 */
public class Server {
    public static void main(String[] args) throws InterruptedException {
        //负责连接
        EventLoopGroup boosGroup=new NioEventLoopGroup(1);
        //负责事件处理
        EventLoopGroup workerGroup=new NioEventLoopGroup(2);
        //server的启动类
        //指定线程池
        try {
            ServerBootstrap b=new ServerBootstrap();
            ChannelFuture f=b.group(boosGroup, workerGroup)//指定线程池组，一个是用来连接 一个是用来处理IO事件
                    .channel(NioServerSocketChannel.class)//指定Channel的类型
                    .childHandler(new ChannelInitializer<SocketChannel>() {//每一个连接的事件的处理
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();//管道 可以放很多个handle用来处理数据
                            pipeline.addLast(new ServerChildHandler());
                        }
                    })
                    .bind(8888)//绑定8888端口
                    .sync();//同步
            System.out.println("server started!");
            f.channel().closeFuture().sync();//close()->ChannelFuture
        }finally {
            workerGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }
}
class ServerChildHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(Thread.currentThread().getId());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            byte[] bytes=new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(),bytes);
            System.out.println(new String(bytes) );
            ctx.writeAndFlush(buf);
        }finally {
            //if(buf!=null)ReferenceCountUtil.release(buf);
            //System.out.println(buf.refCnt());
        }
    }
}