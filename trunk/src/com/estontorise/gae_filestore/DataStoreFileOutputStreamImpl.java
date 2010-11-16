package com.estontorise.gae_filestore;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;

// TODO: lock support (?)
public class DataStoreFileOutputStreamImpl extends OutputStream {

	private long file_pointer;
	private DataStoreBlockManager blockManager;
	private Entity fileEntity;

	public DataStoreFileOutputStreamImpl(Entity fileEntity) {
		this.fileEntity = fileEntity;
		this.file_pointer = 0;
		this.blockManager = new DataStoreBlockManager(fileEntity);
	}

	@Override
	public void write(int b) throws IOException {
		blockManager.setData(this.file_pointer, b);
		this.file_pointer++;
		this.fileEntity.setProperty("size", this.file_pointer);
	}

	@Override
	public void flush() throws IOException {
		blockManager.flush();
		CachedDataStore.put(this.fileEntity);		
	}

	@Override
	public void close() throws IOException {
		fileEntity.setProperty("last_mod", new Long((new Date()).getTime()));
		flush();
	}

}
