<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.application.model.IPNode"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%
  List list = (List)request.getAttribute("list");
  int rc = list.size();

  String rootPath = request.getContextPath();  
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />


<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script>
<script language="javascript">
  var delAction = "<%=rootPath%>/ipnode.do?action=delete";
  var listAction = "<%=rootPath%>/ipnode.do?action=list";
  
  
    function toDelete2()
  {
  if(confirm('是否确定删除这条记录?')) {
     mainForm.action = "<%=rootPath%>/ipnode.do?action=delete";
     mainForm.submit();
     }
  } 
  
  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/ipnode.do?action=ready_add";
     mainForm.submit();
  }
  
  function detail(id)
  {
     window.open("/afunms/detail/ip_detail.jsp?id=" + id,"window","toolbar=no,height=620,width=820,scrollbars=yes,center=yes,screenY=0");
  }
  
</script>
 

<script language="JavaScript" type="text/JavaScript">
var show = true;
var hide = false;
//修改菜单的上下箭头符号
function my_on(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="on";
}
function my_off(head,body)
{
	var tag_a;
	for(var i=0;i<head.childNodes.length;i++)
	{
		if (head.childNodes[i].nodeName=="A")
		{
			tag_a=head.childNodes[i];
			break;
		}
	}
	tag_a.className="off";
}
//添加菜单	
function initmenu()
{
	var idpattern=new RegExp("^menu");
	var menupattern=new RegExp("child$");
	var tds = document.getElementsByTagName("div");
	for(var i=0,j=tds.length;i<j;i++){
		var td = tds[i];
		if(idpattern.test(td.id)&&!menupattern.test(td.id)){					
			menu =new Menu(td.id,td.id+"child",'dtu','100',show,my_on,my_off);
			menu.init();		
		}
	}

}

</script>
</head>
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa" onload="initmenu();">


<form method="post" name="mainForm">
<table id="body-container" class="body-container">
	<tr>
		<td width="16"></td>
		<td bgcolor="#9FB0C4" align="center">
		<br>
		<table width="90%" border=0 cellpadding=0 cellspacing=0>
			<tr>
				<td><img src="<%=rootPath%>/resource/image/main_frame_ltcorner.gif" width=12 height=34></td>
				<td background = "<%=rootPath%>/resource/image/main_frame_tbg.gif" align="right">
				<img border="0" src="<%=rootPath%>/resource/image/main_frame_tflag.gif" width="66" height="34"></td>
				<td><img src="<%=rootPath%>/resource/image/main_frame_rtcorner.gif" width=13 height=34></td>
			</tr>
			<tr>
				<td background="<%=rootPath%>/resource/image/main_frame_lbg.gif" width=12></td>
				<td height=300 bgcolor="#FFFFFF" valign="top"><br>				
					<table cellSpacing="1" cellPadding="0" width="100%" border="0">
						<tr>
							<td colspan="2">
								<table class="microsoftLook" cellspacing="1" cellpadding="0" width="100%">
								<tr><th width='22'></th>
    <th width='30'>序号</th>
    <th width='120'>IP地址</th>
    <th width='160'>名称</th>
    <th width='120'>当前状态</th>
    <th width='120'>详细</th>    
    <th width='30'>编辑</th>
</tr>
<%
    for(int i=0;i<rc;i++)
    {
       IPNode vo = (IPNode)list.get(i);
%>
       <tr class="microsoftLook0" >
    	<td class="microsoftLook0"><INPUT type="radio" class=noborder name=radio value="<%=vo.getId()%>"></td>
    	<td class="microsoftLook0"><font color='blue'><%=1 + i%></font></td>
		<td class="microsoftLook0"><%=vo.getIpAddress()%></td>
		<td class="microsoftLook0"><%=vo.getAlias()%></td> 				
		<td class="microsoftLook0" align='center'><img src="<%=rootPath%>/resource/<%=NodeHelper.getStatusImage(vo.getStatus())%>" border="0"/></td>		
		<td class="microsoftLook0" align='center'><a href="#" onclick='detail(<%=vo.getId()%>)'>
		<img src="<%=rootPath%>/resource/image/detail.jpg" border="0"/></a></td>		
		<td class="microsoftLook0"><a href="<%=rootPath%>/ipnode.do?action=ready_edit&id=<%=vo.getId()%>">
		<img src="<%=rootPath%>/resource/image/editicon.gif" border="0"/></a></td>
  	</tr>
<%}%>  	  				
							</table>
							</td>
						</tr>	
					</table>
					<table cellspacing="5" border="0" >
					    <tr>
			<td><a href="#" onclick="toAdd()">添加</a></td>
			<td><a href="#" onclick="toDelete2()">删除</a></td>
						</tr>
					 </table>				
			</td>
				<td background="<%=rootPath%>/resource/image/main_frame_rbg.gif" width=13></td>
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
</BODY>
</HTML>
