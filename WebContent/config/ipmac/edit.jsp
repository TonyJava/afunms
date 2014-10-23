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
<%@ page import="com.afunms.polling.api.I_Portconfig"%>
<%@ page import="com.afunms.polling.om.Portconfig"%>
<%@ page import="com.afunms.polling.om.IpMac"%>
<%@ page import="com.afunms.polling.impl.PortconfigManager"%>

<%@page import="java.util.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<% 

  String rootPath = request.getContextPath(); 
  IpMac vo = (IpMac)request.getAttribute("vo");
 
    

java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");					  	 
         
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
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
function changeOrder(para){
      mainForm.orderflag.value = para;
      mainForm.id.value = <%=vo.getId()%>;
      mainForm.action = "<%=rootPath%>/monitor.do?action=netif";
      mainForm.submit();
}
function openwin3(operate,ipaddress,index,ifname) 
{	//var ipaddress = document.forms[0].ipaddress.value;
  window.open ("<%=rootPath%>/monitor.do?action="+operate+"&ipaddress="+ipaddress+"&ifindex="+index+"&ifname="+ifname, "newwindow", "height=400, width=850, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes") 
}

  function toAdd()
  {
     var chk = checkinput("sysname","string","系统名称",30,false);
     var chk1 = checkinput("syscontact","string","联系人",100,false);
     var chk2 = checkinput("syslocation","string","位置",50,false);
     
     if(chk && chk1 && chk2)
     {
     	if (confirm("确定要修改吗?")){
        	mainForm.action = "<%=rootPath%>/network.do?action=updatesysgroup";
        	mainForm.submit();
        }
     }
  }
  

  
</script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4" >
<form method="post" name="mainForm">
<input type=hidden name="id" value="<%=vo.getId()%>">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="200" valign=top align=center>
			<%=menuTable%>
		</td>
		<td bgcolor="#9FB0C4" align="center">
		<table width="100%" border=0 cellpadding=0 cellspacing=0>
			<tr>
			  	<td height=380 bgcolor="#FFFFFF" valign="top">	            
					<table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none align=center border=1 algin="center">
                  				<tr>
                    					<td width="619" height="25" bgcolor=#397DBD style="HEIGHT: 25px" colspan=2><div align="left">&nbsp;&nbsp;<font color=#ffffff><b>>> 设备维护 >> IP/MAC >> 编辑</b></font></div></td>
                  				</tr>
                  				<tr>
                  					<TD nowrap align="right" height="24" width="10%">网络设备名称&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="sysname" maxlength="50" size="20" class="formStyle" value="<%=vo.getRelateipaddr()%>">
                  					</TD>
                				</tr>
                  				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">网络设备IP地址&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=vo.getRelateipaddr()%>
                  					</TD>
                				</tr>
                				<tr>
                  					<TD nowrap align="right" height="24" width="10%">IP&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=vo.getIpaddress()%>
                  					</TD>
                				</tr>  
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">MAC&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="syscontact" maxlength="50" size="20" class="formStyle" value="<%=vo.getMac()%>">
                  					</TD>
                				</tr> 
                				<tr>
                  					<TD nowrap align="right" height="24" width="10%">是否绑定&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="syslocation" maxlength="50" size="20" class="formStyle" value="<%=vo.getIfband()%>">
                  					</TD>
                				</tr>
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">是否发送短信&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=vo.getIfsms()%>
                  					</TD>
                				</tr>                				                 				                				              				
                				<tr>
                					<td colspan=2 align=center><br>
								<input type="button" value="设 置" style="width:50" class="formStylebutton" onclick="toAdd()">&nbsp;&nbsp;
								<input type=reset class="formStylebutton" style="width:50" value="返 回" onclick="javascript:history.back(1)">                					
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
 