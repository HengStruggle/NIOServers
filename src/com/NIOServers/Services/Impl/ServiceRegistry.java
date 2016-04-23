package com.NIOServers.Services.Impl;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import com.NIOServers.Services.InService;
import com.NIOServers.Services.Service;

/**
 * 服务注册中心
 */
public class ServiceRegistry {
	
	private static final Logger logger = Logger.getLogger("ServiceRegistry");
	private static Map<String , Service> services  = new TreeMap<>();
	
	public static void register(String pattern , Service service){
		services.put(pattern, service);
	}
	
	/**
	 * 根据路径查找对于服务
	 * @param path 请求路径
	 * @return 对应服务
	 */
	public static Service findService(String path){
		for(Map.Entry<String, Service> entry : services.entrySet()){
			if(path.matches(entry.getKey())){
				return entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * 注册服务
	 */
	public static void registerServices(){
		URL packageUrl = null;
		if(packageUrl == null){
			return ;
		}
		String name = null;
		registerFromPackage(name, packageUrl.getFile() + name.replaceAll("\\", Matcher.quoteReplacement(File.separator)), file -> file.isDirectory() || file.getName().endsWith(".class"));
	}
	
	private static void registerFromPackage(String packageName , String packagePath , FileFilter fileFilter){
		
		File dir = new File(packagePath);
		if(!dir.exists() || !dir.isDirectory()){
			return ;
		}
		File[] dirFiles = dir.listFiles(fileFilter);
		for(File file : dirFiles){
			if(file.isDirectory()){
				registerFromPackage(packageName+"."+file.getName(), file.getAbsolutePath(), fileFilter);
			}else{
				String className = file.getName().substring(0,file.getName().length() - 6);
				try{
					Class<?> aclass = Class.forName(packageName+"."+className);
					InService annotation = aclass.getAnnotation(InService.class);
					if(annotation != null && Service.class.isAssignableFrom(aclass)){
						register(annotation.urlPattern(), aclass.asSubclass(Service.class).newInstance());
						System.err.println("成功注册服务："+annotation.urlPattern()+" "+className);
					}
				}catch(ClassNotFoundException | InstantiationException | IllegalAccessException e){
					logger.log(Level.SEVERE, e, ()->"注册服务出错");
				}
			}
		}
	}
}
