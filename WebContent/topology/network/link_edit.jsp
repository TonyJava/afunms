<%@page language="java" contentType="text/html;charset=gb2312" %>
<%@page import="java.util.*"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.polling.node.IfEntity"%>
<%  
   String rootPath = request.getContextPath();  
   
   List list = (List)request.getAttribute("list");  
   Iterator it1 = (Iterator)request.getAttribute("start_if");  
   Iterator it2 = (Iterator)request.getAttribute("end_if");  
   int startId = ((Integer)request.getAttribute("start_id")).intValue();
   int endId = ((Integer)request.getAttribute("end_id")).intValue();
   String startIndex = (String)request.getAttribute("start_index");  
   String endIndex = (String)request.getAttribute("end_index");      
   String id = (String)request.getAttribute("id");  
   String maxSpeed = (String)request.getAttribute("maxSpeed");
   String maxPer =(String)request.getAttribute("maxPer");
   String linkName = (String)request.getAttribute("linkName");
   String linetext = (String)request.getAttribute("linetext");
   String interf = (String)request.getAttribute("interf");
   List<IfEntity> startHostIfentityList = (ArrayList<IfEntity>)request.getAttribute("startHostIfentityList");
   List<IfEntity> endHostIfentityList = (ArrayList<IfEntity>)request.getAttribute("endHostIfentityList");
   String linkAliasName = (String)request.getAttribute("linkAliasName");
   String startAliasName = (String)request.getAttribute("startAliasName");
   String endAliasName = (String)request.getAttribute("endAliasName");
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<!--nielin add for timeShareConfig at 2010-01-04 start-->
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js" charset="gb2312"></script>
<!--nielin add for timeShareConfig at 2010-01-04 end-->


<script language="JavaScript" type="text/javascript">
	var start_ip = '<%=(String)request.getAttribute("startIp")%>';
	var end_ip = '<%=(String)request.getAttribute("endIp")%>';
	
