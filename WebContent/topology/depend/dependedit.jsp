<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.topology.util.XmlInfo"%>
<%  
   String rootPath = request.getContextPath();  
   String ip = (String)request.getAttribute("ip");  
   String alias = (String)request.getAttribute("alias");    
   String nodeId = (String)request.getAttribute("nodeId");
   String category = (String)request.getAttribute("category");
   List selectTable = (List)request.getAttribute("table");
%>
<html>
<head>
<base target="_self">
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Pragma" content="no-cache">
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script type="text/javascript" src="<%=rootPath%>/topology/view/js/customview.js"></script>
<script language="javascript">
function save()
{
    var args = window.dialogArguments;
    if ( mainForm.viewnodes.options.length == 0 )
     {
        alert("右边下拉框为空,请增加记录!")
        return false;
     }

     var len = mainForm.viewnodes.options.length; //右边下拉框全选
     for ( i=0 ; i < len; i++ )
        mainForm.viewnodes.options[i].selected = true;

     mainForm.action = "<%=rootPath%>/nodedepend.do?action=save&xml="+args.fatherXML;
     mainForm.submit();
     window.close(); 
}

</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4">
<form name="mainForm" method="post">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=500px>
	<tr>
		<td bgcolor="#cedefa" align="center" valign=top>
		<table width="500px" border=0 cellpadding=0 cellspacing=0>
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;&nbsp;编辑节点依赖关系</td>
				</tr>
			<tr>
				<td height=250 bgcolor="#FFFFFF" valign="top">
				 <table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%" align="center">
					<TBODY>
						<tr>
							<td nowrap colspan="2" height="3" bgcolor="#8EADD5"></td>
						</tr>
			<tr>						
			<TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">当前节点&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <%=nodeId%>
			</TD>
			</tr>
			<tr >						
			<TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">设备名称&nbsp;</TD>				
			<TD nowrap width="80%">&nbsp; <%=alias%></TD>	
            </tr>	
            <tr>						
			<TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">设备IP&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <%=ip%>
			</TD>
			<tr>
            <tr><td align='center' colspan="2"><table>
          	<tr  borderColor=#cedefa height="32">
			<td width="45%" align="center">选择节点</td>
			<td width="10%" align="center"></td>
			<td width="45%" align="center">子节点</td></tr>
		  <tr>
		    <td align="center" valign="top">
			<select name="node_list" style='width:250px;' size=20 multiple>
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
			  <select name="viewnodes" size="20" style="width:250px;" multiple></select>
				<br><a href="#" onclick="del()"><-删除</a>
			</td>
		 </tr>
       </table></td></tr>
			<tr><td>
			  <input type="hidden" name='category' value="<%=category%>"/>
			  <input type="hidden" name='nodeId' value="<%=nodeId%>"/>
			</td></tr>
			<tr>
				<TD nowrap colspan="4" align=center>
					<br>
					<input type="button" value="保存" style="width:50" class="formStylebutton" onclick="save()">
					<input type="button" value="关闭" style="width:50" class="formStylebutton" onclick="window.close();">
				</TD>	
			</tr>
			</TBODY>
				</TABLE>				
				</td>
			</tr>			
		</table>
		</td>
	</tr>
</table>
</form>
</body>
</html>