package com.NIOServers.Services;

import com.NIOServers.Http.Request.Request;
import com.NIOServers.Http.Response.Response;

/**
 * 服务接口
 */
public interface Service {
	Response service(Request request);
}
