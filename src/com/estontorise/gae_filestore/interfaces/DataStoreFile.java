package com.estontorise.gae_filestore.interfaces;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataStoreFile {

	public boolean isDirectory();
	public boolean isFile();
	public long length();
	public long lastModified();
	public String[] list();
	public DataStoreFile[] listFiles();
	public boolean mkdirs();
	public String getPath();
	public boolean exists();
	public InputStream openForRead() throws FileNotFoundException;
	public OutputStream openForWrite() throws FileNotFoundException;
	
}
