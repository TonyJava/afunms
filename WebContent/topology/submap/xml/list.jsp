<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.topology.model.CustomXml"%>
<%@ include file="/include/globe.inc"%>
<%
  List list = (List)request.getAttribute("list");
  int rc = list.size();
  String rootPath = request.getContextPath();
  JspPage jp = (JspPage)request.getAttribute("page");
%>
<html>
<head>
<title>dhcnms</title>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="JavaScript" type="text/javascript">
  var listAction = "<%=rootPath%>/customxml.do?action=list";
  var delAction = "<%=rootPath%>/customxml.do?action=delete";
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;

  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/customxml.do?action=ready_add";
     mainForm.submit();
  }
  
  function setDefault()
  {
    var bExist = false;

    if ( mainForm.radio.length == null ) 
    {
      if( mainForm.radio.checked )  
         bExist = true;
    }
    else  
    {
       for( var i=0; i < mainForm.radio.length; i++ )
       {
         if(mainForm.radio[i].checked)
            bExist = true;
       }
    }
    if(bExist)
    {
        mainForm.action = "<%=rootPath%>/customxml.do?action=set_default";
        mainForm.submit();
    }
    else
    {
       alert("请选择要设置的记录");
       return false;
    }
  }
</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4">
<form method="post" name="mainForm">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="200" valign=top align=center>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;拓扑</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/icon_cloud.gif" border=0>&nbsp;<a href="javascript:void(null)" onClick='window.open("<%=rootPath%>/topology/network/index.jsp","fullScreenWindow", "toolbar=no,height="+ window.screen.height + ","+"width=" + (window.screen.width-8) + ",scrollbars=no"+"screenX=0,screenY=0")'>网络拓扑</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/bmgl.GIF" border=0>&nbsp;<a href="javascript:void(null)" onClick='window.open("<%=rootPath%>/topology/server/index.jsp","fullScreenWindow", "toolbar=no,height="+ window.screen.height + ","+"width=" + (window.screen.width-8) + ",scrollbars=no"+"screenX=0,screenY=0")'>主机服务器</td>
                    		</tr>                    										                 										                      								
            		</tbody>
            		</table>  
            		<br>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;设备维护</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/addDevice.gif" border=0>&nbsp;<a href="<%=rootPath%>/network.do?action=ready_add">添加设备</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/icon_detail.gif" border=0>&nbsp;<a href="<%=rootPath%>/network.do?action=list&jp=1">设备列表</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/mac.jpg" border=0>&nbsp;<a href="<%=rootPath%>/ipmac.do?action=list&jp=1">IP/MAC</td>
                    		</tr>  
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/manageDev.gif" border=0>&nbsp;<a href="<%=rootPath%>/portconfig.do?action=list&jp=1">端口配置</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/infopoint.gif" border=0>&nbsp;<a href="<%=rootPath%>/link.do?action=list&jp=1">链路信息</td>
                    		</tr>                    		                      		                   		                   										                 										                      								
            		</tbody>
            		</table> 
            		<br>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;性能监视</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/hostl.gif" border=0 width=18>&nbsp;<a href="<%=rootPath%>/network.do?action=monitornodelist&jp=1">监视对象一览表</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/desktops.gif" border=0 width=18>&nbsp;<a href="<%=rootPath%>/moid.do?action=allmoidlist&jp=1">指标全局阀值一览表</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/manageDev.gif" border=0>&nbsp;<a href="<%=rootPath%>/network.do?action=list">TopN</td>
                    		</tr>                     		                   										                 										                      								
            		</tbody>
            		</table>             		           				
            		<br>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;视图管理</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/mkdz.gif" border=0>&nbsp;<a href="<%=rootPath%>/customxml.do?action=list&jp=1">视图编辑</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/zcbf.gif" border=0>&nbsp;<a href="javascript:void(null)" onClick='window.open("<%=rootPath%>/topology/view/custom.jsp","fullScreenWindow", "toolbar=no,height="+ window.screen.height + ","+"width=" + (window.screen.width-8) + ",scrollbars=no"+"screenX=0,screenY=0")'>视图展示</td>
                    		</tr>                    		                   										                 										                      								
            		</tbody>
            		</table> 		
            		<br>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;设备面板配置管理</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/editicon.gif" border=0>&nbsp;<a href="<%=rootPath%>/panel.do?action=showaddpanel&jp=1">面板模板编辑</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/manageDev.gif" border=0 width=18>&nbsp;<a href="<%=rootPath%>/network.do?action=panelnodelist&jp=1">设备面板编辑</td>
                    		</tr>                     		                   										                 										                      								
            		</tbody>
            		</table>  		
		
		</td>
		<td bgcolor="#cedefa" align="center" valign="top">
			<table width="98%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none align=center border=1 algin="center">
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=3>&nbsp;&nbsp;资源 >> 视图管理 >> 视图列表</td>
				</tr>
			<tr>
				<td height=300 bgcolor="#FFFFFF" valign="top">
<br>
<input type="hidden" name="intMultibox">	
	<table cellSpacing="1" cellPadding="0" width="100%" border="0">
    <tr><td colspan="2" width="80%" align="center">
    <jsp:include page="../../../common/page.jsp">
     <jsp:param name="curpage" value="<%=jp.getCurrentPage()%>"/>
     <jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>"/>
   </jsp:include>
        </td></tr> 
		<tr>
			<td colspan="2">
			<table cellspacing="1" cellpadding="0" width="100%" >
			<tr class="microsoftLook0" height=28>
    <th align='center' width='15%'>序号</th>				
    <th align='center' width='30%'>视图名</th>
    <th align='center' width='10%'>编辑</th>
    <th align='center' width='15%'>编辑结点</th>
    <th align='center' width='15%'>编辑连线</th>
</tr>
<%
    int startRow = jp.getStartRow();
    for(int i=0;i<rc;i++)
    {
       CustomXml vo = (CustomXml)list.get(i);
%>
<tr bgcolor="DEEBF7" <%=onmouseoverstyle%> class="microsoftLook">
    	<td  align='center'><INPUT type="radio" class=noborder name=radio value="<%=vo.getId()%>" class=noborder><font color='blue'><%=startRow + i%></font></td>    	 	
    	<td  align='center'><%=vo.getViewName()%></td>		
        <td  align='center'><a href="<%=rootPath%>/customxml.do?action=ready_edit&id=<%=vo.getId()%>">
        <img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></a></td>		
		<td  align='center'><a href="<%=rootPath%>/customview.do?action=ready_edit_nodes&id=<%=vo.getId()%>"><img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></a></td>
		<td  align='center'><a href="<%=rootPath%>/customview.do?action=ready_edit_lines&id=<%=vo.getId()%>"><img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></a></td>
  	</tr>
<%
    }
%> 				
			</table>
			</td>
		</tr>	
	</table>
	<table cellspacing="5" border="0" >
	    <tr>
			<td><a href="#" onclick="toAdd()">添加</a></td>
			<td><a href="#" onclick="toDelete2()">删除</a></td>
		</tr>
	 </table>
</td>
			</tr>
		</table>
</form>		
</BODY>
</HTML>
