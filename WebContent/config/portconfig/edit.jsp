<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.config.model.Portconfig"%>
<% 

  String rootPath = request.getContextPath(); 
  Portconfig port = (Portconfig)request.getAttribute("vo");
  //System.out.println("----------------"+port.getId());
  String id = port.getId()+"";
  Host host = (Host)PollingEngine.getInstance().getNodeByIP(port.getIpaddress());
  String alias="";
  if(host != null)alias=host.getAlias();
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
	String sms0="";
	if(port.getSms() == 0){
		sms0="selected";
	}
	String sms1="";
	if(port.getSms() == 1){
		sms1="selected";
	}
	String reportflag0="";
	if(port.getReportflag() == 0){
		reportflag0="selected";
	}
	String reportflag1="";
	if(port.getReportflag() == 1){
		reportflag1="selected";
	}						  	 
       //System.out.println("====================");  
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
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
     var chk = checkinput("linkuse","string","端口应用",50,false);
     var chk1 = checkinput("inportalarm","string","入口流量阀值",50,false);
	 var chk2 = checkinput("outportalarm","string","出口流量阀值",50,false);

   var linkUseValue= document.all.linkuse.value;
     
     if(chk&chk1&chk2)
     {
     	if (confirm("确定要修改吗?")){
        	mainForm.action = "<%=rootPath%>/portconfig.do?action=updateport";
        	mainForm.submit();
        	var useId =window.opener.document.getElementById("linkUse<%=id%>");
        	useId.innerHTML=linkUseValue;
        	alert("修改成功!");
      //  	window.opener.location.reload();
        	window.close();
        }
     }
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
<title>端口修改 -> <%=port.getName()%></title>

<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4" >
<form method="post" name="mainForm">
<input type=hidden name="id" value="<%=id%>">
<input type=hidden name="ipaddress" value="<%=port.getIpaddress()%>">
<table id="body-container" class="body-container">
<tr>
<td class="td-container-main">
	<table id="container-main" class="container-main">
		<tr>
			<td class="td-container-main-add">
			<table id="container-main-add" class="container-main-add">
				<tr>
			  		<td height=380 valign="top" align=center >	
			  		
						<table id="add-content" class="add-content" bgcolor="#FFFFFF" >
                  				<tr>
                    					<td height="25" colspan="2" >
                    						<table id="add-content-header" class="add-content-header">
							                	<tr>
								                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
								                	<td class="add-content-title"> 资源管理 >> 设备维护 >>修改端口 >> <%=port.getName()%>  </td>
								                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
								       			</tr>
											</table>
                    					</td>
                  				</tr>
                  				<tr>
                  					<TD nowrap align="right" height="24" width="10%">设备名称(IP地址)&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=alias%>(<%=port.getIpaddress()%>)
                  					
                  					</TD>
                				</tr>
                  				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">端口&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;<%=port.getPortindex()%>
                  					</TD>
                				</tr>
                				<tr>
                  					<TD nowrap align="right" height="24" width="10%">端口应用&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="linkuse" maxlength="50" size="20" class="formStyle" value="<%=port.getLinkuse()%>">
                  					</TD>
                				</tr>  
                				<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">短信&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<select name="sms">
                  					<option value="0" <%=sms0%>>否</option>
                  					<option value="1" <%=sms1%>>是</option>
                  					</select>
                  					
                  					</TD>
                				</tr> 
                				<tr>
                  					<TD nowrap align="right" height="24" width="10%">显示于报表&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<select name="reportflag">
                  					<option value="0" <%=reportflag0%>>否</option>
                  					<option value="1" <%=reportflag1%>>是</option>
                  					</select>
                  					
                  					</TD>
                				</tr>                				
                  				
								<tr bgcolor="#F1F1F1">
                  					<TD nowrap align="right" height="24" width="10%">入口流速阀值&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="inportalarm" maxlength="50" size="20" class="formStyle" value="<%=port.getInportalarm()%>">
                  					</TD>
                				</tr>  
                                <tr>
                  					<TD nowrap align="right" height="24" width="10%">出口流速阀值&nbsp;</TD>
                  					<TD nowrap width="40%">&nbsp;
                  					<input type="text" name="outportalarm" maxlength="50" size="20" class="formStyle" value="<%=port.getOutportalarm()%>">
                  					</TD>
                				</tr> 



                				<tr align=center>
                					<td colspan=11 align=center><br>
								<input type=reset class="formStylebutton" style="width:50" value="修 改" onclick="toEdit()">&nbsp;&nbsp; 
								<input type=reset class="formStylebutton" style="width:50" value="关 闭" onclick="window.close()">               					
                  					</td>
                  				</tr>  
                  				<tr>
                  					<td>&nbsp;</td>
                  				
                  				</tr>             				                				              				
                
                  			</table>			  		
			  		
			  		
			  		            

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
 