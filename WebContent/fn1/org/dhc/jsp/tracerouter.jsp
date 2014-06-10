 <%@ page language="java" contentType="text/html; charset=gb2312" %>
 <%@ page  import="java.io.*"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%
  String rootPath = request.getContextPath();
%>
<!-- snow add 2010-5-28 -->
<style>
<!--
body{
background-image: url(${pageContext.request.contextPath}/resource/image/bg4.jpg);
TEXT-ALIGN: center; 
}
-->
</style>
<!-- snow add end -->
<title>Tracetouter</title>
<%
	String getIp=(String)request.getParameter("ip") ;
	if(getIp==null) getIp="";
%>
  <script type='text/javascript' src='/afunms/dwr/interface/DWRUtil.js'></script>
  <script type='text/javascript' src='/afunms/dwr/engine.js'></script>
<script type='text/javascript' src='/afunms/dwr/util.js'></script>

<script language="javascript" src="/afunms/js/tool.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css"/>
<script language="javascript">
	function init(){
		//window.resizeTo(100,100);
		//window.open('ping.jsp?ip=10.10.1.1','pingwindow','toolbar=no,height=600,width=500');
		//window.open('','_top'); 
		//window.top.close(1);
		window.resizeTo(600,580);
		document.getElementById("ipaddress").value='<%=getIp.trim() %>';
		<%
			if(getIp!=null&&!"".equals(getIp.trim())){
			%>
			ifExecute('traceroute','execute');
			<%
			}
		%>
		//alert(document.getElementById("ipaddress").value);
	}

	window.onbeforeunload  =  function()   
       {   
            if((event.clientX>document.body.clientWidth&&event.clientY<0)||event.altKey)   
            {          
                // alert("关闭触发");
                 var ip=document.getElementById("ipaddress").value;
         		 closePage('traceroute',ip);
            }
            else
            {
                //alert("刷新触发");
                 var ip=document.getElementById("ipaddress").value;
         		 closePage('traceroute',ip);
            }
        }
 
</script>
</head>
<body id="body" class="body" onload="init()">
<table id="container-main" class="container-main">
	<tr>
		<td>
			<table id="container-main-win" class="container-main-win">
				<tr>
					<td>
						<table id="win-content" class="win-content">
							<tr>
               					<td>
                					<table id="win-content-header" class="win-content-header" >
                						<tr>
			                				<td align="left" width="5"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
							               	<td class="win-content-title">&nbsp;参数设置</td>
							                <td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
									    </tr>
									</table>
								</td>
							</tr>
					       	<tr>
				       			<td>
				       				<table id="win-content-body" class="win-content-body" >
				       					<tr>
				       						<td>
												<table  bgcolor="#ECECEC">
													<tr>
														<td><b>IP</b>：<input type="text" id="ipaddress" name="ipaddress" value="<%=getIp%>"></td>
														<td height="28" align=right>&nbsp;&nbsp;最大跳数：</td>		
														<td height="28">
															<select id="maxjumpnumber" NAME="maxhop">
																<%
																for(int i=1;i<31;i++){
																	if(i==10){
																%>
																<option value=<%=i%> selected><%=i%></option>
																<%
																}else{	  						
																%>
																<option value=<%=i%>><%=i%></option>
																<%
																	}
																}
																%>
														<td align="center"><input type="button"  id="execute" name="traceroute" value="执行" onClick="ifExecute('traceroute','execute')"></td>
													</tr>        
												</table>
							       			</td>
							       		</tr>
							       		<tr>
										   <td class="win-data-title" style="height: 29px;" >&nbsp;路由跟踪[<%=getIp%>]</td>
										</tr>
										<tr align="left" valign="center"> 
                							<td height="28" align="left">
												<textarea id="resultofping" name="display" rows="15" cols="76" readonly="readonly"></textarea>                    			
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
</body>
</html>