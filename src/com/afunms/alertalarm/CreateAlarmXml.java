/**
 * <p>Description:action center,at the same time, the control legal power</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.alertalarm;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.afunms.event.model.AlarmInfo;
import com.afunms.initialize.ResourceCenter;

@SuppressWarnings("unchecked")
public class CreateAlarmXml {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void createXml(List alarmArray) {
		String indent = "	";
		String fileName = "demo.xml";
		FileOutputStream fos = null;
		Element root = new Element("alarmTree");
		root.setAttribute("alarmNum", alarmArray.size() + ""); // 该处应该是个统计数据；
		for (int i = 0; i < alarmArray.size(); i++) {
			AlarmInfo alarm = (AlarmInfo) alarmArray.get(i);
			Element alarmNode = new Element("alarmNode");
			alarmNode.setAttribute("ip", alarm.getIpaddress());
			alarmNode.setAttribute("level", alarm.getLevel1().toString());
			alarmNode.setAttribute("content", alarm.getContent());
			root.addContent(alarmNode);
		}
		Format format = Format.getCompactFormat();
		format.setEncoding("gb2312");
		format.setIndent(indent);
		XMLOutputter serializer = new XMLOutputter(format);
		try {
			fos = new FileOutputStream(ResourceCenter.getInstance().getSysPath() + fileName);
			Document doc = new Document(root);
			serializer.output(doc, fos);
			fos.close();
			serializer = null;
			fos = null;
		} catch (Exception e) {
			e.printStackTrace();
			fos = null;
			serializer = null;
		}
	}
}
