package com.zcf.nettystudy.netty.v2;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-12-23:37
 * @Description: com.zcf.nettystudy.netty.v2
 * @version: 1.0
 */
public class Server {
    //channelGroup：用于存放所有的channel
    public static ChannelGroup clients=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static void main(String[] args) {
        //线程池
        EventLoopGroup bootGroup=new NioEventLoopGroup();
        EventLoopGroup workGroup=new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            ChannelFuture f = b.group(bootGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializerHandler())
                    .bind("127.0.0.1", 8888);
            f.sync();
            f.channel().closeFuture();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
class ServerChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.clients.add(ctx.channel());
        System.out.println(Server.clients.size());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf=null;
        buf=(ByteBuf)msg;
        byte[] bytes=new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(),bytes);
        String content = new String(bytes);
        System.out.println(ctx.channel().alloc().toString());
        //往所有的channel里发送数据
        Server.clients.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
class ServerChannelInitializerHandler extends ChannelInitializer {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new ServerChannelHandler());
    }
}
