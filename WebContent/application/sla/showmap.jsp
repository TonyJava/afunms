<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@ include file="/include/globe.inc"%>
<%
	String rootPath = request.getContextPath();
	String menuTable = (String) request.getAttribute("menuTable");
	String slatype=(String)request.getAttribute("slatype");
	String title="";
	if(slatype!=null){
		if(slatype.equals("icmp")){
			 title="ICMP端到端的响应 >> ICMP性能拓扑图";
		}else if(slatype.equals("jitter")){
			 title="UDP端到端抖动 >> UDP抖动性能拓扑图";
		}else if(slatype.equals("udp")){
			title="UDP端到端的响应 >> UDP性能拓扑图";
		}else if(slatype.equals("tcp")){
			title="TCP端到端的响应 >> TCP性能拓扑图";
		}
	
	}
%>
<html>
	<head>
		<script language="JavaScript" type="text/javascript"
			src="<%=rootPath%>/include/navbar.js"></script>
		<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath()%>css/global/global.css"
			rel="stylesheet" type="text/css" />

		<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
		<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
		<meta http-equiv="Page-Enter" content="revealTrans(duration=x, transition=y)">
		<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
		
		<script language="JavaScript" type="text/javascript">
  
</script>
	
<script language="JavaScript" type="text/JavaScript">

</script>
	</head>
	<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa">


		<form method="post" name="mainForm">
			<table id="body-container" class="body-container">
				<tr>
					<td width="200" valign=top align=center>
						<%=menuTable%>
					</td>
					<td align="center" valign=top>
						<table style="width: 98%" cellpadding="0" cellspacing="0" algin="center">
							<tr>
								<td background="<%=rootPath%>/common/images/right_t_02.jpg"
									width="100%">
									<table width="100%" cellspacing="0" cellpadding="0">
										<tr>
											<td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /> </td>
											<td class="content-title">服务质量 >> <%=title %> </td>
											<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" />
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td width="100%">
									<table width="100%" cellspacing="0" cellpadding="0">
										<tr>
											<td bgcolor="#FFFFFF" align=center>
                                                   <iframe src="<%=rootPath%>/application/sla/iframeTopo.jsp?slatype=<%=slatype %>" width="980" height="550" frameborder="0" scrolling="no"></iframe>
											
                                            </td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
					</td>
			

				</tr>


				
			</table>
	</BODY>
</HTML>
