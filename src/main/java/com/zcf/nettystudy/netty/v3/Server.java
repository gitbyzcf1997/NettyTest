package com.zcf.nettystudy.netty.v3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-14-13:20
 * @Description: com.zcf.nettystudy.netty.v3
 * @version: 1.0
 */
public class Server {
    private static ChannelGroup clients=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static ChannelGroup getClients() {
        return clients;
    }


    public void serverStart() {
        //线程池
        EventLoopGroup bootGroup=new NioEventLoopGroup(1);
        EventLoopGroup workGroup=new NioEventLoopGroup(2);
        //Netty引导类
        try {
            ServerBootstrap b = new ServerBootstrap();
            ChannelFuture future=b.group(bootGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitliaizerHandler())
                    .bind(8888)
                    .sync();
            ServerFrame.INSTANCE.updateServerMsg("启动服务成功！");
            future.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
            bootGroup.shutdownGracefully();
        }
    }
}
class ServerChannelInitliaizerHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new TankMsgDecoder()).addLast(new ServerChannelHandler());
    }
}
class ServerChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.getClients().add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf=null;
//        buf=(ByteBuf)msg;
//        byte[] bytes = new byte[buf.readableBytes()];
//        buf.getBytes(buf.readerIndex(),bytes);
//        String str = new String(bytes);
//        ServerFrame.INSTANCE.updateClientrMsg(ctx.channel().remoteAddress()+":"+str);
//        if(str.equals("_bye_")){
//            ServerFrame.INSTANCE.updateServerMsg(ctx.channel().remoteAddress()+"关闭连接");
//            Server.getClients().remove(ctx.channel());
//            ctx.close();
//        }else{
//            Server.getClients().writeAndFlush(msg);
//        }
        try {
            TankMsg tank = (TankMsg) msg;
            ServerFrame.INSTANCE.updateServerMsg(tank.toString());
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //删除出现异常的客户端channel并关闭连接
        Server.getClients().remove(ctx.channel());
        ctx.close();
    }
}
