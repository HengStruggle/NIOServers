package com.NIOServers.Services.Impl;

import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.NIOServers.Http.Request.MIMEData;
import com.NIOServers.Http.Request.Request;
import com.NIOServers.Http.Response.FileResponse;
import com.NIOServers.Http.Response.Response;
import com.NIOServers.Http.Response.Status;
import com.NIOServers.Services.InService;
import com.NIOServers.Services.Service;
import com.NIOServers.util.PropertiesHelper;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

@InService(urlPattern = "^/search$")
public class SearchService implements Service{

	@Override
	public Response service(Request request) {
		if(request.mimeContainKey("photo")){
			MIMEData photo = request.mimeValue("photo");
			byte[] data = photo.getData();
			try{
				BufferedImage read = ImageIO.read(new ByteInputStream(data, data.length));
				if(read == null){
					return new Response(Status.BAD_REQUEST_400);
				}
				showImage(read);
			}catch(IOException e){
				return new Response(Status.BAD_REQUEST_400);
			}
		} else{
			return new Response(Status.BAD_REQUEST_400);
		}
		return new FileResponse(Status.SUCCESS_200, PropertiesHelper.getTemplateFile("search.html"));
	}
	
	public static void showImage(BufferedImage image){
		ImageIcon ic = new ImageIcon(image);
		JFrame world = new JFrame("world");
		JLabel jLabel = new JLabel(ic);
		Panel panel = new Panel();
				
		panel.add(jLabel);		
		world.setContentPane(panel);
		world.setSize(image.getWidth() , image.getHeight());
		world.setVisible(true);
	}
}
