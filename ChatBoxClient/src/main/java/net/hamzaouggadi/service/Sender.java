package net.hamzaouggadi.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Sender implements Runnable {

    private final SocketChannel socketChannel;
    private final Scanner scanner;

    public Sender(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Please Enter a message : ");
                String message = scanner.nextLine();
                ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(message);
                socketChannel.write(byteBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
