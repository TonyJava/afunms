<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.common.util.*" %>
<%@page import="com.afunms.monitor.item.*"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.impl.*"%>
<%@page import="com.afunms.polling.api.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.monitor.item.base.MoidConstants"%>
<%@page import="org.jfree.data.general.DefaultPieDataset"%>
<%@ page import="com.afunms.polling.api.I_Portconfig"%>
<%@ page import="com.afunms.polling.om.Portconfig"%>
<%@ page import="com.afunms.polling.om.*"%>
<%@ page import="com.afunms.polling.impl.PortconfigManager"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>

<%@page import="com.afunms.config.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.application.model.*"%>

<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.lang.*"%>
<%@page import="com.afunms.monitor.item.base.*"%>
<%@page import="com.afunms.monitor.executor.base.*"%>
<%@page import="com.afunms.common.util.CreatePiePicture"%>



<%
  String rootPath = request.getContextPath();;
  DBVo vo  = (DBVo)request.getAttribute("db");	
     
Hashtable max = (Hashtable) request.getAttribute("max");
//Hashtable imgurl = (Hashtable)request.getAttribute("imgurl");
String chart1 = (String)request.getAttribute("chart1");
String dbtye = (String)request.getAttribute("dbtye");
String managed = (String)request.getAttribute("managed");
String runstr = (String)request.getAttribute("runstr");
Vector tableinfo_v = (Vector)request.getAttribute("tableinfo_v");
Vector tableinfo_v1 = (Vector)request.getAttribute("tableinfo_v1");
Vector tableinfo_v2 = (Vector)request.getAttribute("tableinfo_v2");
Vector tableinfo_v3 = (Vector)request.getAttribute("tableinfo_v3");
Vector variables=(Vector)request.getAttribute("variables");
Vector status=(Vector)request.getAttribute("status");
String versionvalue=(String)request.getAttribute("versionvalue");
String vercommaval=(String)request.getAttribute("vercommaval");
String vercomosval=(String)request.getAttribute("vercomosval");
String verwaittimeval=(String)request.getAttribute("verwaittimeval");   
 
     			  	   
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="utf-8" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="utf-8"/>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="utf-8"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">

<script language="javascript">	

  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
  
  Ext.MessageBox.wait('数据加载中，请稍后.. ');   
  //location.href="EventqueryMgr.do";
        //msg.style.display="block";
        mainForm.action = "<%=rootPath%>/cfgfile.do?action=getcfgfile&ipaddress=<%=vo.getIpAddress()%>";
        mainForm.submit();
 });	
	
});

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

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
  } 
  
