package com.bpm.system.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

@SuppressWarnings("unchecked")
public class UnZipFile {
	private ZipFile zipFile;

	public void unZip(String unZipFileName) {
		File file = null;
		try {
			zipFile = new ZipFile(unZipFileName);
			for (Enumeration entries = zipFile.getEntries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();

				file = new File(entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					File parent = file.getParentFile();
					if (parent != null && !parent.exists()) {
						parent.mkdirs();
					}
					InputStream is = zipFile.getInputStream(entry);
					is.close();
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (zipFile != null)
					zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
