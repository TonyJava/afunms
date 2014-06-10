<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.topology.dao.ManageXmlDao"%>
<%@page import="com.afunms.topology.model.ManageXml"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SysLogger"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";

	String user = request.getParameter("user");
	if (user == null)
		user = "admin";

	String fullscreen = request.getParameter("fullscreen");
	if (fullscreen == null) {
		fullscreen = "0";
	} else {
		fullscreen = "1";
	}

	User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
	String bids[] = current_user.getBusinessids().split(",");
	String roleid = current_user.getRole() + "";
	if ("0".equals(roleid)) {
		session.setAttribute("fatherXML", "network.jsp"); //yangjun add
		session.setAttribute(SessionConstant.CURRENT_TOPO_VIEW,
				"network.jsp");
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
							if (vo.getBid() != null
									&& !"".equals(vo.getBid())
									&& !"".equals(bids[j])
									&& vo.getBid().indexOf(bids[j]) != -1) {
								tag++;
							}
						}
					}
					xml_current = vo.getXmlName();
					if (tag > 0) {
						++tt;
						if ("network.jsp".equals(xml_current)) {
							session.setAttribute(
									SessionConstant.CURRENT_TOPO_VIEW,
									xml_current);
						} else {
							response.sendRedirect(request
									.getContextPath()
									+ "/topology/submap/index.jsp?submapXml="
									+ xml_current);
						}
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (tt == 0) {
				response.sendRedirect(request.getContextPath()
						+ "/topology/network/blank.jsp");
			} else {
				session.setAttribute("fatherXML", "network.jsp"); //yangjun add
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<script src="<%=basePath%>js/jquery-1.3.2.min.js" type="text/javascript"></script>
<script src="<%=basePath%>jsp/js/commonMethod.js"></script>
<title>server view</title>
<script type="text/javascript">
	function changeFlags() {
		if (fullscreen == 0)
			window.parent.changeFlags();
		else
			window.moveTo(0, 0);
	}
	$(function() {
		$("#topoMask").bind("click", function() {
			openFullWindow("<%=basePath%>topology/network/index.jsp","topo");
		});
	});
	
</script>
<style type="text/css">
html,body {
	margin: 0px;
	padding: 0px;
	width: 100%;
	height: 100%;
}

#topoMask {
	position: absolute;
	left: 5px;
	top: 5px;
	z-index: 999;
	width: 16px;
	height: 16px;
	background: url('image/topo.gif') no-repeat center;
}

#topoFrame {
	width: 100%;
	height: 98%;
}
</style>
</head>
<body>
	<div id="topoMask"></div>
	<iframe frameborder=0 scrolling=yes src="pShowMap.jsp?filename=networkData.jsp&fullscreen=<%=fullscreen%>" id="topoFrame"></iframe>
</body>
</html>