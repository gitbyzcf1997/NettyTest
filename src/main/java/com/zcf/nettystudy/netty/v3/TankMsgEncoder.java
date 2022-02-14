package com.zcf.nettystudy.netty.v3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Auther:ZhenCF
 * @Date: 2022-02-14-15:58
 * @Description: com.zcf.nettystudy.netty.v3
 * @version: 1.0
 */
public class TankMsgEncoder extends MessageToByteEncoder<TankMsg> {
    @Override
    protected void encode(ChannelHandlerContext ctx, TankMsg msg, ByteBuf buf) throws Exception {
        buf.writeInt(msg.x);
        buf.writeInt(msg.y);
    }
}
