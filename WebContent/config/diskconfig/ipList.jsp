<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.config.model.Diskconfig"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
	List list = (List) request.getAttribute("list");
	List ips = (List) request.getAttribute("ips");
   String name=(String) request.getAttribute("name");
    String ipaddress=(String) request.getAttribute("ipaddress");
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

		<script language="javascript">	
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/disk.do?action=ipDelete";
  var listAction = "<%=rootPath%>/disk.do?action=ipList";
  
  function doQuery()
  {  
     mainForm.action = "<%=rootPath%>/disk.do?action=find";
     mainForm.submit();
  }
  function refresh()
  {  
     mainForm.action = "<%=rootPath%>/disk.do?action=refresh";
     mainForm.submit();
  }  
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/disk.do?action=ready_add";
      mainForm.submit();
  }
  
    function toEmpty()
  {
      mainForm.action = "<%=rootPath%>/disk.do?action=ipEmpty";
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
	<body id="body" class="body" onload="initmenu();" >
		<form id="mainForm" method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					
					<td class="td-container-main" bgcolor="#FFFFFF">
					<input type="hidden" id="name" name="name" value="<%=name %>">
					<input type="hidden" id="ipaddress" name="ipaddress" value="<%=ipaddress %>">
						<table id="container-main" class="container-main">
							<tr>
								<td class="td-container-main-content">
									<table id="container-main-content" class="container-main-content">
										<tr>
											<td>
												<table id="content-header" class="content-header">
													<tr>
														<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
														<td class="content-title"> 资源 >> 性能监视 >> 磁盘阀值配置 </td>
														<td align="right"> <img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
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
																		
																		<INPUT type="button" class="formStyle" value="刷 新" onclick=" return refresh()">
																		<INPUT type="button" class="formStyle" value="删 除" onclick=" return toDelete()">
																		&nbsp;&nbsp;
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
														<td colspan="2">
															<table cellspacing="1" cellpadding="0" width="100%">
																<tr class="microsoftLook0">
																	<td align="center" class="body-data-title">
																		<INPUT type="checkbox" class=noborder name="checkall"
																			onclick="javascript:chkall()">
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>IP地址</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>磁盘名称</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>是否告警</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>一级阀值</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>短信</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>二级阀值</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>短信</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>三级阀值</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		<strong>短信</strong>
																	</td>
																	<td align="center" class="body-data-title">
																		报表显示操作
																	</td>
																	
																	<td align="center" class="body-data-title">
																		操作
																	</td>

																</tr>
																<%
																	Diskconfig vo = null;
																	int startRow = jp.getStartRow();
																	for (int i = 0; i < rc; i++) {
																		vo = (Diskconfig) list.get(i);
																%>
																<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>>
																	<td align="center" class="body-data-list">
																		<font color='blue'>&nbsp;<%=startRow + i%></font>
																		<INPUT type="checkbox" class=noborder name=checkbox
																			value="<%=vo.getId()%>" class=noborder>
																	</td>
																	<td align="center" class="body-data-list"><%=vo.getIpaddress()%></td>
																	<td align="center" class="body-data-list"><%=vo.getName()%></td>
																	<%
																		String monflag = "是";
																			if (vo.getMonflag() == 0)
																				monflag = "否";
																			String sms = "是";
																			if (vo.getSms() == 0)
																				sms = "否";
																			String sms1 = "是";
																			if (vo.getSms1() == 0)
																				sms1 = "否";
																			String sms2 = "是";
																			if (vo.getSms2() == 0)
																				sms2 = "否";
																	%>
																	<td align="center" class="body-data-list"><%=monflag%></td>
																	<td align="center" class="body-data-list"><%=vo.getLimenvalue()%></td>
																	<td align="center" class="body-data-list"><%=sms%></td>
																	<td align="center" class="body-data-list"><%=vo.getLimenvalue1()%></td>
																	<td align="center" class="body-data-list"><%=sms1%></td>
																	<td align="center" class="body-data-list"><%=vo.getLimenvalue2()%></td>
																	<td align="center" class="body-data-list"><%=sms2%></td>
																	<%
																		String reportflag = "<a href=" + rootPath
																					+ "/disk.do?action=ipUpdate&id=" + vo.getId()+"&ipaddress="+vo.getIpaddress()+"&name="+name
																					+ "&reportflag=0>取消显示于报表</a>";
																			if (vo.getReportflag() == 0) {
																				reportflag = "<a href=" + rootPath
																						+ "/disk.do?action=ipUpdate&id=" + vo.getId()+"&ipaddress="+vo.getIpaddress()+"&name="+name
																						+ "&reportflag=1>显示于报表</a>";
																			}
																	%>
																	<td align="center"  class="body-data-list"><%=reportflag%></td>
																	
																	<td align="center" class="body-data-list">
																		<a href="javascript:void(null)"
																			onClick='window.open("<%=rootPath%>/disk.do?action=showedit&id=<%=vo.getId()%>","editdiskconfig", "height=400, width= 500, top=200, left= 200")'>
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
													<tr>
														<td align=center>
					        								<input type="button" value="关 闭" onclick="window.close()">
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