// 全屏观看
function gotoFullScreen() {
	parent.mainFrame.resetProcDlg();
	var status = "toolbar=no,height="+ window.screen.height + ",";
	status += "width=" + (window.screen.width-8) + ",scrollbars=no";
	status += "screenX=0,screenY=0";
	window.open("topology/network/index.jsp", "fullScreenWindow", status);
	parent.mainFrame.zoomProcDlg("out");
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
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="initmenu();">

<form method="post" name="mainForm">
<input type=hidden name="orderflag">
<input type=hidden name="id">
<div id="loading">
	<div class="loading-indicator">
	<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
</div>
<div id="loading-mask" style=""></div>
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=1000>
	<tr>
		<td width="200" valign=top align=center>
			<%=menuTable%>
		</td>
		<td bgcolor="#cedefa" align="left">
<table id="body-container" class="body-container">
	<tr>
		<td width="0"></td>
		<td align="left">
		<table width="100%" border=0 cellpadding=0 cellspacing=0>
			<tr>
			  	<td height=385 bgcolor="#FFFFFF" valign="top">
			  	
					<table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none align=center border=1 algin="center">
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=3>&nbsp;&nbsp;<font color=#ffffff>应用 >> 数据库监视 >> <%=vo.getDbName()%> 详细信息</font></td>
				</tr>					
                  				<tr>
                    					<td>
								<table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none w align=center border=1 algin="center">
                        					<tbody>
                          						<tr>
                								<td width="80%" align="left" valign="top" class=dashLeft>
                									<table style="BORDER-COLLAPSE: collapse" bordercolor=#cedefa  cellpadding=0 rules=none width=100% align=center border=1 algin="center">
                    										<tr bgcolor="#D1DDF5">
                      											<td height="28" colspan="4" valign=center nowrap class=txtGlobal><div align="center" class="txtGlobalBigBold">数据库信息</div></td>
                    										</tr>
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;数据库别名:</td>
                      											<td width="35%"><%=vo.getAlias()%> </td>
													<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;库名称:</td>
                      											<td width="35%"><%=vo.getAlias()%> </td>                    										
                    										</tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;类型:</td>
                      											<td width="35%"><%=dbtye%> </td>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;IP地址:</td>
                      											<td width="35%"><%=vo.getIpAddress()%> </td>
                    										</tr>   
                    											<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;端口:</td>
                      											<td width="35%"><%=vo.getPort()%> </td>
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;管理状态:</td>
                      											<td width="35%"><%=managed%> </td>
                    										</tr>  
                    									   
                    											<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;当前状态:</td>
                      											<td width="85%" colspan=3><%=runstr%> </td>
                    											
                    										</tr>                										
                   																	
                									</table>
                									  
                        <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                        <tr bgcolor="397dbd">
                        <td width="20%">数据库安装的产品信息</td>
                        <td width="80%">
                       <tr bgcolor="#F1F1F1">
            <%  if(versionvalue!=null)
            {
             %>
            <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;服务器版本号：</td>
                 <td width="35%"><%=versionvalue%> </td>
                 <%} %>                  										   
          </tr>
          <tr>
             <%  if(vercomosval!=null)
            {
             %> 
			<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;MySql数据库构建的类型:</td>
              <td width="35%"><%=vercommaval%> </td>   
              <%} %>                   										   
          </tr>
           <tr bgcolor="#F1F1F1">
             <%  if(vercomosval!=null)
            {
             %> 
			<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;MySql数据库构建的操作系统的类型类型:</td>
              <td width="35%"><%=vercomosval%> </td>   
              <%} %>                   										   
          </tr>
           <tr >
            <%  if(verwaittimeval!=null)
            {
             %> 
            <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;服务器关闭非交互连接之前等待活动的秒数:</td>
                 <td width="35%"><%=verwaittimeval%> </td>  
                  <%} %>                 										   
          </tr>
     
                        </td>
                        </tr>
                        </table>
										</td>																					
                								<td width="20%" align="center" valign="middle" class=dashLeft>

											<table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none w align=center border=1 algin="center">
                        								<tbody>
                          									<tr>
                											<td width="400" align="left" valign="middle" class=dashLeft>
                												<table  style="BORDER-COLLAPSE: collapse" bordercolor=#cedefa  cellpadding=0 rules=none width=80% align=center border=1 algin="center" >
                    													<tr class="topNameRight">
                      														<td height="11" rowspan="2">&nbsp;</td>
                      														<td height="11" align="center" bordercolor="#9FB0C4" bgcolor="#FFFFFF" class="txtGlobalBold">今天的可用率</td>
                      														<td height="11" rowspan="2">&nbsp;</td>
                    													</tr>
                    													<tr class="topNameRight">
                      														<td height="30" align="center"><img src="<%=rootPath%>/artist?series_key=<%=chart1%>"></td>
                    													</tr>
                    													<tr class="topNameRight">
                      														<td height="7">&nbsp;</td>
                      														<td height="7">&nbsp;</td>
                      														<td height="7">&nbsp;</td>
                    													</tr>
                												</table>				
											
				              																							  
                											</td>
              											</tr>
            										</tbody>
            										</table>
            									</td>           									
            								</tr>
                						</tbody>
                						</table>
                					</td>
                				</tr>
                				<tr>
                					<td>

					<table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none align=center border=1 algin="center">

                  				<tr>
                    					<td>
            							<table width="100%" border="0" cellpadding="0" cellspacing="0">
              								<tr> 
                								<td width="30" height="22">&nbsp;</td>
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
											<div align="center"><a href="<%=rootPath%>/mysql.do?action=mysqlping&id=<%=vo.getId()%>"><font color="#397dbd">连通率</font></a></div></td>
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian.gif">
                							<div align="center"><a href="<%=rootPath%>/mysql.do?action=mysqlsys&id=<%=vo.getId()%>"><font color="#FFFFFF">配置信息</font></a></div></td>
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
											<div align="center"><a href="<%=rootPath%>/mysql.do?action=mysqlspace&id=<%=vo.getId()%>"><font color="#397dbd">性能情况</font></a></div></td>	
                											<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
                								<div align="center"><a href="<%=rootPath%>/mysql.do?action=mysqlstatus&id=<%=vo.getId()%>"><font color="#397dbd">Status信息</font></a></div></td>	
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
                								<div align="center"><a href="<%=rootPath%>/mysql.do?action=mysqlvariables&id=<%=vo.getId()%>"><font color="#397dbd">Variables信息</font></a></div></td>	
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
											<div align="center"><a href="<%=rootPath%>/mysql.do?action=mysqlevent&id=<%=vo.getId()%>"><font color="#397dbd">告警信息</font></a></div></td>															
                								<td align=right>&nbsp;&nbsp;</td>
              								</tr>
            							</table>
                  <table  cellspacing="1" cellpadding="0" width="80%">
        
			<%
				for(int i=0;i<tableinfo_v.size();i++){
					Hashtable ht = (Hashtable)tableinfo_v.get(i);
					
					String size = ht.get("variable_name").toString();
					String free = ht.get("value").toString();
					if(i==0)
					{
					 size=size+"创建临时表状况(递增)";
					}
					if(i==1)
					{
					 size=size+"创建临时文件的数量";
					}
					if(i==2)
					{
					 size=size+"同Created_tmp_tables递增";
					}
					
			%>
          <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >          
            <td align=left >&nbsp;<%=size%></td>
            <td align=left >&nbsp;<%=free%></td>
          </tr>
          <%}%>
			<%
				for(int i=0;i<tableinfo_v1.size();i++){
					Hashtable ht = (Hashtable)tableinfo_v1.get(i);
					
					String size = ht.get("variable_name").toString();
					String free = ht.get("value").toString();
					if(i==0)
					{
					size=size+"只有这个数值一下的信息会放到内存中，超出的会放到硬盘上";
					}
					
			%>
			 <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >          
            <td align=left >&nbsp;<%=size%></td>
            <td align=left >&nbsp;<%=free%></td>
          </tr>
          <%}%>
			<%
				for(int i=0;i<tableinfo_v2.size();i++){
					Hashtable ht = (Hashtable)tableinfo_v2.get(i);
					
					String size = ht.get("variable_name").toString();
					String free = ht.get("value").toString();
					if(i==0)
					{
					size=size+"打开表的数量";
				   }else
				   {
				    size=size+"打开表的总数量";
				   }
					
			%>
			 <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >          
            <td align=left >&nbsp;<%=size%></td>
            <td align=left >&nbsp;<%=free%></td>
          </tr>
          <%}%>
			<%
				for(int i=0;i<tableinfo_v3.size();i++){
					Hashtable ht = (Hashtable)tableinfo_v3.get(i);
					
					String size = ht.get("variable_name").toString();
					String free = ht.get("value").toString();
					if(i==0)
					{
					size=size+"全索引扫描";
					}
					if(i==1)
					{
					size=size+"使用索引状况";
					}
					if(i==2)
					{
					size=size+"索引扫描中 取数据的次数";
					}
					if(i==3)
					{
					size=size+"索引扫描中 按索引倒序取数据的次数";
					}
					if(i==4)
					{
					size=size+"直接操作数据文件的次数";
					}
					if(i==5)
					{
					size=size+"数据文件扫描时 取出文件的次数";
					}
			%>
          <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=size%></td>
            <td align=left>&nbsp;<%=free%></td>
          </tr>
          <%}%>
          <%   
             for(int i=0;i<status.size();i++)
             {
              Hashtable hs=(Hashtable)status.get(i);
              if(hs.get("variable_name").equals("Compression"))
              {
               String name="Compression";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
          </tr>
          <%} %>
           <%
           if(hs.get("variable_name").equals("Rpl_status"))
              {
               String name="失败安全复制状态(还未使用)";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("Slave_running"))
              {
               String name="如果该服务器是连接到主服务器的从服务器，则该值为ON";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("Ssl_session_cache_mode"))
              {
               String name="用于SSL连接的变量session_cache_mode";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
          <%} %>
          <%   
             for(int i=0;i<variables.size();i++)
             {
              Hashtable hs=(Hashtable)variables.get(i);
              if(hs.get("variable_name").equals("automatic_sp_privileges"))
              {
               String name="automatic_sp_privileges";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
          </tr>
          <%} %>
           <%
           if(hs.get("variable_name").equals("basedir"))
              {
               String name="MySQL安装基准目录";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("character_set_client"))
              {
               String name="来自客户端的语句的字符集";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("character_set_connection"))
              {
               String name="没有字符集导入符的字符串转换";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("character_set_database"))
              {
               String name="默认数据库使用的字符集";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("character_set_filesystem"))
              {
               String name="character_set_filesystem";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("character_set_server"))
              {
               String name="服务器的默认字符集";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("character_set_system"))
              {
               String name="服务器用来保存识别符的字符集";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("character_sets_dir"))
              {
               String name="字符集安装目录";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("collation_connection"))
              {
               String name="连接字符集的校对规则";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("collation_database"))
              {
               String name="默认数据库使用的校对规则";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("collation_server"))
              {
               String name="服务器的默认校对规则";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
           <%
           if(hs.get("variable_name").equals("datadir"))
              {
               String name="MySQL数据目录";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("date_format"))
              {
               String name="date_format(为被使用)";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("datetime_format"))
              {
               String name="datetime_format(为被使用)";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("delay_key_write"))
              {
               String name="使用的DELAY_KEY_WRITE表选项的处理";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("engine_condition_pushdown"))
              {
               String name="适用于NDB的检测";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("flush"))
              {
               String name="flush选项启动mysqld值";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("ft_boolean_syntax"))
              {
               String name="使用IN BOOLEAN MODE执行的布尔全文搜索支持的操作符系列";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("ft_stopword_file"))
              {
               String name="用于读取全文搜索的停止字清单的文件";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("ft_stopword_file"))
              {
               String name="用于读取全文搜索的停止字清单的文件";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("have_archive"))
              {
               String name="mysqld支持ARCHIVE表支持表情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("have_bdb"))
              {
               String name="mysqld支持BDB表情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("have_blackhole_engine"))
              {
               String name="mysqld支持BLACKHOLE表情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("have_compress"))
              {
               String name="是否zlib压缩库适合该服务器";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("have_crypt"))
              {
               String name="是否crypt()系统调用适合该服务器";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("have_csv"))
              {
               String name="mysqld支持ARCHIVE表情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("have_example_engine"))
              {
               String name="mysqld支持EXAMPLE表情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("have_federated_engine"))
              {
               String name="mysqld支持FEDERATED表情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("have_geometry"))
              {
               String name="是否服务器支持空间数据类型";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("have_innodb"))
              {
               String name="mysqld支持InnoDB表情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("have_isam"))
              {
               String name="向后兼容";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("have_ndbcluster"))
              {
               String name="mysqld支持NDB CLUSTER表情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("have_openssl"))
              {
               String name="mysqld支持客户端/服务器协议的SSL(加密)情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("have_query_cache"))
              {
               String name="mysqld支持查询缓存情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("have_raid"))
              {
               String name="mysqld支持RAID选项情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("have_rtree_keys"))
              {
               String name="RTREE索引是否可用";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("have_symlink"))
              {
               String name="是否启用符号链接支持";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("hostname"))
              {
               String name="hostname";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("init_file"))
              {
               String name="启动服务器时用--init-file选项指定的文件名";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("init_slave"))
              {
               String name="SQL线程启动时从服务器应执行该字符串";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("innodb_checksums"))
              {
               String name="InnoDB在所有对磁盘的页面读取上的状态";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("innodb_data_file_path"))
              {
               String name="单独数据文件和它们尺寸的路径";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("innodb_doublewrite"))
              {
               String name="InnoDB存储所有数据情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("innodb_locks_unsafe_for_binlog"))
              {
               String name="InnoDB搜索和索引扫描中关闭下一键锁定";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("innodb_log_archive"))
              {
               String name="日志处理情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("innodb_log_arch_dir"))
              {
               String name="使用日志档案 被完整写入的日志文件所在的目录的归档值";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("innodb_log_group_home_dir"))
              {
               String name="InnoDB日志文件的目录路径";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("innodb_table_locks"))
              {
               String name="InnoDB对表的锁定情况";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("language"))
              {
               String name="错误消息所用语言";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("large_files_support"))
              {
               String name="mysqld编译时是否使用了大文件支持选项";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("large_pages"))
              {
               String name="是否启用了大页面支持";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("license"))
              {
               String name="服务器的许可类型";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("local_infile"))
              {
               String name="是否LOCAL支持LOAD DATA INFILE语句";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("log"))
              {
               String name="是否启用将所有查询记录到常规查询日志中";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("log_bin"))
              {
               String name="是否启用二进制日志";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("log_bin_trust_function_creators"))
              {
               String name="是否可以信任保存的程序的作者不会创建向二进制日志写入不安全事件的程序";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("log_error"))
              {
               String name="错误日志的位置";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("log_slave_updates"))
              {
               String name="是否从服务器从主服务器收到的更新应记入从服务器自己的二进制日志";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("log_slow_queries"))
              {
               String name="是否记录慢查询";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("lower_case_file_system"))
              {
               String name="说明是否数据目录所在的文件系统对文件名的大小写敏感";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("myisam_stats_method"))
              {
               String name="MyISAM表搜集关于索引值分发的统计信息时服务器如何处理NULL值";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("named_pipe"))
              {
               String name="明服务器是否支持命名管道连接";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("new"))
              {
               String name="表示在MySQL 4.0中使用该变量来打开4.1中的一些行为，并用于向后兼容性";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("old_passwords"))
              {
               String name="是否服务器应为MySQL用户账户使用pre-4.1-style密码性";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("pid_file"))
              {
               String name="进程ID (PID)文件的路径名";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("read_only"))
              {
               String name="变量对复制从服务器设置为ON时，服务器是否允许更新";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("relay_log_purge"))
              {
               String name="当不再需要中继日志时禁用或启用自动清空中继日志";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("secure_auth"))
              {
               String name="如果用--secure-auth选项启动了MySQL服务器，是否将阻塞有旧格式(4.1之前)密码的所有账户所发起的连接";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("shared_memory"))
              {
               String name="只用于Windows)服务器是否允许共享内存连接";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("shared_memory_base_name"))
              {
               String name="(只用于Windows)说明服务器是否允许共享内存连接，并为共享内存设置识别符";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("skip_external_locking"))
              {
               String name="mysqld是否使用外部锁定";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("slave_compressed_protocol"))
              {
               String name="如果主、从服务器均支持，确定是否使用从/主压缩协议";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("slave_load_tmpdir"))
              {
               String name="从服务器为复制LOAD DATA INFILE语句创建临时文件的目录名";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("table_type"))
              {
               String name="默认表类型(存储引擎)";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("time_zone"))
              {
               String name="当前的时区";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("tmpdir"))
              {
               String name="保存临时文件和临时表的目录";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("tx_isolation"))
              {
               String name="默认事务隔离级别";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("updatable_views_with_limit"))
              {
               String name="该变量控制如果更新包含LIMIT子句，是否可以在当前表中使用不包含主关键字的视图进行更新";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("version"))
              {
               String name="服务器版本号";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("version_bdb"))
              {
               String name="BDB存储引擎版本";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("version_comment"))
              {
               String name="configure脚本有一个--with-comment选项，当构建MySQL时可以进行注释";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
             <%
           if(hs.get("variable_name").equals("version_compile_machine"))
              {
               String name="MySQL构建的机器或架构的类型";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            <%
           if(hs.get("variable_name").equals("version_compile_os"))
              {
               String name="MySQL构建的操作系统的类型";
               String value=hs.get("value").toString();           
           %>
           <tr  class="othertr" bgcolor="DEEBF7"  onmouseout="this.style.background='#DEEBF7'"  onmouseover="this.style.background='#F0E288'" height=18  >         
            <td align=left>&nbsp;<%=name%></td>
            <td align=left>&nbsp;<%=value%></td>
            <%} %>
            
            
            
            
          <%} %>
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