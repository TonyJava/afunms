<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.system.model.TimeShareConfig"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.*"%>
<%
  String rootPath = request.getContextPath();;
  Hashtable jboss_ht = null;

  double jvm_memoryuiltillize=0;
  JBossConfig vo=(JBossConfig)request.getAttribute("vo");
 
 // Integer status=(Integer)request.getAttribute("status");
  
 
  
  jboss_ht = (Hashtable)com.afunms.common.util.ShareData.getJbossdata().get("jboss"+":"+vo.getIpaddress());
  if(jboss_ht == null)jboss_ht = new Hashtable();
  String status_str = (String)jboss_ht.get("status");
  Integer status = Integer.valueOf(status_str);
  if(jboss_ht.get("version")!=null)
  {
	 
  String freememory=(String)jboss_ht.get("freememory");
  String totalmemory=(String)jboss_ht.get("totalmemory");
  String maxmemory=(String)jboss_ht.get("maxmemory");
  int num_totalmemory=Integer.parseInt(totalmemory);
  int num_freememory=Integer.parseInt(freememory);
  jvm_memoryuiltillize=(num_totalmemory-num_freememory)*100/num_totalmemory;
  }
 
  String chart2=ChartCreator.createMeterChart(Math.rint(jvm_memoryuiltillize),"",100,100);
  String conn_name = request.getAttribute("conn_name").toString();
  String connsrc = rootPath+"/resource/image/jfreechart/"+conn_name+".png";
  String wave_name = request.getAttribute("wave_name").toString();
  String wavesrc =  rootPath+"/resource/image/jfreechart/"+wave_name+".png";
  String connrate = request.getAttribute("connrate").toString();
  String timeFormat = "yyyy-MM-dd";
String fdate = (String)request.getAttribute("from_date1");
String tdate = (String)request.getAttribute("to_date1");
String fhour = (String)request.getAttribute("from_hour");
String thour = (String)request.getAttribute("to_hour");

java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
String from_date1 = "";
if (fdate == null ){
	 from_date1=timeFormatter.format(new java.util.Date());
	}else{
		from_date1 = fdate;
		
	}
String to_date1 = "";
if (tdate == null ){
	 to_date1=timeFormatter.format(new java.util.Date());
	}else{
		to_date1 = tdate;
		
	}

String from_hour = "";
if (fhour == null ){
	 from_hour=new java.util.Date().getHours()+"";
	}else{
		from_hour = fhour;
		
	}
String to_hour = "";
if (thour == null ){
	 to_hour=new java.util.Date().getHours()+"";
	}else{
		to_hour = thour;
		
	}
	
//double avgpingcon = (Double)request.getAttribute("avgpingcon");
connrate = connrate.replaceAll("%","");
int percent1 = Double.valueOf(connrate).intValue();
int percent2 = 100-percent1;

double cpuper =0;
cpuper = jvm_memoryuiltillize;
%>
<% String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>

<!--nielin add for timeShareConfig at 2010-01-04 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js" charset="gb2312"></script>
<!--nielin add for timeShareConfig at 2010-01-04 end-->




<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
  
     var chk1 = checkinput("alias","string","名称",30,false);
     var chk2 = checkinput("ipaddress","ip","IP地址",30,false);
     var chk3 = checkinput("username","string","用户名",30,false);
     var chk4 = checkinput("password","string","密码",30,false);
     var chk5 = checkinput("port","string","端口号",30,false);
     
     if(chk1&&chk2&&chk3&&chk4&&chk5)
     {
     
        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
        //msg.style.display="block";
        mainForm.action = "<%=rootPath%>/jboss.do?action=add";
        mainForm.submit();
     }  
       // mainForm.submit();
 });	
	
});
//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
	
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function setReceiver(eventId){
	var event = document.getElementById(eventId);
	return CreateWindow('<%=rootPath%>/user.do?action=setReceiver&event='+event.id+'&value='+event.value);
}
//-- nielin modify at 2010-01-04 end ----------------


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
	timeShareConfiginit(); // nielin add for time-sharing at 2010-01-04
}

