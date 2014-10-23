<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="com.afunms.system.model.TimeShareConfig"%>
<%@page import="com.afunms.detail.service.jbossInfo.JBossInfoService"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.report.jfree.ChartCreator"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.polling.*"%>

<%
    String menuTable = (String) request.getAttribute("menuTable");
    String rootPath = request.getContextPath();
    JBossConfig jBossConfig = (JBossConfig) request.getAttribute("jBossConfig");
    
    Double pingAvg = Double.valueOf((String) request.getAttribute("pingAvg"));
    Integer status = (Integer) request.getAttribute("status");
    String version = (String) request.getAttribute("version");
    String date = (String) request.getAttribute("date");
    String versionname = (String) request.getAttribute("versionname");
    String builton = (String) request.getAttribute("builton");
    String startdate = (String) request.getAttribute("startdate");
    String host = (String) request.getAttribute("host");
    String baselocation = (String) request.getAttribute("baselocation");
    String baselocationlocal = (String) request.getAttribute("baselocationlocal");
    String runconfig = (String) request.getAttribute("runconfig");
    String threads = (String) request.getAttribute("threads");
    String os = (String) request.getAttribute("os");
    String jvmversion = (String) request.getAttribute("jvmversion");
    String jvmname = (String) request.getAttribute("jvmname");

    String ajp = (String) request.getAttribute("ajp");
    String ajp_maxthreads = (String) request.getAttribute("ajp_maxthreads");
    String ajp_thrcount = (String) request.getAttribute("ajp_thrcount");
    String ajp_thrbusy = (String) request.getAttribute("ajp_thrbusy");
    String ajp_maxtime = (String) request.getAttribute("ajp_maxtime");
    String ajp_processtime = (String) request.getAttribute("ajp_processtime");
    String ajp_requestcount = (String) request.getAttribute("ajp_requestcount");
    String ajp_errorcount = (String) request.getAttribute("ajp_errorcount");
    String ajp_bytereceived = (String) request.getAttribute("ajp_bytereceived");
    String ajp_bytessent = (String) request.getAttribute("ajp_bytessent");
%>
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
<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>

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

