<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.afunms.topology.model.RemotePingHost"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="com.afunms.topology.model.IpDistrictDetail"%>
<%@page import="com.afunms.config.model.DistrictConfig"%>
<%@page import="com.afunms.config.model.Business"%>
<%@page import="java.util.ArrayList"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%
  String rootPath = request.getContextPath();
  String menuTable = (String)request.getAttribute("menuTable");
  List list = (List)request.getAttribute("list");
%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
		<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/config/business/businessTree/businessTree.js"></script>
		<link href="<%=rootPath%>/config/business/businessTree/businessTree.css" rel="stylesheet" type="text/css"/>
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
			var dataArray = new Array();
			
			<%
				if(list!=null){
					for(int i = 0 ; i < list.size() ; i ++){
						Business business = (Business)list.get(i);
						%>
						dataArray[<%=i%>] = ['<%=business.getId().trim()%>' , '<%=business.getName().trim()%>' , '<%=business.getDescr().trim()%>', '<%=business.getPid().trim()%>'];
						
						<%
					}
				}
			
			%>
			
			function show(){
				var tree = new Tree();
				tree.init(dataArray);
				tree.setNodeClick(nodeClick);
				tree.setNodeContextmenu(showMenuItem);
				tree.setImgPath("/afunms/config/business/businessTree/images/");
				tree.show("businessTree");
				
				var addMenuItem = new MenuItem('1' , '添加' , 'add.gif','');	
				var editMenuItem = new MenuItem('2' , '编辑' ,'edit.gif','');	
				var deleteMenuItem = new MenuItem('3' , '删除','delete.small.png','' );
				
				tree.addMenuItem(addMenuItem);	
				tree.addMenuItem(editMenuItem);	
				tree.addMenuItem(deleteMenuItem);	
				
				tree.createContextMenuItem();			
			}
			
			function nodeClick(div , node){
				changeSelectedNodeDiv(div);
				editNode.call(this , node);
				//this.closeContextMenuItem();
			}
			
			function showNodeDiv(){
				var showNodeDiv = document.getElementById("showNodeDiv");
				showNodeDiv.style.display = "inline";
			}
			
			function changeSelectedNodeDiv(div){
				var nodeNameDivs = document.getElementsByTagName("A");
				if(nodeNameDivs){
					var num = nodeNameDivs.length;
					for(var i = 0 ; i < num ; i++){
						if(nodeNameDivs[i].name = div.name){
							nodeNameDivs[i].className = 'noSelected';
						}
					}
				}
				div.className = 'selected';
			}
			
			function setNodeDivValue(node , action){
				var id = document.getElementById("id");
				
				var name = document.getElementById("name");
				
				var descr = document.getElementById("descr");
				
				var pid = document.getElementById("pid");
				
				id.value = node.id;
				name.value = node.name;
				descr.value = node.descr;
				pid.value = node.pid;
				
				var save = document.getElementById("save");
				save.onclick = function(){
					mainForm.action = action;
					mainForm.submit();
				}
				
			}
			
			function showMenuItem(tree , node){
				changeSelectedNodeDiv(this);
				tree.setOnClickForMenuItemById.call(tree , '1' , addNode , node);
				tree.setOnClickForMenuItemById.call(tree , '2' , editNode , node);
				tree.setOnClickForMenuItemById.call(tree , '3' , deleteNode , node);
				tree.showContextMenuItem.call(tree , event.clientY , event.clientX);
			}
			function addNode(node){
				var addNode = new TreeNode('', '' , '', node.id);
				showNodeDiv(addNode);
				setNodeDivValue(addNode , "<%=rootPath%>/business.do?action=add");
				this.closeContextMenuItem();
			}
			function editNode(node){
				showNodeDiv(node);
				setNodeDivValue(node , "<%=rootPath%>/business.do?action=update");
				this.closeContextMenuItem();
			}
			function deleteNode(node){
				this.closeContextMenuItem();
				if(window.confirm("是否删除名称为：\"" + node.name + "\"的节点")){
					if(node.isHadChild){
						if(window.confirm("此节点含有子节点 ， 若此节点删除 ， 同时删除其子节点！！！是否继续")){
							mainForm.action = "<%=rootPath%>/business.do?action=delete&id=" + node.id;
							mainForm.submit();
						}
					}else{
						mainForm.action = "<%=rootPath%>/business.do?action=delete&id=" + node.id;
						mainForm.submit();
					}
				}
				
			}
		</script>
		
		
	</head>
	<body id="body" class="body" onload="initmenu();show();">
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
									                	<td class="content-title"> 系统管理 >> 系统配置 >> 业务分类 </td>
									                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									       			</tr>
									        	</table>
		        							</td>
		        						</tr>
		        						<tr>
		        							<td align="left">
		        								<table id="content-body" class="content-body" align="left">
		        									<tr align="left">
														<td width="40%" align="left" valign="top" >
															<div id="businessTree"></div>
														</td>
														<td width="200" align="left" valign="middle" >
															<div id="showNodeDiv" style="display: none;" >
																<div><input name="id" id="id" type="hidden"></div>
																<div><input name="pid" id="pid" type="hidden"></div>
																<div>名称：<input name="name" id="name" type="text"></div>
																<div>描述：<input name="descr" id="descr" type="text"></div>
																<br>
																<div><input id="save" type="button" value="保  存"></div>
															</div>
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
