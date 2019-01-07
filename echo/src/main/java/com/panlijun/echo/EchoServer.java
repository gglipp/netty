package com.panlijun.echo;

import com.panlijun.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;

import java.net.InetSocketAddress;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author panlijun
 */
public class EchoServer {
    private static final Logger log = getLogger(EchoServer.class);

    public static void main(String[] args) throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            // create serverBootstrap
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(loopGroup)
                    // assign the channel for nio
                    .channel(NioServerSocketChannel.class)
                    // assign the InetSocketAddress
                    .localAddress(new InetSocketAddress(8888))
                    // add the EchoServerHandler to child channel's pipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // because of the Sharable annotation, all different channels share the same EchoServerHandler instance
                            ch.pipeline().addLast(serverHandler);
                        }
                    });

            ChannelFuture future = serverBootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            loopGroup.shutdownGracefully().sync();
        }
    }
}
