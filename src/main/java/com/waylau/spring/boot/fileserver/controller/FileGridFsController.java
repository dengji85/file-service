package com.waylau.spring.boot.fileserver.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;
import com.waylau.spring.boot.fileserver.domain.File;
import com.waylau.spring.boot.fileserver.service.FileGridFsService;
import com.waylau.spring.boot.fileserver.util.MD5Util;

/**
 * 超过4M上传
 * 
 * @author dell
 * 
 * @date: 2017-12-6 下午3:22:50
 * 
 */
@Controller
@RequestMapping("/gf")
@CrossOrigin(origins = "*", maxAge = 3600)  // 允许所有域名访问
public class FileGridFsController {
	@Autowired
	private FileGridFsService gridFsService;

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

	/**
	 * 上传接口
	 * 
	 * @param file
	 * @return
	 */
	@PostMapping("/upload")
	@ResponseBody
	public ResponseEntity<String> handleFileUpload(
			@RequestParam("file") MultipartFile file) {
		String id = null;
		String path = "";
		try {
			File f = new File(file.getOriginalFilename(),
					file.getContentType(), file.getSize(), file.getBytes());
			f.setMd5(MD5Util.getMD5(file.getInputStream()));
			id = gridFsService.save(f);
			if ("image".contains(file.getContentType())) {
				path = "//" + serverAddress + ":" + serverPort + "/view/" + id;
			} else {
				path = id;
			}
			return ResponseEntity.status(HttpStatus.OK).body(path);

		} catch (IOException | NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ex.getMessage());
		}

	}

	/**
	 * 获取文件片信息
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("files/{id}")
	@ResponseBody
	public ResponseEntity<?> serveFile(@PathVariable String id) {

		GridFSDBFile file = gridFsService.getById(id);

		if (file != null) {
			return ResponseEntity
					.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; fileName=\"" + file.getFilename()
									+ "\"")
					.header(HttpHeaders.CONTENT_TYPE,
							"application/octet-stream")
					.header(HttpHeaders.CONTENT_LENGTH, file.getLength() + "")
					.header("Connection", "close").body(this.getByteArray(file.getInputStream()));
		} else {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"File was not fount");
		}

	}

	/**
	 * 在线显示文件
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/view/{id}")
	@ResponseBody
	public ResponseEntity<?> serveFileOnline(@PathVariable String id) {

		GridFSDBFile file = gridFsService.getById(id);

		if (file != null) {
			return ResponseEntity
					.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"fileName=\"" + file.getFilename() + "\"")
					.header(HttpHeaders.CONTENT_TYPE, file.getContentType())
					.header(HttpHeaders.CONTENT_LENGTH, file.getLength() + "")
					.header("Connection", "close").body(this.getByteArray(file.getInputStream()));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					"File was not fount");
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
