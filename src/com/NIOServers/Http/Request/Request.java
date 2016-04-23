package com.NIOServers.Http.Request;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;

import com.NIOServers.Http.Exception.IllegalRequestException;
import com.NIOServers.util.BytesUtil;

/**
 * Http请求信息由3部分组成：
 * <ul>
 * <li>请求方法URI协议/版本
 * <li>请求头
 * <li>请求正文
 * </ul>
 */
public class Request {
	
	private final RequestHeader header ;
	private final RequestBody body;
	
	private Request(RequestHeader header , RequestBody body){
		this.header = header ;
		this.body = body;
	}
	
	public static Request parseRequest(SocketChannel channel) throws IllegalRequestException, IOException{
		
		//1.读取数据
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		channel.read(buffer);// 会抛出IOException
		buffer.flip();
		
		//2.解析HTTP请求头部数据
		int remaining = buffer.remaining();
		if(remaining == 0){
			return null;
		}
		byte[] bytes = new byte[remaining];
		buffer.get(bytes);
		
		//找到两个\r\n同时出现的地方，即请求头部的尾端
		//因为请求头和请求正文之间是一个空行，表示请求头已经结束
		int position = BytesUtil.indexOf(bytes, "\r\n\r\n");
		if(position == -1){
			throw new IllegalRequestException("请求不合法");
		}
		//得到请求头部
		byte[] head = Arrays.copyOf(bytes, position);
		RequestHeader requestHeader = new RequestHeader();
		//解析头部数据
		requestHeader.parseHeader(head);//会抛出IOException
		//根据contentLength的值继续读取数据
		int contentLength = requestHeader.getContentLength();
		buffer.position(position + 4);
		ByteBuffer bodyBuffer = ByteBuffer.allocate(contentLength);
		bodyBuffer.put(buffer);
		//若有其他Buffer执行读取操作，则会阻塞，故需要循环判断
		while(bodyBuffer.hasRemaining()){
			channel.read(bodyBuffer);//抛出IOException
		}
		byte[] body = bodyBuffer.array();
		RequestBody requestBody = new RequestBody();
		if(body.length != 0){
			requestBody.parseBody(body, requestHeader);
		}
		return new Request(requestHeader, requestBody);
	}
	
	static void parseParameters(String s , Map<String , String> requestParameters){
		String[] paras = s.split("&");
		for(String para : paras){
			String[] split = para.split("=");
			requestParameters.put(split[0], split[1]);
		}
	}
	
	public Map<String, String> getQueryMap() {
        return header.getQueryMap();
    }

    public boolean queryContainKey(String key) {
        return header.containKey(key);
    }

    public String queryValue(String key) {
        return header.queryValue(key);
    }

    public boolean formContainKey(String key) {
        return body.formContainKey(key);
    }

    public boolean mimeContainKey(String key) {
        return body.mimeContainKey(key);
    }


    public Map<String, String> getFormMap() {
        return body.getFormMap();
    }

    public Map<String, MIMEData> getMimeMap() {
        return body.getMimeMap();
    }

    public String formValue(String key) {
        return body.formValue(key);
    }

    public MIMEData mimeValue(String key) {
        return body.mimeValue(key);
    }

    public String getURI() {
        return header.getURI();
    }

    public String getMethod() {
        return header.getMethod();
    }
	
}
