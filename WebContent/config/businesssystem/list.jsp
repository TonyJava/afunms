<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@ include file="/include/globe.inc"%>
<%
	String rootPath = request.getContextPath();
	List list = (List) request.getAttribute("list");
	JspPage jp = (JspPage) request.getAttribute("page");
	//String findselect=(String)session.getAttribute("findselect");
	List nameList = (List)request.getAttribute("nameList");
	List contactNameList = (List)request.getAttribute("contactNameList");
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script language="JavaScript" type="text/javascript">
  var listAction = "<%=rootPath%>/businesssystem.do?action=list";
  var delAction = "<%=rootPath%>/businesssystem.do?action=delete";
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  
  
    function toDelete(){
      if(confirm('是否确定删除这条记录?')) {
        mainForm.action="<%=rootPath%>/businesssystem.do?action=delete";
        mainForm.submit();
      }
  }
  
  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/businesssystem.do?action=ready_add";
     mainForm.submit();
  }
  
  /**
	*查询方法
	*/
  function toFind()
  {
     mainForm.action = "<%=rootPath%>/businesssystem.do?action=find";
     mainForm.submit();
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

</script>	
	</head>
	<body id="body" class="body" onload="initmenu();">
		<form method="post" name="mainForm">
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
														<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg"width="5" height="29" /></td>
														<td class="content-title">系统管理 >> 业务系统管理 >> 业务系统列表</td>
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
																	<td class="body-data-title" style="text-align: left;">
																		&nbsp;&nbsp;名称：&nbsp;&nbsp;
																		<select id="name" name="namechk" style="width:150px" size="1">
													    				<option value="全部">全部</option>													
																			<%
																				if(nameList!=null){
																				    for (int i=0;i<nameList.size();i++) {
								                                                        String namechk=((String)nameList.get(i));
																					    %>
																							<option value="<%=namechk%>"><%=namechk%></option>
																						<%
																				    }
																				}
																		    %>
																		</select> 																								
																		&nbsp;&nbsp;联系人姓名：&nbsp;&nbsp;
																		<select id="contactnamechk" name="contactnamechk" style="width:150px" size="1">														
																			<option value="全部">全部</option>
																			<%
																				if(contactNameList!=null){
																				    for (int i=0;i<contactNameList.size();i++) {
								                                                        String contactnamechk=((String)contactNameList.get(i));
																					    %>
																							<option value="<%=contactnamechk%>"><%=contactnamechk%></option>
																						<%
																				    }
																				}
																		    %>
																		</select>
																		&nbsp;&nbsp;请输入查询关键字：&nbsp;&nbsp;
																		<input type="text" value="" name="wordkey" width="20">
																		<input type="button" name="find" value="查询" onclick="toFind()">
																	</td>
																	<td class="body-data-title" style="text-align: right;">
																		<a href="#" onclick="toAdd()">添加</a>
																		<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table width="100%" cellpadding="0" cellspacing="1">
																<tr>
																	<td class="body-data-title">
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
														<td>
															<table>
																<tr class="microsoftLook0" height=28>
																	<td width='5%' align="center" class="body-data-title">
																		<INPUT type="checkbox" class=noborder name="checkall" onclick="javascript:chkall()">
																	</td>
																	<td width='5%' align="center" class="body-data-title">
																		序号
																	</td>
																	<td width='7%' class="body-data-title">
																		名称
																	</td>
																	<td width='12%' class="body-data-title">
																		描述
																	</td>
																	<td width="5%" class="body-data-title">
																		联系人姓名
																	</td>
																	<td width="8%" class="body-data-title">
																		联系人电话
																	</td>
																	<td width="8%" class="body-data-title">
																		联系人邮箱
																	</td>
																	<td width='5%' class="body-data-title">
																		编辑
																	</td>
																</tr>
																<%
																	//User x=(User)session.getAttribute(SessionConstant.CURRENT_USER);
																	int startRow = jp.getStartRow();
																	BusinessSystem vo = null;
																	for (int i = 0; i < list.size(); i++) {
																		vo = (BusinessSystem) list.get(i);
																		//String attachfiles = vo.getAttachfiles();
																		//request.setAttribute("attachfiles",attachfiles);
																%>
																<tr <%=onmouseoverstyle%>>
																	<td align='center' class="body-data-list">
																		<INPUT type="checkbox" class=noborder name=checkbox
																			value="<%=vo.getId()%>">
																	</td>
																	<td align='center' class="body-data-list">
																			<font color='blue'><%=startRow + i%></font>
																	</td>
																	<td align='center' class="body-data-list"><%=vo.getName()%></td>
																	<td align='center' class="body-data-list"><%=vo.getDescr()%></td>
																	<td align='center' class="body-data-list"><%=vo.getContactname()%></td>
																	<td align='center' class="body-data-list"><%=vo.getContactphone()%></td>
																	<td align='center' class="body-data-list"><%=vo.getContactemail()%></td>
																	<td align='center' class="body-data-list">
																		<a
																			href="<%=rootPath%>/businesssystem.do?action=ready_edit&id=<%=vo.getId()%>"><img
																				src="<%=rootPath%>/resource/image/editicon.gif" border="0" />
																		</a>
																	</td>
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
					</td>
				</tr>
			</table>
		</form>
	</body>
</HTML>
