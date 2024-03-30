package net.hamzaouggadi.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Receiver implements Runnable {

    private final SocketChannel socketChannel;

    public Receiver(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        while (true) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            try {
                byteBuffer.clear();
                int bytesRead = socketChannel.read(byteBuffer);
                if (bytesRead == -1) {
                    System.out.println("Server closed the connection.");
                    return;
                }
                byteBuffer.flip();
                String message = new String(StandardCharsets.UTF_8.decode(byteBuffer).array());
                System.out.println("Received Message : " + message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
