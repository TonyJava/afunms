<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.topology.model.RemotePingHost"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.topology.model.IpDistrictDetail"%>
<%@page import="com.afunms.config.model.DistrictConfig"%>
<%@page import="com.afunms.portscan.model.PortScanConfig"%>
<%@page import="com.afunms.portscan.model.PortConfig"%>
<%@page import="com.afunms.application.model.ProcessGroup"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>

<%
  String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");
  String ipaddress = (String)request.getAttribute("ipaddress");
  String nodeid = (String)request.getAttribute("nodeid");
  Hashtable processhash = (Hashtable)request.getAttribute("processhash");
  String eventId = (String)request.getAttribute("eventId");
 
%>


<html>
	<head>
	
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">    
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
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
			var delAction = "<%=rootPath%>/processgroup.do?action=delete";
		  	var listAction = "<%=rootPath%>/processgroup.do?action=list";
		  
		  	function toAdd()
		  	{
		    	mainForm.action = "<%=rootPath%>/processgroup.do?action=ready_add";
		    	mainForm.submit();
		  	}
		  
		  	function detail(id)
		  	{
				mainForm.action = "<%=rootPath%>/processgroup.do?action=tomcat_jvm&id="+id;
				mainForm.submit();
		  	}
		  	
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
									                	<td class="content-title"> 应用 >> 进程组管理 >> 进程选择列表 </td>
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
		        													<td class="application-detail-data-body-title">序号</td>
																	<td class="application-detail-data-body-title">进程ID</td>
																 	<td class="application-detail-data-body-title">进程名称</td>
																	<td class="application-detail-data-body-title">进程类型</td>
																	<td class="application-detail-data-body-title">cpu时间</td>
																	<td class="application-detail-data-body-title">内存占用率</td>
																	<td class="application-detail-data-body-title">内存占用量</td>
																	<td class="application-detail-data-body-title">当前状态</td>
       															</tr>
          															<%
          																Hashtable phash = null;
															            for(int m=0;m<processhash.size();m++){
															            	phash=(Hashtable)processhash.get(new Integer(m));  
															            	if(phash == null){
															            		continue;
															            	}
															            	String path = "";
															            	if(phash.get("Path")!=null){
															            		path = ((String)phash.get("Path")).trim();
															            	}
															            	
															            	String[] statusArray = new String[]{"运行中", "等待资源", "等待事件", "无效进程" };
															            	
															            	String[] typeArray = new String[]{"未知", "操作系统", "驱动程序", "应用程序" };
															            	
															            	String type = "";
															            	if(phash.get("Type")!=null){
															            	
															            		try{
															            			type = typeArray[Integer.valueOf(((String)phash.get("Type")).trim())-1];
															            		}catch(Exception e){
															            			type = (String)phash.get("Type");
															            		}
															            		
															            	}
															            	
															            	String status = "";
															            	if(phash.get("Status")!=null){
															            		try{
															            			status = statusArray[Integer.valueOf(((String)phash.get("Status")).trim())-1];
															            		}catch(Exception e){
															            			status = (String)phash.get("Status");
															            		}
															            	}
															        %>
	      															<tr <%=onmouseoverstyle%> title='<%=path%>'>
	      																<td class="application-detail-data-body-list"><input name="radio" type="radio" value="<%=phash.get("Name")%>"> <%=m+1%></td>
	          															<td class="application-detail-data-body-list"><%=phash.get("process_id")%></td>
																	 	<td class="application-detail-data-body-list"><%=phash.get("Name")%></td>
																		<td class="application-detail-data-body-list"><%=type%></td>
																		<td class="application-detail-data-body-list"><%=phash.get("CpuTime")%></td>
																		<td class="application-detail-data-body-list"><%=phash.get("MemoryUtilization")%></td>
																		<td class="application-detail-data-body-list"><%=phash.get("Memory")%></td>
																		<td class="application-detail-data-body-list"><%=status%></td>
																	</tr>
														            <%
														             phash = null;
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
