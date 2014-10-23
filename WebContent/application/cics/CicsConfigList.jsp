<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.CicsConfig"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="com.afunms.polling.base.Node"%>
<%
    String rootPath = request.getContextPath();
    List list = (List) request.getAttribute("list");
%>
<%
    String menuTable = (String) request.getAttribute("menuTable");
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

		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link
			href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script language="javascript">	
  var delAction = "<%=rootPath%>/network.do?action=webdelete";
  var listAction = "<%=rootPath%>/network.do?action=list";
  
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/network.do?action=find";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toFind()
  {
      mainForm.action = "<%=rootPath%>/cics.do?action=ready_find";
      mainForm.submit();
  }
  
  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/cics.do?action=ready_add";
      mainForm.submit();
  }
  
  function toDelete()
  {
     mainForm.action = "<%=rootPath%>/cics.do?action=delete";
     mainForm.submit();
  }

</script>
	</head>
	<BODY>
		<form method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td width="200" valign=top align=center>
						<%=menuTable%>
					</td>
					<td align="center" valign=top>
						<table style="width: 98%" cellpadding="0" cellspacing="0"
							algin="center">
							<tr>
								<td background="<%=rootPath%>/common/images/right_t_02.jpg"
									width="100%">
									<table id="content-header" class="content-header">
										<tr>
											<td align="left" width="5">
												<img src="<%=rootPath%>/common/images/right_t_01.jpg"
													width="5" height="29" />
											</td>
											<td class="content-title">
												&nbsp;应用 >> 中间件管理 >> CICS监视列表
											</td>
											<td align="right">
												<img src="<%=rootPath%>/common/images/right_t_03.jpg"
													width="5" height="29" />
											</td>
										</tr>
									</table>
								</td>
							<tr>
								<td>
									<table width="100%" cellpadding="0" cellspacing="0">
										<tr>
											<td width="100%" style="text-align: right;"
												class="body-data-title">
												<a href="#" onclick="toFind()">发现服务</a>
												<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<table cellspacing="0" cellpadding="0" width="100%">
										<tr height=28>
											<td align="center" class="body-data-title">
												<INPUT type="checkbox" class=noborder name="checkall"
													onclick="javascript:chkall()">
											</td>
											<td align="center" class="body-data-title">
												序号
											</td>
											<td align="center" class="body-data-title">
												服务名称
											</td>
											<td align="center" class="body-data-title">
												主机名或IP地址
											</td>
											<td align="center" class="body-data-title">
												网络协议
											</td>
											<td align="center" class="body-data-title">
												监听端口
											</td>
											<td align="center" class="body-data-title">
												是否监控
											</td>
											<td align="center" class="body-data-title">
												当前状态
											</td>
											<td align="center" class="body-data-title">
												Gateway
											</td>
											<td align="center" class="body-data-title">
												操作
											</td>
										</tr>
										<%
										    CicsConfig vo = null;
										    for (int i = 0; i < list.size(); i++) {
										        vo = (CicsConfig) list.get(i);
										        Node node = PollingEngine.getInstance().getCicsByID(vo.getId());
										        String alarmmessage = "";
										        if (node != null) {
										            vo.setStatus(1);
										            if (node.isAlarm())
										                vo.setStatus(3);
										            List alarmlist = node.getAlarmMessage();
										            if (alarmlist != null && alarmlist.size() > 0) {
										                for (int k = 0; k < alarmlist.size(); k++) {
										                    alarmmessage = alarmmessage + alarmlist.get(k).toString();
										                }
										            }
										        } else {
										        }
										%>
										<tr height=25>
											<td align="center" class="body-data-list">
												<INPUT type="checkbox" name=checkbox class=noborder
													value="<%=vo.getId()%>">
												<font color='blue'><%=1 + i%></font>
											</td>
											<td align="center" class="body-data-list"><%=vo.getRegion_name()%></td>
											<td align="center" class="body-data-list">
												<font color="#397DBD"><%=vo.getIpaddress()%></font>
											</td>
											<td align="center" class="body-data-list"><%=vo.getNetwork_protocol()%></td>
											<td align="center" class="body-data-list"><%=vo.getPort_listener()%></td>
											<%
											    if (vo.getFlag() == 0) {
											%>
											<td align="center" class="body-data-list">
												否
											</td>
											<%
											    } else {
											%>
											<td align="center" class="body-data-list">
												是
											</td>
											<%
											    }
											%>
											<td align="center" class="body-data-list">
												<img
													src="<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(vo.getStatus())%>"
													border="0" alt="<%=alarmmessage%>" />
											</td>
											<td align="center" class="body-data-list"><%=vo.getGateway()%></td>
											<td align="center" class="body-data-list">
												<input type="hidden" id="id" name="id"
													value="<%=vo.getId()%>">
												<img class="img"
													src="<%=rootPath%>/resource/image/status.gif" border="0"
													width=15 alt="右键操作">
											</td>
										</tr>
										<%
										    ;
										    }
										%>
									</table>
								</td>
							</tr>
							<tr>
								<td background="<%=rootPath%>/common/images/right_b_02.jpg">
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td align="left" valign="bottom">
												<img src="<%=rootPath%>/common/images/right_b_01.jpg"
													width="5" height="11" />
											</td>
											<td></td>
											<td align="right" valign="bottom">
												<img src="<%=rootPath%>/common/images/right_b_03.jpg"
													width="5" height="11" />
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
				detail(id);
			}
		},{
                  text :'取消监视',
                  icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/cancelMng.gif',
                  action: function(target){
                  	var id=$($(target).parent()).find('#id').val();
                  	cancelmanager(id);
                  }
              },{
                  text :'添加监视',
                  icon :'<%=rootPath%>/common/contextmenu/skins/default/icons/addMn.gif',
                  action: function(target){
                  	var id=$($(target).parent()).find('#id').val();
                      addmanager(id);
                  }
              }]
	});	
	
//右键菜单方法开始
	function detail(node)
	{
	    location.href="<%=rootPath%>/cics.do?action=detail&id="+node;
	}
	function edit(node)
	{
		location.href="<%=rootPath%>/cics.do?action=ready_edit&id="+node;
	}
	function cancelmanage(node)
	{
		location.href="<%=rootPath%>/cics.do?action=cancelalert&id="+node;
	}
	function addmanage(node)
	{
		location.href="<%=rootPath%>/cics.do?action=addalert&id="+node;
	}	
</script>
</HTML>
