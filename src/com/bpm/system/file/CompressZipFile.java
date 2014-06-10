package com.bpm.system.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class CompressZipFile {
	private static CompressZipFile instance = new CompressZipFile();

	public CompressZipFile() {
	}

	public static CompressZipFile getInstance() {
		return instance;
	}

	public synchronized void zip(String inputFilename, String zipFilename) throws IOException {
		zip(new File(inputFilename), zipFilename);
	}

	public synchronized void zip(File inputFile, String zipFilename) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFilename));

		try {
			zip(inputFile, out, "");
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
	}

	private synchronized void zip(File inputFile, ZipOutputStream out, String base) throws IOException {
		if (inputFile.isDirectory()) {
			File[] inputFiles = inputFile.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < inputFiles.length; i++) {
				zip(inputFiles[i], out, base + inputFiles[i].getName());
			}

		} else {
			if (base.length() > 0) {
				out.putNextEntry(new ZipEntry(base));
			} else {
				out.putNextEntry(new ZipEntry(inputFile.getName()));
			}

			FileInputStream in = new FileInputStream(inputFile);
			try {
				int c;
				byte[] by = new byte[1024];
				while ((c = in.read(by)) != -1) {
					out.write(by, 0, c);
				}
			} catch (IOException e) {
				throw e;
			} finally {
				in.close();
			}
		}
	}

	public static void main(String[] args) {
		CompressZipFile bean = new CompressZipFile();
		try {
			bean.zip("d:/temp", "d:/test.zip");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
