<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.topology.model.CustomXml"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.base.Node"%>
<%@page import="com.afunms.topology.util.XmlInfo"%>
<%
  CustomXml vo = (CustomXml)request.getAttribute("vo");
  List selectTable = (List)request.getAttribute("table");
  
  String rootPath = request.getContextPath();
%>
<html>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<%
  out.println("<script language=\"JavaScript\" type=\"text/javascript\">");
  out.println("	var fileName = \"" + vo.getXmlName() + "\"");
  out.println("</script>");
%>
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script type="text/javascript" src="<%=rootPath%>/topology/view/js/customview.js"></script>
<script language="JavaScript" type="text/javascript">
  
  function doInit()
  {
  	 var url = "<%=rootPath%>/resource/xml/" + fileName;
	 load4AddNodes(url);
  }
   
  function doEdit()
  {
     if ( mainForm.viewnodes.options.length == 0 )
     {
        alert("右边下拉框为空,请增加记录!")
        return false;
     }

     var len = mainForm.viewnodes.options.length; //右边下拉框全选
     for ( i=0 ; i < len; i++ )
        mainForm.viewnodes.options[i].selected = true;

     mainForm.action = "<%=rootPath%>/customview.do?action=edit_nodes";
     mainForm.submit();
  }
 
</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4" onload="doInit()">
<form name="mainForm" method="post">
<input type=hidden name="xml" value="<%=vo.getXmlName()%>">
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
		<td bgcolor="#cedefa" align="center" valign=top>
			<table width="98%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none align=center border=1 algin="center">
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=3>&nbsp;&nbsp;资源 >> 视图管理 >> 定制视图 >> 编辑结点</td>
				</tr>
			<tr>
				<td height=300 bgcolor="#FFFFFF" valign="top" align="center">				 
				  <table border="0" id="table1" cellpadding="0" cellspacing="1" width="95%">
					<TBODY>
						<tr><td nowrap height="3" bgcolor="#8EADD5" colspan="2"></td></tr>
<!--=================================-->
       <tr><td align='center'><table>
          	<tr  borderColor=#cedefa height="32">
			<td width="45%" align="center">选择节点</td>
			<td width="10%" align="center"></td>
			<td width="45%" align="center">定制视图</td></tr>
		  <tr>
		    <td align="center" valign="top">
			<select name="node_list" style='width:300px;' size=20 multiple>
<%
		
		  for(int i=0;i<selectTable.size();i++)
		  {
			  XmlInfo item = (XmlInfo)selectTable.get(i);			  
		      out.print("<option value='" + item.getId() + "'>" + item.getInfo() + "</option>");
		  }
%>
		 </select><br><a href="#" onclick="add()">增加-></a></td>
		  <td>&nbsp;&nbsp;</td>
			<td align="center" nowrap>
			  <select name="viewnodes" size="20" style="width:300px;" multiple></select>
				<br><a href="#" onclick="del()"><-删除</a>
			</td>
		 </tr>
       </table></td></tr>
<!--=================================-->
						<tr align=center>
							<TD nowrap colspan="2">
								<br>
								<input type="button" value="保存" style="width:50" class="formStylebutton" onclick="doEdit()">&nbsp;&nbsp;
								<input type=reset class="formStylebutton" style="width:50" value="返回" onclick="javascript:history.back(1)">
							</TD>	
						</tr>						
					</TBODY>
				</TABLE>
				<br>
				<br>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>	
</body>
</html>
