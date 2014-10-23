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
 //System.out.println("==============666666====="); 
  String rootPath = request.getContextPath();;
  DBVo vo  = (DBVo)request.getAttribute("db");	
 //System.out.println("==============555555555555=====");       
		
		

Hashtable max = (Hashtable) request.getAttribute("max");
Hashtable imgurl = (Hashtable)request.getAttribute("imgurl");
	double avgpingcon = (Double)request.getAttribute("avgpingcon");	
	int percent1 = Double.valueOf(avgpingcon).intValue();
	int percent2 = 100-percent1;
String chart1 = (String)request.getAttribute("chart1");
String dbtye = (String)request.getAttribute("dbtye");
String managed = (String)request.getAttribute("managed");
String runstr = (String)request.getAttribute("runstr");

String basePath=(String)request.getAttribute("basePath");
String dataPath=(String)request.getAttribute("dataPath");
String logerrorPath=(String)request.getAttribute("logerrorPath");
String version=(String)request.getAttribute("version");
String hostOS=(String)request.getAttribute("hostOS");


Hashtable tablesHash=(Hashtable)request.getAttribute("tablesHash");


	String ip = host.getIpAddress();
	String picip = CommonUtil.doip(ip);
	
    	//生成当天平均连通率图形
	CreatePiePicture _cpp = new CreatePiePicture();
	_cpp.createAvgPingPic(picip,avgpingcon); 

///System.out.println("===========9999");
%>
<% String menuTable = (String)request.getAttribute("menuTable");%>
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


<script>
function gzmajax(){
$.ajax({
			type:"GET",
			dataType:"json",
			url:"<%=rootPath%>/serverAjaxManager.ajax?action=ajaxUpdate_availability&tmp=<%=tmp%>&nowtime="+(new Date()),
			success:function(data){
				 $("#pingavg").attr({ src: "<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>pingavg.png?nowtime="+(new Date()), alt: "平均连通率" });
				$("#cpu").attr({ src: "<%=rootPath%>/resource/image/jfreechart/reportimg/<%=picip%>cpu.png?nowtime="+(new Date()), alt: "当前CPU利用率" }); 
			}
		});
}
$(document).ready(function(){
	//$("#testbtn").bind("click",function(){
	//	gzmajax();
	//});
setInterval(gzmajax,60000);
});
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
		<td bgcolor="#cedefa" align="left" valign=top>
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
												<jsp:include page="/topology/includejsp/db_mysql.jsp">
													<jsp:param name="dbName" value="<%=vo.getDbName() %>"/>
													<jsp:param name="alias" value="<%=vo.getAlias() %>"/>
													<jsp:param name="ipAddress" value="<%=vo.getIpAddress() %>"/>
													<jsp:param name="port" value="<%=vo.getPort() %>"/>
													<jsp:param name="dbtye" value="<%=dbtye %>"/>
													<jsp:param name="managed" value="<%=managed %>"/>
													<jsp:param name="runstr" value="<%=runstr %>"/>
													<jsp:param name="version" value="<%=version %>"/>
													<jsp:param name="hostOS" value="<%=hostOS %>"/>
													<jsp:param name="basePath" value="<%=basePath %>"/>
													<jsp:param name="dataPath" value="<%=dataPath %>"/>
													<jsp:param name="logerrorPath" value="<%=logerrorPath %>"/>
													<jsp:param name="picip" value="<%=picip %>"/> 
													<jsp:param name="pingavg" value="<%=avgpingcon %>"/> 
													  
												</jsp:include>
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
                									<div align="center"><a href="<%=rootPath%>/mysql.do?action=mysqlconnect&id=<%=vo.getId()%>"><font color="#FFFFFF">连接信息</font></a></div></td>
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
                									<div align="center"><a href="<%=rootPath%>/mysql.do?action=mysqltables&id=<%=vo.getId()%>"><font color="#397dbd">表信息</font></a></div></td>
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
                									<div align="center"><a href="<%=rootPath%>/mysql.do?action=mysqlsys&id=<%=vo.getId()%>"><font color="#397dbd">配置信息</font></a></div></td>
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
                  <table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none border=1 algin="center">
                  		<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=6>&nbsp;&nbsp;<font color=#ffffff> >> 数据库表信息</font></td>
				</tr>
				<tr>
					<td align=center>
				<table border="0" id="table1" cellpadding="0" cellspacing="1" width="80%">           							
                  <%
                  String[] dbs = vo.getDbName().split(","); 
                  if(dbs != null && dbs.length>0){
                  	for(int i=0;i<dbs.length;i++){
                  		String dbStr = dbs[i];
                  		if(tablesHash.containsKey(dbStr)){
                  			List tableslist = (List)tablesHash.get(dbStr);
                  %>
                  			<TBODY>
					<tr bgcolor="#397DBD" height=28>
					<td colspan=5><%=dbStr%>
					</td>
                  			</tr>
                  <%
                  			
                  		if(tableslist != null && tableslist.size()>0){
                  			for(int k=0;k<tableslist.size();k++){
                  				String[] tables = (String[])tableslist.get(k);
                  				if(tables != null && tables.length ==4){
                  %> 
          				<tr  class="othertr"  <%=onmouseoverstyle%>>
						<td><%=i+1%></td>
            					<td align=center>&nbsp;<%=tables[0]%></td>
            					<td align=center>&nbsp;<%=tables[1]%></td>
            					<td align=center>&nbsp;<%=tables[2]%></td>
            					<td align=center>&nbsp;<%=tables[3]%></td>
         
          				</tr>  			
          	<%
          					}
          				}
          			}
          	%>
          				</TBODY>
          	<%
          		}
          	%>
          		
          		
          	<%
          	}
          	%>   
          				
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
		</td>
	</tr>
</table>
</form>
</BODY>
</HTML>	<