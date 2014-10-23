<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.common.util.SysUtil" %>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.HostLastCollectDataManager"%>
<%@page import="com.afunms.polling.api.I_HostLastCollectData"%>

<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@ page import="com.afunms.polling.api.I_Portconfig"%>
<%@ page import="com.afunms.polling.om.Portconfig"%>
<%@ page import="com.afunms.polling.om.IpMacBase"%>
<%@ page import="com.afunms.polling.impl.PortconfigManager"%>

<%@ page import="com.afunms.system.model.Department"%>
<%@ page import="com.afunms.system.dao.DepartmentDao"%>
<%@ page import="com.afunms.config.model.Employee"%>
<%@ page import="com.afunms.config.dao.EmployeeDao"%>

<%@page import="java.util.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@ include file="/include/globe.inc"%>

<% 

  String rootPath = request.getContextPath(); 
  IpMacBase vo = (IpMacBase)request.getAttribute("vo");
  String key = (String)request.getAttribute("key");
  String value = (String)request.getAttribute("value");
  //System.out.println("key==="+key+"====value:"+value);
  EmployeeDao dao = new EmployeeDao();
  List employeelist = dao.loadAll();
  dao.close();
  if(employeelist != null && employeelist.size()>0){
  	for(int i=0;i<employeelist.size();i++){
  		Employee employee = (Employee)employeelist.get(i);
  	}
  }
 
    

java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");					  	 
         
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
     	if (confirm("确定要修改吗?")){
     		<%
     			if(value != null && value.trim().length() > 0){
     		%>
        		mainForm.action = "<%=rootPath%>/ipmacbase.do?action=selupdateemployee";
        	<%
        		}else{
        	%>
        		mainForm.action = "<%=rootPath%>/ipmacbase.do?action=updateemployee";
        	<%
        		}
        	%>
        	mainForm.submit();
        }
  }
  

  
</script>
<script language="JavaScript" type="text/JavaScript">
var show = true;
var hide = false;
//修改菜单的上下箭头符号
function my_on(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="on";
}
function my_off(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="off";
}
//添加菜单	
function initmenu()
{
	var idpattern=new RegExp("^menu");
	var menupattern=new RegExp("child$");
	var tds = document.getElementsByTagName("div");
	for(var i=0,j=tds.length;i<j;i++){
		var td = tds[i];
		if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}

}

</script>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

</head>
<body id="body" class="body" onload="initmenu();">
	<form id="mainForm" method="post" name="mainForm">
		<input type=hidden name="id" value="<%=vo.getId()%>">
		<input type=hidden name="key" value="<%=key%>">
		<input type=hidden name="value" value="<%=value%>">
		
		<table id="body-container" class="body-container">
			<tr>
				<td class="td-container-menu-bar">
					<table id="container-menu-bar" class="container-menu-bar">
						<tr>
							<td>
								<%=menuTable%>
							</td>	
						</tr>
					</table>
				</td>
				<td class="td-container-main">
					<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
													<tr>
											        	<td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											       	 	<td class="layout_title"><b>设备维护 >> IP/MAC >> 编辑</b></td>
											        	<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
										  			</tr>
										  		</table>
										  	</td>
										  </tr>
			  						 	  <tr>
			  						 	  	<td>
			  						 	  		<table id="content-body" class="content-body">
			  						 	  			<tr>
			  						 	  				<td>
			  						 	  					<table>
			  						 	  						<tr>
								                  					<TD nowrap align="right" height="24" width="10%">网络设备名称&nbsp;</TD>
								                  					<TD nowrap width="40%">&nbsp;<%=vo.getRelateipaddr()%></TD>
								                				</tr>
								                  				<tr bgcolor="#F1F1F1">
								                  					<TD nowrap align="right" height="24" width="10%">网络设备IP地址&nbsp;</TD>
								                  					<TD nowrap width="40%">&nbsp;<%=vo.getRelateipaddr()%></TD>
								                				</tr>
								                				<tr>
								                  					<TD nowrap align="right" height="24" width="10%">IP&nbsp;</TD>
								                  					<TD nowrap width="40%">&nbsp;<%=vo.getIpaddress()%></TD>
								                				</tr>  
								                				<tr bgcolor="#F1F1F1">
								                  					<TD nowrap align="right" height="24" width="10%">MAC&nbsp;</TD>
								                  					<TD nowrap width="40%">&nbsp;<%=vo.getMac()%></TD>
								                				</tr> 
								                				<tr>
								                  					<TD nowrap align="right" height="24" width="10%">用户&nbsp;</TD>
								                  					<TD nowrap width="40%">&nbsp;
								                  					<select name=employee_id>
								                  					<option value="0"> </option>
								                  					<%
								                  						int employee_id = vo.getEmployee_id();
								                  						dao = new EmployeeDao();
								                  						Employee vo_ployee =(Employee)dao.findByID(employee_id+"");
								                  						if(employeelist != null && employeelist.size()>0){
								                  							for(int i=0;i<employeelist.size();i++){
								  										Employee employee = (Employee)employeelist.get(i);
								  										DepartmentDao deptdao = new DepartmentDao();
								  										Department dept = (Department)deptdao.findByID(employee.getDept()+"");
								  										deptdao.close();
								  										String namestr = employee.getName()+"("+dept.getDept()+")";
								  										String checkedflag = "";
								  										if(vo_ployee != null){
								  											if(employee.getId()==vo.getEmployee_id()) checkedflag="selected";
								  										}
								                  					%>
								                  					<option value="<%=employee.getId()%>" <%=checkedflag%>><%=namestr%></option>
								                  					<%
								                  							}
								                  						}
								                  						if(vo_ployee == null){
								                  					%>
								                  					<option value="0" selected> </option>
								                  					<%
								                  						}
								                  					%>
								                  					
								                  					</select>
								                  					</TD>
								                				</tr>               				                 				                				              				
								                				<tr bgcolor="#F1F1F1">
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
								</td>
							</tr>	
					</table> 
				</td>
			</tr>
		</table>                	              
	</form>
</BODY>
</HTML>
 