package com.NIOServers.Http.Response;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.NIOServers.util.PropertiesHelper;

/**
 * HTTP响应
 *
 */
public class Response {
	
	private static Logger logger = Logger.getLogger("Response");
	
	/**
	 * 协议版本固定为HTTP/1.1
	 */
	private static final String HTTP_VERSION = "HTTP/1.1";
	/**
	 * 编码格式固定为UTF-8
	 */
	public static final String CHARSET = "utf-8";
	/**
	 * Date固定为rfc822格式 , 注意要讲Locale设置为English
	 */
	private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE,d MMM yyyy HH:mm:ss zzz" , Locale.ENGLISH);
	
	static {
		//设置时区
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	/**
	 * 响应状态码
	 */
	protected Status status;
	/**
	 * 响应头部信息
	 */
	protected Map<String , String> heads;
	/**
	 * 响应正文
	 */
	protected byte[] content;
	
	public Response(Status status){
		this.status = status;
		heads = new HashMap<>();
		content = new byte[0];
		heads.put("Date", simpleDateFormat.format(new Date()));
		heads.put("Server", PropertiesHelper.getProperty("server_name" , "Telemarketer"));
		heads.put("Connection", "Close");
	}
	
	public void setHead(String key, String value) {
        heads.put(key, value);
    }

    public String getField(String key) {
        return heads.get(key);
    }

    public Status getStatus() {
        return status;
    }

    /**
     *  使用一个finalData保存最后的结果，一旦调用就不可修改，同时防止重复读取时发送同一个内容
     */
    private ByteBuffer finalData = null;
    
    public ByteBuffer getByteBuffer(){
    	if(finalData == null){
    		heads.put("Content-Length", String.valueOf(content.length));
    		StringBuilder sb = new StringBuilder();
    		//拼接响应头第一行（ep:HTTP/1.1 200 OK）
    		sb.append(HTTP_VERSION).append(" ").append(status.getCode()).append(" ").append(status.getMessage()).append("\r\n");
    		//拼接响应头
    		/*ep:
    		 * Server:Apache Tomcat/5.0.12
				Date:Mon,6Oct2003 13:23:42 GMT
				Content-Length:112
    		 */
    		for(Map.Entry<String, String> entry : heads.entrySet()){
    			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
    		}
    		//表示响应头结束
    		sb.append("\r\n");
    		byte[] head;
    		try{
    			//将响应头设为UTF-8编码格式
    			head = sb.toString().getBytes(CHARSET);
    		}catch(UnsupportedEncodingException e){
    			logger.log(Level.SEVERE, "amazing, 不支持编码"+CHARSET);
    			throw new IllegalStateException("amazing, 不支持编码"+CHARSET);
    		}
    		finalData = ByteBuffer.allocate(head.length + content.length + 2);
    		finalData.put(head);
    		finalData.put(content);
    		finalData.put((byte) '\r');
    		finalData.put((byte) '\n');
    		//翻转缓冲区，
    		finalData.flip();
    	}
    	return finalData;
    }
}
