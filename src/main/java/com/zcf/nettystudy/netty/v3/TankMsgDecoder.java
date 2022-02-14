package com.zcf.nettystudy.netty.v3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-14-16:00
 * @Description: com.zcf.nettystudy.netty.v3
 * @version: 1.0
 */
public class TankMsgDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<8)return;//解决 TCP 拆包 粘包问题
        //in.markReaderIndex();
        int x=in.readInt();
        int y=in.readInt();
        out.add(new TankMsg(x,y));
    }
}
