<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%
    String rootPath = request.getContextPath();
    List list = (List) request.getAttribute("list");
    List mylist = (List) session.getAttribute("attachfiles_event_list");
    //更新LIST文件
    list = mylist;
    JspPage jp = (JspPage) request.getAttribute("page");
    String eventid = (String) session.getAttribute("idforknowledge");
%>
<html>
	<head>
		<title>事件处理</title> 
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet"
			type="text/css" />
		<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css"
			rel="stylesheet">
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>

		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css"
			rel="stylesheet">
		<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet"
			type="text/css">
		<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css"
			type="text/css">
		<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet"
			type="text/css">

		<script language="JavaScript" type="text/javascript">
		  	var curpage= <%=jp.getCurrentPage()%>;
		  	var totalpages = <%=jp.getPageTotal()%>;
			var delAction = "<%=rootPath%>/knowledgebase.do?action=delete";
  			var listAction = "<%=rootPath%>/knowledgebase.do?action=hostfind";
		</script>
		<script language="JavaScript" type="text/javascript">
			function myKnowledge(){
			  //alert("进入方法");
			  	mainForm.action = "<%=rootPath%>/knowledge.do?action=hostfind";
			    mainForm.submit();
			  }
			  
			 function myKnowledgebase(){
			  //alert("进入方法");
			  	mainForm.action = "<%=rootPath%>/knowledgebase.do?action=hostfind";
			    mainForm.submit();
			  }
			  
			 function myEvent(){
			  //alert("返回event");
				  mainForm.action = "<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>";
				  mainForm.submit();
			  }
		</script>
		<script type="text/javascript">
		function setClass(){
			document.getElementById('knowledgeDetailTitle-2').className='detail-data-title';
			document.getElementById('knowledgeDetailTitle-2').onmouseover="this.className='detail-data-title'";
			document.getElementById('knowledgeDetailTitle-2').onmouseout="this.className='detail-data-title'";
		}
	</script> 
	</head>
	<BODY leftmargin="0" topmargin="0" bgcolor="#ececec"
		onload="setClass()">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0
			noResize scrolling=no src="<%=rootPath%>/include/calendar.htm"
			style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form method="post" name="mainForm">
			<table id="detail-data" class="detail-data">
				<tr>
					<td class="detail-data-header">
						<%=knowledgeDetailTitleTable%>
					</td>
				</tr>
			</table>
			<table border="0" id="table1" cellpadding="0" cellspacing="0"
				width=100%>
				<tr>
					<td align="center" valign=top bgcolor="#ababab">
						<table width="98%" cellpadding="0" cellspacing="0" algin="center"> 
						<tr>
							<td>
								<table width="100%" cellpadding="0" cellspacing="1">
									<tr>
										<td bgcolor="#ECECEC" width="80%" align="center">
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
							<td colspan="2">
								<table cellspacing="1" cellpadding="0" width="100%"
									style="table-layout: auto;">
									<tr class="microsoftLook0" height=28>
										<th width='5%'>
											序号
										</th>
										<th width='5%'>
											类别
										</th>
										<th width='5%'>
											类型
										</th>
										<th width="5%">
											指标
										</th>
										<th width="10%">
											标题
										</th>
										<th width='10%'>
											附件
										</th>
										<th width='10%'>
											上次修改时间
										</th>
										<th width='10%'>
											添加用户ID
										</th>
										<th width='10%'>
											查看详细内容
										</th>
									</tr>
									<%
									    int startRow = jp.getStartRow();
									    Knowledgebase vo = null;
									    for (int i = 0; i < list.size(); i++) {
											vo = (Knowledgebase) list.get(i);
											String attachfiles = vo.getAttachfiles();
									%>
									<tr bgcolor="#FFFFFF" <%=onmouseoverstyle%>
										class="microsoftLook" height=25>
										<td align='center'>
											<a href="#" style="cursor: hand"
												onclick="window.showModalDialog('<%=rootPath%>/knowledge.do?action=read&id=<%=vo.getId()%>',window,',dialogHeight:400px;dialogWidth:600px')">
												<font color='blue'><%=startRow + i%></font>
											</a>
										</td>
										<td align='center'><%=vo.getCategory()%></td>
										<td align='center'><%=vo.getEntity()%></td>
										<td align='center'><%=vo.getSubentity()%></td>
										<td align='center'><%=vo.getTitles()%></td>
										<td align='center'>
											<a href="#"
												onclick="window.open('<%=rootPath%>/config/knowledgebase/download.jsp?name=<%=attachfiles%>')"><%=vo.getAttachfiles()%></a>
										</td>
										<td align='center'><%=vo.getKtime()%></td>
										<td align='center'><%=vo.getUserid()%></td>
										<td align='center'>
											<a href="#"
												onclick="window.open('<%=rootPath%>/knowledgebase.do?action=show&id=<%=vo.getId()%>')"><img
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
							<td background="<%=rootPath%>/common/images/right_b_02.jpg">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td align="left" valign="bottom">
											<img src="<%=rootPath%>/common/images/right_b_01.jpg"
												width="5" height="12" />
										</td>
										<td></td>
										<td align="right" valign="bottom">
											<img src="<%=rootPath%>/common/images/right_b_03.jpg"
												width="5" height="12" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</tr>
			</table>
	</BODY>
</HTML>
