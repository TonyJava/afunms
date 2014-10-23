<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<%@ page import="com.jspsmart.upload.*"%>
<%
String rootPath = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+rootPath+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>下载</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
　	<script language="JavaScript"> 
/**
	*自动关闭
	*/
	　　function closeit() 
	　　{ 
	　　setTimeout("self.close()",0) //毫秒 
	　　} 
　　</script>
  </head>
  
  <body onload="closeit()">
    <%
    	String dname_get=request.getParameter("name");//获得文件名
    	String  dname = new String(dname_get.getBytes("ISO-8859-1"),"GBK");//防止乱码转译
    	String path=".."+request.getContextPath()+"/config/knowledgebase/attachfiles/"+dname;
    	SmartUpload su=new SmartUpload();
    	su.initialize(pageContext);//初始化
    	su.setContentDisposition(null);//不允许打开文件
    	su.downloadFile(path);//下载路径
    	out.clear();//清理
    	out=pageContext.pushBody();//刷新
     %>
  </body>
</html>
