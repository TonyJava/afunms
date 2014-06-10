package com.afunms.topology.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ReadFile {
	public ReadFile() {
	}

	public static boolean deletefile(String delpath) throws FileNotFoundException, IOException {
		try {

			File file = new File(delpath);
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + "/" + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
					} else if (delfile.isDirectory()) {
						deletefile(delpath + "/" + filelist[i]);
					}
				}
				file.delete();

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 删除某个文件夹下的所有文件夹和文件
	 * 
	 * @param delpath
	 *            String
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return boolean
	 */
	public List<String> readfile(String filepath) throws FileNotFoundException, IOException {
		List list = new ArrayList();
		try {
			File file = new File(filepath);
			if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File readfile = new File(filepath + "/" + filelist[i]);
					if (!readfile.isDirectory()) {
						if (!"Thumbs.db".equals(readfile.getName())) {
							list.add(readfile.getName());
						}
					} else if (readfile.isDirectory()) {
						readfile(filepath + "/" + filelist[i]);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}

}