/*********************************************
		动态修改链路名称
********************************************/
function refreshLinkName(){
 	var start_index = document.getElementById('start_index').value;
	var end_index = document.getElementById('end_index').value;
	document.getElementById('link_name').value=start_ip+'_'+start_index+'/'+end_ip+'_'+end_index;
	document.getElementById('link_alias_name').value='<%=startAliasName%>'+'_'+start_index+'/'+'<%=endAliasName%>'+'_'+end_index;
}
	
  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
  
     {
     	    
        	mainForm.action = "<%=rootPath%>/link.do?action=edit";
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


</head>
<body id="body" class="body" onload="initmenu();">

    <form method="post" name="mainForm">
          <input type=hidden name="id" value="<%=id%>">
          <input type=hidden name="radio" value="<%=id%>">
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
											                	<td class="add-content-title">资源>> 设备维护 >> 链路编辑</td>
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
			        												<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
																		<tr>
																			<TD nowrap align="right" height="24" width="10%">
																				链路名称&nbsp;<input type='hidden' name='direction1' id='direction1' value='1'/>
																			</TD>
																			<TD nowrap width="40%">&nbsp; <input type="text" id='link_name' name="link_name" size="47" class="formStyle" value="<%=linkName%>" readonly="readonly"></TD>
																			<TD nowrap align="right" height="24" width="10%">
																				链路别名&nbsp;<!-- <input type='hidden' name='direction1' id='direction1' value='1'/> -->
																			</TD>
																			<TD nowrap width="40%">&nbsp; <input type="text" id='link_alias_name' name="link_alias_name" size="47" class="formStyle" value="<%=linkAliasName%>"></TD>
																				
																		
																		</tr>
																		<tr style="background-color: #ECECEC;">
																			<TD nowrap align="right" height="24" width="10%">
																				带宽利用率阀值&nbsp;
																			</TD>
																			<TD nowrap width="40%">&nbsp; <input type="text"  id='max_per' name="max_per" maxlength="50%" size="20" class="formStyle" value="<%=maxPer %>">(%)</TD>
																			<TD nowrap align="right" height="24" width="10%">
																				链路流量阀值&nbsp;
																			</TD>
																			<TD nowrap width="40%">&nbsp; <input type="text"  id='max_speed' name="max_speed" maxlength="50%" size="20" class="formStyle" value="<%=maxSpeed %>">(KB/秒)</TD>
																		
																			
																		</tr>
																		<tr>
																			<TD nowrap align="right" height="24" width="10%">
																				起点设备&nbsp;
																			</TD>
																			<TD nowrap width="40%">
																				&nbsp;
																				<select size=1 name='start_id' id='start_id' disabled="true"
																					style='width: 250px;' onchange="doChange('start_id','start_index')">
																					<%
																						for (int i = 0; i < list.size(); i++) {
																							HostNode node = (HostNode) list.get(i);
																							String selected = "";
																							if (node.getId() == startId){
																								selected = "selected";
																							}
																					%>
																					<option value='<%=node.getId()%>' <%=selected%>><%=node.getAlias()%>(<%=node.getIpAddress()%>)</option>
																					<%
																						}
																					%>
																				</select>
																				<input type='hidden' value='<%=startId%>' name='startId' id='startId'/>
																				<input type='hidden' value='<%=endId%>' name='endId' id='endId'/>
																			</TD>
																			<TD nowrap align="right" height="24" width="10%">
																				起点端口索引&nbsp;
																			</TD>
																			<TD nowrap width="40%">
																				&nbsp;
																				<select size=1 name='start_index' id='start_index'
																					style='width: 250px;' onchange="refreshLinkName();">
																					<%
																					
																						for(IfEntity ifObj:startHostIfentityList){
																							String selected = "";
																							if (ifObj.getIndex().equals(startIndex)){
																								selected = "selected";
																							}
																					%>
																					<option value='<%=ifObj.getIndex()%>' <%=selected%>><%=ifObj.getIndex()%>(<%=ifObj.getDescr()%>)</option>
																					<%
																						}
																					%>
																				</select>
																			</TD>
																		</tr>
																		<tr  style="background-color: #ECECEC;">
																			<TD nowrap align="right" height="24" width="10%">
																				终点设备&nbsp;
																			</TD>
																			<TD nowrap width="40%">
																				&nbsp;
																				<select size=1 name='end_id' id='end_id' style='width: 250px;' disabled="true"
																					onchange="doChange('end_id','end_index')">
																					<%
																						for (int i = 0; i < list.size(); i++) {
																							HostNode node = (HostNode) list.get(i);
																							String selected = "";
																							if (node.getId() == endId){
																								selected = "selected";
																							}
																					%>
																					<option value='<%=node.getId()%>' <%=selected%>><%=node.getAlias()%>(<%=node.getIpAddress()%>)</option>
																					<%
																						}
																					%>
																				</select>
																			</TD>
																			<TD nowrap align="right" height="24" width="10%">
																				终点端口索引&nbsp;
																			</TD>
																			<TD nowrap width="40%">
																				&nbsp;
																				<select size=1 name='end_index' id='end_index'
																					style='width: 250px;' onchange="refreshLinkName();">
																					<%
																						for(IfEntity ifObj:endHostIfentityList){
																							String selected = "";
																							if (ifObj.getIndex().equals(endIndex)){
																								selected = "selected";
																							}
																					%>
																					<option value='<%=ifObj.getIndex()%>' <%=selected%>><%=ifObj.getIndex()%>(<%=ifObj.getDescr()%>)</option>
																					<%
																						}
																					%>
																				</select>
																			</TD>
																		</tr>
																		<tr>
																			<TD nowrap align="right" height="24" width="10%">接口显示&nbsp;</TD>
																			<TD nowrap width="40%">&nbsp; 
																				<select size=1 name='interf' style='width:200px;' id="interf">
																			       <option value="0" selected>不显示</option>
																				   <option value="1">显示</option>
																			   </select>
																			   <script>
																			   		setSelectItem('interf','<%=interf%>');
																			   	</script>
																			</TD>
																			
																			<TD nowrap align="right" height="24" width="10%">
																				链路显示&nbsp;
																			</TD>
																			<TD nowrap width="40%">&nbsp;
																				<select size=1 name='linetext' style='width:200px;' id="linetext">
																			       <option value="0" selected>无</option>
																				   <option value="1">上行带宽利用率(%)</option>
																				   <option value="2">下行带宽利用率(%)</option>
																				   <option value="3">上行流速(KB/s)</option>
																				   <option value="4">下行流速(KB/s)</option>
																			   	</select>
																			   	<script  language="javascript">
																			   		setSelectItem('linetext','<%=linetext%>');
																			   	</script>
																			</TD>
																		</tr>
																		<tr>
																			<TD nowrap colspan="4" align=center>
																				<br>
																				<input type="button" value="保 存" style="width: 50"
																					id="process" onclick="#">
																				&nbsp;&nbsp;
																				<input type="reset" style="width: 50" value="返回"
																					onclick="javascript:history.back(1)">
																			</TD>
																		</tr>
																	</TABLE>
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