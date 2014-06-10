<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
  String rootPath = request.getContextPath();  
  String menu = request.getParameter("menu");
  if(menu!=null)
     session.setAttribute(SessionConstant.CURRENT_MENU,menu); 
     
  session.setAttribute(SessionConstant.CURRENT_CUSTOM_VIEW,"netping.jsp"); 
  session.setAttribute("fatherXML","netping.jsp"); 
%> 
<html>
<head>
<title>连通性拓扑图</title>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
</head>  
<frameset rows="30,*" cols="*" frameborder="NO" border="0" framespacing="0">
  <frame name="topFrame" scrolling="NO" noresize src="pingTop.jsp" marginheight="0" marginwidth="0">
  <frame name="mainFrame" src="pingsubmap.jsp">
</frameset>
</html>
