<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.application.model.Storage"%>
<%@page import="java.util.List"%>

<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  List list = (List)request.getAttribute("list");
  JspPage jp = (JspPage)request.getAttribute("page");
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
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		
		<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/application/environment/resource/ext3.1/resources/css/ext-all.css" />
		<script type="text/javascript" 	src="<%=rootPath%>/application/environment/resource/ext3.1/adapter/ext/ext-base.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/application/environment/resource/ext3.1/ext-all-debug.js"></script>
		
		<script type="text/javascript">
			var curpage= <%=jp.getCurrentPage()%>;
  			var totalpages = <%=jp.getPageTotal()%>;
  			var delAction = "<%=rootPath%>/storage.do?action=delete";
  			var listAction = "<%=rootPath%>/storage.do?action=list";
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
		<script>
			function toAdd(){
				Ext.MessageBox.wait('数据加载中，请稍后.. '); 
				mainForm.action = "<%=rootPath%>/storage.do?action=ready_add"; 
				mainForm.submit();
			}
			function toDelete()
  {
    if(confirm('是否确定删除这条记录?')) {
     mainForm.action = "<%=rootPath%>/storage.do?action=delete";
     mainForm.submit();
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
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
						<tr>
							<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
							<td class="content-title">&nbsp;资源 &gt;&gt; 存储管理 &gt;&gt; 存储列表</td>
							<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
						</tr>
					</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td>
		        								<table id="content-body" class="content-body">
		        									<tr>
       													<td width="100%" style="text-align: right;" class="body-data-title">
       														<a href="#" onclick="toAdd()">添加</a>&nbsp;
       														<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
       													</td>
		        									</tr>
		        									
		        									<tr>
														<td class="body-data-title" colspan="6">
															<table width="100%" cellpadding="0" cellspacing="1" >
																<tr>
																    <td width="80%" align="center">
																		<jsp:include page="../../common/page.jsp">
																			<jsp:param name="curpage" value="<%=jp.getCurrentPage()%>" />
																			<jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>" />
																		</jsp:include>
																	    </td>
										        				</tr>
															</table>
														</td>
													</tr> 
		        									<tr>
       													<td align="center" class="body-data-title"><INPUT type="checkbox" id="checkall" name="checkall" onclick="javascript:chkall()">序号</td>
       													<td align="center" class="body-data-title">名称</td>
       													<td align="center" class="body-data-title">IP 地址</td>
       													<td align="center" class="body-data-title">运行状态</td>
       													<td align="center" class="body-data-title">管理状态</td>
       													<td align="center" class="body-data-title">操作</td>
		        									</tr>
		        									<%
		        										if(list != null && list.size() > 0 ){
		        											for(int i = 0 ; i < list.size(); i++){
		        												Storage storage = (Storage)list.get(i);
		        												String mon_flag = storage.getMon_flag();
		        												
		        												String mon_flag_ch = "否";
		        												if("1".equals(mon_flag)){
		        													mon_flag_ch = "是";
		        												}
		        												
		        												String status_ch = "不连通";
		        												System.out.println(storage.getStatus()+"=====storage.getStatus()=========");
		        												if("1".equals(storage.getStatus())){
		        													status_ch = "正常";
		        												}
		        											%>
		        												<tr>
			        												<td align="center" class="body-data-list"><INPUT type="checkbox" name="checkbox" value="<%=storage.getId()%>" ><%=i + jp.getStartRow()%></td>
			       													<td align="center" class="body-data-list"><%=storage.getName()%></td>
			       													<td align="center" class="body-data-list"><%=storage.getIpaddress()%></td>
			       													<td align="center" class="body-data-list"><%=status_ch%></td>
			       													<td align="center" class="body-data-list"><%=mon_flag_ch%></td>
			       													<td align="center" class="body-data-list">
																		<img src="<%=rootPath%>/resource/image/status.gif"
																			border="0" width=15 oncontextmenu=showMenu('2','<%=storage.getId()%>','<%=storage.getIpaddress()%>','1111') title="右键菜单">
			       													</td>
		       													</tr>
		        											<%
		        											}
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
	</body>
<script language="JavaScript">
$('.img').contextmenu({
		height:115,
		width:100,
		items : [{
			text :'编辑',
			icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/edit.gif',
			action: function(target){
				var id=$($(target).parent()).find('#id').val();
				edit(id);
			}
		},{
			text :'详细监视信息',
			icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/detail.gif',
			action: function(target){
				var id=$($(target).parent()).find('#id').val();
				toDetail(id);
			}
		},{
            text :'取消管理',
            icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/cancelMng.gif',
            action: function(target){
            	var id=$($(target).parent()).find('#id').val();
            	cancelmanage(id);
            }
        },{
            text :'添加管理',
            icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/addMn.gif',
            action: function(target){
            	var id=$($(target).parent()).find('#id').val();
                addmanage(id);
            }
        }]
	});	
	function toDetail(node){
		mainForm.action = "<%=rootPath%>/storage.do?action=toDetail&id=" + node;
		mainForm.submit();
	}
	
	function edit(node){
		mainForm.action = "<%=rootPath%>/storage.do?action=ready_edit&id=" + node;
		mainForm.submit();
	}
	
	function cancelmanage(node){
		mainForm.action = "<%=rootPath%>/storage.do?action=changeMon_flag&mon_flag=0&id=" + node;
		mainForm.submit();
	}
	
	function addmanage(node){
		mainForm.action = "<%=rootPath%>/storage.do?action=changeMon_flag&mon_flag=1&id=" + node;
		mainForm.submit();
	}
</script>
</html>
