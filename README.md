gae-filestore is a really simple (but powerful!) virtual file system implementation upon Google App Engine DataStore. The implementation uses the Low-level DataStore API to store files, and memcache for the faster file metadata access.

Look at a simple Java example which uses gae-filestore:

```java
FileStore fileStore = FileStoreFactory.getFileStore();

DataStoreFile file = fileStore.getFile("foo/bar/test.txt", true);
PrintWriter pw = new PrintWriter(new OutputStreamWriter(file.openForWrite()));
pw.println("Hello World!");
pw.close();
		
file = fileStore.getFile("foo/bar/test.txt", false);
BufferedReader br = new BufferedReader(new InputStreamReader(file.openForRead()));
System.out.println(bw.readLine());
```
