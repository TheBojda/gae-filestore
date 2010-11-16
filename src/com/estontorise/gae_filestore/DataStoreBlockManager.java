package com.estontorise.gae_filestore;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class DataStoreBlockManager {

	public static final int BLOCK_SIZE = 512 * 1024; // 512 K
	private static final String FILE_BLOCK_KIND = "gae_ds_file_block";
	
	private Entity fileEntity;
	private long last_block_number;
	private Entity last_block_entity;
		
	public DataStoreBlockManager(Entity fileEntity) {
		this.fileEntity = fileEntity;
		this.last_block_number = 0;
		this.last_block_entity = null;
	}

	private Entity getBlockByPointer(long file_pointer, boolean create) {
		long block_number = (file_pointer / BLOCK_SIZE) + 1;
		if(block_number == last_block_number)
			return last_block_entity;
		if(last_block_entity != null)
			flushBlock(last_block_entity);
		Key blockKey = KeyFactory.createKey(fileEntity.getKey(), FILE_BLOCK_KIND, block_number);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity blockEntity = null;
		try {
			blockEntity = datastore.get(blockKey);
		} catch (EntityNotFoundException e) {
			if(!create)
				return null;
			blockEntity = new Entity(blockKey);
			blockEntity.setProperty("block", new Blob(new byte[BLOCK_SIZE]));
			datastore.put(blockEntity);
		}
		last_block_entity = blockEntity;
		last_block_number = block_number;
		return blockEntity;	
	}

	private void flushBlock(Entity blockEntity) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(blockEntity);		
	}
	
	public void setData(long file_pointer, int b) {
		Entity blockEntity = getBlockByPointer(file_pointer, true);
		Blob block = (Blob) blockEntity.getProperty("block");
		byte[] blockBytes = block.getBytes();
		int offset = (int)file_pointer % BLOCK_SIZE;
		blockBytes[offset] = (byte)b;
		blockEntity.setProperty("block", new Blob(blockBytes));
	}

	public void flush() {
		flushBlock(last_block_entity);
	}

	public int getData(long file_pointer) {
		Entity blockEntity = getBlockByPointer(file_pointer, true);
		Blob block = (Blob) blockEntity.getProperty("block");
		byte[] blockBytes = block.getBytes();
		int offset = (int)file_pointer % BLOCK_SIZE;
		return blockBytes[offset];
	}

}
