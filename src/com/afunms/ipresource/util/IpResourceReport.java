package com.afunms.ipresource.util;

import java.util.List;

import com.afunms.common.util.SysUtil;
import com.afunms.ipresource.dao.IpResourceDao;
import com.afunms.ipresource.model.IpResource;
import com.afunms.report.base.ImplementorReport;

@SuppressWarnings("unchecked")
public class IpResourceReport extends ImplementorReport {
	public void createReport() {
		setHead("");
		setNote("");
		setTimeStamp(SysUtil.getCurrentDate());
		setTableHead(new String[] { "序号", "IP地址", "MAC地址", "直连设备", "直连端口" });
		setColWidth(new int[] { 2, 5, 6, 5, 6 });

		IpResourceDao dao = new IpResourceDao();
		List list = dao.loadAll();
		table = new String[list.size()][tableHead.length];
		for (int i = 0; i < list.size(); i++) {
			IpResource vo = (IpResource) list.get(i);
			table[i][0] = String.valueOf(i + 1); // 序号
			table[i][1] = vo.getIpAddress();
			table[i][2] = vo.getMac();
			table[i][3] = vo.getNode();
			table[i][4] = vo.getIfIndex() + "(" + vo.getIfDescr() + ")";
		}
	}
}