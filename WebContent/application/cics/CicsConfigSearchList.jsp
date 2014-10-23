<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.application.model.CicsConfig"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.base.Node"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%
  String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");

%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />

<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="javascript">	
  
  var delAction = "<%=rootPath%>/network.do?action=webdelete";
  var listAction = "<%=rootPath%>/network.do?action=list";
  
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/network.do?action=find";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }
  
  function toFind()
  {
      mainForm.action = "<%=rootPath%>/cics.do?action=ready_find";
      mainForm.submit();
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/cics.do?action=ready_add";
      mainForm.submit();
  }
  
  function toDelete()
  {
     mainForm.action = "<%=rootPath%>/cics.do?action=delete";
     mainForm.submit();
  }

</script>
<script language="JavaScript">

	//公共变量
	var node="";
	var ipaddress="";
	var operate="";
	/**
	*根据传入的id显示右键菜单
	*/
	function showMenu(id,nodeid,ip)
	{	
		ipaddress=ip;
		node=nodeid;
		//operate=oper;
	    if("" == id)
	    {
	        popMenu(itemMenu,100,"100");
	    }
	    else
	    {
	        popMenu(itemMenu,100,"1111");
	    }
	    event.returnValue=false;
	    event.cancelBubble=true;
	    return false;
	}
	/**
	*显示弹出菜单
	*menuDiv:右键菜单的内容
	*width:行显示的宽度
	*rowControlString:行控制字符串，0表示不显示，1表示显示，如“101”，则表示第1、3行显示，第2行不显示
	*/
	function popMenu(menuDiv,width,rowControlString)
	{
	    //创建弹出菜单
	    var pop=window.createPopup();
	    //设置弹出菜单的内容
	    pop.document.body.innerHTML=menuDiv.innerHTML;
	    var rowObjs=pop.document.body.all[0].rows;
	    //获得弹出菜单的行数
	    var rowCount=rowObjs.length;
	    //alert("rowCount==>"+rowCount+",rowControlString==>"+rowControlString);
	    //循环设置每行的属性
	    for(var i=0;i<rowObjs.length;i++)
	    {
	        //如果设置该行不显示，则行数减一
	        var hide=rowControlString.charAt(i)!='1';
	        if(hide){
	            rowCount--;
	        }
	        //设置是否显示该行
	        rowObjs[i].style.display=(hide)?"none":"";
	        //设置鼠标滑入该行时的效果
	        rowObjs[i].cells[0].onmouseover=function()
	        {
	            this.style.background="#397DBD";
	            this.style.color="white";
	        }
	        //设置鼠标滑出该行时的效果
	        rowObjs[i].cells[0].onmouseout=function(){
	            this.style.background="#F1F1F1";
	            this.style.color="black";
	        }
	    }
	    //屏蔽菜单的菜单
	    pop.document.oncontextmenu=function()
	    {
	            return false; 
	    }
	    //选择右键菜单的一项后，菜单隐藏
	    pop.document.onclick=function()
	    {
	        pop.hide();
	    }
	    //显示菜单
	    pop.show(event.clientX-1,event.clientY,width,rowCount*25,document.body);
	    return true;
	}
	function detail()
	{
	    location.href="<%=rootPath%>/cics.do?action=detail&id="+node;
	}
	function edit()
	{
		location.href="<%=rootPath%>/cics.do?action=ready_edit&id="+node;
	}
	function cancelmanage()
	{
		location.href="<%=rootPath%>/cics.do?action=cancelalert&id="+node;
	}
	function addmanage()
	{
		location.href="<%=rootPath%>/cics.do?action=addalert&id="+node;
	}	
	function clickMenu()
	{
	}
</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa">
<!-- 这里用来定义需要显示的右键菜单 -->
	<div id="itemMenu" style="display: none";>
	<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
		style="border: thin;font-size: 12px" cellspacing="0">
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.edit()">修改信息</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.detail();">监视信息</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.cancelmanage()">取消监视</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.addmanage()">添加监视</td>
		</tr>		
	</table>
	</div>
	<!-- 右键菜单结束-->
<form method="post" name="mainForm">
<table id="body-container" class="body-container">
	<tr>
		<td width="200" valign=top align=center>
			<%=menuTable%>
		</td>
		<td bgcolor="#ffffff" align="center" valign=top>
			<table width="98%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none  border=1 algin="center">
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=3>&nbsp;&nbsp;<font color=#ffffff>应用 >> 服务列表 >> WEB服务列表</font></td>
				</tr> 				
				<tr>
								<td width="100%" align='right'>
									<a href="#" onclick="toFind()">发现服务</a>
									<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
		  						</td>                       
        		</tr>
				<tr>
					<td colspan="2">
						<table cellspacing="1" cellpadding="0" width="100%">
	  						<tr class="microsoftLook0" height=28>
      								<td width='10%' align="left"><INPUT type="checkbox" class=noborder name="checkall" onclick="javascript:chkall()">序号</td>				
      								<th width='15%'>服务名称</th>
      								<th width='15%'>主机名或IP地址</th>
      								<th width='10%'>网络协议</th>      
      								<th width='10%'>监听端口</th>
      								<th width='10%'>是否监控</th>
      								<th width='10%'>当前状态</th>
      								<th width='10%'>Gateway</th>
      								<th width='10%'>操作</th>
							</tr>
			<%
    			CicsConfig vo = null;
   				 for(int i=0;i<list.size();i++)
    			{
       				vo = (CicsConfig)list.get(i);
       				Node node = PollingEngine.getInstance().getCicsByID(vo.getId());
       				String alarmmessage = "";
				       if(node != null){
				       		vo.setStatus(1);
				       		//System.out.println(vo.getIpaddress()+"====================="+node.isAlarm());
				       		if(node.isAlarm())vo.setStatus(3);
				       		List alarmlist = node.getAlarmMessage();
				       		if(alarmlist!= null && alarmlist.size()>0){
							for(int k=0;k<alarmlist.size();k++){
								alarmmessage = alarmmessage+alarmlist.get(k).toString();
							}
						}
				       } else {
				           //System.out.println("=====================");
				       }
			%>
   										<tr bgcolor="DEEBF7" <%=onmouseoverstyle%> height=25>  
    											<td ><INPUT type="checkbox" name=checkbox class=noborder value="<%=vo.getId() %>">
    												<font color='blue'><%=1 + i%></font></td>
    											<td  align='center' ><%=vo.getRegion_name()%></td>
    											<td  align='center' style="cursor:hand"><font color="#397DBD"><%= vo.getIpaddress()%></font></td>
    											<td  align='center'><%=vo.getNetwork_protocol()%></td>
    											<td  align='center'><%=vo.getPort_listener()%></td>
    											<%
												if(vo.getFlag()==0){
											%>
											<td  align='center'>否</td>
											<% 
												//if(vo.getFlag()==1);
												}else{
											%>
											 <td  align='center'>是</td>
											 <%
											 	}
											 %>
											<td  align='center' ><img src="<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(vo.getStatus())%>" border="0" alt="<%=alarmmessage%>"/></td>
											<td  align='center' ><%=vo.getGateway()%></td>
											<td height=25>&nbsp;
												<img src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 oncontextmenu=showMenu('2','<%=vo.getId()%>','')>
											</td>											 
  										</tr>			
								<%;} %>
									</table>
								</td>
							</tr>	
						</table>
					</td>
				</tr>
			</table>
		<td></td>
	</tr>
</table>
</form>
</BODY>
</HTML>
