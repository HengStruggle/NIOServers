package com.NIOServers.server;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.NIOServers.Http.Exception.IllegalRequestException;
import com.NIOServers.Http.Exception.ServerInternalException;
import com.NIOServers.Http.Request.Request;
import com.NIOServers.Http.Response.NotFoundResponse;
import com.NIOServers.Http.Response.Response;
import com.NIOServers.Http.Response.ServerInternalResponse;
import com.NIOServers.Services.Service;
import com.NIOServers.Services.Impl.ServiceRegistry;


/**
 * 控制器
 */
public class Connector implements Runnable{
	
	private static Logger logger = Logger.getLogger("Connector");
	private final SocketChannel channel ;
	private final Selector selector;
	
	public Connector( SocketChannel channel , Selector selector) {
		this.channel = channel;
		this.selector = selector;
	}

	@Override
	public void run() {
		Request request = null;
		Response response;
		try{
			request = Request.parseRequest(channel);
			if(request == null){
				return ;
			}
			Service service = ServiceRegistry.findService(request.getURI());
			if(service == null){
				response = new NotFoundResponse();
			}else{
				response = service.service(request);
				if(response == null){
					throw new ServerInternalException("Service返回了一个null");
				}
			}
		} catch(ServerInternalException | IOException e){
			logger.log(Level.SEVERE,e, ()->"服务器内部错误");
			System.exit(1);
			response = new ServerInternalResponse();
		} catch(IllegalRequestException e){
			logger.log(Level.SEVERE, e , ()->"请求有错误");
			response = new ServerInternalResponse();
		}
		attachResponse(response);
		
		assert request != null;
		logger.info(request.getMethod() + "\"" + request.getURI() + "\"" + response.getStatus().getCode());
	}
	
	private void attachResponse(Response response){
		try{
			channel.register(selector, SelectionKey.OP_WRITE , response);
			selector.wakeup();
		}catch(ClosedChannelException e){
			logger.log(Level.SEVERE, e , ()->"通道已关闭");
		}
	}
}
