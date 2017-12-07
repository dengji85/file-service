package com.waylau.spring.boot.fileserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.waylau.spring.boot.fileserver.domain.File;
import com.waylau.spring.boot.fileserver.service.FileGridFsService;

/**
 * GridFs example
 * 
 * @author mkyong
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GridFsAppStore {
	@Autowired
	GridFsTemplate gridOperations;
	@Autowired
	FileGridFsService fsService;

	@Test
	public void t() {

		DBObject metaData = new BasicDBObject();
		metaData.put("extra1", "anything 1");
		metaData.put("extra2", "anything 2");

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream("C:/Users/dell/Pictures/a.JPG");
			gridOperations.store(inputStream, "a.png", "image/png", metaData);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Done");
	}

	@Test
	public void testtt() throws Exception {
		FileInputStream fis = new FileInputStream(
				"C:/Users/dell/Pictures/fq.exe");
		byte[] b = new byte[fis.available()];
		fis.read(b, 0, b.length);
		File f = new File("aa", "exe", 1, b);

		String id = fsService.save(f);
		System.err.println(id);
		GridFSDBFile fils = fsService.getById("id");
		System.err.println(fils.getId());
	}

	@Test
	public void testGetById() throws Exception {
		
		String id = "5a278d8c68882b0d8059c07a";
		
		GridFSDBFile fils = fsService.getById(id);
		InputStream fis = fils.getInputStream();
		byte[] b= new byte[fis.available()];
		fis.read(b, 0, fis.available());
		FileUtils.copyInputStreamToFile(fis,new java.io.File("C:/Users/dell/Pictures/fq2.exe"));
		System.err.println(fils.getId());
	}
}