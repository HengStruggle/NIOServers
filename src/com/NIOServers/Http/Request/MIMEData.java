package com.NIOServers.Http.Request;

/**
 * <p>多媒体文件格式MIME</p>
	<p>最早的HTTP协议中，并没有附加的数据类型信息，所有传送的数据都被客户程序解释为超文本标记语言HTML 文档，而为了支持多媒体数据类型，HTTP协议中就使用了附加在文档之前的MIME数据类型信息来标识数据类型。
    <p>MIME意为多目Internet邮件扩展，它设计的最初目的是为了在发送电子邮件时附加多媒体数据，让邮件客户程序能根据其类型进行处理。然而当它被HTTP协议支持之后，它的意义就更为显著了。它使得HTTP传输的不仅是普通的文本，而变得丰富多彩。</p>
	<p>每个MIME类型由两部分组成，前面是数据的大类别，例如声音audio、图象image等，后面定义具体的种类。</p>
	<p>由于MIME类型与文档的后缀相关，因此服务器使用文档的后缀来区分不同文件的MIME类型，服务器中必须定义文档后缀 和MIME类型之间的对应关系。而客户程序从服务器上接收数据的时候，它只是从服务器接受数据流，并不了解文档的名字，因此服务器必须使用附加信息来告诉 客户程序数据的MIME类型。服务器在发送真正的数据之前，就要先发送标志数据的MIME类型的信息，这个信息使用Content-type关键字进行定 义，例如对于HTML文档，服务器将首先发送以下两行MIME标识信息,这个标识并不是真正的数据文件的一部分。</p>
 */
public class MIMEData {
		/**
		 *  MIME类型
		 */
	    private String type;
	    
	    /**
	     * MIME数据 
	     */
	    private byte[] data;

	    /**
	     * MINE数据的文件名
	     */
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