</script>

<script>
//-- nielin add for timeShareConfig 2010-01-03 end-------------------------------------------------------------
	/*
	* 此方法用于短信分时详细信息
	* 需引入 /application/resource/js/timeShareConfigdiv.js 
	*/
	//接受用户的列表
	var action = "<%=rootPath%>/user.do?action=setReceiver";
	// 获取短信分时详细信息的div
	function timeShareConfiginit(){
		var rowNum = document.getElementById("rowNum");
		rowNum.value = "0";
		// 获取设备或服务的分时数据列表,
		var timeShareConfigs = new Array();
		var smsConfigs = new Array();
		var phoneConfigs = new Array();
		<%	
			List timeShareConfigList = (List) request.getAttribute("timeShareConfigList");
			if(timeShareConfigList!=null&&timeShareConfigList.size()>=0){
			for(int i = 0 ; i < timeShareConfigList.size(); i++){	        
	            TimeShareConfig timeShareConfig = (TimeShareConfig) timeShareConfigList.get(i);
	            int timeShareConfigId = timeShareConfig.getId();
	            String timeShareType = timeShareConfig.getTimeShareType();
	            String timeShareConfigbeginTime = timeShareConfig.getBeginTime();
	            String timeShareConfigendTime = timeShareConfig.getEndTime();
	            String timeShareConfiguserIds = timeShareConfig.getUserIds();
	            
	    %>
	            timeShareConfigs.push({
	                timeShareConfigId:"<%=timeShareConfigId%>",
	                timeShareType:"<%=timeShareType%>",
	                beginTime:"<%=timeShareConfigbeginTime%>",
	                endTime:"<%=timeShareConfigendTime%>",
	                userIds:"<%=timeShareConfiguserIds%>"
	            });
	    <%
	        }
	        }
	    %>   
	    for(var i = 0; i< timeShareConfigs.length; i++){
	    	var item = timeShareConfigs[i];
	    	if(item.timeShareType=="sms"){
	    		smsConfigs.push({
	                timeShareConfigId:item.timeShareConfigId,
	                timeShareType:item.timeShareType,
	                beginTime:item.beginTime,
	                endTime:item.endTime,
	                userIds:item.userIds
	            });
	    	}
	    	if(item.timeShareType=="phone"){
	    		phoneConfigs.push({
	                timeShareConfigId:item.timeShareConfigId,
	                timeShareType:item.timeShareType,
	                beginTime:item.beginTime,
	                endTime:item.endTime,
	                userIds:item.userIds
	            });
	    	}
	    }
		timeShareConfig("smsConfigTable",smsConfigs);
		timeShareConfig("phoneConfigTable",phoneConfigs);
	}
//---- nielin add for timeShareConfig 2010-01-03 end-------------------------------------------------------------------------
</script>


