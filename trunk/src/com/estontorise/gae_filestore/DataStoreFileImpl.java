package com.estontorise.gae_filestore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import com.estontorise.gae_filestore.interfaces.DataStoreFile;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class DataStoreFileImpl implements DataStoreFile {

	public static final String FILE_KIND = "gae_ds_file";
	private String path;

	/*
	 * File metadata:
	 * 	- type (F - file, D - directory)
	 *  - size
	 *  - last_mod
	 *  - parent
	 */
	
	public DataStoreFileImpl(String path, boolean create) {
		this.path = path;
		if(create) {
			recursiveCreatePath(getParentPath(path));
			try {
				createFile(path);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private Entity getFileEntity(String filePath) {
		Key fileKey = KeyFactory.createKey(FILE_KIND, filePath);
		return CachedDataStore.get(fileKey);
	}
	
	@Override
	public boolean isDirectory() {
		Entity fileEntity = getFileEntity(path);
		return "D".equals(fileEntity.getProperty("type"));
	}

	@Override
	public boolean isFile() {
		Entity fileEntity = getFileEntity(path);
		return "F".equals(fileEntity.getProperty("type"));
	}

	@Override
	public long length() {
		Entity fileEntity = getFileEntity(path);
		return (Long)fileEntity.getProperty("size");
	}

	@Override
	public long lastModified() {
		Entity fileEntity = getFileEntity(path);
		return (Long)fileEntity.getProperty("last_mod");
	}

	@Override
	public String[] list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataStoreFile[] listFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean mkdirs() {
		recursiveCreatePath(path);
		return true;
	}

	private String getParentPath(String filePath) {
		int pos = filePath.lastIndexOf("/");
		if(pos > -1)
			return filePath.substring(0, pos);
		return null;
	}
	
	private void recursiveCreatePath(String dirPath) {
		Entity dirEntity = getFileEntity(dirPath);
		if(dirEntity == null) {
			String parent = getParentPath(dirPath);
			if(parent != null)
				recursiveCreatePath(parent);
			createDir(parent, dirPath);
		}
	}

	private void createDir(String parent, String dirPath) {
		Key dirKey = KeyFactory.createKey(FILE_KIND, dirPath);
		Entity dirEntity = new Entity(dirKey);
		dirEntity.setProperty("type", "D");
		dirEntity.setProperty("size", new Long(0));
		dirEntity.setProperty("last_mod", new Long((new Date()).getTime()));
		dirEntity.setProperty("parent", parent);
		CachedDataStore.put(dirEntity);
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean exists() {
		Entity fileEntity = getFileEntity(path);
		return fileEntity != null;
	}

	@Override
	public InputStream openForRead() throws FileNotFoundException {
		Entity fileEntity = getFileEntity(path);
		if(fileEntity == null)
			throw new FileNotFoundException("File not found: " + path);
		return new DataStoreFileInputStreamImpl(fileEntity);
	}

	private Entity createFile(String path) throws FileNotFoundException {
		String parent = getParentPath(path);
		Entity parentEntity = getFileEntity(parent);
		if(parentEntity == null)
			throw new FileNotFoundException("Parent directory not found: " + getParentPath(path));
		Key fileKey = KeyFactory.createKey(FILE_KIND, path);
		Entity fileEntity = new Entity(fileKey);
		fileEntity.setProperty("type", "F");
		fileEntity.setProperty("size", new Long(0));
		fileEntity.setProperty("last_mod", new Long((new Date()).getTime()));
		fileEntity.setProperty("parent", parent);
		CachedDataStore.put(fileEntity);
		return fileEntity;
	}
	
	@Override
	public OutputStream openForWrite() throws FileNotFoundException {
		Entity fileEntity = getFileEntity(path);
		if(fileEntity == null)
			fileEntity = createFile(path);
		return new DataStoreFileOutputStreamImpl(fileEntity);
	}
	
}
