<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.topology.dao.*"%>
<%@page import="com.afunms.topology.model.*"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.topology.model.Link"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();

	List list = (List) request.getAttribute("list");

	int rc = list.size();

	JspPage jp = (JspPage) request.getAttribute("page");
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
	<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
	<script type="text/javascript" src="<%=rootPath%>/js/engine.js"></script> 
	<script type="text/javascript" src="<%=rootPath%>/js/util.js"></script> 
	<script type="text/javascript" src="<%=rootPath%>/dwr/interface/LinkRemoteService.js"></script>
	<script language="JavaScript">	
	  var curpage= <%=jp.getCurrentPage()%>;
	  var totalpages = <%=jp.getPageTotal()%>;
	  var delAction = "<%=rootPath%>/link.do?action=delete";
	  var listAction = "<%=rootPath%>/link.do?action=list";
	    
	  function toAdd()
	  {
	      mainForm.action = "<%=rootPath%>/link.do?action=ready_add";
	      mainForm.submit();
	  }
	    function toEdit()
	  {
		var checkedNum = 0;
		var bExist = false;
		for( var i=0; i < mainForm.checkbox.length; i++ ){
		   if(mainForm.checkbox[i].checked){
		      checkedNum++;
		   }
		}
		if(checkedNum != 1){
			alert('请选中单条链路！');
			return;
		}
		
	      mainForm.action = "<%=rootPath%>/link.do?action=ready_edit";
	      mainForm.submit();
	  }
	     function toDelete2()
	  {
	    if(confirm('是否确定删除这条记录?')) {
	      mainForm.action = "<%=rootPath%>/link.do?action=delete";
	      mainForm.submit();
	      }
	  }
	  
	  function toEditAll(){
	     // mainForm.action = "<%=rootPath%>/link.do?action=editall";
	      //mainForm.submit();
	      var linkids = '';
	      var num = 0;
	     for( var i=0; i < mainForm.checkbox.length; i++ ){
		   if(mainForm.checkbox[i].checked){
		   		num++;
		   		if(num == 1){
		   			linkids = mainForm.checkbox[i].value;
		   		}else{
		   			linkids = linkids+ ","+mainForm.checkbox[i].value;
		   		}
		   }
		}
		if(linkids == ''){
			alert('至少选择一条链路');
			return;
		}
		var result = showModalDialog("<%=rootPath%>/link.do?action=editall&linkids="+linkids, window, "dialogWidth:700px;dialogHeight:500px;dialogLeft:200px;dialogTop:150px;center:yes;help:yes;resizable:yes;status:yes","");
		if(result == 1) {
			window.location.reload(); 
		}
	  }
	  
	  function batchEditLink(linkids,maxPer,maxSpeed){
		LinkRemoteService.batchEditLink(linkids, maxPer, maxSpeed,{
			callback:function(data){
				if(data == "true"){
					//alert("批量修改阀值成功！");
				}else{
					alert("批量修改阀值失败！");
				}
				window.location.reload(); 
			}
		});
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
	function showup(){
		window.open("<%=rootPath%>/topology/network/link/up.jsp","protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
	}
	
	</script>
	</head>
	<body id="body" class="body" onload="initmenu();">
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
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
								                	<tr>
									                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
									                	<td class="content-title"> 资源 >> 设备维护 >> 链路信息 </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
										</tr>
							<tr>
								<td>
									<table id="content-body" class="content-body">
										<tr>
											<td>
												<table>
													<tr>
														<td class="body-data-title" style="text-align: right;">
														    <a href="#" onclick="showup()" >恢复链路信息</a>&nbsp;&nbsp;
															<a href="<%=rootPath%>/link.do?action=backuplinklist">备份链路信息</a>&nbsp;&nbsp;
															<a href="<%=rootPath%>/link.do?action=downloadlinklist" target="_blank"><img name="selDay1" alt='导出EXCEL' style="CURSOR: hand"
																	src="<%=rootPath%>/resource/image/export_excel.gif"
																	width=18 border="0">导出EXCEL</a>&nbsp;&nbsp;
															<a href="#" onclick="toAdd()">添加</a>
															<a href="#" onclick="toEdit()">修改</a>
															<a href="#" onclick="toDelete2()">删除</a>
															<a href="#" onclick="toEditAll()">批量设置阀值</a>&nbsp;&nbsp;&nbsp;&nbsp;
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table width="100%" cellpadding="0" cellspacing="1">
													<tr>
														<td class="body-data-title" style="text-align: left;">
															<jsp:include page="../../common/page.jsp">
																<jsp:param name="curpage"
																	value="<%=jp.getCurrentPage()%>" />
																<jsp:param name="pagetotal"
																	value="<%=jp.getPageTotal()%>" />
															</jsp:include>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table>
													<tr>

														<td align="center" class="body-data-title">
															<INPUT type="checkbox" class=noborder name="checkall"
																onclick="javascript:chkall()" class=noborder>
															序号
														</td>
														<td align="center" class="body-data-title">
															起点设备
														</td>
														<td align="center" class="body-data-title">
															起点端口
														</td>
														<td align="center" class="body-data-title">
															终点设备
														</td>
														<td align="center" class="body-data-title">
															终点端口
														</td>
														<td align="center" class="body-data-title">
															流量阀值（KB/S）
														</td>
														<td align="center" class="body-data-title">
															带宽利用率阀值(%)
														</td>
													</tr>
													<%
														Link vo = null;
														int startRow = jp.getStartRow();
														for (int i = 0; i < list.size(); i++) {
															vo = (Link) list.get(i);
															if(vo==null)continue;
															HostNode node = new HostNode();
															HostNodeDao dao = new HostNodeDao();

															node = dao.loadHost(vo.getStartId());
															HostNode endnode = new HostNode();
															dao = new HostNodeDao();

															endnode = dao.loadHost(vo.getEndId());
															if(node==null)continue;
															if(endnode==null)continue;
													%>
													<tr <%=onmouseoverstyle%>>
														<td align="center" class="body-data-list">
															<INPUT type="checkbox" class=noborder name=checkbox
																value="<%=vo.getId()%>" class=noborder>
															<font color='blue'><%=startRow + i%></font>
														</td>
														<td align="center" class="body-data-list"><%=node.getAlias()%></td>
														<td align="center" class="body-data-list">
															<%
																out.print("IP地址:");
																	out.print(vo.getStartIp());
																	out.print(" 索引:");
																	out.print(vo.getStartIndex());
															%>
														</td>
														<td align="center" class="body-data-list"><%=endnode.getAlias()%></td>
														<td align="center" class="body-data-list">
															<%
																out.print("IP地址:");
																	out.print(vo.getEndIp());
																	out.print(" 索引:");
																	out.print(vo.getEndIndex());
															%>
														</td>
														<td align="center" class="body-data-list"><%=vo.getMaxSpeed()==null?"":vo.getMaxSpeed()%></td>
														<td align="center" class="body-data-list"><%=vo.getMaxPer()==null?"":vo.getMaxPer()%></td>
													</tr>
													<%
														}
													%>
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
	</BODY>
</HTML>
