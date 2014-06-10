
package com.afunms.report.jfree;

import java.util.List;

import com.afunms.monitor.item.base.MonitorResult;

@SuppressWarnings("unchecked")
public class Transformer {
	private String[] xKeys;
	private String[] yKeys;
	private double[][] data;

	public Transformer() {
	}

	public double[][] getData() {
		return data;
	}

	public String[] getXKey() {
		return xKeys;
	}

	public String[] getYKey() {
		return yKeys;
	}

	public void item2Array(List list) {
		int len = list.size();
		xKeys = new String[len];
		yKeys = new String[] { "“—”√", "Œ¥”√" };
		data = new double[2][len];

		for (int i = 0; i < len; i++) {
			MonitorResult mr = (MonitorResult) list.get(i);
			xKeys[i] = mr.getEntity();
			data[0][i] = mr.getPercentage();
			data[1][i] = 100 - mr.getPercentage();
		}
	}
}