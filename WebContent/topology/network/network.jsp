<%@page language="java" contentType="text/html;charset=GB2312"%>
<%
	String user = request.getParameter("user");
	if (user == null)
		user = "admin";

	String fullscreen = request.getParameter("fullscreen");
	if (fullscreen == null) {
		fullscreen = "0";
	} else {
		fullscreen = "1";
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>server view</title>
<script type="text/javascript">
	function changeFlags() {
		if (fullscreen == 0)
			window.parent.changeFlags();
		else
			window.moveTo(0, 0);
	}
</script>
</head>
<frameset id="parentFrame" rows="30,600" frameborder="no" border="0" framespacing="0">
	<frame src="topFrame.jsp?user=<%=user%>&fullscreen=<%=fullscreen%>" name="topFrame" scrolling="No" noresize="noresize" id="topFrame" title="工具栏" />
	<frame src="showMap.jsp?filename=networkData.jsp&fullscreen=<%=fullscreen%>" name="mainFrame" scrolling="No" noresize="noresize" id="mainFrame" title="网络设备视图" />
</frameset>
</html>