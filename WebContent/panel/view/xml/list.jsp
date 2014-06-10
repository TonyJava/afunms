<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.topology.model.CustomXml"%>
<%@ include file="/include/globe.inc"%>
<%
  List list = (List)request.getAttribute("list");
  int rc = list.size();
  String rootPath = request.getContextPath();
  JspPage jp = (JspPage)request.getAttribute("page");
%>
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
<script language="JavaScript" type="text/javascript">
  var listAction = "<%=rootPath%>/customxml.do?action=list";
  var delAction = "<%=rootPath%>/customxml.do?action=delete";
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;

  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/customxml.do?action=ready_add";
     mainForm.submit();
  }
  
  function setDefault()
  {
    var bExist = false;

    if ( mainForm.radio.length == null ) 
    {
      if( mainForm.radio.checked )  
         bExist = true;
    }
    else  
    {
       for( var i=0; i < mainForm.radio.length; i++ )
       {
         if(mainForm.radio[i].checked)
            bExist = true;
       }
    }
    if(bExist)
    {
        mainForm.action = "<%=rootPath%>/customxml.do?action=set_default";
        mainForm.submit();
    }
    else
    {
       alert("请选择要设置的记录");
       return false;
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
	m1 =new Menu("menu1",'menu1_child','dtu','100',show,my_on,my_off);
	m1.init();
	m2 =new Menu("menu2",'menu2_child','dtu','100',show,my_on,my_off);
	m2.init();
	m3 =new Menu("menu3",'menu3_child','dtu','100',show,my_on,my_off);
	m3.init();
	m4 =new Menu("menu4",'menu4_child','dtu','100',hide,my_on,my_off);
	m4.init();
	m5 =new Menu("menu5",'menu5_child','dtu','100',hide,my_on,my_off);
	m5.init();
	m7 =new Menu("menu7",'menu7_child','dtu','100',hide,my_on,my_off);
	m7.init();

}
</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4" onload="initmenu();">
<form method="post" name="mainForm">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="200" valign=top align=center>
	
	<div class="tit" id="menu1" title="菜单标题">		
		<a href="#nojs" title="折叠菜单" target="" class="on" id="menu1_a" tabindex="1" >拓扑</a> 
	</div>
	<div class="list" id="menu1_child" title="菜单功能区" >
		<ul>
			<li id="m2_1" ><a href="javascript:void(null)" onClick='window.open("<%=rootPath%>/topology/network/index.jsp","fullScreenWindow", "toolbar=no,height="+ window.screen.height + ","+"width=" + (window.screen.width-8) + ",scrollbars=no"+"screenX=0,screenY=0")'>&nbsp;网络拓扑</a></li>
			<li id="m2_2" ><a href="javascript:void(null)" onClick='window.open("<%=rootPath%>/topology/server/index.jsp","fullScreenWindow", "toolbar=no,height="+ window.screen.height + ","+"width=" + (window.screen.width-8) + ",scrollbars=no"+"screenX=0,screenY=0")'>&nbsp;主机服务器</a></li>

		</ul>
	</div>
	<br> 
	<div class="tit" id="menu2" title="菜单标题">		
		<a href="#nojs" title="折叠菜单" target="" class="on" id="menu2_a" tabindex="2" >设备维护</a> 
	</div>
	<div class="list" id="menu2_child" title="菜单功能区" >
		<ul>
			<li id="m1_1" ><a href="<%=rootPath%>/network.do?action=ready_add">&nbsp;添加设备</a></li>
			<li id="m3_1" ><a href="<%=rootPath%>/network.do?action=list&jp=1">&nbsp;设备列表</a></li>
			<li id="m3_3" ><a href="<%=rootPath%>/portconfig.do?action=list&jp=1">&nbsp;端口配置</a></li>
			<li id="m3_4" ><a href="<%=rootPath%>/link.do?action=list&jp=1">&nbsp;链路信息</a></li>

		</ul>
	</div> 	
        <br>
	<div class="tit" id="menu3" title="菜单标题">		
		<a href="#nojs" title="折叠菜单" target="" class="on" id="menu3_a" tabindex="3" >性能监视</a> 
	</div>
	<div class="list" id="menu3_child" title="菜单功能区" >
		<ul>
			<li id="m4_1" ><a href="<%=rootPath%>/network.do?action=monitornodelist&jp=1">&nbsp;监视对象一览表</a></li>
			<li id="m4_2" ><a href="<%=rootPath%>/moid.do?action=allmoidlist&jp=1">&nbsp;指标全局阀值一览表</a></li>
		</ul>
	</div>
        <br>
	<div class="tit" id="menu7" title="菜单标题">		
		<a href="#nojs" title="折叠菜单" target="" class="on" id="menu7_a" tabindex="3" >IP/MAC资源</a> 
	</div>
	<div class="list" id="menu7_child" title="菜单功能区" >
		<ul>
			<li id="m4_3" ><a href="<%=rootPath%>/ipmacbase.do?action=list&jp=1">&nbsp;端口-IP-MAC基线</a></li>
			<li id="m4_4" ><a href="<%=rootPath%>/ipmac.do?action=list">&nbsp;当前MAC信息</a></li>
			<li id="m4_5" ><a href="<%=rootPath%>/ipmacchange.do?action=list&jp=1">&nbsp;MAC变更历史</a></li>
		</ul>
	</div>	
	<br>  
	<div class="tit" id="menu4" title="菜单标题">		
		<a href="#nojs" title="折叠菜单" target="" class="on" id="menu4_a" tabindex="4" >视图管理</a> 
	</div>
	<div class="list" id="menu4_child" title="菜单功能区" >
		<ul>
			<li id="m5_1" ><a href="<%=rootPath%>/customxml.do?action=list&jp=1">视图编辑</a></li>
			<li id="m5_2" ><a href="javascript:void(null)" onClick='window.open("<%=rootPath%>/topology/view/custom.jsp","fullScreenWindow", "toolbar=no,height="+ window.screen.height + ","+"width=" + (window.screen.width-8) + ",scrollbars=no"+"screenX=0,screenY=0")'>视图展示</a></li>

		</ul>
	</div>	                   		           				
        <br>	
	<div class="tit" id="menu5" title="菜单标题">		
		<a href="#nojs" title="折叠菜单" target="" class="on" id="menu5_a" tabindex="5" >设备面板配置管理</a> 
	</div>
	<div class="list" id="menu5_child" title="菜单功能区" >
		<ul>
			<li id="m6_1" ><a href="<%=rootPath%>/panel.do?action=showaddpanel&jp=1">面板模板编辑</a></li>
			<li id="m6_2" ><a href="<%=rootPath%>/network.do?action=panelnodelist&jp=1">设备面板编辑</a></li>

		</ul>
	</div>        	
        <br>
            				
	</td>
		<td bgcolor="#cedefa" align="center" valign="top">
			<table width="98%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none align=center border=1 algin="center">
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=3>&nbsp;&nbsp;资源 >> 视图管理 >> 视图列表</td>
				</tr>
			<tr>
				<td height=300 bgcolor="#FFFFFF" valign="top">
<br>
<input type="hidden" name="intMultibox">	
	<table cellSpacing="1" cellPadding="0" width="100%" border="0">
    <tr><td colspan="2" width="80%" align="center">
    <jsp:include page="../../../common/page.jsp">
     <jsp:param name="curpage" value="<%=jp.getCurrentPage()%>"/>
     <jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>"/>
   </jsp:include>
        </td></tr> 
		<tr>
			<td colspan="2">
			<table cellspacing="1" cellpadding="0" width="100%" >
			<tr class="microsoftLook0" height=28>
    <th align='center' width='15%'>序号</th>				
    <th align='center' width='30%'>视图名</th>
    <th align='center' width='10%'>编辑</th>
    <th align='center' width='15%'>编辑结点</th>
    <th align='center' width='15%'>编辑连线</th>
</tr>
<%
    int startRow = jp.getStartRow();
    for(int i=0;i<rc;i++)
    {
       CustomXml vo = (CustomXml)list.get(i);
%>
<tr bgcolor="DEEBF7" <%=onmouseoverstyle%> class="microsoftLook">
    	<td  align='center'><INPUT type="radio" class=noborder name=radio value="<%=vo.getId()%>" class=noborder><font color='blue'><%=startRow + i%></font></td>    	 	
    	<td  align='center'><%=vo.getViewName()%></td>		
        <td  align='center'><a href="<%=rootPath%>/customxml.do?action=ready_edit&id=<%=vo.getId()%>">
        <img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></a></td>		
		<td  align='center'><a href="<%=rootPath%>/customview.do?action=ready_edit_nodes&id=<%=vo.getId()%>"><img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></a></td>
		<td  align='center'><a href="<%=rootPath%>/customview.do?action=ready_edit_lines&id=<%=vo.getId()%>"><img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></a></td>
  	</tr>
<%
    }
%> 				
			</table>
			</td>
		</tr>	
	</table>
	<table cellspacing="5" border="0" >
	    <tr>
			<td><a href="#" onclick="toAdd()">添加</a></td>
			<td><a href="#" onclick="toDelete2()">删除</a></td>
		</tr>
	 </table>
</td>
			</tr>
		</table>
</form>		
</BODY>
</HTML>
