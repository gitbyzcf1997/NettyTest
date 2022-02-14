package com.zcf.nettystudy.netty.v3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-14-13:37
 * @Description: com.zcf.nettystudy.netty.v3
 * @version: 1.0
 */
public class Client {
    private Channel channel=null;

    public void connect(){
        EventLoopGroup workGroup=new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializerHandler())
                    .connect("127.0.0.1",8888)
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if(!future.isSuccess()){
                                ClientFrame.getTa().setText("客户端连接失败！");
                            }else {
                                ClientFrame.getTf().setText("客户端连接成功！请删除我...");
                                channel=future.channel();
                            }
                        }
                    })
                    .channel().closeFuture()
                    .sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
        }
    }
    public void send(String msg){
        try {
            ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
            channel.writeAndFlush(buf);
        }catch (Exception e){
            e.printStackTrace();
            ClientFrame.closeClientFrame();
        }
    }
    public void closeConnect(){
        this.send("_bye_");
    }
}
class ClientChannelInitializerHandler extends ChannelInitializer {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("active");
        ctx.writeAndFlush(new TankMsg(5,8));
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new TankMsgEncoder()).addLast(new ClientHandler());
    }
}
class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf=null;
        try{
            buf=(ByteBuf)msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(),bytes);
            ClientFrame.updateText(new String(bytes));
        }finally {
            if(buf!=null) ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new TankMsg(5,8));
    }
}