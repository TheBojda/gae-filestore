package com.estontorise.gae_filestore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class CachedDataStore {

	public static Entity get(Key key) {
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		Entity entity = (Entity) memcache.get(key);
		if (entity != null)
			return entity;
		try {
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
			entity = datastore.get(key);
			memcache.put(entity.getKey(), entity);
			return entity;
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	public static void put(Entity entity) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		datastore.put(entity);
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		memcache.put(entity.getKey(), entity);
	}

}
