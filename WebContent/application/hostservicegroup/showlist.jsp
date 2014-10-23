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
<%@page import="com.afunms.application.model.HostServiceGroup"%>

<%@page import="java.util.List"%>

<%
  	String rootPath = request.getContextPath();
  	String menuTable = (String)request.getAttribute("menuTable");
  	List list = (List)request.getAttribute("list");
  	JspPage jp = (JspPage)request.getAttribute("page");
  	Hashtable hostNodeHashtable = (Hashtable)request.getAttribute("hostNodeHashtable");
  
  	String ipaddress = (String)request.getAttribute("ipaddress");
  	String hostservicegroupname = (String)request.getAttribute("hostservicegroupname");
  	
  	if(null == ipaddress || ipaddress == "null"){
  		ipaddress = "";
  	}
  	
  	if(null == hostservicegroupname || hostservicegroupname == "null"){
  		hostservicegroupname = "";
  	}
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
			var delAction = "<%=rootPath%>/hostservicegroup.do?action=delete&foward=showlist";
		  	var listAction = "<%=rootPath%>/hostservicegroup.do?action=showlist";
		</script>
		<script language="JavaScript">

			//公共变量
			var node="";
			var ipaddress="";
			var operate="";
			/**
			*根据传入的id显示右键菜单
			*/
			function showMenu(id,nodeid,ip)
			{	
				ipaddress=ip;
				node=nodeid;
				//operate=oper;
			    if("" == id)
			    {
			        popMenu(itemMenu,100,"100");
			    }
			    else
			    {
			        popMenu(itemMenu,100,"1111");
			    }
			    event.returnValue=false;
			    event.cancelBubble=true;
			    return false;
			}
			/**
			*显示弹出菜单
			*menuDiv:右键菜单的内容
			*width:行显示的宽度
			*rowControlString:行控制字符串，0表示不显示，1表示显示，如“101”，则表示第1、3行显示，第2行不显示
			*/
			function popMenu(menuDiv,width,rowControlString)
			{
			    //创建弹出菜单
			    var pop=window.createPopup();
			    //设置弹出菜单的内容
			    pop.document.body.innerHTML=menuDiv.innerHTML;
			    var rowObjs=pop.document.body.all[0].rows;
			    //获得弹出菜单的行数
			    var rowCount=rowObjs.length;
			    //alert("rowCount==>"+rowCount+",rowControlString==>"+rowControlString);
			    //循环设置每行的属性
			    for(var i=0;i<rowObjs.length;i++)
			    {
			        //如果设置该行不显示，则行数减一
			        var hide=rowControlString.charAt(i)!='1';
			        if(hide){
			            rowCount--;
			        }
			        //设置是否显示该行
			        rowObjs[i].style.display=(hide)?"none":"";
			        //设置鼠标滑入该行时的效果
			        rowObjs[i].cells[0].onmouseover=function()
			        {
			            this.style.background="#397DBD";
			            this.style.color="white";
			        }
			        //设置鼠标滑出该行时的效果
			        rowObjs[i].cells[0].onmouseout=function(){
			            this.style.background="#F1F1F1";
			            this.style.color="black";
			        }
			    }
			    //屏蔽菜单的菜单
			    pop.document.oncontextmenu=function()
			    {
			            return false; 
			    }
			    //选择右键菜单的一项后，菜单隐藏
			    pop.document.onclick=function()
			    {
			        pop.hide();
			    }
			    //显示菜单
			    pop.show(event.clientX-1,event.clientY,width,rowCount*25,document.body);
			    return true;
			}
			function toAdd()
		  	{
		    	mainForm.action = "<%=rootPath%>/hostservicegroup.do?action=ready_add&foward=showadd";
		    	mainForm.submit();
		  	}
		  
			function edit()
			{
				mainForm.action ="<%=rootPath%>/hostservicegroup.do?action=ready_edit&forward=showedit&groupId="+node;
				mainForm.submit();
			}
			
			function search()
			{
				mainForm.action ="<%=rootPath%>/hostservicegroup.do?action=showlist";
				mainForm.submit();
			}
		</script>
	</head>
	<body id="body" class="body" onload="initmenu();" >
		<!-- 这里用来定义需要显示的右键菜单 -->
		<div id="itemMenu" style="display: none";>
			<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
				style="border: thin;font-size: 12px" cellspacing="0">
				<tr>
					<td style="cursor: default; border: outset 1;" align="center"
						onclick="parent.edit()">修改信息</td>
				</tr>
			</table>
		</div>
		<!-- 右键菜单结束-->
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
									                	<td class="content-title">&nbsp;资源 >> 性能监视 >> 主机服务监视 </td>
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
					        										<td align="center" class="body-data-title" style="text-align: left;">&nbsp;
			       														IP地址：<input type="text" name="ipaddress" id="ipaddress" value="<%=ipaddress%>">
			       														服务组名称：<input type="text" name="hostservicegroupname" id="hostservicegroupname" value="<%=hostservicegroupname%>">
			       														<input type="button" value="查  找" onclick="search()">
			       													</td>
			       													<td align="center" class="body-data-title" style="text-align: right;">
			       														<a href="#" onclick="toAdd()">添加</a>
			       														<a href="#" onclick="toDelete();">删除</a>&nbsp;&nbsp;&nbsp;
			       													</td>
	       														</tr>
	       													</table>
       													</td>
       												</tr>
       												<tr>
														<td>
															<table>
																<tr>
													    			<td  class="body-data-title">
							    										<jsp:include page="../../common/page.jsp">
																			<jsp:param name="curpage" value="<%=jp.getCurrentPage()%>"/>
																			<jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>"/>
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
		        													<td align="center" class="body-data-title"><INPUT type="checkbox" id="checkall" name="checkall" onclick="javascript:chkall()">序号</td>
		        													<td align="center" class="body-data-title">设备名称</td>
		        													<td align="center" class="body-data-title">设备IP地址</td>
		        													<td align="center" class="body-data-title">服务组名称</td>
		        													<td align="center" class="body-data-title">告警等级</td>
		        													<td align="center" class="body-data-title">监控状态</td>
		        													<td align="center" class="body-data-title">操作</td>
		        												</tr>
		        												<%
					        									    if(list!=null&& list.size()>0){
					        									    	//System.out.println(list.size()+ "===========");
					        									        for(int i = 0 ; i < list.size() ; i++){
					        									        	HostServiceGroup hostServiceGroup = (HostServiceGroup)list.get(i);
					        									        	
					        									        	String alarmlevel = hostServiceGroup.getAlarm_level();
					        									        	
					        									        	String monflag = hostServiceGroup.getMon_flag();
					        									        	
					        									        	if("1".equals(alarmlevel)){
					        									        		alarmlevel = "普通告警";
					        									        	} else if("2".equals(alarmlevel)){
					        									        		alarmlevel = "严重告警";
					        									        	} else if("3".equals(alarmlevel)){
					        									        		alarmlevel = "紧急告警";
					        									        	} 
					        									        	String monflag_str = "否";
					        									        	if("1".equals(monflag)){
					        									        		monflag_str = "是";
					        									        	}
					        									        	
					        									        	String nodeName = "";
					        									        	HostNode hostNode = (HostNode)hostNodeHashtable.get(hostServiceGroup.getNodeid()+ "");
					        									        	if( hostNode != null){
					        									        		nodeName = hostNode.getAlias();
					        									        	}
					        									            %>
					        									            <tr>
					        													<td align="center" class="body-data-list"><INPUT type="checkbox" value="<%=hostServiceGroup.getId()%>" name="checkbox" id="checkbox"><%=i + 1%></td>
					        													<td align="center" class="body-data-list"><%=nodeName%></td>
					        													<td align="center" class="body-data-list"><%=hostServiceGroup.getIpaddress()%></td>
					        													<td align="center" class="body-data-list"><%=hostServiceGroup.getName()%></td>
								        										<td align="center" class="body-data-list"><%=alarmlevel%></td>
								        										<td align="center" class="body-data-list"><%=monflag_str%></td>
								        										<td align="center" class="body-data-list">
								        										<input type="hidden" id="id" name="id" value="<%=hostServiceGroup.getId()%>">
																	            <img class="img" src="<%=rootPath%>/resource/image/status.gif" border="0" width=15 alt="右键操作"></td>
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
             }]
		});
		function edit(node)
		{
			mainForm.action ="<%=rootPath%>/hostservicegroup.do?action=ready_edit&forward=showedit&groupId="+node;
				mainForm.submit();
		}
	</script>
</html>
