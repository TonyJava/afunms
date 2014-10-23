<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@ include file="/include/globe.inc"%>
<%
  String rootPath = request.getContextPath();  
  String ipaddress = (String)request.getAttribute("ipaddress");
  String nodeid = (String)request.getAttribute("nodeid");
  %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link href="<%=rootPath%>/resource/css/global/global.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
<!--nielin add for processgroupconfiguration at 2010-08-17 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/jobforas400groupdetail.js" charset="gb2312"></script>
<!--nielin add for processgroupconfiguration at 2010-08-17 end-->
<script language="JavaScript" type="text/javascript">
  Ext.onReady(function()
{  
setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
 Ext.get("process").on("click",function(){
  			var chk = checkinput("name","string","子系统名称",200,false);
  			var chk1 = checkinput("num","number","作业数",5,false);
  			if(chk&&chk1){
  				 Ext.MessageBox.wait('数据加载中，请稍后.. ');
        	     mainForm.action = "<%=rootPath%>/jobForAS400Group.do?action=savesubsystem";
        	     mainForm.submit();
  			}
 });	
});
//-- nielin modify at 2010-01-04 start ----------------
function CreateWindow(url)
{
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}    

function chooseSubSystemForAS400(eventId){
	return CreateWindow('<%=rootPath%>/jobForAS400Group.do?action=chooseSubSystemForAS400&ipaddress=' + "<%=ipaddress%>" + '&nodeid=' + "<%=nodeid%>" + '&eventId='+eventId);
}

function chooseJobActiveStatus(eventId){
	return CreateWindow('<%=rootPath%>/jobForAS400Group.do?action=chooseJobActiveStatus&eventId='+eventId);
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
<style>
<!--
body{
background-image: url(${pageContext.request.contextPath}/common/images/menubg_report.jpg);
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
											                	<td class="add-content-title"> 应用 &gt;&gt; AS400 子系统管理 &gt;&gt; 添加 </td>
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
																							<td align="right" height="24" width="10%">子系统名称:&nbsp;</td>				
																							<td width="40%">&nbsp;<input type="text" id="name" name="name"><a href="#" onclick="chooseSubSystemForAS400('name')">浏览</a></td>
																						</tr>
																						<tr>						
																							<td align="right" height="24" width="10%">个数:&nbsp;</td>				
																							<td width="40%">&nbsp;<input type="text" id="num" name="num"></td>
																						</tr>
																						<tr>						
																							<td align="right" height="24" width="10%">活动状态类型:&nbsp;</td>				
																							<td width="40%">&nbsp;<select id="jobActiveStatusType" name="jobActiveStatusType">
																									<option value="1">必须出现</option>
																									<option value="0">不允许出现</option>
																									<option value="-1">不限</option>
																								</select>
																							</td>
																						</tr>
																						<tr>						
																							<td align="right" height="24" width="10%">状态:&nbsp;</td>				
																							<td width="40%">&nbsp;<input type="text" onclick="chooseJobActiveStatus('jobActiveStatus')" id="jobActiveStatus" name="jobActiveStatus" style="width:100px"><a href="#" onclick="chooseJobActiveStatus('jobActiveStatus')">浏览</a></td>
																						</tr>
																						<tr>						
																							<td align="right" height="24" width="10%">告警等级:&nbsp;</td>				
																							<td width="40%">&nbsp;<select id="alarm_level" name="alarm_level">
																									<option value="1">普通告警</option>
																									<option value="2">严重告警</option>
																									<option value="3">紧急告警</option>
																								</select>
																							</td>
																						</tr>
																						<tr>						
																							<td align="right" height="24" width="10%">是否监控:&nbsp;</td>				
																							<td width="40%">&nbsp;<select id="mon_flag" name="mon_flag">
																									<option value="0">否</option>
																									<option value="1">是</option>
																								</select>
																							</td>
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