</head>
<body id="body" class="body" onload="initmenu();">
<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
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
														<table width="100%" background="<%=rootPath%>/common/images/right_t_02.jpg" cellspacing="0" cellpadding="0">
	<tr>
	 	<td align="left"><div class="noPrint"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></div></td>
		<td class="layout_title"><font size=2><b>设备详细信息</b></font></td>
	 	<td align="right"><div class="noPrint"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></div></td>
	</tr>
	<tr>
		<td width="20%" height="26" align="left" nowrap  class=txtGlobal>&nbsp;名称:
										        <span id="lable"><%=jBossConfig.getAlias() %></span> </td>
        <td width="20%" height="26" align="left" nowrap class=txtGlobal >&nbsp;状态:
						 					<span id="sysname">
 <td width="35%"><img src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(status)%>" border="0" ></td></span></td>
        <td width="20%" height="26" align=left valign=middle nowrap class=txtGlobal>&nbsp;IP地址:
                                            <span id="sysname"><%=jBossConfig.getIpaddress()%></span>
											</TD>
	</tr>
											        	</table>
				        							</td>
				        						</tr>
				        						
				        						
				        						<tr>
				        							<td>
				        								<table id="detail-content-body" class="detail-content-body">
				        									<tr>
				        										<td>
				        										    <table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
			                                                            <tr>
                								                            <td width="80%" align="left" valign="top" class=dashLeft>
                									                            <table>
                    										                        <tr>
                                              									        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;名称:</td>
                                              											<td width="35%"><%=jBossConfig.getAlias() %></td>
                        													            <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;IP地址:</td>
                                              											<td ><%=jBossConfig.getIpaddress() %> </td>         										
                    										                        </tr>
                    										                        <tr bgcolor="#ECECEC">
                    										                            <td width="10%" height="26" align="left" nowrap class=txtGlobal>&nbsp;状态:</td>
                      											                        <td width="35%"><img src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(status)%>" border="0" ></td>
                    											                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;端口号:</td>
                      											                        <td ><%=jBossConfig.getPort() %> </td>
                    										                        </tr>   
                    										                        <tr>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JBOSS版本:</td>
                      											                        <td width="35%"><%=version%></td>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;版本时间:</td>
                    											                        <td ><%=date%></td>
                    										                        </tr>
                                                                                    <tr bgcolor="#ECECEC">
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JBOSS版本名称:</td>
                      											                        <td width="35%"><%=versionname%></td>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;版本时间:</td>
                    											                        <td ><%=builton%></td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;服务启动时间:</td>
                      											                        <td width="35%"><%=startdate%></td>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;主机名:</td>
                    											                        <td ><%=host%></td>
            										                                </tr>
                    										                        <tr bgcolor="#ECECEC">
                      											                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;安装目录:</td>
                      											                        <td width="35%"><%=baselocation%></td>
                    											                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;总安装目录:</td>
                      											                        <td ><%=baselocationlocal%></td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;运行配置:</td>
                      											                        <td width="35%"><%=runconfig%></td>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;#线程:</td>
                      											                        <td><%=threads%></td>
                                                                                    </tr>
                                                                                    <tr bgcolor="#ECECEC" >
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;操作系统:</td>
                      											                        <td width="35%"><%=os%> </td>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JVM 版本号:</td>
                      											                        <td width="35%"><%=jvmversion%> </td>
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal></td>
                      											                        <td ></td>
                                                                                    </tr>
                                                                                    <tr bgcolor="#ECECEC">
                                                                                        <td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;JVM 版本名称:</td>
                      											                        <td ><%=jvmname%> </td>
                                                                                        <td></td>
                                                                                        <td></td>
                                                                                    </tr>
                                                                                    <tr>
                                                                                    <td  align="left" colspan=4>
                                                                                <table>
                                                                                    <tr >
                                                                                        <td height="26" colspan="7" valign="center" nowrap class=txtGlobal><div align="left" class="txtGlobalBigBold">&nbsp;<%=ajp %></div></td>
                    				                                                </tr>
            				                                                        <tr bgcolor="#ECECEC">
                      							                                        <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;最大线程数:</td>
                      							                                        <td width="20%"><%=ajp_maxthreads %></td>
											                                            <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;当前线程数:</td>
                      							                                        <td width="20%"><%=ajp_thrcount %> </td>
                      							                                        <td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;当前线程忙:</td>
                      							                                        <td width="20%"><%=ajp_thrbusy %> </td>
                    				                                                </tr>
                    				                                                <tr >
                      							                                        <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;最大处理时间:</td>
                      							                                        <td width="20%"><%=ajp_maxtime%></td>
											                                            <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;处理时间:</td>
                      							                                        <td width="20%"><%=ajp_processtime %> </td>
                      							                                        <td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;要求数:</td>
                      							                                        <td width="20%"><%=ajp_requestcount %> </td>
                    				                                                </tr>
                    				                                                <tr bgcolor="#ECECEC">
                      							                                        <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;错误数:</td>
                      							                                        <td width="20%"><%=ajp_errorcount %></td>
											                                            <td width="13%" height="26" align="left" nowrap class=txtGlobal>&nbsp;接收字节大小:</td>
                      							                                        <td width="20%"><%=ajp_bytereceived %> </td>
                      							                                        <td width="14%" height="26" align="left" nowrap class=txtGlobal>&nbsp;发送字节大小:</td>
                      							                                        <td width="20%"><%=ajp_bytessent %> </td> 
                    				                                                </tr>
                                                                                </table>
                                                                            </td>
                                                                            </tr>
                									                            </table>
										                                    </td>
                                                                            <td width="20%" align="center" valign="middle">
                                                                                <table width="100%" cellPadding=0 algin="center">
                                                                                    <tr>
                                                                                        <td   align="center" valign="middle">
                                                                                            <table width="100%" cellPadding=0  align=center>
                                                                                                <tr>
																									<td width="100%" align="center"> 
																								        <div id="flashcontent00">
                                                                                                            <strong>You need to upgrade your Flash Player</strong>
																										</div>
                                                                                                        <script type="text/javascript">
																											var so = new SWFObject("<%=rootPath%>/flex/Pie_Component.swf?percent1=<%=pingAvg%>&percentStr1=可用&percent2=<%=100 - pingAvg%>&percentStr2=不可用", "Pie_Component", "160", "160", "8", "#ffffff");
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
                                                                                                        <div id="flashcontent01"><strong>You need to upgrade your Flash Player</strong></div>
                                                                                                        <script type="text/javascript">
                                                                                                            var so = new SWFObject("<%=rootPath%>/flex/DHCCGauge.swf?percent=<%=0%>&title=JVM利用率", "Pie_Component1", "160", "160", "8", "#ffffff");
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
                                                                                </table>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            
                                                                        </tr>
                                                                    </table>
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
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </form>
    
    
    
    <script>
		Ext.onReady(function()
		{  
		
		setTimeout(function(){
			        Ext.get('loading').remove();
			        Ext.get('loading-mask').fadeOut({remove:true});
			    }, 250);
			    Ext.get("process").on("click",function(){
		  
		  Ext.MessageBox.wait('数据加载中，请稍后.. ');   
		  mainForm.action = "<%=rootPath%>/jboss.do?action=sychronizeData&id=<%=jBossConfig.getId()%>";
		  mainForm.submit();
		 });    
		});
	</script>
</body>
</HTML>