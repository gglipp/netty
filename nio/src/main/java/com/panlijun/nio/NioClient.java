package com.panlijun.nio;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author panlijun
 */
public class NioClient {

    private static final Logger log = getLogger(NioClient.class);

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        socketChannel.connect(new InetSocketAddress("localhost", 8888));

        Selector selector = Selector.open();

        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        while (true) {
            selector.select();
            log.info("selected");

            Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();

            while (selectionKeyIterator.hasNext()) {
                SelectionKey selectionKey = selectionKeyIterator.next();
                selectionKeyIterator.remove();

                if (selectionKey.isConnectable()) {
                    log.info("connecting.");

                    SocketChannel channel = (SocketChannel) selectionKey.channel();

                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }

                    log.info("connected.");
                    channel.configureBlocking(false);

                    channel.write(ByteBuffer.wrap("Message from client.".getBytes()));

                    channel.register(selector, SelectionKey.OP_READ);
                }

                if (selectionKey.isReadable()) {
                    log.info("reading.");
                    SocketChannel channel = (SocketChannel) selectionKey.channel();

                    StringBuilder content = new StringBuilder();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (channel.read(buffer) > 0) {
                        buffer.flip();
                        content.append(new String(buffer.array()));
                    }

                    log.info("Message received : {}", content.toString());

                    selectionKey.interestOps(SelectionKey.OP_READ);

                }
            }
        }
    }
}
