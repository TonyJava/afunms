<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.config.model.Portconfig"%>
<%@page import="com.afunms.config.model.PowerConfig"%>
<%@page import="com.afunms.config.model.EnvConfig"%>
<% 

  String rootPath = request.getContextPath(); 
  EnvConfig power = (EnvConfig)request.getAttribute("vo");
  String id = power.getId()+"";
  Host host = (Host)PollingEngine.getInstance().getNodeByIP(power.getIpaddress());
  String alias="";
  if(host != null)alias=host.getAlias();
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
	String sms0="";
	if(power.getEnabled() == 0){
		sms0="selected";
	}
	String sms1="";
	if(power.getEnabled() == 1){
		sms1="selected";
	}
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script language="javascript">
function showdiv(id)
{
	var obj=document.getElementsByName("tab");
    j=0;
	var tagname=document.getElementsByTagName("DIV");
	for(var i=0;i<tagname.length;i++)
	{
		if(tagname[i].id==id)
		{
		   obj[j].className="selectedtab";
		   tagname[i].style.display="";
		   j++;
		}
		else if(tagname[i].id.indexOf("contentData")==0)
		{
		   obj[j].className="1";
		   j++;
		   tagname[i].style.display="none";
		}
	}
}  

  function toEdit()
  {
		var value=document.getElementById("alarmvalue").value;
		var times=document.getElementById("alarmtimes").value;
		if(isNaN(value)){
		  alert("阀值必须为数字");
		  return;
		}
	    if(isNaN(times)){
		  alert("告警次数必须为数字");
		  return;
		}
     	if (confirm("确定要修改吗?")){
        	mainForm.action = "<%=rootPath%>/envconfig.do?action=updateValue";
        	mainForm.submit();
        	alert("修改成功!");
      //  	window.opener.location.reload();
        	window.close();
     }
  }

</script>
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<title>电源模块修改 -> <%=power.getName()%></title>

<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4" >
<form method="post" name="mainForm">
<input type=hidden name="id" value="<%=id%>">
<input type=hidden name="ipaddress" value="<%=power.getIpaddress()%>">
 <input type=hidden name="entity" value="power">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="0"></td>
		<td bgcolor="#9FB0C4" align="center">
		<table width="100%" border=0 cellpadding=0 cellspacing=0>
			<tr>
				
			  	<td height=380 bgcolor="#FFFFFF" valign="top" align=center>	
			  		
					<table width="80%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none align=center border=1 algin="center">
                  				<tr>
                    					<td width="619" height="25" class=tableMasterHeaderAlone style="HEIGHT: 25px" colspan=2><div align="center"><b><font color="#FFFFFF">资源管理 >> 设备维护 >>修改电源模块>> <%=power.getName()%></font></b></div></td>
                  				</tr>
                  				<tr>
                  					<TD nowrap align="right" height="24" width="10%">设备名称:&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=alias%>
                  					
                  					</TD>
                				</tr>
                  				<tr>	
                  					<TD nowrap align="right" height="24" width="10%"> IP地址:&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=power.getIpaddress()%></TD>
                				</tr>
                				<tr>
                  					<TD nowrap align="right" height="24" width="10%"> 电源模块:&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=power.getName()%></TD>
                				</tr>
								
                                <tr>
                  					<TD nowrap align="right" height="24" width="10%"> 阀值:&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="alarmvalue" id="alarmvalue" maxlength="50" size="20" class="formStyle" value="<%=power.getAlarmvalue()%>">
                  					</TD>
                				</tr> 
                				 <tr>
                  					<TD nowrap align="right" height="24" width="10%"> 告警次数:&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="alarmtimes" id="alarmtimes" maxlength="50" size="20" class="formStyle" value="<%=power.getAlarmtimes()%>">
                  					</TD>
                				</tr> 

                				<tr align=center>
                					<td colspan=11 align=center><br>
								<input type=reset class="formStylebutton" style="width:50" value="修 改" onclick="toEdit()">&nbsp;&nbsp; 
								<input type=reset class="formStylebutton" style="width:50" value="关 闭" onclick="window.close()">               					
                  					</td>
                  				</tr>                				                				              				
                  			</table>			  		
                  		</td>
                  	</tr>
		</table> 
	</td>
	
	</tr>
	</table>                	              
                
                                          
</form>
</BODY>
</HTML>
 