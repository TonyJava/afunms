package com.afunms.ipresource.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.NetworkUtil;
import com.afunms.ipresource.dao.IpResourceDao;
import com.afunms.ipresource.model.IpResource;
import com.afunms.ipresource.util.DrawIPTable;
import com.afunms.ipresource.util.IpResourceReport;
import com.afunms.report.abstraction.ExcelReport;
import com.afunms.report.base.AbstractionReport;

@SuppressWarnings("unchecked")
public class IpResourceManager extends BaseManager implements ManagerInterface {
	public IpResourceManager() {
	}

	private String list() {
		IpResourceDao dao = new IpResourceDao();
		List list = dao.listByPage(getCurrentPage());

		request.setAttribute("page", dao.getPage());
		request.setAttribute("list", list);

		return "/ipresource/list.jsp";
	}

	private String find() {
		String value = getParaValue("value");
		String key = getParaValue("key");
		IpResourceDao dao = new IpResourceDao();
		IpResource ipr = dao.find(key, value);
		request.setAttribute("vo", ipr);
		return "/ipresource/find.jsp";
	}

	private String report() {
		AbstractionReport report = new ExcelReport(new IpResourceReport());
		report.createReport();
		return "/inform/report/download.jsp?filename=" + report.getFileName();
	}

	/**
	 * IP分布
	 */
	private String detail() {
		String jsp = null;
		try {
			String beginip = getParaValue("beginip");
			String endip = getParaValue("endip");
			if (beginip == null)
				beginip = "10.10.20.0";
			if (endip == null)
				endip = "10.10.20.255";

			String outPutInfo = null;
			if (!NetworkUtil.checkIp(beginip) || !NetworkUtil.checkIp(endip))
				outPutInfo = "<font color='red'>无效IP地址,请正确输入IP地址!</font>";
			else {
				long temp1 = NetworkUtil.ip2long(beginip);
				long temp2 = NetworkUtil.ip2long(endip);
				if (temp1 >= temp2)
					outPutInfo = "<font color='red'>起点IP必须小于终点IP,请重新输入!</font>";
				else if (temp2 - temp1 > 255)
					outPutInfo = "<font color='red'>输入的两个IP不在同一网段,请重新输入!</font>";
				else {
					DrawIPTable ipTable = new DrawIPTable();
					outPutInfo = ipTable.drawTable(beginip, endip, request.getContextPath());
				}
			}
			request.setAttribute("beginip", beginip);
			request.setAttribute("endip", endip);
			request.setAttribute("out_put_info", outPutInfo);
			jsp = "/ipresource/table.jsp";
		} catch (Exception sqle) {
			jsp = null;
		}
		return jsp;
	}

	public String execute(String action) {
		if (action.equals("detail"))
			return detail();
		if (action.equals("list"))
			return list();
		if (action.equals("find"))
			return find();
		if (action.equals("report"))
			return report();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}