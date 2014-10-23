<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="java.util.List"%>
<%@page import="com.afunms.indicators.model.GatherIndicators"%>
<%@page import="com.afunms.indicators.model.NodeGatherIndicators"%>
<%
    String rootPath = request.getContextPath();
    String menuTable = (String)request.getAttribute("menuTable");
    List<GatherIndicators> gatherIndicatorsList = (List)request.getAttribute("gatherIndicatorsList");
    NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators)request.getAttribute("nodeGatherIndicators");
    String nodeid = (String)request.getAttribute("nodeid");
	String type = (String)request.getAttribute("type");
	String subtype = (String)request.getAttribute("subtype");
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





<script language="JavaScript" type="text/javascript">


	Ext.onReady(function(){  
	 	Ext.get("process").on("click",function(){
	        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
	        mainForm.action = "<%=rootPath%>/nodeGatherIndicators.do?action=showUpdate";
	        mainForm.submit();
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
	
	initAttribute();
}

</script>

<script type="text/javascript">


	function initAttribute(){
		var name = document.getElementById("name");
		var alias = document.getElementById("alias");
		var category = document.getElementById("category");
		var description = document.getElementById("description");
		var poll_interval = document.getElementById("poll_interval");
		var isCollection = document.getElementById("isCollection");
		var isDefault = document.getElementById("isDefault");
		
		alias.value = "<%=nodeGatherIndicators.getAlias()%>";
		category.value = "<%=nodeGatherIndicators.getCategory()%>";
		description.value = "<%=nodeGatherIndicators.getDescription()%>";
		poll_interval.value = "<%=nodeGatherIndicators.getPoll_interval()%>" + "-" + "<%=nodeGatherIndicators.getInterval_unit()%>";
		isCollection.value = "<%=nodeGatherIndicators.getIsCollection()%>";
		isDefault.value = "<%=nodeGatherIndicators.getIsDefault()%>";
	}
</script>

</head>
<body id="body" class="body" onload="initmenu();">
	<form id="mainForm" method="post" name="mainForm">
		<input type="hidden" name="nodeid" id="nodeid" value="<%=nodeid%>">
		<input type="hidden" name="type" id="type" value="<%=type%>">
		<input type="hidden" name="subtype" id="subtype" value="<%=subtype%>">
		<input type="hidden" name="isDefault" id="isDefault">
		<input type="hidden" name="id" id="id" value="<%=nodeGatherIndicators.getId()%>">
		<input type="hidden" id="name" name="name"  value="<%=nodeGatherIndicators.getName()%>">
		<input type="hidden" id="classpath" name="classpath"  value="<%=nodeGatherIndicators.getClasspath()%>">
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
											                	<td class="add-content-title"> 资源 >> 性能监视 >> 性能指标 >> 性能指标添加 </td>
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
				        											<table cellspacing="1">
																		<tr style="background-color: #ECECEC;">						
																			<td align="right" height="24" width="10%">指标名称:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" value="<%=nodeGatherIndicators.getName()%>" readonly="readonly" disabled="disabled">
																			</td>
																			<td align="right" height="24" width="10%">别名:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<input type="text" id="alias" name="alias">
																			</td>
																		</tr>
																		<tr>
												 							<td align="right" height="24" width="10%">是否采集:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<select id="isCollection" name="isCollection" style="width: 132px;">
																					<option value="0">否</option>
																					<option value="1">是</option>
																				</select>
																			</td>
												  							<td align="right" height="24" width="10%">采集间隔:&nbsp;</td>				
																			<td width="40%">&nbsp;
																				<select id="poll_interval" name="poll_interval">
																					<option value="30-s">30秒</option>
																					<option value="1-m">1分钟</option>
																					<option value="2-m">2分钟</option>
																					<option value="3-m">3分钟</option>
																					<option value="4-m">4分钟</option>
																					<option value="5-m">5分钟</option>
																					<option value="10-m">10分钟</option>
																					<option value="30-m">30分钟</option>
																					<option value="1-h">1小时</option>
																					<option value="4-h">4小时</option>
																					<option value="8-h">8小时</option>
																					<option value="12-h">12小时</option>
																					<option value="1-d">1天</option>
																					<option value="1-w">1周</option>
																					<option value="1-mt">1月</option>
																					<option value="1-y">1年</option>
																				</select>
																			</td>
												 						</tr> 	
												    					<tr style="background-color: #ECECEC;"> 
																			<td align="right" height="24" width="10%">描述:&nbsp;</td>	
																			<td width="40%">&nbsp;
																				<input type="text" name="description" id="description">
																			</td>
																			<td align="right" height="24" width="10%">指标种类:&nbsp;</td>	
																			<td width="40%">&nbsp;
																				<input type="text" name="category" id="category">
																			</td>					
												 						</tr> 
																		<tr>
																			<TD nowrap colspan="4" align=center>
																			<br><input type="button" value="保 存" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
																				<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
																			</TD>	
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
</BODY>
</HTML>