gae-filestore is a really simple (but powerful!) virtual file system implementation upon Google App Engine DataStore. The implementation uses the Low-level DataStore API to store files, and memcache for the faster file metadata access.

Look at a simple Java example which uses gae-filestore:

```
FileStore fileStore = FileStoreFactory.getFileStore();

DataStoreFile file = fileStore.getFile("foo/bar/test.txt", true);
PrintWriter pw = new PrintWriter(new OutputStreamWriter(file.openForWrite()));
pw.println("Hello World!");
pw.close();
		
file = fileStore.getFile("foo/bar/test.txt", false);
BufferedReader br = new BufferedReader(new InputStreamReader(file.openForRead()));
System.out.println(bw.readLine());
```

<a href='https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=LMQGC6YTEQKE4&item_name=Beer'>
<img src='http://www.paypal.com/en_US/i/btn/x-click-but04.gif' /><br />Buy me some beer if you like my code ;)</a>

If you like the code, look at my other projects on http://code.google.com/u/TheBojda/.

If you have any question, please feel free to contact me at thebojda AT gmail DOT com.