</head>
<body id="body" class="body" onload="initmenu();">

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

	<form id="mainForm" method="post" name="mainForm">
		
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
							<td class="td-container-main-add">
								<table id="container-main-add" class="container-main-add">
									<tr>
										<td>
											<table id="add-content" class="add-content">
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">应用 >> 中间件管理 >> JBOSS监视添加</td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        										
				        												<table border="0" id="table1" cellpadding="0" cellspacing="1"
																	width="100%">
																<TBODY>
			<tr>
                								<td width="80%" align="left" valign="top" class=dashLeft>
                									<table>
                    										
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;名称:</td>
                      											<td width="35%"><%=vo.getAlias() %></td>
													            <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;IP地址:</td>
                      											<td ><%=vo.getIpaddress() %> </td>         										
                    										</tr>
                    										<tr>
                    										  <td width="10%" height="26" align="left" nowrap class=txtGlobal>&nbsp;状态:</td>
                      											<td width="35%"><img src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(status)%>" border="0" ></td>
                      											
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;端口号:</td>
                      											<td ><%=vo.getPort() %> </td>
                    										</tr>   
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JBOSS版本:</td>
                      											
                      											<td width="35%">
                      											<%if(jboss_ht.get("version")!=null)
                      											{ %>
                      											<%=jboss_ht.get("version")%> 
                      											<%} %>
                      											</td>
                      											
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;版本时间:</td>
                    											
                      											<td >
                      											<%if(jboss_ht.get("date")!=null)
                    											{ %>
                      											<%=jboss_ht.get("date")%> 
                      											<%} %>
                      											</td>
                      											
                    										</tr> 
                    											
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JBOSS版本名称:</td>
                      											
                      											<td width="35%">
                      											<%if(jboss_ht.get("versionname")!=null)
                      											{ %>
                      											<%=jboss_ht.get("versionname")%> 
                      											<%} %>
                      											</td>
                      											
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;版本时间:</td>
                    											
                      											<td >
                      											<%if(jboss_ht.get("builton")!=null)
                    											{ %>
                      											<%=jboss_ht.get("builton")%> 
                      											<%} %>
                      											</td>
                      											
                    										</tr> 
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;服务启动时间:</td>
                      											
                      											<td width="35%">
                      											<%if(jboss_ht.get("startdate")!=null)
                      											{ %>
                      											<%=jboss_ht.get("startdate")%> 
                      											<%} %>
                      											</td>
                      											
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;主机名:</td>
                    											
                      											<td >
                      											<%if(jboss_ht.get("host")!=null)
                    											{ %>
                      											<%=jboss_ht.get("host")%> 
                      											<%} %>
                      											</td>
                      											
                    										</tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;安装目录:</td>
                      											
                      											<td width="35%">
                      											<%if(jboss_ht.get("baselocation")!=null)
                      											{ %>
                      											<%=jboss_ht.get("baselocation")%>
                      											<%} %>
                      											 </td>
                      											
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;总安装目录:</td>
                    											
                      											<td >
                      											<%if(jboss_ht.get("baselocationlocal")!=null)
                    											{ %>
                      											<%=jboss_ht.get("baselocationlocal")%>
                      											<%} %>
                      											 </td>
                      											
                    										</tr> 
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;运行配置:</td>
                      											
                      											<td width="35%">
                      											<%if(jboss_ht.get("runconfig")!=null)
                      											{ %>
                      											&nbsp;
                      											<%=jboss_ht.get("runconfig")%>
                      											<%} %>
                      											 </td>
                      											
                    											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;#线程:</td>
                    											
                      											<td>
                      											<%if(jboss_ht.get("threads")!=null)
                    											{ %>
                    											&nbsp;
                      											<%=jboss_ht.get("threads")%>
                      											<%} %>
                      											 </td>
                      											
                    										</tr>
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;操作系统:</td>
                      											<%if(jboss_ht.get("os")!=null)
                      											{ %>
                      											<td width="35%"><%=jboss_ht.get("os")%> </td>
                      											<%} %>
                      											
                      											
                    										</tr>
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JVM 版本号:</td>
                      											<%if(jboss_ht.get("jvmversion")!=null)
                      											{ %>
                      											<td width="35%"><%=jboss_ht.get("jvmversion")%> </td>
                      											<%} %>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal></td>
                      											<td ></td>	
                    										</tr> 
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JVM 版本名称:</td>
                      											<%if(jboss_ht.get("jvmname")!=null)
                      											{ %>
                      											<td ><%=jboss_ht.get("jvmname")%> </td>
                      											<%} %>
                    										</tr>
                    										<tr bgcolor="#F1F1F1">
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;上一次轮询:</td>
                      											<td width="35%"><%=request.getAttribute("lasttime") %> </td>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal></td>
                      											<td ></td>		
                    										</tr> 
                    										<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;下一次轮询:</td>
                      											<td ><%=request.getAttribute("nexttime") %> </td>
                    										</tr>                     										            										
                   												<tr></tr>
                   												<tr></tr>					
                									</table>
                				             
										</td>	
										
										<td width="20%" align="center" valign="middle">

											<table width="100%" cellPadding=0 algin="center">
                        								<tbody>
                          									<tr>
                											<td   align="center" valign="middle">
                														
			   											<table width="100%" cellPadding=0  align=center>
															<tr>
																									<td width="100%" align="center"> 
																										<div id="flashcontent00">
																															<strong>You need to upgrade your Flash Player</strong>
																														</div>
																														<script type="text/javascript">
																												var so = new SWFObject("<%=rootPath%>/flex/Pie_Component.swf?percent1=<%=percent1%>&percentStr1=可用&percent2=<%=percent2%>&percentStr2=不可用", "Pie_Component", "160", "160", "8", "#ffffff");
																												so.write("flashcontent00");
																											</script>				
																									</td>
																								</tr>  
																								<tr>
																									<td height="7" align=center><img src="<%=rootPath%>/resource/image/Loading_2.gif"></td>
																								</tr>
                </table>
                <br>
				<table cellPadding=0 width="100%" align=center>
                      			<tr>
													                    	<td align="center" valign="middle" height='30'>
																	<div id="flashcontent01">
																							<strong>You need to upgrade your Flash Player</strong>
																						</div>
																							<script type="text/javascript">
																							var so = new SWFObject("<%=rootPath%>/flex/DHCCGauge.swf?percent=<%=cpuper%>&title=JVM利用率", "Pie_Component1", "160", "160", "8", "#ffffff");
																							so.write("flashcontent01");
																							</script>                       
														                	</td>
													                    </tr>
													                    <tr>
													                    	<td align=center><img src="<%=rootPath%>/resource/image/Loading.gif"></td>
													                    </tr>
                      			
                      			
                      			
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
               <table  borderColor=#cedefa  >
			    <tr>
                    <td width="100%" height="25" bgcolor="#D1DDF5" class="txtGlobalBold" style="HEIGHT: 25px">
                    <div align="left">JBOSS连通曲线(从<%=from_date1%> <%=from_hour%>点 到 <%=to_date1%> <%=to_hour%>点)</div></td>                    
                    <td width="50%" bgcolor="#397DBD" class="txtGlobalBold" style="HEIGHT: 25px"></td>
			    </tr>
				<tr>
				<%if(wavesrc!=null)
				{ %>
				<td align="left" width="100%"><img src="<%=wavesrc%>">
				<%} %>
				</td>
				</tr>									
			</table>
		   			</td>
		   		</tr>					                                  					  
                  					
              <tr>
                    <td width="80%" align="left"  class=dashLeft>		
                  			<table style="BORDER-COLLAPSE: collapse" bordercolor=#cedefa  cellpadding=0 rules=none width=100% align=center border=1 algin="center">
                    				<tr bgcolor="#D1DDF5">
                    				<%if(jboss_ht.get("ajp")!=null)
                    				{ %>
                      							<td height="28" colspan="7" valign=left nowrap class=txtGlobal><div align="left" class="txtGlobalBigBold"><%=jboss_ht.get("ajp") %></div></td>
                    				<%} %>
                    				</tr>
                    				<tr bgcolor="#F1F1F1">
                      							<td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;最大线程数:</td>
                      							<%if(jboss_ht.get("ajp_maxthreads")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("ajp_maxthreads") %></td>
                      							<%} %>
											    <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;当前线程数:</td>
											    <%if(jboss_ht.get("ajp_thrcount")!=null)
											    { %>
                      							<td width="20%"><%=jboss_ht.get("ajp_thrcount") %> </td>
                      							<%} %>
                      							<td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;当前线程忙:</td>
                      							<%if(jboss_ht.get("ajp_thrbusy")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("ajp_thrbusy") %> </td>
                      							<%} %>         										
                    				</tr>
                    				
                    				<tr >
                      							<td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;最大处理时间:</td>
                      							<%if(jboss_ht.get("ajp_maxtime")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("ajp_maxtime") %></td>
                      							<%} %>
											    <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;处理时间:</td>
											    <%if(jboss_ht.get("ajp_processtime")!=null)
											    { %>
                      							<td width="20%"><%=jboss_ht.get("ajp_processtime") %> </td>
                      							<%} %>
                      							<td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;要求数:</td>
                      							<%if(jboss_ht.get("ajp_requestcount")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("ajp_requestcount") %> </td>
                      							<%} %>        										
                    				</tr>
                    				<tr bgcolor="#F1F1F1">
                      							<td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;错误数:</td>
                      							<%if(jboss_ht.get("ajp_errorcount")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("ajp_errorcount") %></td>
                      							<%} %>
											    <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;接收字节大小:</td>
											    <%if(jboss_ht.get("ajp_bytereceived")!=null)
											    { %>
                      							<td width="20%"><%=jboss_ht.get("ajp_bytereceived") %> </td>
                      							<%} %>
                      							<td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;发送字节大小:</td>
                      							<%if(jboss_ht.get("ajp_bytessent")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("ajp_bytessent") %> </td> 
                      							<%} %>        										
                    				</tr>
                    				</table>
                  	</td>
              </tr>    		
              <tr>
                    <td width="80%" align="left"  class=dashLeft>		
                  			<table style="BORDER-COLLAPSE: collapse" bordercolor=#cedefa  cellpadding=0 rules=none width=100% align=center border=1 algin="center">
                    				<tr bgcolor="#D1DDF5">
                    				<%if(jboss_ht.get("http")!=null)
                    				{ %>
                      							<td height="28" colspan="7" valign=left nowrap class=txtGlobal><div align="left" class="txtGlobalBigBold"><%=jboss_ht.get("http") %></div></td>
                    				<%} %>
                    				</tr>
                    				<tr bgcolor="#F1F1F1">
                      							<td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;最大线程数:</td>
                      							<%if(jboss_ht.get("http_maxthreads")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("http_maxthreads") %></td>
                      							<%} %>
											    <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;当前线程数:</td>
											    <%if(jboss_ht.get("http_thrcount")!=null)
											    { %>
                      							<td width="20%"><%=jboss_ht.get("http_thrcount") %> </td>
                      							<%} %>
                      							<td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;当前线程忙:</td>
                      							<%if(jboss_ht.get("http_thrbusy")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("http_thrbusy") %> </td> 
                      							<%} %>        										
                    				</tr>
                    				
                    				<tr >
                      							<td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;最大处理时间:</td>
                      							<%if(jboss_ht.get("http_maxtime")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("http_maxtime") %></td>
                      							<%} %>
											    <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;处理时间:</td>
											    <%if(jboss_ht.get("http_processtime")!=null)
											    { %>
                      							<td width="20%"><%=jboss_ht.get("http_processtime") %> </td>
                      							<%} %>
                      							<td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;要求数:</td>
                      							<%if(jboss_ht.get("http_requestcount")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("http_requestcount") %> </td>  
                      							<%} %>      										
                    				</tr>
                    				<tr bgcolor="#F1F1F1">
                      							<td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;错误数:</td>
                      							<%if(jboss_ht.get("http_errorcount")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("http_errorcount") %></td>
                      							<%} %>
											    <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;接收字节大小:</td>
											    <%if(jboss_ht.get("http_bytereceived")!=null)
											    { %>
                      							<td width="20%"><%=jboss_ht.get("http_bytereceived") %> </td>
                      							<%} %>
                      							<td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;发送字节大小:</td>
                      							<%if(jboss_ht.get("http_bytessent")!=null)
                      							{ %>
                      							<td width="20%"><%=jboss_ht.get("http_bytessent") %> </td>  
                      							<%} %>       										
                    				</tr>
                    				</table>
                  	</td>
              </tr>    								
							
						</TABLE>
										 							
										 							
				        										</td>
				        									</tr>
				        								</table>
				        							</td>
				        						</tr>
				        						<tr>
				        							<td>
				        								<table id="detail-content-footer" class="detail-content-footer">
				        									<tr>
				        										<td>
				        											<table width="100%" border="0" cellspacing="0" cellpadding="0">
											                  			<tr>
											                    			<td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
											                    			<td></td>
											                    			<td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
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
									<tr>
										<td>
											
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
</body>
</HTML>