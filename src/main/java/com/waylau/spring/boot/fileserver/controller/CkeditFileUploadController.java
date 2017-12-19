package com.waylau.spring.boot.fileserver.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.operation.FsyncUnlockOperation;
import com.waylau.spring.boot.fileserver.domain.File;
import com.waylau.spring.boot.fileserver.service.FileGridFsService;
import com.waylau.spring.boot.fileserver.service.FileService;
import com.waylau.spring.boot.fileserver.util.MD5Util;

@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
public class CkeditFileUploadController {
	@Autowired
	private FileService fileService;
	@Autowired
	private FileGridFsService fileGridFsService;
	@Value("${com.dengji85.address}")
	private String serverAddress;

	@Value("${server.port}")
	private String serverPort;
	private static List<String> imgType;

	{
		imgType = Arrays
				.asList("bmp,jpg,png,tiff,gif,pcx,tga,exif,fpx,svg,psd,cdr,pcd,dxf,ufo,eps,ai,raw,WMF"
						.split(","));

	}

	@PostMapping("/ckedit")
	public void uplod(HttpServletRequest req, HttpServletResponse res,
			@RequestParam("upload") MultipartFile upload) {

		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			return;
		}

		try {
			String CKEditorFuncNum = req.getParameter("CKEditorFuncNum");
			File f = new File(upload.getOriginalFilename(),
					upload.getContentType(), upload.getSize(),
					upload.getBytes());
			f.setMd5(MD5Util.getMD5(upload.getInputStream()));
			File returnFile = fileService.saveFile(f);

			res.setContentType("text/html; charset=UTF-8");
			res.setHeader("Cache-Control", "no-cache");
			PrintWriter out = res.getWriter();
			StringBuffer sb = new StringBuffer();
			sb.append("<script type=\"text/javascript\">");
			sb.append("window.parent.CKEDITOR.tools.callFunction("
					+ CKEditorFuncNum + ",'" +"http://" +serverAddress+":"+serverPort+"/image/"
					+ returnFile.getId() + "','')");
			sb.append("</script>");
			System.out.println(sb.toString());
			out.write(sb.toString());
			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@GetMapping("/image/{id}")
	public void getImage(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String id) {
		if (id == null) {
			// 输出默认图片
			// name = "";
		}

		FileInputStream fis = null;
		response.setContentType("image/gif");
		try {
			OutputStream out = response.getOutputStream();
			//File file = fileService.getFileById(id);
			GridFSDBFile f = fileGridFsService.getById(id);
			out.write(getByteArray(f.getInputStream()));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	* @Title: getByteArray  
	* @Description: 流转为byte数组
	* @return byte[]    返回类型  
	* @throws
	 */
	private byte[] getByteArray(InputStream in) {
		byte[] datas = null;
		try {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];

			int count = -1;
			while ((count = in.read(b)) != -1) {
				bos.write(b, 0, count);

			}
			datas = bos.toByteArray();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}
}
