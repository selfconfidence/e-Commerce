package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.CRUDResult;
import util.FastDFSClient;

@Controller
@RestController
public class UploadController {
	//URI地址  注入数据操作 properties中的数据
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;
	
	
	@RequestMapping("/upload")
	public CRUDResult upload(MultipartFile file){
		
		try{
		//获取文件的扩展名称:
		String filename = file.getOriginalFilename();
		//需要指定后缀名称 MIME类型
		//截取操作
		String UrlMime=filename.substring(filename.lastIndexOf(".")+1);
	      //存储图片到服务器操作:  //加载配置文件信息.
		FastDFSClient client = new FastDFSClient("classpath:/config/fdfs_client.conf");
		//           传入图片字节进制信息,并将后缀名传入
		String uploadFile = client.uploadFile(file.getBytes(),UrlMime);
		//返回一个全路径带file_id的数据 我们需要将URI和file_id进行拼接
		String Url = FILE_SERVER_URL+uploadFile;
		//返回页面一个url地址
		return new CRUDResult(true, Url);
		}catch(Exception e){
		e.printStackTrace();
		return new CRUDResult(false, "上传故障");
		}
	}

}
