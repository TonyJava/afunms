package com.afunms.application.tomcatmonitor;

import java.util.HashMap;
import java.util.Hashtable;

import org.apache.log4j.Logger;

@SuppressWarnings("unchecked")
public class ServerInfo {

	private static Logger logger = Logger.getLogger(ServerInfo.class);
	Hashtable hServer = new Hashtable();
	Hashtable hServerInfo = new Hashtable();
	Hashtable hServerInfoFi = new Hashtable();
	HashMap map;
	int sNum;
	int eNum;

	public ServerInfo() {

	}

	public ServerInfo(HashMap map, int sNum, int eNum) {
		this.map = map;
		this.sNum = sNum;
		this.eNum = eNum;
	}

	public Hashtable hServerTag() {
		int j = 0;
		for (int i = 0; i < map.size(); i++) {
			if (i < eNum + 1 && i > sNum - 1) {
				String str = map.get(String.valueOf(i)).toString();
				hServer.put(String.valueOf(j), str);
				j++;
			}
		}
		return hServer;
	}

	// ����html��ǩ��ķ�������Ϣ������һ������
	public Hashtable hServerInfo(Hashtable hashTable) {
		String tmpStr;
		String contextStr;
		int id = 0;
		for (int i = 0; i < hashTable.size(); i++) {
			tmpStr = hashTable.get(String.valueOf(i)).toString().trim();
			contextStr = getContext(tmpStr);
			if (!"".equalsIgnoreCase(contextStr)) {
				hServerInfo.put(String.valueOf(id), contextStr);
				id++;
			}
		}
		return hServerInfo;
	}

	// �������ǩ������ݽ�һ���������Ժ�ֵ��Ӧ
	public Hashtable hServerInfoFi(Hashtable hashTable) {
		hashTable.remove(String.valueOf(0));
		int i = hashTable.size();
		if (i % 2 != 0) {
			logger.error("�����д��󣬲��ܼ������У�");
		}
		for (int j = 0; j < i / 2; j++) {
			hServerInfoFi.put(String.valueOf(j), hashTable.get(String.valueOf(i / 2 + j + 1)));
		}
		return hServerInfoFi;
	}

	// ������������ϢhtmlԴ���룬����������Ϣ
	public String getContext(String str) {
		String returnStr = "";
		StringBuffer tmpBuf = new StringBuffer(str);
		int pos;
		while (tmpBuf.toString().startsWith("<")) {
			pos = tmpBuf.indexOf(">");
			tmpBuf.delete(0, pos + 1);
		}
		while (tmpBuf.toString().endsWith(">")) {
			pos = tmpBuf.indexOf("<");
			tmpBuf.delete(pos, tmpBuf.length() + 1);
		}
		returnStr = tmpBuf.toString();
		return returnStr;
	}
}
