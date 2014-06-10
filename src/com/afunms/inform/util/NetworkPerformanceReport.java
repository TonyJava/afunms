
package com.afunms.inform.util;

import java.util.List;

import com.afunms.inform.dao.InformDao;
import com.afunms.inform.model.NetworkPerformance;
import com.afunms.report.base.ImplementorReport;


@SuppressWarnings("unchecked")
public class NetworkPerformanceReport extends ImplementorReport {
	private String orderField; // �����ֶ�

	@Override
	public void createReport() {
		head = "�����豸�������ݱ���";
		note = "������Դ�������������";
		tableHead = new String[] { "���", "�豸��", "IP��ַ", "CPU������", "�ڴ�������", "�ӿ�������" };
		colWidth = new int[] { 2, 5, 4, 3, 3, 3 };

		InformDao dao = new InformDao();
		List list = dao.queryNetworkPerformance(timeStamp, orderField);
		table = new String[list.size()][tableHead.length];
		for (int i = 0; i < list.size(); i++) {
			NetworkPerformance vo = (NetworkPerformance) list.get(i);
			table[i][0] = String.valueOf(i + 1); // ���
			table[i][1] = vo.getAlias();
			table[i][2] = vo.getIpAddress();
			table[i][3] = vo.getCpuValue() + "%";
			table[i][4] = vo.getMemValue() + "%";
			table[i][5] = vo.getIfUtil() + "%";
		}
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}
}