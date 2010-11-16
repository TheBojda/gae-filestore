package com.estontorise.gae_filestore;

import java.io.IOException;
import java.io.InputStream;

import com.google.appengine.api.datastore.Entity;

public class DataStoreFileInputStreamImpl extends InputStream {

	private long file_pointer;
	private DataStoreBlockManager blockManager;
	private Entity fileEntity;

	public DataStoreFileInputStreamImpl(Entity fileEntity) {
		this.fileEntity = fileEntity;
		this.file_pointer = 0;
		this.blockManager = new DataStoreBlockManager(fileEntity);
	}

	@Override
	public int read() throws IOException {
		fileEntity = CachedDataStore.get(fileEntity.getKey()); // refresh
		long length = (Long) fileEntity.getProperty("size");
		if(file_pointer < length)
			return blockManager.getData(file_pointer++);
		else
			return -1;
	}

}
