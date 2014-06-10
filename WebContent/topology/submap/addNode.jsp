<%@page language="java" contentType="text/html;charset=GB2312"%>
<%  
   String rootPath = request.getContextPath();  
%>
<html>
<head>
<base target="_self">
<meta http-equiv="Pragma" content="no-cache">
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script language="javascript">
function save()
{
    var args = window.dialogArguments;   
    var ipaddress = document.getElementById("ipaddress").value;
    var alias = document.getElementById("alias").value;
    var chk1 = checkinput("ipaddress","ip","IP地址",15,false);
    var chk2 = checkinput("alias","string","机器名",30,false);
    if(chk1&&chk2){
        args.parent.mainFrame.addEquip(ipaddress,alias);
        window.close(); 
    }
}

</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4">
<form name="mainForm" method="post">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=350px>
	<tr>
		<td bgcolor="#cedefa" align="center" valign=top>
		<table width="350px" border=0 cellpadding=0 cellspacing=0>
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;&nbsp;添加设备节点</td>
				</tr>
			<tr>
				<td height=150 bgcolor="#FFFFFF" valign="top">
				 <table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%" align="center">
					<TBODY>
						<tr>
							<td nowrap colspan="2" height="3" bgcolor="#8EADD5"></td>
						</tr>
			<tr>						
			<TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">IP地址&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <input type="text" name="ipaddress" size="30" class="formStyle" value="">
			   <font color="red">&nbsp;*</font>
			</TD>
			</tr>
			<tr>						
			<TD style="background-color: #ECECEC;" nowrap align="right" height="24" width="20%">描述&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <input type="text" name="alias" size="30" class="formStyle" value="">
			   <font color="red">&nbsp;*</font>
			</TD>
			</tr>
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