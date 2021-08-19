package bg.sofia.uni.fmi.mjt.foodanalyzer;

import bg.sofia.uni.fmi.mjt.foodanalyzer.cache.CacheManager;

import bg.sofia.uni.fmi.mjt.foodanalyzer.http.FoodAnalyzerHttpClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class FoodAnalyzerServer {

    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 10000;
    private boolean isStopped;
    private final int serverPort;

    private CommandExecutor commandExecutor;

    public FoodAnalyzerServer(int serverPort, String pathToCache) {
        FoodAnalyzerHttpClient httpClient = new FoodAnalyzerHttpClient(HttpClient.newBuilder().build());
        BarcodeExtractor barcodeExtractor = new BarcodeExtractor();
        CacheManager cacheManager = new CacheManager(pathToCache, "FoodReports.csv", "FoodItems.csv");
        commandExecutor = new CommandExecutor(httpClient, barcodeExtractor, cacheManager);

        this.serverPort = serverPort;
        isStopped = false;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, serverPort));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            System.out.println("Server has started working");
            while (!isStopped) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        buffer.clear();
                        int length = socketChannel.read(buffer);
                        if (length < 0) {
                            System.out.println("Client has closed the connection");
                            socketChannel.close();
                            continue;
                        }
                        sendMessageToClient(buffer, socketChannel, length);


                    } else if (key.isAcceptable()) {
                        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
                        SocketChannel accept = sockChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                    }

                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }

    private void sendMessageToClient(ByteBuffer buffer, SocketChannel socketChannel, int length) {
        String message = new String(buffer.array(), StandardCharsets.UTF_8).substring(0, length);
        String answer = commandExecutor.respondToClient(message);
        buffer.clear();
        buffer.put(answer.getBytes());
        buffer.flip();
        try {
            socketChannel.write(buffer);
        } catch (IOException exception) {
            throw new RuntimeException("There is a problem with the network communication",exception);
        }
    }

    public void stop() {
        isStopped = true;
    }
}
