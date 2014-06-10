package com.afunms.capreport.manage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.JspPage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.snmp.LoadLogFile;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;

/**
 * 此类 为 三峡银行定制的 大小额 日志解析
 */

@SuppressWarnings("unchecked")
public class BusinessReportManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if ("list".equals(action)) {
			return list();
		} else if ("searchfalse".equals(action)) {
			return searchfalse();
		} else if ("createReport".equals(action)) {
			return createReport();
		}
		return null;
	}

	private String list() {
		List list = null;
		list = getListByPage(getResultByWhere(getAllData("valueList")));
		request.setAttribute("list", list);
		return "/capreport/busireport/list.jsp";
	}

	private String searchfalse() {
		List list = null;
		list = getListByPage(getFalseListByWhere(getAllData("falseList")));
		request.setAttribute("list", list);
		return "/capreport/busireport/searcFalselist.jsp";
	}

	private String createReport() {
		List list = getFalseListByWhere(getAllData("falseList"));
		String startdate = (String) request.getAttribute("startdate");
		String todate = (String) request.getAttribute("todate");
		System.out.println(list);
		AbstractionReport1 abstractionReport1 = new ExcelReport1(new IpResourceReport());
		abstractionReport1.createReport_falseLoglist("temp\\portscan_report.xls", "日期 ： " + startdate + "---" + todate, list);
		request.setAttribute("filename", abstractionReport1.getFileName());

		return "/capreport/busireport/download.jsp";
	}

	private List getAllData(String key) {
		Hashtable dataHashtable = null;
		List list = null;
		LoadLogFile loadFile = new LoadLogFile();
		try {
			dataHashtable = loadFile.getTuxedoInfo("");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (dataHashtable == null) {
			dataHashtable = new Hashtable();
		}

		list = (List) dataHashtable.get(key);

		return list;
	}

	private List getResultByWhere(List list) {
		List returnList = new ArrayList();
		if (list == null || list.size() == 0) {
			return returnList;
		}

		List allData = list;
		String startTime = getStartTime();
		String toTime = getToTime();

		for (int i = 0; i < allData.size(); i++) {
			List resultList = (List) allData.get(i);
			String time = (String) resultList.get(2);
			if (time.compareTo(startTime) >= 0 && time.compareTo(toTime) <= 0) {
				returnList.add(resultList);
			}
		}

		return returnList;
	}

	private List getFalseListByWhere(List list) {
		List returnList = new ArrayList();
		if (list == null || list.size() == 0) {
			return returnList;
		}

		List allData = list;

		String startTime = getStartTime();
		String toTime = getToTime();

		for (int i = 0; i < allData.size(); i++) {
			List resultList = (List) allData.get(i);
			String time = (String) resultList.get(0);
			if (time.compareTo(startTime) >= 0 && time.compareTo(toTime) <= 0) {
				returnList.add(resultList);
			}
		}

		return returnList;
	}

	private String getStartTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-M-d");
		String startdate = getParaValue("startdate");
		if (startdate == null || startdate.length() == 0) {
			startdate = simpleDateFormat.format(new Date());
		} else {
			try {
				startdate = simpleDateFormat.format(simpleDateFormat2.parse(startdate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		String startTime = startdate + " 00:00:00";

		request.setAttribute("startdate", startdate);

		return startTime;
	}

	private String getToTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-M-d");

		String todate = getParaValue("todate");
		if (todate == null || todate.length() == 0) {
			todate = simpleDateFormat.format(new Date());
		} else {
			try {
				todate = simpleDateFormat.format(simpleDateFormat2.parse(todate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		String toTime = todate + " 23:59:59";
		request.setAttribute("todate", todate);
		return toTime;
	}

	private List getListByPage(List list) {
		List returnList = new ArrayList();

		int totalRecord = 0; // 总页数
		int perpage = getPerPagenum(); // 每页允许记录数
		int curpage = getCurrentPage(); // 当前页数

		if (list == null || list.size() == 0) {
			totalRecord = 0;
			JspPage jspPage = new JspPage(perpage, curpage, totalRecord);
			request.setAttribute("page", jspPage);
			return returnList;
		}
		totalRecord = list.size();
		JspPage jspPage = new JspPage(perpage, curpage, totalRecord);
		int loop = 0;
		Iterator it = list.iterator();
		while (it.hasNext()) {
			loop++;
			Object object = it.next();
			if (loop < jspPage.getMinNum())
				continue;
			returnList.add(object);
			if (loop == jspPage.getMaxNum())
				break;
		}
		request.setAttribute("page", jspPage);
		return returnList;
	}

}
