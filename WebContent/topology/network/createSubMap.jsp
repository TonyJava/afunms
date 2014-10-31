<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.polling.node.IfEntity"%>
<%  
   String rootPath = request.getContextPath();  
   String objEntityStr = (String)request.getAttribute("objEntityStr");
   String linkStr = (String)request.getAttribute("linkStr");
%>
<html>
<head>
<base target="_self">

<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script language="javascript">
function save(){
    if(checkinput("view_name","string","视图名",50,false)){
        var args = window.dialogArguments;
        mainForm.action = "<%=rootPath%>/submap.do?action=saveSubMap";
        mainForm.submit();
        window.close(); 
        iTimerID = window.setInterval(args.location.reload(),30000);//延时刷新父页面
    }   
}

</script>
<script type="text/javascript" src="<%=rootPath%>/topology/network/js/wincloseoverride.js"></script>
		
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4">
<form name="mainForm" method="post">
<input type=hidden name="objEntityStr" value="<%=objEntityStr%>">
<input type=hidden name="linkStr" value="<%=linkStr%>">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=500px>
	<tr>
		<td bgcolor="#cedefa" align="center" valign=top>
		<table width="500px" border=0 cellpadding=0 cellspacing=0>
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;&nbsp;创建子图</td>
				</tr>
			<tr>
				<td height=250 bgcolor="#FFFFFF" valign="top">
				 <table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%" align="center">
					<TBODY>
						<tr>
							<td nowrap colspan="2" height="3" bgcolor="#8EADD5"></td>
						</tr>
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">视图名称&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="view_name" size="30" class="formStyle"><font color="red">&nbsp;*</font>
			               </TD>
			            </tr>
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">视图别名&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="viewname" size="30" class="formStyle">
			               </TD>
			            </tr>
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">背景图片&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="background" size="30" class="formStyle">
			               </TD>
			            </tr>
			            <!-- 
                        <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">设备个数&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="objEntityStr" size="30" class="formStyle" value="<%=objEntityStr%>"/>
			               </TD>
			            </tr>
			            <tr>						
			                <TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">链路信息&nbsp;</TD>				
			                <TD nowrap width="80%">
			                 &nbsp; <input type="text" name="linkStr" size="30" class="formStyle" value="<%=linkStr%>"/>
			               </TD>
			            </tr> 
			            -->
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