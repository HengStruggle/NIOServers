package com.NIOServers.Http.Response;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import com.NIOServers.Http.Exception.ServerInternalException;

/**
 * 文件响应
 *
 */
public class FileResponse extends Response{

	public FileResponse(Status status , File file) {
		super(status);
		if(file == null){
			throw new ServerInternalException("Response File 对象为空");
		}
		if(!file.isFile()  && file.canRead() && file.getName().endsWith("file")){
			this.status = Status.NOT_FOUND_404;
			return ;
		}
		//获得文件file的绝对路径
		String path = file.getAbsolutePath();
		//获取指定文件名的MIME类型
		String contentType = URLConnection.getFileNameMap().getContentTypeFor(path);
		try{
			content = Files.readAllBytes(FileSystems.getDefault().getPath(path));
		}catch(IOException e){
			this.status = Status.NOT_FOUND_404;
			return ;
		}
		// 如果是文本类型，需要写出charset
		if(contentType.startsWith("text")){
			contentType += "; charset=" + CHARSET;
		}
		heads.put("Content-Type", contentType);
	}
}
