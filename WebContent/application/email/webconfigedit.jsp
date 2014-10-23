<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%  
   String rootPath = request.getContextPath();  
   BusinessDao bussdao = new BusinessDao();
   List allbuss = bussdao.loadAll();  
%>
<html>
<head>

<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312"/>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script language="javascript">
  function toAdd()
  {
     var chk1 = checkinput("ip_address","ip","IP地址",15,false);
     var chk2 = checkinput("alias","string","机器名",30,false);
     var chk3 = checkinput("community","string","共同体",30,false);
     
     if(chk1&&chk2&&chk3)
     {
        msg.style.display="block";
        mainForm.action = "<%=rootPath%>/network.do?action=webadd";
        mainForm.submit();
     }
  }
  Ext.onReady(function()
{  

setTimeout(function(){
	        Ext.get('loading').remove();
	        Ext.get('loading-mask').fadeOut({remove:true});
	    }, 250);
	
 Ext.get("process").on("click",function(){
  
     //var chk1 = checkinput("ip_address","ip","名称",15,false);
     //var chk2 = checkinput("alias","string","描述",30,false);
     //var chk3 = checkinput("community","string","用户名",30,false);
     
     //if(chk1&&chk2&&chk3)
     {
     
        Ext.MessageBox.wait('数据加载中，请稍后.. '); 
        //msg.style.display="block";
        mainForm.action = "<%=rootPath%>/web.do?action=add";
        mainForm.submit();
     }  
       // mainForm.submit();
 });	
	
});
function CreateWindow(url)
{
msgWindow=window.open(url,"protypeWindow","toolbar=no,width=600,height=400,directories=no,status=no,scrollbars=yes,menubar=no")
}
</script>

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa">
<div id="loading">
	<div class="loading-indicator">
	<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
