
package com.afunms.inform.util;

import java.util.List;

import com.afunms.inform.dao.InformDao;
import com.afunms.inform.model.VirusInfo;
import com.afunms.report.base.ImplementorReport;

@SuppressWarnings("unchecked")
public class VirusInfoReport extends ImplementorReport {
	private String orderField; // 排序字段

	@Override
	public void createReport() {
		head = "病毒数据报表";

		note = "数据来源：东华网管软件";

		tableHead = new String[] { "序号", "IP地址", "感染次数", "感染个数", "感染次数最多的病毒", "感染次数最多的文件" };

		colWidth = new int[] { 2, 4, 4, 4, 8, 8 };

		InformDao dao = new InformDao();
		List list = dao.queryVirusInfo(timeStamp, orderField);
		table = new String[list.size()][tableHead.length];
		for (int i = 0; i < list.size(); i++) {
			VirusInfo vo = (VirusInfo) list.get(i);
			table[i][0] = String.valueOf(i + 1); // 序号
			table[i][1] = vo.getIp();
			table[i][2] = String.valueOf(vo.getNumOfTimes());
			table[i][3] = String.valueOf(vo.getNumOfVirus());
			table[i][4] = vo.getVirusName();
			table[i][5] = vo.getVirusFile();
		}
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}
}