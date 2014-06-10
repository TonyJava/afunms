
package com.afunms.inform.util;

import java.util.List;

import com.afunms.inform.dao.InformDao;
import com.afunms.inform.model.ServerPerformance;
import com.afunms.report.base.ImplementorReport;

@SuppressWarnings("unchecked")
public class ServerPerformanceReport extends ImplementorReport {
	private String orderField; // �����ֶ�

	@Override
	public void createReport() {
		setHead("�������������ݱ���");
		setNote("������Դ�������������");
		setTableHead(new String[] { "���", "��������", "IP��ַ", "CPU������", "�ڴ�������", "Ӳ��������" });
		setColWidth(new int[] { 2, 5, 4, 3, 3, 3 });

		InformDao dao = new InformDao();
		List list = dao.queryServerPerformance(timeStamp, orderField);
		table = new String[list.size()][tableHead.length];
		for (int i = 0; i < list.size(); i++) {
			ServerPerformance vo = (ServerPerformance) list.get(i);
			table[i][0] = String.valueOf(i + 1); // ���
			table[i][1] = vo.getAlias();
			table[i][2] = vo.getIpAddress();
			table[i][3] = vo.getCpuValue() + "%";
			table[i][4] = vo.getMemValue() + "%";
			table[i][5] = vo.getDiskValue() + "%";
		}
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}
}