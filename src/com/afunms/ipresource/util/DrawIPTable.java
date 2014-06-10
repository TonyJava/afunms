package com.afunms.ipresource.util;

import java.util.Hashtable;

import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.ipresource.dao.IpResourceDao;
import com.afunms.ipresource.model.IpResource;

@SuppressWarnings("unchecked")
public class DrawIPTable {
	public String drawTable(String beginip, String endip, String rootPath) {
		StringBuffer table0 = new StringBuffer(300);
		StringBuffer table1 = new StringBuffer(300);
		StringBuffer table2 = new StringBuffer(300);
		StringBuffer table3 = new StringBuffer(300);
		StringBuffer table = new StringBuffer(1000);
		String tableHtml = "";
		Hashtable voHash = null;
		IpResourceDao ipdao = null;

		try {
			table1.append("<table border=0 cellspacing=0 cellpadding=0 width=640>");
			table1.append("<tr class='othertr'>");
			for (int i = 0; i < 32; i++) {
				if (i > 9)
					table1.append("<td width='18' align=center>&nbsp;" + i + "&nbsp;</td>");
				else
					table1.append("<td width='18' align=center>&nbsp;&nbsp;" + i + "&nbsp;</td>");
			}
			table1.append("</tr></table>");

			int loc = beginip.lastIndexOf(".");
			String subIp = beginip.substring(0, loc + 1);
			table2.append("<table border=0 cellspacing=0 cellpadding=0>");
			for (int i = 0; i < 8; i++) {
				table2.append("<tr class='othertr'><td height='25' valign=middle>");
				table2.append(subIp);
				table2.append(i * 32);
				table2.append("</td></tr>");
			}
			table2.append("</table>");

			long ip11 = NetworkUtil.ip2long(subIp + "0");
			long ip21 = NetworkUtil.ip2long(beginip);
			long ip22 = NetworkUtil.ip2long(endip);
			ipdao = new IpResourceDao();
			voHash = ipdao.loadByIPRange(ip21, ip22);
			if (voHash.size() != 0) {
				table3.append("<table border=1 cellspacing=0 cellpadding=5 bordercolorlight='#000000' bordercolordark='#FFFFFF' width=600>");
				for (int i = 0; i < 256; i++) {
					if (i % 32 == 0)
						table3.append("<tr class='othertr'>");
					if (i + ip11 >= ip21 && i + ip11 <= ip22) {
						table3.append("<td bgcolor='#CCFFFF' width='18' height='18' align=center>");
						IpResource vo = (IpResource) voHash.get(new Long(i + ip11));
						if (vo != null) {
							table3.append("<img alt='");
							table3.append("IP:" + vo.getIpAddress() + "\n");
							table3.append("MAC:" + vo.getMac() + "\n");
							table3.append("直连设备:" + vo.getNode() + "\n");
							table3.append("直连端口:" + vo.getIfIndex() + "(" + vo.getIfDescr() + ")\n");
							table3.append("' src='" + rootPath + "/resource/image/ipusing.gif' ");
							table3.append("height='10' width='10' border='0'></a></td>");
						} else
							table3.append("&nbsp;&nbsp;</td>");
					} else
						table3.append("<td width='18' height='18'>&nbsp;&nbsp;</td>");

					if (i % 32 == 31)
						table3.append("</tr>");
				}// end_for
				table3.append("</table>");
				table.append("<table border=0 cellspacing=0 cellpadding=0>");
				table.append("<tr><td>&nbsp;&nbsp;</td><td>");
				table.append(table0.toString());
				table.append("</td></tr>");
				table.append("<tr><td>&nbsp;&nbsp;</td><td>");
				table.append(table1.toString());
				table.append("</td></tr><tr><td>");
				table.append(table2.toString());
				table.append("</td><td>");
				table.append(table3.toString());
				table.append("</td></tr></table>");
			}// end_if
			else {
				table.append("<table border=0 width=500 cellspacing=0 cellpadding=0>");
				table.append("<tr><td align=center><b>该网段未被使用!</b></td></tr></table>");
			}
			tableHtml = table.toString();
		} catch (Exception ex) {
			tableHtml = "";
			SysLogger.error("Error in DrawIPTable.drawTable()", ex);
		}
		return tableHtml;
	}
}
