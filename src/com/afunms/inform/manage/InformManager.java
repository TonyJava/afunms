package com.afunms.inform.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SysUtil;
import com.afunms.inform.util.NetworkPerformanceReport;
import com.afunms.inform.util.ServerPerformanceReport;
import com.afunms.inform.util.VirusInfoReport;
import com.afunms.report.abstraction.ExcelReport;
import com.afunms.report.abstraction.JspReport;
import com.afunms.report.abstraction.PdfReport;
import com.afunms.report.base.AbstractionReport;

public class InformManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("server_jsp")) {
			return serverJsp();
		}
		if (action.equals("server_excel")) {
			return serverExcel();
		}
		if (action.equals("server_pdf")) {
			return serverPdf();
		}
		if (action.equals("network_jsp")) {
			return networkJsp();
		}
		if (action.equals("network_excel")) {
			return networkExcel();
		}
		if (action.equals("network_pdf")) {
			return networkPdf();
		}
		if (action.equals("virus_jsp")) {
			return virusJsp();
		}
		if (action.equals("virus_excel")) {
			return virusExcel();
		}
		if (action.equals("virus_pdf")) {
			return virusPdf();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String networkExcel() {
		AbstractionReport report = new ExcelReport(networkReport());
		report.createReport();

		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private String networkJsp() {
		JspReport report = new JspReport(networkReport());
		report.createReport();

		request.setAttribute("report", report);
		return "/inform/report/network_performance.jsp";
	}

	public String networkPdf() {
		AbstractionReport report = new PdfReport(networkReport());
		report.createReport();

		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private NetworkPerformanceReport networkReport() {
		String timeStamp = getParaValue("day");
		if (timeStamp == null) {
			timeStamp = SysUtil.getCurrentDate();
		}
		String orderField = getParaValue("field");
		if (orderField == null) {
			orderField = "ip_long";
		}

		NetworkPerformanceReport npr = new NetworkPerformanceReport();
		npr.setTimeStamp(timeStamp);
		npr.setOrderField(orderField);
		request.setAttribute("day", timeStamp);
		request.setAttribute("field", orderField);

		return npr;
	}

	private String serverExcel() {
		AbstractionReport report = new ExcelReport(serverReport());
		report.createReport();

		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private String serverJsp() {
		JspReport report = new JspReport(serverReport());
		report.createReport();

		request.setAttribute("report", report);
		return "/inform/report/server_performance.jsp";
	}

	public String serverPdf() {
		AbstractionReport report = new PdfReport(serverReport());
		report.createReport();

		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private ServerPerformanceReport serverReport() {
		String timeStamp = getParaValue("day");
		if (timeStamp == null) {
			timeStamp = SysUtil.getCurrentDate();
		}
		String orderField = getParaValue("field");
		if (orderField == null) {
			orderField = "ip_long";
		}

		ServerPerformanceReport spr = new ServerPerformanceReport();
		spr.setTimeStamp(timeStamp);
		spr.setOrderField(orderField);
		request.setAttribute("day", timeStamp);
		request.setAttribute("field", orderField);

		return spr;
	}

	private String virusExcel() {
		AbstractionReport report = new ExcelReport(virusReport());
		report.createReport();

		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private String virusJsp() {
		JspReport report = new JspReport(virusReport());
		report.createReport();

		request.setAttribute("report", report);
		return "/inform/report/virus_info.jsp";
	}

	public String virusPdf() {
		AbstractionReport report = new PdfReport(virusReport());
		report.createReport();

		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	private VirusInfoReport virusReport() {
		String timeStamp = getParaValue("day");
		if (timeStamp == null) {
			timeStamp = SysUtil.getCurrentDate();
		}
		String orderField = getParaValue("field");
		if (orderField == null) {
			orderField = "num_of_times";
		}

		VirusInfoReport npr = new VirusInfoReport();
		npr.setTimeStamp(timeStamp);
		npr.setOrderField(orderField);
		request.setAttribute("day", timeStamp);
		request.setAttribute("field", orderField);

		return npr;
	}
}
