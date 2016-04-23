package com.NIOServers.Http.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 请求方法URI协议/版本+请求正文
 *
 */
class RequestHeader {
	
	private static Logger logger = Logger.getLogger("RequestHeader");
	
	/**
	 * 请求的URI
	 */
	private String URI;
	/**
	 * 请求方法
	 */
	private String method;
	/**
	 * 存放请求头集合
	 */
	private Map<String, String> head;
	/**
	 * 存放请求表单数据集合
	 */
	private Map<String, String> queryMap;
	
	public RequestHeader() {
		URI = "";
		method = "";
		head = Collections.emptyMap();
		queryMap = Collections.emptyMap();
	}
	
	public void parseHeader(byte[] head) throws IOException{
		try(BufferedReader reader = new BufferedReader(new StringReader(new String(head)))){
			Map<String ,String> headMap = new HashMap<>();
			String path;
			String method;
			try{
				String line = reader.readLine();
				//请求的第一行为“方法URL协议/版本”，包括请求方法 请求URI 以及 协议版本（ep:GET /sample.jspHTTP /1.1）
				//其中三部分以空格隔开，故以空格分割第一行
				String[] lineOne = line.split("\\s");
				//可能包括%20等转义字符，故用URL API解码URI
				path = URLDecoder.decode(lineOne[1] , "utf-8");
				//获得请求方法
				method = lineOne[0];
				//继续读取请求头
				while((line = reader.readLine())!=null){
					//请求头包含许多有关的客户端环境和请求正文的有用信息，名字与属性以：分开
					/* Accept:image/gif.image/jpeg.*
						Accept-Language:zh-cn
						Connection:Keep-Alive
						Host:localhost
						User-Agent:Mozila/4.0(compatible:MSIE5.01:Windows NT5.0)
						Accept-Encoding:gzip,deflate
					 */
					String[] keyValue = line.split(":");
					headMap.put(keyValue[0].trim(), keyValue[1].trim());
				}
			}catch(IOException e){
				logger.log(Level.SEVERE, e, ()->"请求解析有错误");
				throw new RuntimeException(e);
			}			
			Map<String , String > queryMap = Collections.emptyMap();
			// 若请求URI中含有表单数据，则解析请求数据
			int index = path.indexOf('?');
			if(index != -1){
				queryMap = new HashMap<>();
				//解析请求数据，并放在queryMap中
				Request.parseParameters(path.substring(index + 1), queryMap);
				//截断URI
				path = path.substring(0 ,  index);
			}
			
			this.URI = path;
			this.method = method;
			this.head = headMap;
			this.queryMap = queryMap;
		}
	}
	
	public String getURI() {
        return URI;
    }

    public String getMethod() {
        return method;
    }

    public boolean containKey(String key) {
        return queryMap.containsKey(key);
    }

    public Map<String, String> getQueryMap() {
        return Collections.unmodifiableMap(queryMap);
    }

    public String queryValue(String key) {
        return queryMap.get(key);
    }

    public String getContentType() {
        return head.get("Content-Type");
    }

    public int getContentLength() {
        return Integer.valueOf(head.getOrDefault("Content-Length", "0"));
    }
}
