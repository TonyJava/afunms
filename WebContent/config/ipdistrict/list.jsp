<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.config.model.IPDistrictConfig"%>
<%@page import="com.afunms.config.dao.DistrictDao"%>
<%@page import="com.afunms.config.model.DistrictConfig"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.*"%>
<%@page import="java.util.List"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
	List list = (List) request.getAttribute("list");
	int rc = list.size();
%>
<%
	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">

		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js"
			charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js"
			charset="gb2312"></script>
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<link rel="stylesheet" type="text/css"
			href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css"
			charset="gb2312" />
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script language="JavaScript" type="text/javascript">
  var delAction = "<%=rootPath%>/ipdistrict.do?action=delete";
  var listAction = "<%=rootPath%>/ipdistrict.do?action=list";
   
  function toAdd(sign)
  {
     mainForm.action = "<%=rootPath%>/ipdistrict.do?action=add&sign="+sign;
     mainForm.submit();
  }
  
  function doDelete(){
     if(confirm('是否确定删除这条记录?')) {
       mainForm.action = "<%=rootPath%>/ipdistrict.do?action=delete";
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
									                	<td class="content-title"> 资源 >> IP/MAC资源 >> IP地址段管理 </td>
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
																		&nbsp;区域名称&nbsp;&nbsp;&nbsp;&nbsp;
																		<select name="district">
																			<option value="value" seclected>
																				--请选择--
																			</option>
																			<%
																				DistrictDao dao = new DistrictDao();
																				List arr = dao.loadAll();
																				for (int i = 0; i < arr.size(); i++) {
																					DistrictConfig vo = (DistrictConfig) arr.get(i);
																			%>
																			<option value="<%=vo.getId()%>"><%=vo.getName()%></option>
																			<%
																				}
																			%>
																		</select>
																		<br>
																		&nbsp;IP地址&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																		<input type="text" name="ipaddress">
																		<input type="button" value="添加" style="width: 50"
																			onclick="toAdd(1)">
																		<br>
																		&nbsp;IP地址段&nbsp;&nbsp;从
																		<input type="text" name="startip">
																		到
																		<input type="text" name="endip">
																		<input type="button" value="添加" style="width: 50"
																			onclick="toAdd(2)">
																	</td>
																	<td class="body-data-title" style="text-align: right;">
																		<a href="#" onclick="toDelete2()">删除</a>&nbsp;&nbsp;&nbsp;
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
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>区域名称</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>IP地址段</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		编辑
																	</td>
																</tr>
																<%
																	IPDistrictConfig vo = null;
																	Hashtable ht = new Hashtable();
																	DistrictDao disdao = new DistrictDao();
																	List array = disdao.loadAll();
																	for (int j = 0; j < array.size(); j++) {
																		DistrictConfig dis = (DistrictConfig) arr.get(j);
																		ht.put(dis.getId(), dis.getName());
																	}
																	for (int i = 0; i < list.size(); i++) {
																		vo = (IPDistrictConfig) list.get(i);
																		String status = null;
																%>
																<tr <%=onmouseoverstyle%>>
																	<td align="center" class="body-data-list">
																		&nbsp;
																		<INPUT type="radio" class=noborder name="radio"
																			value="<%=vo.getId()%>">
																	</td>
																	<td align="center" class="body-data-list">
																		<%=ht.get(vo.getDistrictid())%>
																		&nbsp;
																	</td>
						
																	<td align="center" class="body-data-list">
																		<%
																			if (vo.getEndip() != null && !"".equals(vo.getEndip())
																						&& !"null".equals(vo.getEndip())) {
																		%>
																		<%=vo.getStartip()%>到<%=vo.getEndip()%>
																		<%
																			} else {
																		%>
																		<%=vo.getStartip()%>
																		<%
																			}
																		%>
																		&nbsp;
																	</td>
																	<td align="center" class="body-data-list">
																		<a
																			href="<%=rootPath%>/ipdistrict.do?action=ready_edit&id=<%=vo.getId()%>">
																			<img src="<%=rootPath%>/resource/image/editicon.gif"
																				border="0" />
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
