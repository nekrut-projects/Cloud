package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger log = Logger.getLogger(Server.class.getName());

    public Server() {

        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(200 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                    new ObjectHandler()
                            );
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(8189).sync();
            log.log(Level.SEVERE, "server.Server started");
            channelFuture.channel().closeFuture().sync(); // block
        } catch (Exception e) {
            log.log(Level.SEVERE,"e=", e);
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
