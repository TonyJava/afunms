<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.common.util.SysUtil" %>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.HostLastCollectDataManager"%>
<%@page import="com.afunms.polling.api.I_HostLastCollectData"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>

<%@page import="java.util.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.config.model.Diskconfig"%>
<% 

  String rootPath = request.getContextPath(); 
  Diskconfig port = (Diskconfig)request.getAttribute("vo");
  Host host = (Host)PollingEngine.getInstance().getNodeByIP(port.getIpaddress());
  String alias="";
  if(host != null)alias=host.getAlias();
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
	String monflag0="";
	if(port.getMonflag() == 0){
		monflag0="selected";
	}
	String monflag1="";
	if(port.getMonflag() == 1){
		monflag1="selected";
	}
	String sms0="";
	if(port.getSms() == 0){
		sms0="selected";
	}
	String sms1="";
	if(port.getSms() == 1){
		sms1="selected";
	}
	
	String sms2="";
	if(port.getSms1() == 0){
		sms2="selected";
	}
	String sms3="";
	if(port.getSms1() == 1){
		sms3="selected";
	}
	
	String sms4="";
	if(port.getSms2() == 0){
		sms4="selected";
	}
	String sms5="";
	if(port.getSms2() == 1){
		sms5="selected";
	}
	
	String reportflag0="";
	if(port.getReportflag() == 0){
		reportflag0="selected";
	}
	String reportflag1="";
	if(port.getReportflag() == 1){
		reportflag1="selected";
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
     //var chk = checkinput("linkuse","string","端口应用",50,false);

     
     //if(chk)
     //{
     	if (confirm("确定要修改吗?")){
        	mainForm.action = "<%=rootPath%>/disk.do?action=updatedisk";
        	mainForm.submit();
        	window.opener.location.reload();
        	window.close();
        }
     //}
  }
function openwin3(operate,ipaddress,index,ifname) 
{	//var ipaddress = document.forms[0].ipaddress.value;
  window.open ("<%=rootPath%>/monitor.do?action="+operate+"&ipaddress="+ipaddress+"&ifindex="+index+"&ifname="+ifname, "newwindow", "height=400, width=850, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes") 
}
function toMain() 
{	//var ipaddress = document.forms[0].ipaddress.value;
  window.location="<%=rootPath%>/monitor.do?action=netif&id=<%=port.getId()%>&ipaddress=<%=port.getIpaddress()%>"; 
}
  

  
</script>
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<title>磁盘告警修改 -> <%=port.getName()%></title>

<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4" >
<form method="post" name="mainForm">
<input type=hidden name="id" value="<%=port.getId()%>">
<input type=hidden name="ipaddress" value="<%=port.getIpaddress()%>">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="0"></td>
		<td bgcolor="#9FB0C4" align="center">
		<table width="100%" border=0 cellpadding=0 cellspacing=0>
			<tr>
			  	<td height=380 bgcolor="#FFFFFF" valign="top" align=center>	
			  		<br>
					<table width="80%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none align=center border=1 algin="center">
                  				<tr>
                    					<td width="619" height="25" bgcolor="#ECECEC" style="HEIGHT: 25px" colspan=2><div align="center"><b>资源管理 >> 性能监视 >>修改磁盘监视配置 >> <%=port.getName()%></b></div></td>
                  				</tr>
                  				<tr>
                  					<TD nowrap align="right" height="24" width="10%">IP地址&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=port.getIpaddress()%>
                  					
                  					</TD>
                				</tr>
                  				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">磁盘名称&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=port.getName()%>
                  					</TD>
                				</tr>
                				<tr >
                  					<TD nowrap align="right" height="24" width="10%">监视&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<select name=monflag>
                  					<option value="0" <%=monflag0%>>否</option>
                  					<option value="1" <%=monflag1%>>是</option>
                  					</select>
                  					
                  					</TD>
                				</tr>
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">一级阀值&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="limenvalue" maxlength="50" size="20" class="formStyle" value="<%=port.getLimenvalue()%>">%
                  					</TD>
                				</tr>  
                				<tr >
                  					<TD nowrap align="right" height="24" width="10%">短信&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<select name=sms>
                  					<option value="0" <%=sms0%>>否</option>
                  					<option value="1" <%=sms1%>>是</option>
                  					</select>
                  					
                  					</TD>
                				</tr>
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">二级阀值&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="limenvalue1" maxlength="50" size="20" class="formStyle" value="<%=port.getLimenvalue1()%>">%
                  					</TD>
                				</tr>  
                				<tr >
                  					<TD nowrap align="right" height="24" width="10%">短信&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<select name=sms1>
                  					<option value="0" <%=sms2%>>否</option>
                  					<option value="1" <%=sms3%>>是</option>
                  					</select>
                  					
                  					</TD>
                				</tr>
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">三级阀值&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="limenvalue2" maxlength="50" size="20" class="formStyle" value="<%=port.getLimenvalue2()%>">%
                  					</TD>
                				</tr>  
                				<tr>
                  					<TD nowrap align="right" height="24" width="10%">短信&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<select name=sms2>
                  					<option value="0" <%=sms4%>>否</option>
                  					<option value="1" <%=sms5%>>是</option>
                  					</select>
                  					
                  					</TD>
                				</tr> 
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">显示于报表&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<select name=reportflag>
                  					<option value="0" <%=reportflag0%>>否</option>
                  					<option value="1" <%=reportflag1%>>是</option>
                  					</select>
                  					
                  					</TD>
                				</tr>                				
                  				
                				<tr align=center>
                					<td colspan=11 align=center><br>
								<input type=reset class="formStylebutton" style="width:50" value="修 改" onclick="toEdit()">&nbsp;&nbsp; 
								<input type=reset class="formStylebutton" style="width:50" value="关 闭" onclick="window.close()">  
								<br>             					
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
 