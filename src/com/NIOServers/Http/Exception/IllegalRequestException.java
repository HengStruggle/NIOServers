package com.NIOServers.Http.Exception;

/**
 * 请求解析发生错误抛出此异常
 *
 */
public class IllegalRequestException extends RuntimeException {
	public IllegalRequestException(String msg) {
		super(msg);
	}

	public IllegalRequestException(String msg, Throwable e) {
		super(msg, e);
	}
}
