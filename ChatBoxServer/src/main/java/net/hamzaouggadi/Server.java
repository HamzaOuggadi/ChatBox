package net.hamzaouggadi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Server {

    public static final InetSocketAddress serverAddress = new InetSocketAddress(9090);
    private static final List<SocketChannel> clients = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Server starting ...");

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            Selector selector = Selector.open()) {

            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(serverAddress);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started on port : " + serverAddress.getPort());
            System.out.println("Waiting for events ...");

            while (true) {

                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();

                Iterator<SelectionKey> selectionKeyIterator = keys.iterator();

                while (selectionKeyIterator.hasNext()) {

                    SelectionKey key = selectionKeyIterator.next();

                    selectionKeyIterator.remove();

                    if (key.isAcceptable()) {
                        System.out.println("Got a new connection !");
                        handleAcceptableKey(key, selector);
                    } else if (key.isReadable()) {
                        handleReadableKey(key);
                    }

                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleAcceptableKey(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        clients.add(socketChannel);
    }

    private static void handleReadableKey(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int bytesRead = socketChannel.read(byteBuffer);

        if (bytesRead == -1) {
            socketChannel.close();
            key.cancel();
            return;
        }

        byteBuffer.flip();
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
        String message = new String(charBuffer.array());

        System.out.println("Message : " + message);

        publishToAllClients(message, socketChannel);

        byteBuffer.clear();

    }

    private static void publishToAllClients(String message, SocketChannel senderChannel) throws IOException {
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(message);
        for (SocketChannel channel : clients) {
            if (!channel.equals(senderChannel)) {
                channel.write(byteBuffer);
                byteBuffer.rewind();
            }
        }
    }
}