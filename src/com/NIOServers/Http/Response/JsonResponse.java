package com.NIOServers.Http.Response;

import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import com.NIOServers.Http.Exception.ServerInternalException;

/**
 * Josn响应
 *
 */
public class JsonResponse extends Response{

	public JsonResponse(Status status , JSONObject json) {
		super(status);
		if(json == null){
			throw new ServerInternalException("Json响应对象为空");
		}
		heads.put("Content-Type", "application/json; charset=" + CHARSET);
		try{
			super.content = json.toString().getBytes(CHARSET);
		}catch(UnsupportedEncodingException e){
			
		}
	}
	

}
