package net.hamzaouggadi;

import net.hamzaouggadi.service.Receiver;
import net.hamzaouggadi.service.Sender;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static final InetSocketAddress serverAddress = new InetSocketAddress("oddest.ddns.net", 9090);
    public static final InetSocketAddress localAddress = new InetSocketAddress("127.0.0.1", 9090);

    public static void main(String[] args) throws IOException {

        System.out.println("Client Starting ...");

        try {
            SocketChannel socketChannel = SocketChannel.open(serverAddress);

            Thread senderThread = new Thread(new Sender(socketChannel));
            Thread receiverThread = new Thread(new Receiver(socketChannel));

            senderThread.start();
            receiverThread.start();

            senderThread.join();
            senderThread.join();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}