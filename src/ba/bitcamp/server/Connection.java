package ba.bitcamp.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;

import ba.bitcamp.loger.Logger;

public class Connection implements Runnable {

	private Socket client;

	public Connection(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		InputStream is = null;
		BufferedReader br = null;
		OutputStream os = null;
		PrintStream write = null;
		String line = null;
		try {
			is = client.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));

			os = client.getOutputStream();
			write = new PrintStream(os);

		} catch (IOException e) {
			Logger.log("error", e.getMessage());
			clientClose();
			return;
		}

		try {
			while ((line = br.readLine()) != null) {

				if (line.contains("GET") || line.isEmpty()) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Dobili:" + line);
		if (!line.contains("GET")) {
			Logger.log("warning", "was not GET request");
			Response.error(write, "Invalid request");
			clientClose();
			return;
		}
		String fileName = getFileName(line);
		System.out.println(fileName);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {

			Response.error(write, "This is not the page you are looking for");
			Logger.log("warning",
					"Client request missing file" + e.getMessage());
			clientClose();
			return;
		}
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(
				fis));
		String fileLine = "";
		StringBuilder sb = new StringBuilder();
		try {
			while ((fileLine = fileReader.readLine()) != null) {
				sb.append(fileLine);
			}
		} catch (IOException e) {
			Logger.log("error", e.getMessage());
			Response.servError(write,
					"A well traind group of monkyes is trying to fix the problem");
			clientClose();
			return;
		}
		
		Response.ok(write, sb.toString());
		clientClose();

	}

	private String getFileName(String request) {
		String[] parts = request.split(" ");
		String fileName = null;
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].equals("GET")) {
				fileName = parts[i + 1];
				break;
			}
		}
		String basePath = "." + File.separator + "html" + File.separator;
		if (fileName == null || fileName.equals("/")) {
			return basePath + "index.html";
		}
		if (!fileName.contains(".")) {
			fileName += ".html";
		}
		return basePath + fileName;
	}

	private void clientClose() {
		try {
			client.close();
		} catch (IOException e) {
			Logger.log("warning", e.getMessage());
		}
	}
}
