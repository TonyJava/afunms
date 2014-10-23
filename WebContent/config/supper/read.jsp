<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@ include file="/include/globe.inc"%>
<%@page import="com.afunms.config.model.Supper"%>
<%
   Supper vo = (Supper)request.getAttribute("vo");
   String rootPath = request.getContextPath();
%>
<html><head>
<title>供应商详细信息</title>

<script language="JavaScript" type="text/javascript">
  function toAdd()
  {
     var args = window.dialogArguments;
     args.xx="<%=vo.getSu_id()%>";
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
                    <td class="layout_title">系统管理 >> 用户管理 >> 查看供应商信息</td>
                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
                  </tr>
              </table>
			  </td>
			  </tr>

			<tr>
			<td colspan="2">
				  <table cellpadding="0" cellspacing="1" width="100%" id="detail-content-body" class="detail-content-body">
						<tr style="background-color: #FFFFFF;">
						    <TD nowrap align="right" height="25" width="10%">供应商的名称&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<%=vo.getSu_name()%></TD>
							<TD nowrap align="right" height="25">供应商类别&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=vo.getSu_class()%></TD>	
						</tr>
						<tr style="background-color: #ECECEC;">	
							<TD nowrap align="right" height="25">供应商所在的地区&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=vo.getSu_area()%></TD>						
							<TD nowrap align="right" height="25">供应商联系人的姓名&nbsp;</TD>
							<TD nowrap>&nbsp;<%=vo.getSu_person()%></TD>	
						</tr>
						
						<tr style="background-color: #FFFFFF;">
							<TD nowrap align="right" height="25">供应商联系人地址&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=vo.getSu_address()%></TD>								
							<TD nowrap align="right" height="25">供应商联系人部门&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=vo.getSu_dept()%></TD>	
						</tr>
						<tr style="background-color: #ECECEC;">
						    <TD nowrap align="right" height="25" width="10%">供应商联系人电子邮件&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<%=vo.getSu_email()%></TD>
							<TD nowrap align="right" height="25">供应商联系人电话&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=vo.getSu_phone()%></TD>	
						</tr>
						<tr style="background-color: #FFFFFF;">
							<td align="right" height="24">供应商公司网址&nbsp;</td>
							<td nowrap width="40%" colspan="3" >&nbsp;<%=vo.getSu_url()%></td>
						</tr>
						<tr style="background-color: #ECECEC;">
							<TD nowrap align="right" height="25">供应商的描述&nbsp;</TD>				
							<TD nowrap colspan="3">&nbsp;<textarea cols="50" rows="5" readonly="true"><%=vo.getSu_desc()%></textarea></TD>
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
