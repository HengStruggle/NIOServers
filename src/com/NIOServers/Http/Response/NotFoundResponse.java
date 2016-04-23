package com.NIOServers.Http.Response;

import java.io.File;

import com.NIOServers.util.PropertiesHelper;

/**
 * 404未找到响应
 *
 */
public class NotFoundResponse extends FileResponse{

	private static final File PATH_404HTML;
	
	static {
		PATH_404HTML = new File(PropertiesHelper.getProperty("404html_path" , PropertiesHelper.getResourcePath("template/404.html")));
	}

	public NotFoundResponse() {
		super(Status.NOT_FOUND_404 , PATH_404HTML);
	}
}
