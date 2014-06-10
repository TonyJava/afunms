<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.afunms.common.util.CEIString"%>
<%@ page import="com.jspsmart.upload.SmartUpload"%>
<%
  String rootPath = request.getContextPath();
%>
<html>
<head>
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css"/>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript">	 
  
  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 50);	
	
});
</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa">
	<%    
    String fileName = (String)request.getParameter("filename");
	try
	{
	    SmartUpload download = new SmartUpload();    
	    download.initialize(pageContext);   
	    download.downloadFile(CEIString.native2Unicode(fileName));
	    	
	}
	catch(Exception e)
	{
		e.printStackTrace();
		System.out.println("Error in download!");
	}finally{
		out.clear();
    	out = pageContext.pushBody();
	}
%>
	<div id="loading">
		<div class="loading-indicator">
			<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...
		</div>
	</div>
	<div id="loading-mask" style=""></div>
	<table id="tblListTitle" class="WorkPage_ListTable">
		<tr>
			<td height="7" align=center><input type=button value="关闭窗口" onclick="window.close()"></td>
		</tr>
	</table>

</BODY>
</HTML>