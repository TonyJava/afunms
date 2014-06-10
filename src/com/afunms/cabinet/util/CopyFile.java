package com.afunms.cabinet.util;

import java.io.*;

public class CopyFile {

	String startFilePath = null;
	String desFilePath = null;

	public CopyFile() {

	}

	public boolean copy(String startFilePath, String desFilePath) {
		this.startFilePath = startFilePath;
		this.desFilePath = desFilePath;

		boolean copyFinished = false;

		File startFile = new File(startFilePath);
		File desFile = new File(desFilePath);

		if (startFile.isFile()) {
			copyFinished = this.copySingleFile(startFile, desFile);
		} else {
			if (desFilePath.startsWith(startFilePath)) {
				return false;
			} else {
				copyFinished = this.copyFolder(startFile, desFile);
			}
		}
		return copyFinished;
	}

	public boolean copySingleFile(File startSingleFile, File desSingleFile) {

		boolean rightCopy = false;

		FileInputStream singleFileInputStream = null;
		DataInputStream singleDataInputStream = null;
		FileOutputStream singleFileOutputStream = null;
		DataOutputStream singleDataOutputStream = null;

		try {
			singleFileInputStream = new FileInputStream(startSingleFile);
			singleDataInputStream = new DataInputStream(new BufferedInputStream(singleFileInputStream));
			singleFileOutputStream = new FileOutputStream(desSingleFile);
			singleDataOutputStream = new DataOutputStream(new BufferedOutputStream(singleFileOutputStream));

			byte[] b = new byte[1024];
			int len;
			while ((len = singleDataInputStream.read(b)) != -1) {
				singleDataOutputStream.write(b, 0, len);
			}
			singleDataOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (singleDataInputStream != null)
					singleDataInputStream.close();
				if (singleDataOutputStream != null)
					singleDataOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (startSingleFile.length() == desSingleFile.length()) {
			rightCopy = true;
		} else {
			rightCopy = false;
		}
		return rightCopy;
	}

	public boolean copyFolder(File startFolder, File desFolder) {

		boolean rightCopy = false;

		rightCopy = this.recursionCopy(startFolder, desFolder);

		return rightCopy;
	}

	private boolean recursionCopy(File recFileFolder, File recDesFolder) {

		File desFolder = recDesFolder;

		desFolder.mkdir();

		File[] files = recFileFolder.listFiles();

		if (files.length == 0)
			return true;

		for (File thisFile : files) {
			if (thisFile.isFile()) {
				String desContentFilePath = this.getSubFilePath(startFilePath, desFilePath, thisFile.getAbsolutePath());
				boolean rightCopy = this.copySingleFile(thisFile, new File(desContentFilePath));
				if (!rightCopy)
					return false;
			} else {
				String desContentFilePath = this.getSubFilePath(startFilePath, desFilePath, thisFile.getAbsolutePath());
				boolean rightCopy = this.recursionCopy(thisFile, new File(desContentFilePath));
				if (!rightCopy)
					return false;
			}
		}
		return true;
	}

	private String getSubFilePath(String startFolderPath, String desFolderPath, String currentFilePath) {
		String currentDesFilePath = null;
		int i = startFolderPath.length();
		currentDesFilePath = desFolderPath + "/" + currentFilePath.substring(i + 1);
		return currentDesFilePath;
	}

}
