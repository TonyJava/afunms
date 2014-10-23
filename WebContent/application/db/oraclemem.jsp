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
String[] sysItem3={"HOST_NAME","DBNAME","VERSION","INSTANCE_NAME","STATUS","STARTUP_TIME","ARCHIVER"};
String[] sysItemch3={"主机名称","DB 名称","DB 版本","例程名","例程状态","例程开始时间","归档日志模式"};

String[] sysItem1={"shared pool","large pool","buffer cache","java pool"};
String[] sysItemch1={"共享池","大型池","缓冲区高速缓存","Java池"};

String[] sysItem2={"aggregate PGA target parameter","total PGA allocated","maximum PGA allocated","cache hit percentage","bytes processed","extra bytes read/written","recompute count (total)"};
String[] sysItemch2={"总计PGA目标","分配的当前PGA","分配的最大PGA（自启动以来）","高速缓存命中百分比","已处理字节","额外读写字节","重计算总数"};

Hashtable oramem = (Hashtable)request.getAttribute("oramem");
Hashtable orasys = (Hashtable)request.getAttribute("sysvalue");
 			  	   
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
<input type="hidden" name="sid" id="sid">
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
		<td bgcolor="#cedefa" align="left" valign="top">
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
                      											<td width="35%"><%=vo.getAlias()%> [<a href="<%=rootPath%>/network.do?action=ready_edit&id=<%=vo.getId()%>&ipaddress=<%=vo.getIpAddress()%>"><img src="<%=rootPath%>/resource/image/editicon.gif" border=0>修改</a>]</td>
													<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;库名称:</td>
                      											<td width="35%"><%=vo.getDbName()%> </td>                    										
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
                    										</tr>   
                    											<tr>
                      											<td width="15%" height="26" align="left" nowrap class=txtGlobal>&nbsp;当前状态:</td>
                      											<td width="85%" colspan=3><%=runstr%> </td>
                    											
                    										</tr>                										
                   																	
                									</table>
                									                      <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                  <%
                  if(orasys!=null){                          
                        for(int i=0; i<sysItem3.length; i++){
                     	    String key = sysItemch3[i];
                            String value = "";
                            if(orasys.get(sysItem3[i])!=null){
                               value = (String)orasys.get(sysItem3[i]);
                            }
                            String key1="",value1="",key2="",value2="";
                            if (i+1<sysItem3.length){
                            	key1 = sysItemch3[i+1];
                            	if (orasys.get(sysItem3[i+1])!=null){
                            	   value1 = (String)orasys.get(sysItem3[i+1]);	
                            	}
                            }
                            if(!value.equals("")&&!value1.equals("")){ %>
                       <tr bgcolor="DEEBF7"> 
                       <td width="17%"><%=key%></td>
                       <td width="43%"><%=value%></td>
                       <td width="18%"><%=key1%></td>
                       <td width="22%"><%=value1%></td>                       
                       </tr>
                           <%}else if(!value.equals("")){
                           %>
                       <tr bgcolor="DEEBF7"> 
                       <td width="17%"><%=key%></td>
                       <td width="43%"><%=value%></td>
                       <td width="18%">&nbsp;</td>
                       <td width="22%">&nbsp;</td>                                                  
                           <%
                           }
                           i=i+1;
                  	}                  	
                  }                  
 %>                            
                        </table>
                        <%
                       System.out.println("-------------------------"); 
                  	Vector banners = (Vector)orasys.get("BANNER");
                  	if(banners != null && banners.size()>0){ 
                  	                      
                        %>
                        <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                        <tr bgcolor="DEEBF7">
                        <td width="20%">数据库安装的产品信息</td>
                        <td width="80%">
                        <%
                  		for(int i=0;i<banners.size();i++){
                  		%>
                  		                  		
                  			<%=banners.get(i).toString()%>
                  			
                  			<br>
                  			<%
                  		}                                               
                        }                        
                        %>
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
											<div align="center"><a href="<%=rootPath%>/oracle.do?action=oracleping&id=<%=vo.getId()%>"><font color="#397dbd">连通率</font></a></div></td>
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
											<div align="center"><a href="<%=rootPath%>/oracle.do?action=oraclespace&id=<%=vo.getId()%>"><font color="#397dbd">表空间</font></a></div></td>	
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
											<div align="center"><a href="<%=rootPath%>/oracle.do?action=oraclesession&id=<%=vo.getId()%>"><font color="#397dbd">Session信息</font></a></div></td>
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
											<div align="center"><a href="<%=rootPath%>/oracle.do?action=oraclerollback&id=<%=vo.getId()%>"><font color="#397dbd">回滚段信息</font></a></div></td>	
										<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
											<div align="center"><a href="<%=rootPath%>/oracle.do?action=oraclelock&id=<%=vo.getId()%>"><font color="#397dbd">锁信息</font></a></div></td>
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian.gif">
											<div align="center"><a href="<%=rootPath%>/oracle.do?action=oraclemem&id=<%=vo.getId()%>"><font color="#FFFFFF">内存配置</font></a></div></td>	
                								<td width="80" height="22" background="<%=rootPath%>/resource/image/anjian_1.gif">
											<div align="center"><a href="<%=rootPath%>/oracle.do?action=oracleevent&id=<%=vo.getId()%>"><font color="#397dbd">事件信息</font></a></div></td>															
                								<td align=right>&nbsp;&nbsp;</td>
              								</tr>
            							</table>
                      <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                                <tr  class="firsttr">
          	<td colspan=4 align=left>&nbsp;SGA信息:</td>
           </tr>
                  <%
                  if(oramem!=null){
                        for(int i=0; i<sysItem1.length; i++){
                     	    String key = sysItemch1[i];
                            String value = "";
                            if(oramem.get(sysItem1[i])!=null){
                               value = (String)oramem.get(sysItem1[i]);
                            }
                            String key1="",value1="",key2="",value2="";
                            if (i+1<sysItem1.length){
                            	key1 = sysItemch1[i+1];
                            	if (oramem.get(sysItem1[i+1])!=null){
                            	   value1 = (String)oramem.get(sysItem1[i+1]);	
                            	}
                            }
                            if(!value.equals("")&&!value1.equals("")){ %>
                       <tr bgcolor="DEEBF7"> 
                       <td width="30%"><%=key%></td>
                       <td width="20%"><%=value%>MB</td>
                       <td width="30%"><%=key1%></td>
                       <td width="20%"><%=value1%>MB</td>                       
                       </tr>
                           <%}else if(!value.equals("")){
                           %>
                       <tr bgcolor="DEEBF7"> 
                       <td width="30%"><%=key%></td>
                       <td width="20%"><%=value%>MB</td>
                       <td width="30%">&nbsp;</td>
                       <td width="20%">&nbsp;</td>                                                  
                           <%
                           }
                           i=i+1;
                  	}                  	
                  }                  
 %>                            
                        </table>


                      <br>
                      <table width="100%" border="0" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF">
                      	<tr  class="firsttr">
          			<td align=left colspan=4 align=left>&nbsp;PGA信息:</td>
           		</tr>
                  <%
                  if(oramem!=null){
                        for(int i=0; i<sysItem2.length; i++){
                     	    String key = sysItemch2[i];
                            String value = "";
                            if(oramem.get(sysItem2[i])!=null){
                               value = (String)oramem.get(sysItem2[i]);
                            }
                            String key1="",value1="",key2="",value2="";
                            if (i+1<sysItem2.length){
                            	key1 = sysItemch2[i+1];
                            	if (oramem.get(sysItem2[i+1])!=null){
                            	   value1 = (String)oramem.get(sysItem2[i+1]);	
                            	}
                            }
                            if(!value.equals("")&&!value1.equals("")){ %>
                       <tr bgcolor="DEEBF7"> 
                       <td width="30%"><%=key%></td>
                       <td width="30%"><%=value%></td>
                       <td width="20%"><%=key1%></td>
                       <td width="30%"><%=value1%></td>                       
                       </tr>
                           <%}else if(!value.equals("")){
                           %>
                       <tr bgcolor="DEEBF7"> 
                       <td width="20%"><%=key%></td>
                       <td width="30%"><%=value%></td>
                       <td width="20%">&nbsp;</td>
                       <td width="30%">&nbsp;</td>                                                  
                           <%
                           }
                           i=i+1;
                  	}                  	
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
</form>
</BODY>
</HTML>