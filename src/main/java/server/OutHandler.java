package server;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx,
                      Object msg,
                      ChannelPromise promise) throws Exception {
        String s = (String) msg;
        ByteBuf buf = ctx.alloc().directBuffer();
        buf.writeBytes(s.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf);
    }
}
