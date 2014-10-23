<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.SubsystemForAS400"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="java.util.*"%>

<%
  String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");
  String ipaddress = (String)request.getAttribute("ipaddress");
  String nodeid = (String)request.getAttribute("nodeid");
  String eventId = (String)request.getAttribute("eventId");
  Hashtable jobHashtable = (Hashtable)request.getAttribute("jobHashtable");
  System.out.println(eventId);
%>
<html>
	<head>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">    
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script type="text/javascript">
		 	
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
		<script language="javascript">
		  
		  	
		  	function toDetermine()
		  	{
		  		var radios = document.getElementsByName('radio');
		  		for(var i = 0; i < radios.length ; i++){
		  			var radio = radios[i];
		  			if(radio.checked){
		  				parent.opener.document.getElementById("<%=eventId%>").value=radio.value;
		  				window.close();
		  			}
		  		}
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
	<body id="body" class="body" onload="initmenu();" >
		<form id="mainForm" method="post" name="mainForm">
			<table>
				<tr>
					<td class="td-container-main" style="border: none; ">
						<table>
							<tr>
								<td >
									<table>
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title"> 应用 >> 子系统组管理 >> 子系统选择列表 </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
		        									<tr>
		        										<td align="center" class="body-data-title" style="text-align: left;">
		        											IP地址：
		        											<input type="text" name="ipaddress" id="ipaddress" value="<%=ipaddress%>">
		        											<input type="hidden" name="nodeid" id="nodeid" value="<%=nodeid%>">
       													</td>
       													<td align="center" class="body-data-title" style="text-align: right;">
       														<a href="#" onclick="toDetermine();">确定</a>&nbsp;&nbsp;&nbsp;
       													</td>
       												</tr>
		        									<tr>
		        										<td colspan="2">
		        											<table>
       															   <tr>
											      						<td class="detail-data-body-title">序号</td>
											      						<td class="detail-data-body-title">名称</td>
											      						<td class="detail-data-body-title">作业数</td>
											      						<td class="detail-data-body-title">路径</td>
											      					</tr>
														            <%
											      					
											      						if(list!=null){
											      							for(int i = 0 ; i < list.size() ; i++){
											      								SubsystemForAS400 subsystemForAS400 = (SubsystemForAS400)list.get(i);
											      								List joblist = (List)jobHashtable.get(subsystemForAS400);
											      								int size = 0;
											      								if(joblist!=null){
											      									size = joblist.size();
											      								}
											      							%>
											      							<tr <%=onmouseoverstyle%>>
											      							    <td class="detail-data-body-list"><input type="radio" name="radio" value="<%=subsystemForAS400.getName()%>"><%=i + 1%></td>
												      							<td class="detail-data-body-list"><a href="#" onclick="searchJobs('<%=subsystemForAS400.getPath()%>')"><%=subsystemForAS400.getName()%></td>
																				<td class="detail-data-body-list"><%=size%></td>
																				<td class="detail-data-body-list"><%=subsystemForAS400.getPath()%></td>
																			</tr>
											      							<%
											      								
											      							}
											      						}
											      					 %>
		        											</table>
		        										</td>
		        									</tr>
		        								</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-footer" class="content-footer">
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
		</form>
	</body>
</html>
