package com.estontorise.gae_filestore.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.estontorise.gae_filestore.FileStoreFactory;
import com.estontorise.gae_filestore.interfaces.DataStoreFile;
import com.estontorise.gae_filestore.interfaces.FileStore;

@SuppressWarnings("serial")
public class FilestoreTestServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		FileStore fileStore = FileStoreFactory.getFileStore();

		fileStore.getFile("foo/bar/xxx.txt", true);
		fileStore.getFile("foo/bar/yyy.txt", true);
		fileStore.getFile("foo/bar/zzz.txt", true);

		DataStoreFile file = fileStore.getFile("foo/bar/test.txt", true);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(file.openForWrite()));
		pw.println("Hello World!");
		pw.close();
		
		file = fileStore.getFile("foo/bar/test.txt", false);
		BufferedReader br = new BufferedReader(new InputStreamReader(file.openForRead()));
		
		resp.setContentType("text/plain");
		resp.getWriter().println(br.readLine());
		
		br.close();
	}
}
