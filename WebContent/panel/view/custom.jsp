<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
    String rootPath = request.getContextPath();
    String filename = (String)request.getParameter("filename");
    String oid = (String)request.getParameter("oid");
    String ip = (String)request.getParameter("ip");
    String imageType = (String)request.getParameter("imageType");
	session.setAttribute(SessionConstant.CURRENT_MENU,request.getParameter("menu"));
	
	//-----------判断全屏显示状态----------------
	String fullScreen = request.getParameter("fullscreen");
	if (fullScreen == null || fullScreen.equals("0")) {
		fullScreen = "0";
		out.println("<script type=\"text/javascript\"> var fullscreen = 0; </script>");
	}
	else {
	// 如果是全屏显示，修改 viewWidth
		fullScreen = "1";
		out.println("<script type=\"text/javascript\"> var fullscreen = 1;");
		out.println("viewWidth = window.screen.width;</script>");
	}
%>	   
<html xmlns:v="urn:schemas-microsoft-com:vml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title><%=ip%>设备面板图</title>
<script type="text/javascript" src="js/profile.js"></script>
<script type="text/javascript">
	var fullscreen = <%=fullScreen%>;
	if(fullscreen == "1")
	  viewWidth = window.screen.width;      
	function changeFlags() 
	{
		if (fullscreen == 0)
		   window.parent.changeFlags();
		else
		   window.moveTo(0, 0);
	}			
</script>
</head>
<frameset id="parentFrame" rows="30,600" frameborder="no" border="0" framespacing="0">
<frame src="<%=rootPath%>/panel/view/topFrame.jsp?fullscreen=<%=fullScreen%>" name="topFrame" scrolling="No" noresize="noresize" id="topFrame" title="工具栏" />
<frame src="<%=rootPath%>/panel/view/showMap.jsp?fullscreen=<%=fullScreen%>&filename=<%=filename%>&oid=<%=oid%>&imageType=<%=imageType%>&ip=<%=ip%>" name="mainFrame" scrolling="No" noresize="noresize" id="mainFrame" title="网络设备视图" />
</frameset>
</html>