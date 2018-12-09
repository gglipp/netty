package com.panlijun.nio;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author panlijun
 */
public class NioServer {
    private static final Logger log = getLogger(NioServer.class);

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.socket().bind(new InetSocketAddress(8888));

        Selector selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("nio server started.");

        while (true) {
            if (selector.select() == 0) {
                continue;
            }
            log.info("selected.");

            Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();
                selectionKeyIterator.remove();

                if (selectionKey.isAcceptable()) {
                    log.info("acceptable.");

                    ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel channel = server.accept();

                    channel.configureBlocking(false);

                    channel.register(selector, SelectionKey.OP_READ);
                }

                if (selectionKey.isReadable()) {
                    log.info("readable");

                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    if (channel.isOpen()) {
                        StringBuilder content = new StringBuilder();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        while (channel.read(byteBuffer) > 0) {
                            byteBuffer.flip();
                            content.append(new String(byteBuffer.array()).trim());
                        }

                        log.info("message received : {}", content.toString());

                        ByteBuffer outBuffer = ByteBuffer.wrap("Hello".getBytes());
                        channel.write(outBuffer);
                        log.info("message 'Hello' returned.");
                    }
                }
            }
        }


    }

}
