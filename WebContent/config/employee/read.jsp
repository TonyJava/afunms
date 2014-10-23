<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.util.UserView"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%
   UserView view = new UserView();
   User vo = (User)request.getAttribute("vo");
   String rootPath = request.getContextPath();
%>
<html><head>
<title>dhcnms</title>

<script language="JavaScript" type="text/javascript">
  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/user.do?action=ready_edit";
     mainForm.submit();
  }
</script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4">
<form name="mainForm" method="post">
<input type=hidden name="id" value="<%=vo.getId()%>">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="16">　</td>
		<td bgcolor="#9FB0C4" align="center">
		<br>
		<table width="95%" border=0 cellpadding=0 cellspacing=0>
			<tr>
				<td><img src="<%=rootPath%>/resource/image/main_frame_ltcorner.gif" width=12 height=34></td>
				<td background="<%=rootPath%>/resource/image/main_frame_tbg.gif" align="right">
				<img border="0" src="<%=rootPath%>/resource/image/main_frame_tflag.gif" width="66" height="34"></td>
				<td><img src="<%=rootPath%>/resource/image/main_frame_rtcorner.gif" width=13 height=34></td>
			</tr>
			<tr>
				<td background="<%=rootPath%>/resource/image/main_frame_lbg.gif" width=12>　</td>
				<td height=300 bgcolor="#FFFFFF" valign="top"  align="center">
				<form name="AssetsForm" method="post" action="/itsm/operation/Assets.do?method=add" onsubmit="return validateAssetsForm(this);"> 
					<input type="hidden" name="id" value="">
					<input type="hidden" name="category" value="25">
					<input type="hidden" name="code" value="DATABASE">
				  <table border="0" id="table1" cellpadding="0" cellspacing="1"
						width="95%">
					<TBODY>
						<tr>
							<TD align="left" colspan="4" height="24">用户--查看</TD>
						</tr>
						<tr>
							<td nowrap colspan="4" height="3" bgcolor="#8EADD5"></td>
						</tr>
						<tr style="background-color: #ECECEC;">
						    <TD nowrap align="right" height="24" width="10%">帐号&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;<%=vo.getUserid()%></TD>
							<TD nowrap align="right" height="24">姓名&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=vo.getName()%></TD>	
						</tr>
<%
       String sex = null;
       if(vo.getSex()==1) sex="男";
       else sex="女";
%>						
						<tr>
							<TD nowrap align="right" height="24">性别&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=sex%></TD>								
							<td align="right" height="20">角色&nbsp;</td>
							<td colspan="3" align="left">&nbsp;<%=view.getRole(vo.getRole())%></td>
						</tr>
						<tr style="background-color: #ECECEC;">
							<TD nowrap align="right" height="24">部门&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=view.getDept(vo.getDept())%></TD>								
							<td align="right" height="20">职务&nbsp;</td>
							<td colspan="3" align="left">&nbsp;<%=view.getPosition(vo.getPosition())%></td>
						</tr>
						<tr>
							<TD nowrap align="right" height="24">电话&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=SysUtil.ifNull(vo.getPhone())%></TD>								
							<td align="right" height="20">手机&nbsp;</td>
							<td colspan="3" align="left">&nbsp;<%=SysUtil.ifNull(vo.getMobile())%></td>
						</tr>
						<tr style="background-color: #ECECEC;">
							<TD nowrap align="right" height="24">Email&nbsp;</TD>				
							<TD nowrap>&nbsp;<%=SysUtil.ifNull(vo.getEmail())%></TD>								
							<td align="right" height="20">&nbsp;</td>
							<td colspan="3" align="left">&nbsp;</td>
						</tr>											
						<tr>
							<td nowrap colspan="4" height="1" bgcolor="#8EADD5"></td>
						</tr>
						<tr>
							<TD nowrap colspan="4">
								<br>
								<input type="button" value="编辑" style="width:50" class="formStylebutton" onclick="toAdd()">&nbsp;&nbsp;
								<input type=reset class="formStylebutton" style="width:50" value="返回" onclick="javascript:history.back(1)">
							</TD>	
						</tr>						
					</TBODY>
				</TABLE>
				</td>
				<td background="<%=rootPath%>/resource/image/main_frame_rbg.gif" width=13 colspan="3">　</td>
			</tr>
			<tr>
				<td valign="top"><img src="<%=rootPath%>/resource/image/main_frame_lbcorner.gif" width=12 height=15></td>
				<td background="<%=rootPath%>/resource/image/main_frame_bbg.gif" height=15></td>
				<td valign="top"><img src="<%=rootPath%>/resource/image/main_frame_rbcorner.gif" width=13 height=15></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>	
</body>
</html>
