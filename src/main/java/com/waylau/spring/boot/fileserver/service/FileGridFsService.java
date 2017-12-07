package com.waylau.spring.boot.fileserver.service;

import com.mongodb.gridfs.GridFSDBFile;
import com.waylau.spring.boot.fileserver.domain.File;

public interface FileGridFsService {
	
	public String save(File file);
	public GridFSDBFile getById(String id);
}
