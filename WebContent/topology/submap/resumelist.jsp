<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@ include file="/include/globe.inc"%>
<%
  String filename = (String)request.getAttribute("filename");
  List list = (List)request.getAttribute("list");
  int rc = list.size();
  String rootPath = request.getContextPath();
%>
<html>
<head>
<title>恢复拓扑图</title>
<base target="_self">
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<meta http-equiv="Pragma" content="no-cache">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link href="<%=rootPath%>/resource/css/global/global.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" 	src="<%=rootPath%>/application/resource/js/timeShareConfigdiv.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/include/navbar.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script language="JavaScript" type="text/javascript">
function resume()
{
	mainForm.action = "<%=rootPath%>/submap.do?action=resume";
	mainForm.target = 'hack';
    mainForm.submit();
    alert("恢复成功!");
	window.close();
}

</script>
<script type="text/javascript" src="<%=rootPath%>/topology/network/js/wincloseoverride.js"></script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#9FB0C4">
<form method="post" name="mainForm">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td align="center" valign="top">
			<table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#ffffff cellPadding=0 rules=none align=center border=1>
				<tr>
					<td height="28" align="left" colspan=3>
							<table id="add-content-header" class="add-content-header">
										                	<tr>
											                	<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
											                	<td class="add-content-title">>> 恢复拓扑图 </td>
											                    <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
											       			</tr>
											        	</table>
					</td>
				</tr>
			    <tr>
				    <td bgcolor="#FFFFFF" valign="top">
                       <input type="hidden" name="xml" value="<%=filename%>">	
	                   <table bgcolor="#efefef" cellSpacing="1" cellPadding="0" width="100%" border="0">
		                   <tr>
			               <td>
			               <table cellspacing="1" cellpadding="0" width="100%" >
			                   <tr class="microsoftLook0" height=28>
	                           <th align='center' width='15%'>&nbsp;序号</th>				
	                           <th align='center' width='85%'>&nbsp;文件名</th>
	                           </tr>
								<%
								    //int startRow = jp.getStartRow();
								    //System.out.println(rc);
								    for(int i=0;i<rc;i++)
								    {
								        String name = (String)list.get(i);
								%>
                               <tr bgcolor="FFFFFF" <%=onmouseoverstyle%> class="microsoftLook">
    	                       <td  align='center'><INPUT type="radio" class=noborder name=radio value="<%=name%>" class=noborder ><font color='blue'><%=i+1%></font></td>    	 	
    	                       <td  align='center'><%=name%></td>			
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
		    </table>
        </td>
    </tr>		
    <tr>
	    <td nowrap align=center>
			<input type="button" value="恢复" style="width:50" class="formStylebutton" onclick="resume()">
			<input type="button" value="关闭" style="width:50" class="formStylebutton" onclick="window.close();">
       </td>
   </tr>
</table>
</form>		
<iframe name="hack" style="display:none;"></iframe>	
</BODY>
</HTML>
