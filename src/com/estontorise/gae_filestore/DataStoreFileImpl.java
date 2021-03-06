package com.estontorise.gae_filestore;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.estontorise.gae_filestore.interfaces.DataStoreFile;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

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
		if(path.startsWith("/"))
			path = path.substring(1);
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

	private Iterable<Entity> getEntitiesByParent(String parent) {
		Query query = new Query(FILE_KIND);
		query.addFilter("parent", FilterOperator.EQUAL, path);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		return datastore.prepare(query).asIterable();		
	}
	
	@Override
	public String[] list() {
		List<String> result = new ArrayList<String>();
		Iterable<Entity> entities = getEntitiesByParent(path);
		for(Entity entity : entities) {
			result.add(getFileName(entity.getKey().getName()));
		}
		return result.toArray(new String[]{});
	}

	@Override
	public DataStoreFile[] listFiles() {
		List<DataStoreFile> result = new ArrayList<DataStoreFile>();
		Iterable<Entity> entities = getEntitiesByParent(path);
		for(Entity entity : entities) {
			result.add(new DataStoreFileImpl(entity.getKey().getName(), false));
		}
		return result.toArray(new DataStoreFile[]{});
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
	
	private String getFileName(String filePath) {
		int pos = filePath.lastIndexOf("/");
		if(pos > -1)
			return filePath.substring(pos + 1);
		else
			return path;
	}
	
	private void recursiveCreatePath(String dirPath) {
		if(dirPath == null)
			return;
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
		if(parent != null) {
			Entity parentEntity = getFileEntity(parent);
			if(parentEntity == null)
				throw new FileNotFoundException("Parent directory not found: " + getParentPath(path));
		}
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
