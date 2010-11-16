package com.estontorise.gae_filestore.interfaces;

public interface FileStore {

	public DataStoreFile getFile(String path, boolean create);
	
}
