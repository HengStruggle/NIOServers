package com.NIOServers.Services.Impl;

import com.NIOServers.Http.Request.Request;
import com.NIOServers.Http.Response.FileResponse;
import com.NIOServers.Http.Response.Response;
import com.NIOServers.Http.Response.Status;
import com.NIOServers.Services.InService;
import com.NIOServers.Services.Service;
import com.NIOServers.util.PropertiesHelper;

@InService(urlPattern = "^/$")
public class IndexService implements Service{

	@Override
	public Response service(Request request) {
		return new FileResponse(Status.SUCCESS_200, PropertiesHelper.getTemplateFile("index.html"));
	}
}
