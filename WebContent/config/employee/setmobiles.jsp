
<%@ page language="java" contentType="text/html; charset=GBK" %>

<%@ include file="/include/globe.inc"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Vector"%>
<%@ page import="com.afunms.system.model.*"%>
<%
	String rootPath = request.getContextPath();
	Vector ids = (Vector)request.getAttribute("ids");
	List operatorlist = new ArrayList();
	operatorlist = (List)request.getAttribute("list");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head> 
    <title>设置短信接收人</title>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
 
 <script language="JavaScript" fptype="dynamicoutline">
 	function valid(){
 		var oids ="";	
 		for (var i=0;i<mainForm.checkbox.length;i++){
 			if(mainForm.checkbox[i].checked==true){
 				if (oids==""){
 					oids=mainForm.checkbox[i].value;
 				}else{
 					oids=oids+","+mainForm.checkbox[i].value;
 				}
 			}
 		}

 		parent.opener.document.all.sendmobiles.value=oids;
                /*
                 * lijun modify begin;
                 */
                if(typeof(parent.opener.setMobileValue)!="undefined"){
                    parent.opener.setMobileValue();
                }
                /*
                 * lijun modify end;
                 */
		window.close();
	}		 
</script>

 
  </head>
  <body>
  
 <table align="center" cellSpacing="0" cellPadding="0" id="tblListTitle" class="WorkPage_ListTable" width="60%" border="0">
  <form name="mainForm"> 
    <input type=hidden name="oid">
  <tr align="center" valign="middle" class="noprint"> 
 	     <td valign="middle"  class="WorkPage_ListTitle_Left" align=left> 
            <table  height="25" border="0" cellpadding="0" cellspacing="0">
                <tr>                 
                <td width="20%" align=left>&nbsp;</td>
                <td width="80%">&nbsp;设置接收短信的用户</td>
                </tr>
            </table>
            </td>
  </tr>
  <tr><td colspan=10>&nbsp;</td></tr>
  <tr><td colspan=10>
  <table class="tab3"  cellSpacing="0"  cellPadding="3" border=0>
  <tr align="right" > 
  <td colspan=10 align=left height=18>
  <table width="100%" cellSpacing="0"  cellPadding="0" border=0  class="noprint">
  <tr >
  <td align="right" height=18>
  <input type="submit" value="确 定" name="delbutton" class=button onclick="return valid()">

<INPUT type="button" value="关闭窗口" id=button1 name=button1 onclick="window.close()" class="button"> 
           
    </td> </tr> </table>
    </td>
  </tr>
  
  <tr  class="firsttr">
    <td><input type=checkbox name="checkall" onclick="javascript:chkall()"  class="noborder"> <strong></strong><br> </td>
    <td>&nbsp;</td>
    <td><strong>ID</strong></td>
    <td><strong>姓名</strong></td>
    <td><strong>手机号码</strong></td>
    	<td><strong>邮件</strong></td>
    </tr>
  <%
  	int index = 0;
  %>
  <%
  	if (operatorlist != null && operatorlist.size()>0){
  		for(int i=0;i<operatorlist.size();i++){
  			User user = (User)operatorlist.get(i);
  			%>
  			<tr  class="othertr" <%=onmouseoverstyle%> ondblclick="submitCheck1('<%=user.getId()%>','dbclicksubmit')">
  			<%
  			if (ids != null && ids.size()>0){
  				if (ids.contains(user.getId())){
  				%>
    				<td height="20">
    					<input type=checkbox name=checkbox value='<%=user.getId()%>' class=noborder onclick="submitCheck1('<%=user.getId()%>','checkthisbox')" checked>
    				</td>  				
  				<%
  				}else{
  				%>
    				<td height="20">
    					<input type=checkbox name=checkbox value='<%=user.getId()%>' class=noborder onclick="submitCheck1('<%=user.getId()%>','checkthisbox')">
    				</td>  				  				
  				<%
  				}
  			}else{
  			%>
    				<td height="20">
    					<input type=checkbox name=checkbox value='<%=user.getId()%>' class=noborder onclick="submitCheck1('<%=user.getId()%>','checkthisbox')">
    				</td>  				  			
  			<%
  			}%>
    <td onclick="submitCheck1('<%=user.getId()%>','checkthis')">&nbsp;<%=i+1%></td>
    <td onclick="submitCheck1('<%=user.getId()%>','checkthis')">       
      <%=user.getUserid()%></td>
      <td onclick="submitCheck1('<%=user.getId()%>','checkthis')">
      <%=user.getName()%></td>
      <td onclick="submitCheck1('<%=user.getId()%>','checkthis')">
      <%=user.getMobile()%></td>  			
      <td onclick="submitCheck1('<%=user.getId()%>','checkthis')">
      <%=user.getEmail()%></td>  			
  			
  			<%	
  		}
  	}
  %>
</table>
</td></tr></table>
</form> 
  </body>
</html>
