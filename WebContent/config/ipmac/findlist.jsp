<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.polling.om.IpMac"%>
<%@ page import="com.afunms.system.model.Department"%>
<%@ page import="com.afunms.system.dao.DepartmentDao"%>
<%@ page import="com.afunms.config.model.Employee"%>
<%@ page import="com.afunms.polling.om.IpMacBase"%>
<%@ page import="com.afunms.config.dao.EmployeeDao"%>
<%@ page import="com.afunms.topology.dao.IpMacBaseDao"%>
<%@ include file="/include/globe.inc"%>

<%
	 String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");
  
  int rc = list.size();

  JspPage jp = (JspPage)request.getAttribute("page");
  String key = (String)request.getAttribute("key");
  String value = (String)request.getAttribute("value");
  System.out.println("key==="+key+"====value:"+value);
  String relateipaddrchecked = "";
  String ipaddresschecked = "";
  String macchecked = "";
  String valuestr = "";
  if(value != null)valuestr=value;
  if(key != null && key.equals("relateipaddr")){
  	relateipaddrchecked = "selected";
  }
  if(key != null && key.equals("ipaddress")){
  	ipaddresschecked = "selected";
  }
  if(key != null && key.equals("mac")){
  	macchecked = "selected";
  }
  IpMacBaseDao dao = new IpMacBaseDao(); 
  List macbaselist = dao.loadAll();
  Hashtable macbaseHash = new Hashtable();
  if(macbaselist != null&& macbaselist.size()>0){
  	for(int i=0;i<macbaselist.size();i++){
  		IpMacBase ipmacbase = (IpMacBase)macbaselist.get(i);
  		macbaseHash.put(ipmacbase.getMac(),ipmacbase);
  	}
  }
  dao.close();
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
  var delAction = "<%=rootPath%>/ipmac.do?action=delete";
  var listAction = "<%=rootPath%>/ipmac.do?action=list";
  
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/ipmac.do?action=find";
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
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
  }
  
  function toDelete()
  {
  	var arr = document.getElementsByName("checkbox");
  	if(arr.length == 0){
  		alert("没有要删除的数据");
  		return false;
  	}
  	var count =  0;
	for(var i=0;i<arr.length;i++){
	   if(arr[i].type == "checkbox" && arr[i].checked){
	       count++;
	    }
	}
  	 if(count == 0){
  	 	alert("请选择要删除的记录");
  	 	return false;
  	 }
     if(confirm('是否确定删除所选记录?')) {
      mainForm.action = "<%=rootPath%>/ipmac.do?action=delete";
      mainForm.submit();
     }
  }
  
  function toDeleteAll()
  {
     if(confirm('是否确定删除全部记录?')) {
      mainForm.action = "<%=rootPath%>/ipmac.do?action=deleteall";
      mainForm.submit();
     }
  }
    function toSetBaseline()
  {
    
      mainForm.action = "<%=rootPath%>/ipmac.do?action=setlistbaseline";
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
									                	<td class="content-title"> 资源 >> IP/MAC资源 >> IP/MAC</td>
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
																		&nbsp;&nbsp;&nbsp;&nbsp;
																		<B>查询:</B>
																		<SELECT name="key" style="">
																			<OPTION value="relateipaddr" <%=relateipaddrchecked%>>网络设备IP</OPTION>
								          									<OPTION value="ipaddress" <%=ipaddresschecked%>>IP地址</OPTION>
								          									<OPTION value="mac" <%=macchecked%>>MAC</OPTION>    
			
																		</SELECT>
																		&nbsp;
																		<b>=</b>&nbsp;
																		<INPUT type="text" name="value" width="15" class="formStyle" value="<%=valuestr%>">
																		<INPUT type="button" class="formStyle" value="查询"
																			onclick=" return doQuery()">
																	</td>
																	<td class="body-data-title" style="text-align: right;">
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
																	<td class="body-data-title" style="text-align: right;">
																		<a href="<%=rootPath%>/ipmac.do?action=downloadipmacreport"
																			target="_blank"><img name="selDay1" alt='导出EXCEL'
																			style="CURSOR: hand"
																			src="<%=rootPath%>/resource/image/export_excel.gif" width=18
																			border="0">导出EXCEL</a>&nbsp;&nbsp;&nbsp;&nbsp;
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
																			onclick="javascript:chkall()">
																		序号
																	</td>
																	<td align="center" class="body-data-title">
																		网络设备(ip)
																	</td>
																	<td align="center" class="body-data-title">
																		端口
																	</td>
																	<td align="center" class="body-data-title">
																		IP地址
																	</td>
																	<td align="center" class="body-data-title">
																		MAC
																	</td>
																	<td align="center" class="body-data-title">
																		基线
																	</td>
																</tr>
																<%
																	IpMac vo = null;
																	int startRow = jp.getStartRow();
																	session.setAttribute("startRow", startRow);
																	session.setAttribute("list", list);
																	for (int i = 0; i < rc; i++) {
																		vo = (IpMac) list.get(i);
																%>
																<tr <%=onmouseoverstyle%>>
																	<td align="center" class="body-data-list">
																		<INPUT type="checkbox" class=noborder name=checkbox
																			value="<%=vo.getId()%>">
																		<font color='blue'><%=startRow + i%></font>
																	</td>
																	<td align="center" class="body-data-list"><%=vo.getRelateipaddr()%></td>
																	<td align="center" class="body-data-list"><%=vo.getIfindex()%></td>
																	<td align="center" class="body-data-list"><%=vo.getIpaddress()%></td>
																	<td align="center" class="body-data-list"><%=vo.getMac()%></td>
																	<%
																		int baseflag = 0;
																			String band = "<a href=" + rootPath
																					+ "/ipmac.do?action=update&id=" + vo.getId()
																					+ "&ifband=0>取消绑定</a>";
																			if (vo.getIfband().equals("0")) {
																				band = "<a href=" + rootPath
																						+ "/ipmac.do?action=update&id=" + vo.getId()
																						+ "&ifband=1>绑定</a>";
																			}
																			String sms = "<a href=" + rootPath
																					+ "/ipmac.do?action=update&id=" + vo.getId()
																					+ "&ifsms=0>取消发送</a>";
																			if (vo.getIfsms().equals("0")) {
																				sms = "<a href=" + rootPath + "/ipmac.do?action=update&id="
																						+ vo.getId() + "&ifsms=1>发送</a>";
																			}
																			if (macbaseHash != null && macbaseHash.size() > 0) {
																				if (macbaseHash.containsKey(vo.getMac() + ":"
																						+ vo.getRelateipaddr())) {
																					baseflag = 1;
																				}
																			}
																			if (baseflag == 0) {
																	%>
																	<td align="center" class="body-data-list">
																		<a
																			href="<%=rootPath%>/ipmac.do?action=setmacbase&relateip=<%=vo.getRelateipaddr()%>&ifindex=<%=vo.getIfindex()%>&macip=<%=vo.getIpaddress()%>&mac=<%=vo.getMac()%>"><font
																			color=blue>设为基线</font>
																		</a>
																	</td>
																	<%
																		} else {
																	%>
																	<td align="center" class="body-data-list">
																		<a
																			href="<%=rootPath%>/ipmac.do?action=cancelmacbase&relateip=<%=vo.getRelateipaddr()%>&index=<%=vo.getIfindex()%>&ip=<%=vo.getIpaddress()%>&mac=<%=vo.getMac()%>"><font
																			color=blue>取消基线</font>
																		</a>
																	</td>
																	<%
																		}
																	%>
						
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
