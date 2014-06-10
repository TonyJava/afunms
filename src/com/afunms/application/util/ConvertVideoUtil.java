package com.afunms.application.util;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

public class ConvertVideoUtil {
	private static Logger logger = Logger.getLogger(ConvertVideoUtil.class.getName());
	public String Convert(String path, String fname) {
		if (!checkfile(path + fname)) {
			return "";
		}
		return process(path, fname);
	}

	private String process(String path, String fname) {
		int type = checkContentType(path + fname);
		String status = "";
		if (type == 0) {
			status = processFLV(path, fname);// ֱ�ӽ��ļ�תΪflv�ļ�
		} else if (type == 1) {
			String avifilepath = processAVI(path, fname);
			avifilepath = avifilepath.substring(0, avifilepath.lastIndexOf("\\"));
			String avifilename = avifilepath.substring(avifilepath.lastIndexOf("\\"));
			if (avifilepath == null) {
				return "";// avi�ļ�û�еõ�
			}
			status = processFLV(avifilepath, avifilename);// ��aviתΪflv
		}
		return status;
	}

	private static int checkContentType(String path) {
		String type = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
		// ffmpeg�ܽ����ĸ�ʽ����asx��asf��mpg��wmv��3gp��mp4��mov��avi��flv�ȣ�
		if (type.equals("avi")) {
			return 0;
		} else if (type.equals("mpg")) {
			return 0;
		} else if (type.equals("wmv")) {
			return 0;
		} else if (type.equals("3gp")) {
			return 0;
		} else if (type.equals("mov")) {
			return 0;
		} else if (type.equals("mp4")) {
			return 0;
		} else if (type.equals("asf")) {
			return 0;
		} else if (type.equals("asx")) {
			return 0;
		} else if (type.equals("flv")) {
			return 0;
		}
		// ��ffmpeg�޷��������ļ���ʽ(wmv9��rm��rmvb��),
		// �������ñ�Ĺ��ߣ�mencoder��ת��Ϊavi(ffmpeg�ܽ�����)��ʽ.
		else if (type.equals("wmv9")) {
			return 1;
		} else if (type.equals("rm")) {
			return 1;
		} else if (type.equals("rmvb")) {
			return 1;
		}
		return 9;
	}

	private static boolean checkfile(String path) {
		File file = new File(path);
		if (!file.isFile()) {
			return false;
		}
		return true;
	}

	// ��ffmpeg�޷��������ļ���ʽ(wmv9��rm��rmvb��), �������ñ�Ĺ��ߣ�mencoder��ת��Ϊavi(ffmpeg�ܽ�����)��ʽ.
	private static String processAVI(String path, String fname) {
		List<String> commend = new java.util.ArrayList<String>();
		String back = path + fname.split(".") + ".avi";
		commend.add(path + "ffmpeg\\mencoder");
		commend.add(path);
		commend.add("-oac");
		commend.add("lavc");
		commend.add("-lavcopts");
		commend.add("acodec=mp3:abitrate=64");
		commend.add("-ovc");
		commend.add("xvid");
		commend.add("-xvidencopts");
		commend.add("bitrate=600");
		commend.add("-of");
		commend.add("avi");
		commend.add("-o");
		commend.add(back);
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			if (p.waitFor() != 0) {
				logger.info("AVI��ʽת���쳣����");
				return back;
			} else {
				logger.info("AVI��ʽת�����");
				return back;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// ffmpeg�ܽ����ĸ�ʽ����asx��asf��mpg��wmv��3gp��mp4��mov��avi��flv�ȣ�
	private static String processFLV(String oldfilepath, String fname) {
		if (!checkfile(oldfilepath + fname)) {
			return "";
		}
		List<String> commend = new java.util.ArrayList<String>();
		commend.add(oldfilepath + "ffmpeg\\ffmpeg");
		commend.add("-i");
		commend.add(oldfilepath + fname);
		commend.add("-ab");
		commend.add("56");
		commend.add("-ar");
		commend.add("22050");
		commend.add("-qscale");
		commend.add("8");
		commend.add("-r");
		commend.add("15");
		commend.add("-s");
		commend.add("600x500");
		commend.add(oldfilepath + fname.substring(0, fname.lastIndexOf(".")) + ".flv");
		try {
			ProcessBuilder builder = new ProcessBuilder(commend);
			builder.command(commend);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			if (p.waitFor() != 0) {
				return "";
			} else {
				return fname.substring(0, fname.lastIndexOf(".")) + ".flv";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
