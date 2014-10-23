<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.config.model.DistrictConfig"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
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
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script language="JavaScript" type="text/javascript">
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/district.do?action=delete";
  var listAction = "<%=rootPath%>/district.do?action=list";
   
  function toDelete(){
      if(confirm('是否确定删除这条记录?')) {
        mainForm.action="<%=rootPath%>/district.do?action=delete";
        mainForm.submit();
      }
  }
 
  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/district.do?action=ready_add";
     mainForm.submit();
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
									                	<td class="content-title"> 系统管理 >> 系统配置 >> 区域管理 </td>
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
																		<a href="#" onclick="toAdd()">添加</a>
																		<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
																	</td>
																</tr>
															</table>
														</td>
													</tr>
													<tr>
														<td>
															<table>
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
																<tr>
																	<td align="center" class="body-data-title"></td>
																	<td align="center" class="body-data-title">
																		序号
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>区域名称</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>描述</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>颜色</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		编辑
																	</td>
																</tr>
																<%
																	int startRow = jp.getStartRow();
																	DistrictConfig vo = null;
																	for (int i = 0; i < list.size(); i++) {
																		vo = (DistrictConfig) list.get(i);
																		String status = null;
																%>
																<tr <%=onmouseoverstyle%>>
																	<td align="center" class="body-data-list">
																		&nbsp;
																		<INPUT type="radio" class=noborder name="radio"
																			value="<%=vo.getId()%>">
																	</td>
																	<td align="center" class="body-data-list">
																		&nbsp;
																		<%=startRow + i%></td>
																	<td align="center" class="body-data-list"><%=vo.getName()%>&nbsp;
																	</td>
																	<td align="center" class="body-data-list"><%=vo.getDesc()%>&nbsp;
																	</td>
																	<td align="center" class="body-data-list" bgcolor="#<%=vo.getDescolor()%>">&nbsp;</td>
																	<td align="center" class="body-data-list">
																		<a href="<%=rootPath%>/district.do?action=ready_edit&id=<%=vo.getId()%>">
																			<img src="<%=rootPath%>/resource/image/editicon.gif" border="0" />
																		</a>
																	</td>
																</tr>
																<%
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
	</BODY>
</HTML>
