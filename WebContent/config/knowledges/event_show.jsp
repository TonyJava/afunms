<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@ include file="/include/globeChinese.inc"%>
<%
    String rootPath = request.getContextPath();
%>
<%
    String eventid = (String) session.getAttribute("idforknowledge");
%>
<%
    String filename = (String) request
		    .getAttribute("attachfiles_event");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>事件处理</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<!--<link rel="stylesheet" type="text/css" href="styles.css">-->
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
		<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css"> 
		<script src="<%=rootPath%>/config/knowledges/AC_PDF.js" language="javascript"/>
		<script type="text/javascript">
			function ss(){
				var width=window.screen.width;
				var height=window.screen.height;
				document.getElementById("pdfshow").width=width;
				document.getElementById("pdfshow").height=height;
			};
		</script>

		<script language="javascript" type="text/javascript">
	        check = isAcrobatPluginInstall();
			if(!check)
			{
			    if(window.confirm("您的机器上没有安装Acrobat Reader，是否现在安装？")) 
			    {
					window.open( "http://www.adobe.com/cn/products/reader/")
				} 
				else
				{
				    alert("您的机器没有安装 Acrobat Reader ， 可能就不能正常显示下面的文档");
				}
			}
    	</script>
		<script type="text/javascript">
	        var ua = navigator.userAgent.toLowerCase();
	        var isIE  = (ua.indexOf("msie") != -1) ? true : false;
	        if(!isIE)
	        {
	            document.write("pdf内容不能正常显示，需要用IE6.0或6.0以上的浏览器<br>");
	        }
    	</script>
		<script language="JavaScript" type="text/javascript">
			function myKnowledge(){
				setClass();
				mainForm.action = "<%=rootPath%>/knowledge.do?action=hostfind";
				mainForm.submit();
			}	  
			function myKnowledgebase(){
				mainForm.action = "<%=rootPath%>/knowledgebase.do?action=hostfind";
				mainForm.submit();
			}	  
			function myEvent(){
				mainForm.action = "<%=rootPath%>/alarm/event/accitevent.jsp?eventid=<%=eventid%>";
				mainForm.submit();
			}
		</script>
		<script type="text/javascript">
			function setClass(){
				document.getElementById('knowledgeDetailTitle-1').className='detail-data-title';
				document.getElementById('knowledgeDetailTitle-1').onmouseover="this.className='detail-data-title'";
				document.getElementById('knowledgeDetailTitle-1').onmouseout="this.className='detail-data-title'";
				ss();
			}
		</script>
	</head> 
	<body leftmargin="0" topmargin="0" onload="setClass()">
		<IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
		<form method="post" name="mainForm">
			<table id="detail-data" class="detail-data">
				<tr>
					<td class="detail-data-header">
						<%=knowledgeDetailTitleTable%>
					</td>
				</tr>
			</table> 
			<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
				<tr>
					<td align="center" valign=top bgcolor="#ababab">
						<table width="98%" cellpadding="0" cellspacing="0" algin="center"> 
							<tr>
								<td>
								<%
								    if (filename != null) {
										out.println("<table>");
										out.println("<tr><td  height='28' colspan='2' align='left' bordercolor='#ececec' bgcolor='#ececec'>方案为：" + filename + "</td></tr>");
										out.println("<tr>");
										out.println("<td>");
										out.println("<embed width='100%' height='800' src='" + rootPath
											+ "/config/knowledges/pdf/" + filename
											+ "' vmmode='transparent' id='pdfshow'></embed>");
										out.println("</td>");
										out.println("</tr>");
										out.println("</table>");
								    } else {
										out.println("<h3>对不起，您未上传相应方案</h3>");
										out.println("请您上传相应方案");
								    }
								%>
								</td>
							</tr> 
						</table>
					</td>
				</tr> 
			</table>
		</form>
	</body>
</html>
