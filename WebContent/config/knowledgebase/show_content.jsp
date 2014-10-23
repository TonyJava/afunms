<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.config.model.Knowledgebase"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.config.model.*"%>
<%
   Knowledgebase vo = (Knowledgebase)request.getAttribute("vo");
   String rootPath = request.getContextPath();
%>
<html><head>
<title>相关知识详细信息</title>

<script language="JavaScript" type="text/javascript">
  function toAdd()
  {
     var args = window.dialogArguments;
     args.xx="<%=vo.getId()%>";
     args.toedit();
  	 window.close();
  }
</script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<meta http-equiv="Pragma" content="no-cache">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>

</head>
<BODY  id="body" class="body" background="<%=rootPath%>/resource/image/bg6.jpg">
<form id="mainForm" method="post" name="mainForm">
<table>
	<tr>
		<td width="16">　</td>
		<td align="center">
		<br>
		<table width="100%" border=0 cellpadding=0 cellspacing=0>
			<tr>
				<td background="<%=rootPath%>/common/images/right_t_02.jpg" width="100%">
				<table width="100%" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
                    <td class="layout_title">事件信息 >> 处理页面 >> 查看详细知识清单</td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>
			  </tr>

			<tr>
			<td colspan="2">
				  <table cellpadding="0" cellspacing="1" width="100%" id="detail-content-body" class="detail-content-body">
						<tr style="background-color: #FFFFFF;">
						    <TD nowrap align="right" height="25" width="10%">标题&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<%=vo.getTitles()%></TD>
						</tr>
						<tr style="background-color: #ECECEC;">	
							<TD nowrap align="right" height="25">类别&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=vo.getCategory()%></TD>						
						</tr>
						
						<tr style="background-color: #FFFFFF;">
							<TD nowrap align="right" height="25">类型&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=vo.getEntity()%></TD>								
						<tr style="background-color: #ECECEC;">
						    <TD nowrap align="right" height="25" width="10%">类别&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<%=vo.getSubentity()%></TD>
						</tr>
						<%String att=vo.getAttachfiles(); %>
						<tr style="background-color: #FFFFFF;">
							<td align="right" height="24">附件&nbsp;</td>
							<td nowrap width="40%" colspan="3" >&nbsp;<a href="#" onclick="window.open('<%=rootPath%>/config/knowledgebase/download.jsp?name=<%=att%>')"><%=vo.getAttachfiles()%></a></td>
						</tr>
						<tr style="background-color: #ECECEC;">
							<TD nowrap align="right" height="25">详细内容&nbsp;</TD>				
							<TD nowrap colspan="3">&nbsp;<textarea cols="50" rows="10" readonly="true"><%=vo.getContents()%></textarea></TD>
						</tr>
						
						<tr style="background-color: #FFFFFF;">
							<TD nowrap colspan="4" align="center">
								<br>
								<!--<input type="button" value="编辑" style="width:50" onclick="toAdd()">&nbsp;&nbsp;-->
								<input type="reset"  style="width:50" value="关闭" onclick="javascript:window.close()">
							</TD>	
						</tr>						
				</TABLE>
				</td>
			</tr>
			<tr>
              <td background="<%=rootPath%>/common/images/right_b_02.jpg" ><table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td align="left" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_01.jpg" width="5" height="12" /></td>
                    <td></td>
                    <td align="right" valign="bottom"><img src="<%=rootPath%>/common/images/right_b_03.jpg" width="5" height="12" /></td>
                  </tr>
              </table></td>
            </tr>	
		</table>
		</td>
	</tr>
</table>
</form>	
</body>
</html>
