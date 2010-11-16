package com.estontorise.gae_filestore;

import com.estontorise.gae_filestore.interfaces.DataStoreFile;
import com.estontorise.gae_filestore.interfaces.FileStore;

public class FileStoreImpl implements FileStore {

	@Override
	public DataStoreFile getFile(String path, boolean create) {
		return new DataStoreFileImpl(path, create);
	}

	
}
