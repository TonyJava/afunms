<%@page language="java" contentType="text/html;charset=gb2312"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.Hashtable"%>
<%
String rootPath = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+rootPath+"/";

String startdate = (String)request.getAttribute("startdate");
String todate = (String)request.getAttribute("todate");

String starttime = (String)request.getAttribute("starttime");
String totime = (String)request.getAttribute("totime");

SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
if(startdate == null)
{ 
   startdate = sdf1.format(new Date());  
}
if(todate == null)
{  
   todate = sdf1.format(new Date());   
}
String dbid = (String)request.getAttribute("dbid");
Vector sql_v = (Vector)request.getAttribute("sqltop");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>   
    
    <title>占用内存sql语句实时信息</title>	
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	<script type="text/javascript" src="<%=rootPath%>/include/swfobject.js"></script>
	<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>

	<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />	
	<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
	<script language="JavaScript" src="<%=rootPath%>/include/date.js"></script>
	<script type="text/javascript" src="<%=rootPath%>/resource/js/jquery-1.4.2.min.js"></script>
	
    <script type="text/javascript" src="<%=rootPath%>/application/resource/js/My97DatePicker/WdatePicker.js"></script>
    
    <script type="text/javascript">
	function dotopQuery(last)
	{
	     mainForm.action = "<%=rootPath%>/oracle.do?action=getrealtimeoratop&dbid=<%=dbid %>&last=" + last;
         mainForm.submit();
	}
	</script>
  </head>
  
  <body>
    <IFRAME frameBorder=0 id=CalFrame marginHeight=0 marginWidth=0 noResize scrolling=no src="<%=rootPath%>/include/calendar.htm" style="DISPLAY: none; HEIGHT: 194px; POSITION: absolute; WIDTH: 148px; Z-INDEX: 100"></IFRAME>
    <form id="mainForm" method="post" name="mainForm">
    <table class="application-detail-data-body">
       <tr height="28" bgcolor="#ECECEC">
          <td colspan="5">
             开始日期
	       <input type="text" name="startdate" value="<%=startdate%>" size="10">
	       <a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar1,document.forms[0].startdate,null,0,330)">
           <img id=imageCalendar1 align=absmiddle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0> </a> 
            开始时间
           <input type="text" id="starttime" name="starttime" value="<%=starttime%>" onfocus="WdatePicker({skin:'whyGreen',dateFmt:'HH:mm:ss'})" class="Wdate"/>
            截止日期
	      <input type="text" name="todate" value="<%=todate%>" size="10" />
	      <a onclick="event.cancelBubble=true;" href="javascript:ShowCalendar(document.forms[0].imageCalendar2,document.forms[0].todate,null,0,330)">
	      <img id=imageCalendar2 align=absmiddle width=34 height=21 src=<%=rootPath%>/include/calendar/button.gif border=0> </a>
	       结束时间
           <input type="text" id="totime" name="totime" value="<%=totime%>" onfocus="WdatePicker({skin:'whyGreen',dateFmt:'HH:mm:ss'})" class="Wdate"/>
          <input type="button" id="process" name="process" value="查 询" onclick="dotopQuery('false')">
          <input type="button" id="process" name="process" value="最 新" onclick="dotopQuery('true')">           
         </td>
      </tr>
      <tr height="28" bgcolor="#ECECEC">
		 <td align=center>
			&nbsp;
	     </td>
		 <td align=center>
			占用内存最多的前十条sql语句
		    &nbsp;&nbsp;			
		 </td>
		 <td align=center>
			占内存读取量的比例
		 </td>
		 <td align=center>
			用户
		 </td>
         <td align=center>
            执行时间
         </td>
	  </tr>
	  <%
				for(int i=0;i<sql_v.size();i++)
				{
					Hashtable ht = (Hashtable)sql_v.get(i);
					String sql_text = ht.get("sql_text").toString();
					String pct_bufgets = ht.get("pct_bufgets").toString();
					String username = ht.get("username").toString();
                    String last_active_time = ht.get("last_active_time").toString();
										
	   %>
	   <tr height="28" bgcolor="#FFFFFF" <%=onmouseoverstyle%>>
	     <td class="application-detail-data-body-list" align=center><%=i+1%></td>
	     <td class="application-detail-data-body-list" style="width:75%;word-break: break-all; word-wrap:break-word;nowrap:false;">
				&nbsp;<%=sql_text%></td>
		 <td class="application-detail-data-body-list" align=center>
				&nbsp;<%=pct_bufgets%></td>
		 <td class="application-detail-data-body-list" align=center>
				&nbsp;<%=username%></td>
         <td class="application-detail-data-body-list" align=center>
                &nbsp;<%=last_active_time%></td>
	   </tr>
	  <%
	          }
	  %>
    </table>       
    </form>
  </body>
</html>
