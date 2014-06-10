package com.afunms.inform.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.inform.util.MemoryReport;
import com.afunms.inform.util.NetworkCpuReport;
import com.afunms.inform.util.ServerCpuReport;
import com.afunms.inform.util.ServerDiskReport;
import com.afunms.inform.util.TrafficReport;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.report.abstraction.ExcelReport;
import com.afunms.report.abstraction.JspReport;
import com.afunms.report.abstraction.PdfReport;
import com.afunms.report.base.AbstractionReport;

public class ReportManager extends BaseManager implements ManagerInterface {
	private String day;
	private String moid;
	private int nodeId;
	private boolean isValue = false;

	public ReportManager() {
	}

	public String execute(String action) {
		if (action.equals("report_jsp")) {
			return reportJsp();
		}
		if (action.equals("report_pdf")) {
			return reportPdf();
		}
		if (action.equals("report_excel")) {
			return reportExcel();
		}

		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String memoryExcel() {
		AbstractionReport report = new ExcelReport(memoryReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private String memoryJsp() {
		JspReport report = new JspReport(memoryReport());
		report.createReport();
		request.setAttribute("report", report);
		return "/inform/report/allreport.jsp";
	}

	private String memoryPdf() {
		AbstractionReport report = new PdfReport(memoryReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private MemoryReport memoryReport() {
		MemoryReport report = new MemoryReport();
		report.setTimeStamp(day);
		report.setMoid(moid);
		report.setNodeId(nodeId);
		return report;
	}

	private String networkCpuExcel() {
		AbstractionReport report = new ExcelReport(networkCpuReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private String networkCpuJsp() {
		JspReport report = new JspReport(networkCpuReport());
		report.createReport();
		request.setAttribute("report", report);
		return "/inform/report/allreport.jsp";
	}

	private String networkCpuPdf() {
		AbstractionReport report = new PdfReport(networkCpuReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private NetworkCpuReport networkCpuReport() {
		NetworkCpuReport report = new NetworkCpuReport();
		report.setTimeStamp(day);
		report.setMoid(moid);
		report.setNodeId(nodeId);
		return report;
	}

	private String reportExcel() {
		String target = null;
		String rc = getParaValue("report_category");
		day = getParaValue("day");
		nodeId = getParaIntValue("node_id");

		request.setAttribute("day", day);
		request.setAttribute("report_category", rc);
		request.setAttribute("node_id", new Integer(nodeId));

		Host host = (Host) PollingEngine.getInstance().getNodeByID(nodeId);
		if (host == null) {
			return null;
		}

		int hostCategory = host.getCategory();
		if (rc.equals("cpu")) {
			if (hostCategory == 4) {
				moid = "001001";
				target = serverCpuExcel();
			} else {
				moid = "002001";
				target = networkCpuExcel();
			}
		} else if (rc.equals("memory")) {
			if (hostCategory == 4) {
				moid = "001002";
			} else {
				moid = "002002";
			}
			target = memoryExcel();
		} else if (rc.equals("disk")) {
			target = serverDiskExcel();
		} else {
			if (rc.equals("rx")) {
				moid = "003002";
				isValue = true;
			}// 入口流量
			else if (rc.equals("tx")) {
				moid = "003003";
				isValue = true;
			}// 出口流量
			else if (rc.equals("rx_util")) {
				moid = "003002";
				isValue = false;
			}// 入口百分率
			else if (rc.equals("tx_util")) {
				moid = "003003";
				isValue = false;
			}// 出口百分率
			else if (rc.equals("error")) {
				moid = "003004";// 错误率
			} else if (rc.equals("discard")) {
				moid = "003005";// 丢包率
			}
			target = trafficExcel();
		}
		return target;
	}

	private String reportJsp() {
		String target = null;
		String rc = getParaValue("report_category");
		day = getParaValue("day");
		nodeId = getParaIntValue("node_id");

		request.setAttribute("day", day);
		request.setAttribute("report_category", rc);
		request.setAttribute("node_id", new Integer(nodeId));

		Host host = (Host) PollingEngine.getInstance().getNodeByID(nodeId);
		if (host == null) {
			return null;
		}

		int hostCategory = host.getCategory();
		if (rc.equals("cpu")) {
			if (hostCategory == 4) {
				moid = "001001";
				target = serverCpuJsp();
			} else {
				moid = "002001";
				target = networkCpuJsp();
			}
		} else if (rc.equals("memory")) {
			if (hostCategory == 4) {
				moid = "001002";
			} else {
				moid = "002002";
			}
			target = memoryJsp();
		} else if (rc.equals("disk")) {
			target = serverDiskJsp();
		} else {
			if (rc.equals("rx")) {
				moid = "003002";
				isValue = true;
			}// 入口流量
			else if (rc.equals("tx")) {
				moid = "003003";
				isValue = true;
			}// 出口流量
			else if (rc.equals("rx_util")) {
				moid = "003002";
				isValue = false;
			}// 入口百分率
			else if (rc.equals("tx_util")) {
				moid = "003003";
				isValue = false;
			}// 出口百分率
			else if (rc.equals("error")) {
				moid = "003004";// 错误率
			} else if (rc.equals("discard")) {
				moid = "003005";// 丢包率
			}
			target = trafficJsp();
		}
		return target;
	}

	private String reportPdf() {
		String target = null;
		String rc = getParaValue("report_category");
		day = getParaValue("day");
		nodeId = getParaIntValue("node_id");

		request.setAttribute("day", day);
		request.setAttribute("report_category", rc);
		request.setAttribute("node_id", new Integer(nodeId));

		Host host = (Host) PollingEngine.getInstance().getNodeByID(nodeId);
		if (host == null) {
			return null;
		}

		int hostCategory = host.getCategory();
		if (rc.equals("cpu")) {
			if (hostCategory == 4) {
				moid = "001001";
				target = serverCpuPdf();
			} else {
				moid = "002001";
				target = networkCpuPdf();
			}
		} else if (rc.equals("memory")) {
			if (hostCategory == 4) {
				moid = "001002";
			} else {
				moid = "002002";
			}
			target = memoryPdf();
		} else if (rc.equals("disk")) {
			target = serverDiskPdf();
		} else {
			if (rc.equals("rx")) {
				moid = "003002";
				isValue = true;
			}// 入口流量
			else if (rc.equals("tx")) {
				moid = "003003";
				isValue = true;
			}// 出口流量
			else if (rc.equals("rx_util")) {
				moid = "003002";
				isValue = false;
			}// 入口百分率
			else if (rc.equals("tx_util")) {
				moid = "003003";
				isValue = false;
			}// 出口百分率
			else if (rc.equals("error")) {
				moid = "003004";// 错误率
			} else if (rc.equals("discard")) {
				moid = "003005";// 丢包率
			}
			target = trafficPdf();
		}
		return target;
	}

	private String serverCpuExcel() {
		AbstractionReport report = new ExcelReport(serverCpuReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private String serverCpuJsp() {
		JspReport report = new JspReport(serverCpuReport());
		report.createReport();
		request.setAttribute("report", report);
		return "/inform/report/allreport.jsp";
	}

	private String serverCpuPdf() {
		AbstractionReport report = new PdfReport(serverCpuReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private ServerCpuReport serverCpuReport() {
		ServerCpuReport report = new ServerCpuReport();
		report.setTimeStamp(day);
		report.setMoid(moid);
		report.setNodeId(nodeId);
		return report;
	}

	private String serverDiskExcel() {
		AbstractionReport report = new ExcelReport(serverDiskReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private String serverDiskJsp() {
		JspReport report = new JspReport(serverDiskReport());
		report.createReport();
		request.setAttribute("report", report);
		return "/inform/report/allreport.jsp";
	}

	private String serverDiskPdf() {
		AbstractionReport report = new PdfReport(serverDiskReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private ServerDiskReport serverDiskReport() {
		ServerDiskReport report = new ServerDiskReport();
		report.setTimeStamp(day);
		report.setNodeId(nodeId);
		return report;
	}

	private String trafficExcel() {
		AbstractionReport report = new ExcelReport(trafficReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private String trafficJsp() {
		JspReport report = new JspReport(trafficReport());
		report.createReport();
		request.setAttribute("report", report);
		return "/inform/report/allreport.jsp";
	}

	private String trafficPdf() {
		AbstractionReport report = new PdfReport(trafficReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private TrafficReport trafficReport() {
		TrafficReport report = new TrafficReport();
		report.setTimeStamp(day);
		report.setValue(isValue);
		report.setMoid(moid);
		report.setNodeId(nodeId);
		return report;
	}
}
