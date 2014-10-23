<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.polling.node.*"%>
<%@page import="com.afunms.polling.*"%>
<%@page import="java.util.List"%>
<%@ include file="/include/globe.inc"%>

<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<script type="text/javascript"
			src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>

		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script language="javascript">	
  
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
  
    function doCancelManage()
  {  
     mainForm.action = "<%=rootPath%>/network.do?action=cancelmanage";
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


</script>
		<script language="JavaScript" type="text/javascript">
  function toCheck()
  {
     if(FrmDeal.pwd.value=="")
     {
        alert("<密码>不能为空!");
        FrmDeal.pwd.focus();
        return false;
     }
     FrmDeal.action = "check.jsp";
     FrmDeal.submit();
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

	//网络设备的ip地址
	function modifySystemConfigflagAjax(flagkey){
		var t = document.getElementById(flagkey);
		var flagvalue = t.options[t.selectedIndex].value;//获取下拉框的值
		$.ajax({
				type:"GET",
				dataType:"json",
				url:"<%=rootPath%>/systemConfigAjaxManager.ajax?action=updateSystemconfigFlag&flagkey="+flagkey+"&flagvalue="+flagvalue,
				success:function(data){
					window.alert(data.message);
				},
				error:function(data){ 
					window.alert(data.message); 
				}
		});
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
									                	<td class="content-title"> 系统管理 >> 系统配置 >> 系统运行模式配置 </td>
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
											<td align="center" class="body-data-title">
												<!--table begin-->
												<table width="350px;" cellpadding="0"
													bordercolorlight='#000000' bordercolordark='#FFFFFF'
													height="100px;">
													<tr class="othertr1">
														<td align="center">
															&nbsp;&nbsp;系统运行模式:
															<select style="width: 110px;" id="collectwebflag">
																<%
																	String collectwebflag = PollingEngine.getCollectwebflag();
																%>
																<option <%if("0".equals(collectwebflag)){ %> selected
																	<%} %> value='0'>
																	采集与访问集成
																</option>
																<option <%if(!"0".equals(collectwebflag)){ %> selected
																	<%} %> value='1'>
																	采集与访问分离
																</option>
															</select>
															<input type="button" class="button" value="修 改"
																style="width: 80"
																onclick="modifySystemConfigflagAjax('collectwebflag');">
														</td>
													</tr>
													<tr class="othertr1">
														<td align="center">
															资源树显示模式:
															<select style="width: 110px;" id="treeshowflag">
																<%
																	String treeshowflag = PollingEngine.getTreeshowflag();
																%>
																<option <%if("0".equals(treeshowflag)){ %> selected
																	<%} %> value='0'>
																	隐藏物设备节点
																</option>
																<option <%if(!"0".equals(treeshowflag)){ %> selected
																	<%} %> value='1'>
																	显示无设备节点
																</option>
															</select>
															<input type="button" class="button" value="修 改"
																style="width: 80"
																onclick="modifySystemConfigflagAjax('treeshowflag');">
														</td>
													</tr>
													<tr class="othertr1">
														<td align="center">
															&nbsp;&nbsp;定时报表发送:
															<select style="width: 110px;" id="sendReportFlag">
																<%
																	String sendReportFlag = PollingEngine.getSendReportFlag();
																System.out.println("ss"+sendReportFlag);
																String selected0="";
																String selected1="";
																if(sendReportFlag==null||sendReportFlag.equals("0")){
																	selected0="";
																}else if(!sendReportFlag.equals("0")){
																	selected1="selected";
																}
																%>
																<option <%=selected0 %> value='0'>未启动</option>
																<option <%=selected1 %> value='1'>启动</option>
															</select>
															<input type="button" class="button" value="修 改"
																style="width: 80"
																onclick="modifySystemConfigflagAjax('sendReportFlag');">
														</td>
													</tr>
												</table>
												<!--table end-->
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
</html>
