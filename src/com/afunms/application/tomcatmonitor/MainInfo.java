package com.afunms.application.tomcatmonitor;

import java.util.HashMap;
import java.util.Hashtable;

@SuppressWarnings("unchecked")
public class MainInfo {

	Hashtable hMain = new Hashtable();

	HashMap map;

	String sMain;

	StringBuffer bufMain;

	int tNum;

	Hashtable hHead;

	Hashtable hContext;

	String sPortHead;

	String portInfo = "";

	public MainInfo() {

	}

	public MainInfo(HashMap map, int tNum) {
		this.map = map;
		this.tNum = tNum;
	}

	// ȡ������Ϣ�ַ������ȴ�����
	public String sMainTag() {
		sMain = map.get(String.valueOf(tNum)).toString();
		return sMain;
	}

	// ��һ�δ���ȡ�õ�һ������Ϣ��tomcat5.0ΪJVM��
	public String JVMInfo(String sMain) {
		bufMain = new StringBuffer(sMain.trim());
		int i = bufMain.indexOf("</p>");
		bufMain.delete(i + 4, bufMain.length());
		return bufMain.toString();
	}

	// ȡ��JVMС����Ϣ (���ͣ����ԣ�ֵ����λ)
	public Hashtable hJVMInfo(String sMain) {
		Hashtable returnVal = new Hashtable();
		String jvmInfo = JVMInfo(sMain.substring(sMain.indexOf("<h1>JVM</h1>")));
		String sContextRaw = jvmInfo.substring(jvmInfo.indexOf("<p>") + 3, jvmInfo.indexOf("MB</p>"));
		String[] sContext = sContextRaw.trim().split("MB");

		for (int i = 0; i < sContext.length; i++) {
			String[] tmp = sContext[i].trim().split(":");
			returnVal.put(String.valueOf(i), tmp[1]);
		}
		return returnVal;
	}

	// ȡ�÷������˿�ԭʼ��Ϣ
	public String PORTInfo(String sMain, int flag) {
		int i = sMain.indexOf("</p>");
		int j = sMain.indexOf("</table>");
		if (flag == 0) { // web����˿�
			portInfo = sMain.substring(i + 4, j + 8);
		}
		if (flag == 1) { // web��ض˿�
			portInfo = sMain.substring(j + 8, sMain.length());
			try {
				portInfo = portInfo.substring(portInfo.indexOf("</p>") + 4, portInfo.indexOf("</table>"));
			} catch (Exception e) {

			}
		}
		return portInfo;
	}

	// ȡ�÷������˿�������Ϣ(���ͣ����ԣ�ֵ����λ:�к�)�������к��趨Ϊ0
	public Hashtable hPORTInfoSum(String sMain, int flag) {
		Hashtable returnVal = new Hashtable();
		String portInfo = PORTInfo(sMain, flag);
		try {
			sPortHead = portInfo.substring(portInfo.indexOf("<h1>") + 4, portInfo.indexOf("</h1>"));
		} catch (Exception e) {

		}
		String sContext = portInfo.substring(portInfo.indexOf("<p>") + 3, portInfo.indexOf("</p>"));
		sContext = sContext.replaceAll("<br>", " ");
		sContext = sContext.replaceAll("<br/>", " ");
		String[] tmp = sContext.trim().split(" ");
		StringBuffer sBuf = new StringBuffer(tmp[0]);
		for (int i = 1; i < tmp.length - 1; i++) {
			sBuf.append(" ");
			sBuf.append(tmp[i]);
			if (!tmp[i + 1].equals(tmp[i + 1].toLowerCase())) {
				if (!"MB".equals(tmp[i + 1])) {
					sBuf.append(";");
				}
			}
		}
		sBuf.append(" " + tmp[tmp.length - 1]); // �õ���";"�ָ����ַ���
		tmp = null;
		tmp = sBuf.toString().split(";");
		for (int i = 0; i < tmp.length; i++) {
			String[] sTmp = tmp[i].split(":");
			String sPert = sTmp[0];
			String sValRaw = sTmp[1].trim();
			String[] sTmp1 = sValRaw.split(" ");
			String sContexts = sPert + ":" + sTmp1[0];
			returnVal.put(String.valueOf(i), sContexts);
		}
		return returnVal;
	}

	// ȡ�÷������˿���ϸ��Ϣ
	public Hashtable hPORTInfoDetail(String sMain, int flag) {
		Hashtable hMains = new Hashtable();
		hDetailPublic(portInfo);
		StringBuffer sb_raw = new StringBuffer();
		for (int i = 0; i < hContext.size(); i++) {
			String sContextRaw = hContext.get(String.valueOf(i)).toString();
			String[] sContextVal = sContextRaw.split(":");
			// ����ֵ�͵�λ
			String strVal = "";
			if (sContextVal.length > 0) {
				String[] valDw = sContextVal[0].split(" ");
				strVal = valDw[0];
				if (valDw.length > 1) {
					if (!valDw[1].startsWith("/")) {
					} else {
						strVal = valDw[0] + " " + valDw[1];
					}
				}
			}
			sb_raw.append(strVal);
			sb_raw.append(":");
		}
		sb_raw.append("huilet");
		sb_raw.toString();

		String[] tmp = sb_raw.toString().split(":");
		for (int i = 0; i < (tmp.length - 1) / 7; i++) {
			StringBuffer sub_buf = new StringBuffer();
			sub_buf.append(tmp[7 * i]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 1]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 2]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 3]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 4]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 5]);
			sub_buf.append(":");
			sub_buf.append(tmp[7 * i + 6]);
			hMains.put(String.valueOf(i), sPortHead + ":" + sub_buf.toString());
		}

		return hMains;
	}

	public void hDetailPublic(String portInfo) {
		hHead = new Hashtable();
		hContext = new Hashtable();
		String sDetailRaw = portInfo.substring(portInfo.indexOf("<table"), portInfo.length());

		// �ֶ�
		String[] sSeg = sDetailRaw.split("</tr>");
		// �����ͷ
		int id = 0;
		StringBuffer bufHead = new StringBuffer(sSeg[0]);

		// ȥ����ǩ���õ���ͷ�ֶ�
		while (bufHead.toString().startsWith("<")) {
			if (bufHead.length() < 6)
				break;
			bufHead.delete(0, bufHead.indexOf(">") + 1);
			String tmp = bufHead.substring(0, bufHead.indexOf("<"));
			if (tmp == "" || "".equals(tmp)) {
			} else {
				hHead.put(String.valueOf(id), tmp);
				id++;
			}
			bufHead.delete(0, bufHead.indexOf("<"));
		}

		// ��������{ֵ���кţ�1-N�����ֶκţ�0-N��}
		int rid = 0;
		for (int i = 1; i < sSeg.length - 1; i++) {
			StringBuffer bufCont = new StringBuffer(sSeg[i]);
			int cid = 0;
			while (bufCont.toString().startsWith("<")) {
				if (bufCont.length() < 6)
					break;
				bufCont.delete(0, bufCont.indexOf(">") + 1);
				String tmp = bufCont.substring(0, bufCont.indexOf("<"));
				if (tmp == "" || "".equals(tmp)) {
				} else {
					hContext.put(String.valueOf(rid), tmp + ":" + String.valueOf(i) + ":" + String.valueOf(cid));
					cid++;
					rid++;
				}
				bufCont.delete(0, bufCont.indexOf("<"));
			}
		}
	}
}