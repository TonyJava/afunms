<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%
  String rootPath = request.getContextPath();  
  String fileName = request.getParameter("submapXml");
  String menu = request.getParameter("menu");
  if(menu!=null)
     session.setAttribute(SessionConstant.CURRENT_MENU,menu); 
     
  session.setAttribute(SessionConstant.CURRENT_SUBMAP_VIEW,fileName); 
  session.setAttribute("fatherXML",fileName); 
%> 
<html>
<head>
<title>网络设备拓扑图</title>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
</head>     
<frameset id="search" name=search cols="0,*" frameborder="NO" border="0" framespacing="0" rows="*">
    <frame name="leftFrame" src="../network/tree.jsp?treeflag=0&fileName=<%=fileName%>">
    <frame name="mainFrame" src="submap.jsp?fileName=<%=fileName%>">
  </frameset>
</html>
