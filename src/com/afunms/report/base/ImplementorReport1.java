/**
 * <p>Description:implementor report,bridge pattern</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-11-18
 */

package com.afunms.report.base;

import com.afunms.report.jfree.JFreeChartBrother;

public abstract class ImplementorReport1 {
	protected String head;
	protected String timeStamp;
	protected String note;
	protected String chartKey;
	protected JFreeChartBrother chart;
	protected String[][] table;
	protected int[] colWidth; // ап╣д©М╤х
	protected String[] tableHead;

	public abstract void createReport();

	public JFreeChartBrother getChart() {
		return chart;
	}

	public String getChartKey() {
		return chartKey;
	}

	public int[] getColWidth() {
		return colWidth;
	}

	public String getHead() {
		return head;
	}

	public String getNote() {
		return note;
	}

	public String[][] getTable() {
		return table;
	}

	public String[] getTableHead() {
		return tableHead;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setChart(JFreeChartBrother chart) {
		this.chart = chart;
	}

	public void setChartKey(String chartKey) {
		this.chartKey = chartKey;
	}

	public void setColWidth(int[] colWidth) {
		this.colWidth = colWidth;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setTable(String[][] table) {
		this.table = table;
	}

	public void setTableHead(String[] tableHead) {
		this.tableHead = tableHead;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
}