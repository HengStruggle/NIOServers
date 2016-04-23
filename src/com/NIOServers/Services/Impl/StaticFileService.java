package com.NIOServers.Services.Impl;

import java.io.File;

import com.NIOServers.Http.Request.Request;
import com.NIOServers.Http.Response.FileResponse;
import com.NIOServers.Http.Response.NotFoundResponse;
import com.NIOServers.Http.Response.Response;
import com.NIOServers.Http.Response.Status;
import com.NIOServers.Services.InService;
import com.NIOServers.Services.Service;
import com.NIOServers.util.PropertiesHelper;

/**
 * 静态文件服务
 */

@InService(urlPattern = "^" + StaticFileService.prefix + ".*$")
public class StaticFileService implements Service{

	public static final String prefix = "/s/";
	private static String staticPath;
	
	static{
		staticPath = PropertiesHelper.getProperty("static_path");
	}
	
	@Override
	public Response service(Request request) {
		String filePath = staticPath + File.separator + request.getURI().replaceAll(prefix,"");
		File file = new File(filePath);
		if(!file.exists() || !file.isFile() || !file.canRead()){
			return new NotFoundResponse();
		}
		return new FileResponse(Status.SUCCESS_200, file);
	}
	
}
