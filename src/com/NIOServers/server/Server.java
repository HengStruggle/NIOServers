package com.NIOServers.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.NIOServers.Http.Response.Response;
import com.NIOServers.Services.Impl.ServiceRegistry;

/**
 *  服务器类
 *  负责等候和处理IO事件
 */
public class Server {
	public static final int DEFAULT_PORT = 8080;
	private Logger logger = Logger.getLogger("Server");
	private InetAddress ip;
	private int port;
	private ExecutorService exeutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private Selector selector;
	
	public Server( InetAddress ip , int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public static void main(String[] args){
		
	}
	
	public void start(){
		init();
		while(true){
			try{
				if(selector.select() == 0){
					continue;
				}
			} catch(IOException e){
				logger.log(Level.SEVERE, e, ()->"selector错误");
				break;
			}
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = readyKeys.iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				
				try{
					iterator.remove();
					if(key.isAcceptable()){
						ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
						SocketChannel cilent = serverSocket.accept();
						cilent.configureBlocking(false);
						cilent.register(selector, SelectionKey.OP_READ);
					} else if(key.isWritable()){
						SocketChannel cilent = (SocketChannel) key.channel();
						Response response = (Response) key.attachment();
						ByteBuffer byteBuffer = response.getByteBuffer();
						if(byteBuffer.hasRemaining()){
							cilent.write(byteBuffer);
						}
						if(!byteBuffer.hasRemaining()){
							key.cancel();
							cilent.close();
						}
					} else if(key.isReadable()){
						SocketChannel client = (SocketChannel) key.channel();
						exeutor.execute(new Connector(client, selector));
						key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
					}
				} catch(Exception e){
					logger.log(Level.SEVERE, e,  ()->"socket channel 出错了");
					key.cancel();
					try{
						key.channel().close();
					} catch(IOException ignored){
						
					}
				}
			}
		}
	}
	
	private void init(){
		System.out.println("初始化中...");
		ServerSocketChannel serverChannel ;
		try{
			ServiceRegistry.registerServices();
			serverChannel = ServerSocketChannel.open();
			serverChannel.bind(new InetSocketAddress(this.ip, this.port));
			serverChannel.configureBlocking(false);
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch(IOException e){
			logger.log(Level.SEVERE, e , ()->"初始化错误");
			System.exit(1);
		}
		System.out.println("服务器启动 http://" + ip.getHostAddress() + ":" + port + "/");
	}
}
