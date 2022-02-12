package com.zcf.nettystudy.netty.v2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.net.Inet4Address;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-12-23:23
 * @Description: com.zcf.nettystudy.netty.v2
 * @version: 1.0
 */
public class Client {
    public static void main(String[] args) {
        //线程池
        //EventLoopGroup bootGroup=new NioEventLoopGroup(1);
        EventLoopGroup workGroup=new NioEventLoopGroup(2);
        //Netty 启动类
        try {
            Bootstrap b = new Bootstrap();
            ChannelFuture f = b.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer())
                    .connect("127.0.0.1", 8888);
            f.sync();
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
        }
    }
}
class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ChannelHandler());
    }
}
class ChannelHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello".getBytes()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
