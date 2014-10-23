<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.system.util.UserView"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.polling.base.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.config.model.*"%>
<%
  String rootPath = request.getContextPath();
  List<CiscoSlaCfgCmdFile> list = (List<CiscoSlaCfgCmdFile>)request.getAttribute("list");
  // String devicetype=(String)request.getAttribute("devicetype");
  // if(devicetype==null)devicetype="";
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />

<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/js/jquery/jquery-1.4.2.min.js"></script>
<script language="JavaScript" type="text/javascript">
  var delAction = "<%=rootPath%>/db.do?action=delete";
  var listAction = "<%=rootPath%>/db.do?action=list";

  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/slacmd.do?action=ready_add";
     mainForm.submit();
  }
  function toDelete()
  {
     mainForm.action = "<%=rootPath%>/slacmd.do?action=delete";
     mainForm.submit();
  }  
  function toRefresh()
  {
     mainForm.action = "<%=rootPath%>/ciscosla.do?action=listperf";
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
	    location.href="<%=rootPath%>/FTP.do?action=detail&id="+node;
	}
	function edit()
	{
		location.href="<%=rootPath%>/slacmd.do?action=ready_edit&id="+node;
	}
	function cancelmanage()
	{
		location.href="<%=rootPath%>/FTP.do?action=changeMonflag&value=0&id="+node;
	}
	function reboot()
	{
		location.href="<%=rootPath%>/serverUpAndDown.do?action=reboot&value=1&id="+node;
	}
	function shutdown()
	{
		location.href="<%=rootPath%>/serverUpAndDown.do?action=shutdown&value=1&id="+node;
	}
	function clickMenu()
	{
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

			function submitip(){
				var obj= document.getElementsByName("radio");
				var Rev="";
				var retVal="";
			   for(var i=0;i<obj.length;i++)
			   {
			      if(obj[i].checked)
				 {
					Rev=obj[i].value;
					retVal= Rev;
					break;
				  }
			   }
			   //alert(	retVal);		
				$.ajax({
				type:"post",
				dataType:"json",		
			    url:"<%=rootPath%>/networkDeviceAjaxManager.ajax?action=loadFileFromMenu",
				data:"id="+retVal,
				success:function(data){
				window.opener.document.getElementById("commands").value=data.value;
				window.opener.document.getElementById("cmdid").value=data.cmdid;
				window.opener.document.getElementById("slatype").value=data.slatype;
				window.opener.document.getElementById("deviceType1").value=data.deviceType;
			    window.opener.document.getElementById("icmp").style.display='none';
				window.opener.document.getElementById("icmppath").style.display='none';
				window.opener.document.getElementById("udp").style.display='none';
				window.opener.document.getElementById("tcpconnectwithresponder").style.display='none';
				window.opener.document.getElementById("tcpconnectnoresponder").style.display='none';
				window.opener.document.getElementById("jitter").style.display='none';
				window.opener.document.getElementById("http").style.display='none';
				window.opener.document.getElementById("dns").style.display='none';
				window.opener.document.getElementById("h3cIcmp").style.display='none';
				window.opener.document.getElementById("h3cHttp").style.display='none';
				window.opener.document.getElementById("h3cNqa").style.display='none';
				if("cisco"==data.deviceType){
				if("icmp" == data.slatype){
					window.opener.document.getElementById("icmp").style.display='block';
				}else if("icmppath" == data.slatype){
					window.opener.document.getElementById("icmppath").style.display='block';
				}else if("udp" == data.slatype){
					window.opener.document.getElementById("udp").style.display='block';
				}else if("tcpconnectwithresponder" == data.slatype){
					window.opener.document.getElementById("tcpconnectwithresponder").style.display='block';
				}else if("tcpconnect-noresponder" == data.slatype){
					window.opener.document.getElementById("tcpconnectnoresponder").style.display='block';
				}else if("jitter" == data.slatype){
					window.opener.document.getElementById("jitter").style.display='block';
				}else if("http" == data.slatype){
					window.opener.document.getElementById("http").style.display='block';
				}else if("dns" == data.slatype){
					window.opener.document.getElementById("dns").style.display='block';												
				}
				}else if("h3c"==data.deviceType){
				if("icmp" == data.slatype){
					window.opener.document.getElementById("h3cIcmp").style.display='block';
				}else if("http"==data.slatype){
				   window.opener.document.getElementById("h3cHttp").style.display='block';
				}else{
				  window.opener.document.getElementById("h3cNqa").style.display='block';
				  window.opener.document.getElementById("sqatype").innerHTML=data.slatype+"类型&nbsp;&nbsp;";
				 }
				}
		 		window.close();
			     }
			     });
			}

</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#FFFFFF" onload="initmenu();">

<!-- 这里用来定义需要显示的右键菜单 -->
	<div id="itemMenu" style="display: none";>
	<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
		style="border: thin;font-size: 12px" cellspacing="0">
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.edit()">修改信息</td>
		</tr>		
	
	</table>
	</div>
	<!-- 右键菜单结束-->
<form method="post" name="mainForm">
<table id="body-container" class="body-container">
	<tr>
            <td align="center" valign=top>
			<table style="width:98%"  cellpadding="0" cellspacing="0" algin="center">
			<tr>
				<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%"><table width="100%" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
                    <td class="content-title">服务质量 >> 服务级别管理 >> SLA 命令列表</td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>						
				
		<tr >
			<td colspan="2">
				<table cellspacing="1" cellpadding="0" width="100%" >
					<tr class="microsoftLook0" height=28>
    					<th width='5%'>&nbsp;序号</th>
      					<th width='15%'>名称</th>
      					<th width='20%'>描述</th>
      					<th width='15%'>设备类型</th>
      					<th width='15%'>SLA类型</th>
</tr>
<%
	if(list != null && list.size()>0){
    		for(int i=0;i<list.size();i++){
       				CiscoSlaCfgCmdFile vo = list.get(i);
       				String type=vo.getDevicetype();
       				//if(type!=null&&!type.equals(devicetype))continue;
       
%>
       <tr bgcolor="#FFFFFF" <%=onmouseoverstyle%> class="microsoftLook">
    	<td height=25>&nbsp;&nbsp;<INPUT type="radio" class=noborder name="radio" value="<%=vo.getId()%>"><%=1 + i%></td>
		<td height=25>&nbsp;<%=vo.getName()%></td> 
		<td height=25>&nbsp;<%=vo.getFileDesc()%></td>
		<td height=25>&nbsp;<%=type%></td>
		<td height=25>&nbsp;<%=vo.getSlatype()%></td> 
  	</tr>
<%	}
}
%>				
			</table>
			</td>
		</tr>
		<tr>
              <td background="<%=rootPath%>/common/images/right_b_02.jpg" >
	              <table width="100%" border="0" cellspacing="0" cellpadding="0">
	                  <tr>
	                    <td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
	                    <td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
	                  </tr>
	              </table>
              </td>
            </tr>
		        												<tr>
																<TD nowrap colspan="7" align=center>
																<br><input type="button" value="提 交" style="width: 50" onclick="submitip()">
																					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																	<input type="reset" style="width: 50" value="关 闭" onclick="window.close()">
																</TD>	
															</tr>
	</table>
</td>
			</tr>
		</table>
</BODY>
</HTML>
