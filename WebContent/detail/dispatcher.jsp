<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.polling.node.Host"%>
<%@page import="com.afunms.polling.PollingEngine"%>

<%!private String parseString(Object ob) {
		String rtString = "NaV";
		if (null != ob && !"null".equals(ob) && !"".equals(ob)) {
			rtString = (String) ob;
		}
		return rtString.trim();
	}

	private int parseInt(String arg) {
		int rtInteger = -1;
		if (null != arg && !"null".equals(arg) && !"".equals(arg)) {
			rtInteger = Integer.parseInt(arg.trim());
		}
		return rtInteger;
	}

	private Hashtable<Integer, String> osTypeHt = new Hashtable<Integer, String>();%>
<%
	osTypeHt.put(1, "cisco");
	osTypeHt.put(2, "h3c");
	osTypeHt.put(38, "topsec");
	osTypeHt.put(5, "windows");
	osTypeHt.put(6, "aix");
	osTypeHt.put(9, "linux");
%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

	String flag = request.getParameter("flag");
	String checkString = parseString(request.getParameter("id"));
	String nodeCategory = (String) null;
	String nodeId = (String) null;
	Host node = null;
	String href = (String) null;

	if (!checkString.equals("NaV")) {
		nodeCategory = checkString.substring(0, 3);
		nodeId = checkString.substring(3);
		node = (Host) PollingEngine.getInstance().getNodeByID(parseInt(nodeId));
		if (null != node) {
			if (!parseString(nodeCategory).equals("NaV")) {
				if (nodeCategory.equals("net")) {
					String subType =osTypeHt.get(node.getOstype());
					if (node.getCategory() < 4) {
						href = basePath + "jsp/performance/detail/netPerformanceDetail.jsp?ip=" + node.getIpAddress() + "&alias=" + node.getAlias() + "&nodeId=" + node.getId() + "&type=net&subType=" + subType;
					} else if (node.getCategory() == 4) {
						href = basePath + "jsp/performance/detail/"+subType+"PerformanceDetail.jsp?ip=" + node.getIpAddress() + "&alias=" + node.getAlias() + "&nodeId=" + node.getId() + "&type=net&subType=" + subType;
					}else if(node.getCategory() == 8){
						href = basePath + "jsp/performance/detail/firewallPerformanceDetail.jsp?ip=" + node.getIpAddress() + "&alias=" + node.getAlias() + "&nodeId=" + node.getId() + "&type=net&subType=" + subType;
					}
				}
			}
		}
		  response.sendRedirect(href);
	}
%>
