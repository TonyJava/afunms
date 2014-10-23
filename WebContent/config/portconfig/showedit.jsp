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
<%@page import="com.afunms.topology.model.*"%>
<% 

  String rootPath = request.getContextPath(); 
  NodeMonitor nm = (NodeMonitor)request.getAttribute("nodemointor");
  String id = (String)request.getAttribute("id");
  Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
  String ipaddress = (String)request.getAttribute("ipaddress");
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
	String sms0_0="";
	if(nm.getSms0() == 0){
		sms0_0="selected";
	}
	String sms0_1="";
	if(nm.getSms0() == 1){
		sms0_1="selected";
	}
	String sms1_0="";
	if(nm.getSms1() == 0){
		sms1_0="selected";
	}
	String sms1_1="";
	if(nm.getSms1() == 1){
		sms1_1="selected";
	}
	String sms2_0="";
	if(nm.getSms2() == 0){
		sms2_0="selected";
	}
	String sms2_1="";
	if(nm.getSms2() == 1){
		sms2_1="selected";
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
   
  window.onunload   =   onClose;   
  function   onClose()   
  {   
      window.opener.location.reload();   
      //alert("close?");   
  }   

  function toEdit()
  {
     var chk = checkinput("limenvalue0","number","一级阀值",30,false);
     var chk1 = checkinput("time0","number","次数",5,false);
     var chk2 = checkinput("limenvalue1","number","二级阀值",30,false);
     var chk3 = checkinput("time1","number","次数",5,false);
     var chk4 = checkinput("limenvalue2","number","三级阀值",30,false);
     var chk5 = checkinput("time2","number","次数",5,false);

     
     if(chk && chk1 && chk2 && chk3 && chk4 && chk5)
     {
     	if (confirm("确定要修改吗?")){
        	mainForm.action = "<%=rootPath%>/moid.do?action=updatemoid";
        	mainForm.submit();
        	window.opener.location.reload();
        	window.close();
        }
     }
     
     //window.close();
  }
function openwin3(operate,ipaddress,index,ifname) 
{	//var ipaddress = document.forms[0].ipaddress.value;
  window.open ("<%=rootPath%>/monitor.do?action="+operate+"&ipaddress="+ipaddress+"&ifindex="+index+"&ifname="+ifname, "newwindow", "height=400, width=850, toolbar= no, menubar=no, scrollbars=yes, resizable=yes, location=yes, status=yes") 
}
function toMain() 
{	//var ipaddress = document.forms[0].ipaddress.value;
  window.location="<%=rootPath%>/monitor.do?action=netif&id=<%=id%>&ipaddress=<%=ipaddress%>"; 
}
  

  
</script>
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<title>阀值修改 -> <%=nm.getDescr()%></title>

<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4" >
<form method="post" name="mainForm">
<input type=hidden name="id" value="<%=id%>">
<input type=hidden name="moid" value="<%=nm.getId()%>">
<input type=hidden name="ipaddress" value="<%=ipaddress%>">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="0"></td>
		<td bgcolor="#9FB0C4" align="center">
		<table width="100%" border=0 cellpadding=0 cellspacing=0>
			<tr>
				<td><img src="<%=rootPath%>/resource/image/main_frame_ltcorner.gif" width=12 height=34></td>
				<td background="<%=rootPath%>/resource/image/main_frame_tbg.gif" align="right">
				<img border="0" src="<%=rootPath%>/resource/image/main_frame_tflag.gif" width="66" height="34"></td>
				<td><img src="<%=rootPath%>/resource/image/main_frame_rtcorner.gif" width=13 height=34></td>
			</tr>
			<tr>
				<td background="<%=rootPath%>/resource/image/main_frame_lbg.gif" width=12>　</td>
			  	<td height=380 bgcolor="#FFFFFF" valign="top" align=center>	
			  		
					<table width="80%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none align=center border=1 algin="center">
                  				<tr>
                    					<td width="619" height="25" class=tableMasterHeaderAlone style="HEIGHT: 25px" colspan=2><div align="center"><b><font color="#FFFFFF">阀值修改 >> <%=nm.getDescr()%></font></b></div></td>
                  				</tr>
                  				<tr>
                  					<TD nowrap align="right" height="24" width="10%">阀值名称&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=nm.getDescr()%>
                  					
                  					</TD>
                				</tr>
                  				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">一级阀值(普通)&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="limenvalue0" maxlength="50" size="20" class="formStyle" value="<%=nm.getLimenvalue0()%>"><%=nm.getUnit()%>
                  					</TD>
                				</tr>
                				<tr>
                  					<TD nowrap align="right" height="24" width="10%">次数&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="time0" maxlength="50" size="20" class="formStyle" value="<%=nm.getTime0()%>">
                  					</TD>
                				</tr>  
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">短信&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<select name=sms0>
                  					<option value="0" <%=sms0_0%>>否</option>
                  					<option value="1" <%=sms0_1%>>是</option>
                  					</select>
                  					
                  					</TD>
                				</tr> 
                  				<tr >
                  					<TD nowrap align="right" height="24" width="10%">二级阀值(严重)&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="limenvalue1" maxlength="50" size="20" class="formStyle" value="<%=nm.getLimenvalue1()%>"><%=nm.getUnit()%>
                  					</TD>
                				</tr>
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">次数&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="time1" maxlength="50" size="20" class="formStyle" value="<%=nm.getTime1()%>">
                  					</TD>
                				</tr>  
                				<tr >
                  					<TD nowrap align="right" height="24" width="10%">短信&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<select name=sms1>
                  					<option value="0" <%=sms1_0%>>否</option>
                  					<option value="1" <%=sms1_1%>>是</option>
                  					</select>                  					
                  					
                  					</TD>
                				</tr>  
                  				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">三级阀值(紧急)&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="limenvalue2" maxlength="50" size="20" class="formStyle" value="<%=nm.getLimenvalue2()%>"><%=nm.getUnit()%>
                  					</TD>
                				</tr>
                				<tr>
                  					<TD nowrap align="right" height="24" width="10%">次数&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="time2" maxlength="50" size="20" class="formStyle" value="<%=nm.getTime2()%>">
                  					</TD>
                				</tr>  
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">短信&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<select name=sms2>
                  					<option value="0" <%=sms2_0%>>否</option>
                  					<option value="1" <%=sms2_1%>>是</option>
                  					</select>                  					
                  					
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
                  		<td background="<%=rootPath%>/resource/image/main_frame_rbg.gif" width=13>　</td>
                  	</tr>
		</table> 
	</td>
	
	</tr>
	</table>                	              
                
                                          
</form>
</BODY>
</HTML>
 