</div>
<div id="loading-mask" style=""></div>
<form name="mainForm" method="post">
<table id="body-container" class="body-container">
	<tr>
		<td width="200" valign=top align=center >
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;数据库管理</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<a href="<%=rootPath%>/dbtype.do?action=list" ><img src="<%=rootPath%>/resource/image/dbtype.gif" border=0>&nbsp;&nbsp;数据库类型管理</a></td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<a href="<%=rootPath%>/db.do?action=list&jp=1" ><img src="<%=rootPath%>/resource/image/db.gif" border=0>&nbsp;&nbsp;数据库监视</a></td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left"><a href="<%=rootPath%>/oraspace.do?action=list&jp=1" ><img src="<%=rootPath%>/resource/image/oracle.gif" border=0>&nbsp;Oracle告警设置</a></td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<a href="<%=rootPath%>/sqldbconfig.do?action=list&jp=1" ><img src="<%=rootPath%>/resource/image/sqlserver.gif" border=0>&nbsp;&nbsp;SQLServer告警设置</a></td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<a href="<%=rootPath%>/db2config.do?action=list&jp=1" ><img src="<%=rootPath%>/resource/image/db2.gif" border=0>&nbsp;&nbsp;DB2告警设置</a></td>
                    		</tr>   
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<a href="<%=rootPath%>/sybaseconfig.do?action=list&jp=1" ><img src="<%=rootPath%>/resource/image/sybase.gif" border=0>&nbsp;&nbsp;Sybase告警设置</a></td>
                    		</tr>                    		                  		                   		                   		                  		                   		                   										                 										                      								
            		</tbody>
            		</table>  
            		<br>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;服务管理</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/ftp.jpg" border=0>&nbsp;<a href="<%=rootPath%>/user.do?action=list&jp=1">FTP服务监视</a></td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/friend.gif" border=0 width=18>&nbsp;<a href="<%=rootPath%>/role.do?action=list&jp=1">EMail服务监视</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/www.jpg" border=0>&nbsp;<a href="<%=rootPath%>/web.do?action=list&jp=1">WEB访问服务监视</td>
                    		</tr>                     		                   										                 										                      								
            		</tbody>
            		</table>              		           				
            		<br>
			<table width="90%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none w align=center border=1 algin="center">
                        <tbody>                     										                        								
                    		<tr algin="left" valign="center">                      														
                      			<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold">&nbsp;中间件管理</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/mq.jpg" border=0 width=18>&nbsp;<a href="<%=rootPath%>/mq.do?action=list&jp=1">MQ监视</a></td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/setalert.gif" border=0>&nbsp;<a href="<%=rootPath%>/mqchannel.do?action=list&jp=1">MQ告警设置</a></td>
                    		</tr>                    		
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/domino.gif" border=0 width=18>&nbsp;<a href="<%=rootPath%>/domino.do?action=list&jp=1">Domino监视</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/webphere.gif" border=0>&nbsp;<a href="<%=rootPath%>/was.do?action=list&jp=1">WAS监视</td>
                    		</tr> 
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/bea.gif" border=0>&nbsp;<a href="<%=rootPath%>/weblogic.do?action=list&jp=1">Weblogic监视</td>
                    		</tr>
                    		<tr align="left" valign="center"> 
                    			<td height="28" align="left">&nbsp;<img src="<%=rootPath%>/resource/image/tomcat.gif" border=0>&nbsp;<a href="<%=rootPath%>/tomcat.do?action=list&jp=1">Tomcat监视</td>
                    		</tr>                    		                     		                    		                   										                 										                      								
            		</tbody>
            		</table>          
		
		</td>
		<td bgcolor="#cedefa" align="center" valign=top>
		<table width="98%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none border=1 algin="center">
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=3>&nbsp;&nbsp;设备维护 >> 添加设备</td>
				</tr>
			<tr>
				<td height=300 bgcolor="#FFFFFF" valign="top"><br>
				<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
					<TBODY>
						<tr style="background-color: #ECECEC;">						
							<TD nowrap align="right" height="24" width="10%">名称&nbsp;</TD>				
							<TD nowrap width="40%" colspan=3>&nbsp;
								<input type="text" name="name" maxlength="50" size="20" class="formStyle">
							</TD>
						</tr>
						<tr>					
							<TD nowrap align="right" height="24" width="10%">路径&nbsp;</TD>				
							<TD nowrap width="40%" colspan=3>&nbsp;
								<input type="text" name="str" maxlength="50" size="50" class="formStyle" value="http://">
							</TD>
						</tr>
						<tr style="background-color: #ECECEC;">						
							<TD nowrap align="right" height="24" width="10%">超时(毫秒)&nbsp;</TD>				
							<TD nowrap width="40%" colspan=3>&nbsp;
								<input type="text" name="timeout" maxlength="50" size="20" class="formStyle">
							</TD>		
						</tr>
						<tr>					
							<TD nowrap align="right" height="24" width="10%">是否监控&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<select   name="flag"  class="formStyle">
									<option value=0>否</option>
									<option value=1>是</option>
								</select>
							</TD>															
						</tr>
						<tr style="background-color: #ECECEC;"> 
  							<TD nowrap align="right" height="24" width="10%">短信接收人&nbsp;</TD>       
							<TD nowrap width="40%" colspan=3>&nbsp;
							<input type="text" name="sendmobiles" maxlength="32" size="50" value="">&nbsp;&nbsp;<input type=button value="设置短信接收人" onclick="javascript:return CreateWindow('<%=rootPath%>/user.do?action=setwebmobiles')"> 
							</td>
 						</tr>
    						<tr> 
  							<TD nowrap align="right" height="24" width="10%">邮件接收人&nbsp;</TD>       
							<TD nowrap width="40%" colspan=3>&nbsp;
							<input type="text" name="sendemail" maxlength="32" size="50" value="">&nbsp;&nbsp;<input type=button value="设置邮件接收人" onclick="javascript:return CreateWindow('<%=rootPath%>/user.do?action=setwebemail&flag=db')">
							</td>
 						</tr> 
						<tr>						
							<TD nowrap align="right" height="24" width="10%">所属业务&nbsp;</TD>				
							<TD nowrap width="40%" colspan=3>&nbsp;
								<table width="100%" style="BORDER-COLLAPSE: collapse" borderColor=#cedefa cellPadding=0 rules=none w align=center border=1 algin="center">
                        					<tbody>  
                        									<% if( allbuss.size()>0){
           												for(int i=0;i<allbuss.size();i++){
           													Business buss = (Business)allbuss.get(i);
                        									%>                  										                        								
                    										<tr align="left" valign="center"> 
                    											<td height="28" align="left">&nbsp;<INPUT type="checkbox" class=noborder name=checkbox value="<%=buss.getId()%>" >&nbsp;<%=buss.getName()%></td>
                    										</tr>  
                    										<%
                    											}
                    										}
                    										%>                  										                 										                      								
            							</tbody>
            							</table>
							</TD>																			
						</tr> 						 								
						<tr>
							<td colspan="4" align='center'>
								<div id="msg" style="display:none"><font color="#FF0000">需要一点时间,请稍候...</font></div>
							</td>
						</tr>			

						<tr>
							<TD nowrap colspan="4" align=center>
							<br><input type="button" value="保 存" style="width:50" id="process" onclick="#">&nbsp;&nbsp;
								<input type="reset" style="width:50" value="返回" onclick="javascript:history.back(1)">
							</TD>	
						</tr>
					</TBODY>
				</TABLE>				
				</td>
			</tr>			
			</table>
		</td>
	</tr>
</table>
</form>
</body>
</html>