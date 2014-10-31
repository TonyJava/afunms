<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.topology.dao.ManageXmlDao"%>
<%@page import="com.afunms.topology.model.ManageXml"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SysLogger"%>
<%
	String rootPath = request.getContextPath();
	String menu = request.getParameter("menu");
	if (menu != null) {
		session.setAttribute(SessionConstant.CURRENT_MENU, menu);
	}
	User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	String bids[] = current_user.getBusinessids().split(",");
	String roleid = current_user.getRole() + "";
	if ("0".equals(roleid)) {
		session.setAttribute("fatherXML", "network.jsp"); //yangjun add
		session.setAttribute(SessionConstant.CURRENT_TOPO_VIEW, "network.jsp");
	} else {
		try {
			String xml_current = "";
			int tt = 0;
			ManageXmlDao dao = new ManageXmlDao();
			List list = dao.loadAll();
			for (int i = 0; i < list.size(); i++) {
				try {
					ManageXml vo = (ManageXml) list.get(i);
					int tag = 0;
					if (bids != null && bids.length > 0) {
						for (int j = 1; j < bids.length; j++) {
							if (vo.getBid() != null && !"".equals(vo.getBid()) && !"".equals(bids[j]) && vo.getBid().indexOf(bids[j]) != -1) {
								tag++;
							}
						}
					}
					xml_current = vo.getXmlName();
					if (tag > 0) {
						++tt;
						if ("network.jsp".equals(xml_current)) {
							session.setAttribute(SessionConstant.CURRENT_TOPO_VIEW, xml_current);
						} else {
							response.sendRedirect(request.getContextPath() + "/topology/submap/index.jsp?submapXml=" + xml_current);
						}
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (tt == 0) {
				response.sendRedirect(request.getContextPath() + "/topology/network/blank.jsp");
			} else {
				session.setAttribute("fatherXML", "network.jsp"); //yangjun add
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
%>
<html>
<head>
<title>ÍøÂçÉè±¸ÍØÆËÍ¼</title>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
</head>
<frameset id="search" name=search cols="0,*" frameborder="NO" border="0" framespacing="0" rows="*">
	<frame name="leftFrame" src="tree.jsp?treeflag=0&fromtopo=true">
	<frame name="mainFrame" src="network.jsp">
</frameset>

</html>
