package com.NIOServers.Http.Request;

public class MIMEData {
	  private String type;
	    private byte[] data;
	    private String fileName;

	    public MIMEData(String type, byte[] data, String fileName) {
	        this.type = type;
	        this.data = data;
	        this.fileName = fileName;
	    }

	    public byte[] getData() {
	        return data;
	    }

	    public String getFileName() {
	        return fileName;
	    }

	    public String getType() {
	        return type;
	    }
}
