/**
 * <p>Description:ups node</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2007-01-24
 */

package com.afunms.polling.node;

import java.util.List;

import com.afunms.inform.model.Alarm;
import com.afunms.monitor.item.UPSItem;
import com.afunms.monitor.item.UPSPhase;
import com.afunms.polling.base.Node;

@SuppressWarnings("unchecked")
public class UPSNode extends Node {
	private String community; // �������Ը��඼��

	private String Location;
	private String subtype;

	public UPSNode() {
		category = 101;
	}

	public String getCommunity() {
		return community;
	}

	@Override
	public String getLocation() {
		return Location;
	}

	/**
	 * ���Ǹ���ͬ������
	 */
	@Override
	public String getShowMessage() {
		StringBuffer msg = new StringBuffer(200);
		msg.append("<font color='green'>����:UPS");
		msg.append("</font><br>����:");
		msg.append(alias);
		msg.append("<br>");
		msg.append("IP��ַ:");
		msg.append(ipAddress);
		msg.append("<br>");

		if (status == 0 || status == 2 || status == 3) {
			msg.append("<font color='red'>");
			if (status == 0) {
				msg.append("��������</font>");
			} else if (status == 2) {
				msg.append("�豸æ</font>");
			} else {
				msg.append("Ping��ͨ</font>");
			}
			return msg.toString();
		}
		UPSItem item = (UPSItem) moidList.get(1);
		List phasesList = item.getPhasesList();

		// --------ȡ����ֵ---------
		StringBuffer vSb = new StringBuffer(50);
		StringBuffer aSb = new StringBuffer(50);
		vSb.append("�����ѹ:<br>");
		aSb.append("�������:<br>");
		for (int i = 0; i < 3; i++) {
			UPSPhase phase = (UPSPhase) phasesList.get(i);
			String phaseIndex = "(" + phase.getIndex() + "):";
			vSb.append(phaseIndex);
			vSb.append(phase.getVoltage() + "V");
			aSb.append(phaseIndex);
			aSb.append(phase.getCurrent() + "A");
			if (i != 2) {
				vSb.append(",");
				aSb.append(",");
			}
		}
		msg.append(vSb.toString());
		msg.append("<br>");
		msg.append(aSb.toString());
		msg.append("<br>");

		// ---------���ֵ----------
		vSb = new StringBuffer(50);
		aSb = new StringBuffer(50);
		StringBuffer loadSb = new StringBuffer(50);
		vSb.append("�����ѹ:<br>");
		aSb.append("�������:<br>");
		loadSb.append("�������:<br>");
		for (int i = 3; i < 6; i++) {
			UPSPhase phase = (UPSPhase) phasesList.get(i);
			String phaseIndex = "(" + phase.getIndex() + "):";
			vSb.append(phaseIndex);
			vSb.append(phase.getVoltage() + "V");
			aSb.append(phaseIndex);
			aSb.append(phase.getCurrent() + "A");
			loadSb.append(phaseIndex);
			loadSb.append(phase.getLoadPercent() + "%");
			if (i != 5) {
				vSb.append(",");
				aSb.append(",");
				loadSb.append(",");
			}
		}
		msg.append(vSb.toString());
		msg.append("<br>");
		msg.append(aSb.toString());
		msg.append("<br>");
		msg.append(loadSb.toString());
		msg.append("<br>");
		msg.append("������:");
		msg.append(item.getBatteryLevel());
		msg.append("%<br>");
		msg.append("��ص�ѹ:");
		msg.append(item.getBatteryVoltage());
		msg.append("V<br>");

		if (alarm) // status=4,5
		{
			msg.append("<font color='red'>--������Ϣ:--</font><br>");
			for (int i = 0; i < alarmMessage.size(); i++) {
				msg.append(((Alarm) alarmMessage.get(i)).getMessage());
				msg.append("<br>");
			}
		}
		msg.append("����ʱ��:" + lastTime);
		return msg.toString();
	}

	public String getSubtype() {
		return subtype;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	@Override
	public void setLocation(String location) {
		Location = location;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
}