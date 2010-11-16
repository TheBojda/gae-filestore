package com.estontorise.gae_filestore;

import com.estontorise.gae_filestore.interfaces.FileStore;

public class FileStoreFactory {

	private static FileStore instance = null;
	
	public static FileStore getFileStore() {
		if(instance == null)
			instance = new FileStoreImpl();
		return instance;
	}
}
