<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.topology.model.RemotePingHost"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.topology.model.IpDistrictMatch"%>
<%@page import="com.afunms.topology.model.IpDistrictMatchConfig"%>
<%@page import="com.afunms.config.model.DistrictConfig"%>
<%@page import="com.afunms.config.dao.DistrictDao"%>
<%@page import="com.afunms.application.model.JBossConfig"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="java.util.List"%>

<%
    String rootPath = request.getContextPath();
    String menuTable = (String) request.getAttribute("menuTable");
    List<JBossConfig> list = (List<JBossConfig>) request.getAttribute("list");
    JspPage jp = (JspPage) request.getAttribute("page");
    Hashtable<Integer, Integer> statusHashtable = (Hashtable<Integer, Integer>) request.getAttribute("statusHashtable");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<link
			href="<%=rootPath%>/common/contextmenu/skins/default/contextmenu.css"
			rel="stylesheet">
		<script src="<%=rootPath%>/common/contextmenu/js/jquery-1.8.2.min.js"
			type="text/javascript"></script>
		<script src="<%=rootPath%>/common/contextmenu/js/contextmenu.js"
			type="text/javascript"></script>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all-debug.js"></script>


		<script type="text/javascript">
			var curpage= "<%=jp.getCurrentPage()%>";
  			var totalpages = "<%=jp.getPageTotal()%>";
  			var delAction = "<%=rootPath%>/jboss.do?action=delete";
            var listAction = "<%=rootPath%>/jboss.do?action=list";
		</script>
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
									<table id="container-main-content"
										class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
													<tr>
														<td align="left" width="5">
															<img src="<%=rootPath%>/common/images/right_t_01.jpg"
																width="5" height="29" />
														</td>
														<td class="content-title">
															应用 >> 中间件管理 >> JBOSS监视列表
														</td>
														<td align="right">
															<img src="<%=rootPath%>/common/images/right_t_03.jpg"
																width="5" height="29" />
														</td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table id="content-body" class="content-body">
													<tr>
														<td style="text-align: right;" class="body-data-title">
															<a href="#" onclick="toAdd()">添加</a>&nbsp;&nbsp;&nbsp;
															<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
														</td>
													</tr>
													<tr>
														<td>
															<table width="100%">
																<tr>
																	<td class="body-data-title" width="80%" align="center">
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
															<table width="100%">
																<tr>
																	<td align="center" class="body-data-title">
																		<INPUT type="checkbox" id="checkall" name="checkall"
																			onclick="javascript:chkall()">
																		序号
																	</td>
																	<td align="center" class="body-data-title">
																		名称
																	</td>
																	<td align="center" class="body-data-title">
																		IP地址
																	</td>
																	<td align="center" class="body-data-title">
																		端口
																	</td>
																	<td align="center" class="body-data-title">
																		状态
																	</td>
																	<td align="center" class="body-data-title">
																		是否监控
																	</td>
																	<td align="center" class="body-data-title">
																		操作
																	</td>
																</tr>
																<%
																    if (list != null) {
																        int i = -1;
																        for (JBossConfig jBossConfig : list) {
																            i++;
																            int status = statusHashtable.get(jBossConfig.getId());
																            String flag = "是";
																            if (jBossConfig.getFlag() == 0) {
																                flag = "否";
																            }
																%>
																<tr>
																	<td align="center" class="body-data-list">
																		<INPUT type="checkbox"
																			value="<%=jBossConfig.getId()%>" id="checkbox"
																			name="checkbox" value="<%=jBossConfig.getId()%>"><%=jp.getStartRow() + i%></td>
																	<td align="center" class="body-data-list"><%=jBossConfig.getAlias()%></td>
																	<td align="center" class="body-data-list"><%=jBossConfig.getIpaddress()%></td>
																	<td align="center" class="body-data-list"><%=jBossConfig.getPort()%></td>
																	<td align="center" class="body-data-list">
																		<img
																			src="<%=rootPath%>/resource/<%=NodeHelper.getCurrentStatusImage(status)%>"
																			border="0">
																	</td>
																	<td align="center" class="body-data-list"><%=flag%></td>
																	<td align="center" class="body-data-list">
																		<input type="hidden" id="id" name="id"
																			value="<%=jBossConfig.getId()%>">
																		<img class="img"
																			src="<%=rootPath%>/resource/image/status.gif"
																			border="0" width=15 alt="右键操作">
																	</td>
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
															<table width="100%" border="0" cellspacing="0"
																cellpadding="0">
																<tr>
																	<td align="left" valign="bottom">
																		<img src="<%=rootPath%>/common/images/right_b_01.jpg"
																			width="5" height="12" />
																	</td>
																	<td></td>
																	<td align="right" valign="bottom">
																		<img src="<%=rootPath%>/common/images/right_b_03.jpg"
																			width="5" height="12" />
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
	</body>
	<script language="JavaScript">
	$('.img').contextmenu({
				height:115,
				width:100,
				items : [{
					text :'修改信息',
					icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/edit.gif',
					action: function(target){
						var id=$($(target).parent()).find('#id').val();
						edit(id);
					}
				},{
					text :'监视信息',
					icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/detail.gif',
					action: function(target){
						var id=$($(target).parent()).find('#id').val();
						detail1(id);
					}
				},{
                    text :'取消监视',
                    icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/cancelMng.gif',
                    action: function(target){
                    	var id=$($(target).parent()).find('#id').val();
                    	cancelmanage(id);
                    }
                },{
                    text :'添加监视',
                    icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/addMn.gif',
                    action: function(target){
                    	var id=$($(target).parent()).find('#id').val();
                        addmanage(id);
                    }
                }]
			});	
			
		//右键菜单方法开始
        	function detail1(node)
        	{
        	    mainForm.action = "<%=rootPath%>/jboss.do?action=detail&id="+node;
                mainForm.submit();
        	}
        	function edit(node)
        	{
        		mainForm.action = "<%=rootPath%>/jboss.do?action=edit&id="+node;
                mainForm.submit();
        	}
        	function cancelmanage(node)
        	{
        		location.href="<%=rootPath%>/jboss.do?action=changeMonflag&value=0&id="+node;
        	}
        	function addmanage(node)
        	{
        		location.href="<%=rootPath%>/jboss.do?action=changeMonflag&value=1&id="+node;
        	}	
        	function toAdd() {
                mainForm.action = "<%=rootPath%>/jboss.do?action=add";
                mainForm.submit();
            }
        </script>
</html>
