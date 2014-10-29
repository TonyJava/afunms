<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.config.dao.DistrictDao"%>
<%@page import="com.afunms.config.model.DistrictConfig"%>



<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  String ipaddress = (String)request.getAttribute("ipaddress");
  String nodeid = (String)request.getAttribute("nodeid");
  
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

<!--nielin add for hostservicegroupconfiguration at 2010-08-19 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/hostservicegroupconfiguration.js" charset="gb2312"></script>
<!--nielin add for hostservicegroupconfiguration at 2010-08-19 end-->




<script language="JavaScript" type="text/javascript">


  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
	
 Ext.get("process").on("click",function(){
  			var chk = checkinput("name","string","主机服务组名称",200,false);
  			if(chk){
  				 Ext.MessageBox.wait('数据加载中，请稍后.. ');
        	mainForm.action = "<%=rootPath%>/hostservicegroup.do?action=add";
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

function chooseHostService(eventId){
	return CreateWindow('<%=rootPath%>/hostservicegroup.do?action=chooseHostService&ipaddress=' + "<%=ipaddress%>" + '&nodeid=' + "<%=nodeid%>" + '&eventId='+eventId);
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
}

</script>
<script type="text/javascript" src="<%=rootPath%>/topology/network/js/wincloseoverride.js"></script>
<style>
<!--
body{
background-image: url(${pageContext.request.contextPath}/common/images/bg.jpg);
TEXT-ALIGN: center; 
}
-->
</style>

</head>
<body id="body" style="padding:5px;" class="body" onload="initmenu();">



	<form id="mainForm" method="post" name="mainForm">
		
		<table>
			<tr>
				<td class="td-container-main">
					<table>
						<tr>
							<td class="td-container-main-add">
								<table >
									<tr>
										<td>
											<table>
												<tr>
													<td>
														<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title"> 应用 &gt;&gt; 主机服务组管理 &gt;&gt; 添加 </td>
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
				        												<table>
				        													<tr>
				        														<td>
				        															<table>
																						<tr>						
																							<td align="right" height="24" width="10%">IP 地址:&nbsp;</td>				
																							<td width="40%">&nbsp;<input type="text" id="ipaddress" readonly="readonly" value="<%=ipaddress%>" name="ipaddress">
																							<input type="hidden" id="nodeid"  value="<%=nodeid%>" name="nodeid">
																							</td>
																						</tr>														
																						<tr>						
																							<td align="right" height="24" width="10%">主机服务组名称:&nbsp;</td>				
																							<td width="40%">&nbsp;<input type="text" id="name" name="name">
																							</td>
																						</tr>
																						<tr>						
																							<td align="right" height="24" width="10%">告警等级:&nbsp;</td>				
																							<td width="40%">
																								<select name="alarm_level" id="alarm_level">
																									<option value="1">普通告警</option>
																									<option value="2">严重告警</option>
																									<option value="3">紧急告警</option>
																								</select>
																							</td>
																						</tr>
																						<tr>						
																							<td align="right" height="24" width="10%">是否监控:&nbsp;</td>				
																							<td width="40%">
																								<select name="mon_flag" id="mon_flag">
																									<option value="1">是</option>
																									<option value="0">否</option>
																								</select>
																							</td>
																						</tr>
																						<tr>
																							<td nowrap colspan="8">
																							
																							    <!-- nielin modify begin (Phone div)*/ 2009-01-03--->
																							        <div id="formDiv" style="">         
																						                <table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none border=1 algin="center" >
																					                        <tr>
																					                            <td style="padding:0px 0px 0px 5px">&nbsp;&nbsp;主机服务组详细配置</td>
																					                        </tr>
																					                        <tr>
																					                            <td align="center">           
																					                                <table id="hostServiceGroupConfigureTable"  style="width:80%; padding:0;  background-color:#FFFFFF;" >
																				                                        <tr>
																				                                            <td colspan="0" height="50" align="center"> 
																				                                                <span  onClick='addRow("hostServiceGroupConfigureTable","hostservice");' style="border: 1px solid black;margin:10px;padding:0px 3px 0px 3px;cursor: hand;line-height:15px">增加一行</span>
																				                                            </td>
																				                                        </tr>
																					                                </table>
																					                            </td>
																					                        </tr>
																						                </table>
																						            </div> 
																							      <!-- nielin modify end */ 2009-01-03--->	
																												
																								
																							</td>
																						</tr>
																						<tr>
																							<!-- nielin add (for timeShareConfig) start 2010-01-03 -->
																							<td><input type="hidden" id="rowNum" name="rowNum"></td>
																							<!-- nielin add (for timeShareConfig) end 2010-01-03 -->
																						</tr>	
																						<tr>
																							<TD nowrap colspan="4" align=center>
																							<br><input type="button" value="保 存" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																								<input type="reset" style="width:50" value="关 闭" onclick="javascript:window.close()">&nbsp;&nbsp;
																								<input type="reset" style="width:50" value="返 回" onclick="javascript:window.history.back(1)">
																							</TD>	
																						</tr>	
														
													                        </TABLE>		
			        														</td>
			        													<tr>
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
	</form>
</body>
</HTML>