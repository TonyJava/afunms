<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.polling.om.IpMac"%>
<%@ page import="com.afunms.system.model.Department"%>
<%@ page import="com.afunms.system.dao.DepartmentDao"%>
<%@ page import="com.afunms.config.model.Employee"%>
<%@ page import="com.afunms.polling.om.IpMacBase"%>
<%@ page import="com.afunms.config.dao.EmployeeDao"%>
<%@ page import="com.afunms.topology.dao.IpMacBaseDao"%>
<%
  String rootPath = request.getContextPath();
  List list = (List)request.getAttribute("list");
  
  int rc = list.size();

  JspPage jp = (JspPage)request.getAttribute("page");
  String key = (String)request.getAttribute("key");
  String value = (String)request.getAttribute("value");
  System.out.println("key==="+key+"====value:"+value);
  String relateipaddrchecked = "";
  String ipaddresschecked = "";
  String macchecked = "";
  String valuestr = "";
  if(value != null)valuestr=value;
  if(key != null && key.equals("relateipaddr")){
  	relateipaddrchecked = "selected";
  }
  if(key != null && key.equals("ipaddress")){
  	ipaddresschecked = "selected";
  }
  if(key != null && key.equals("mac")){
  	macchecked = "selected";
  }
  IpMacBaseDao dao = new IpMacBaseDao(); 
  List macbaselist = dao.loadAll();
  Hashtable macbaseHash = new Hashtable();
  if(macbaselist != null&& macbaselist.size()>0){
  	for(int i=0;i<macbaselist.size();i++){
  		IpMacBase ipmacbase = (IpMacBase)macbaselist.get(i);
  		macbaseHash.put(ipmacbase.getMac(),ipmacbase);
  	}
  }
%>
<%String menuTable = (String)request.getAttribute("menuTable");%>
<html>
<head>
<script language="JavaScript" type="text/javascript" src="<%=rootPath%>/include/navbar.js"></script>
<link href="<%=rootPath%>/resource/<%=com.afunms.common.util.CommonAppUtil.getSkinPath() %>css/global/global.css" rel="stylesheet" type="text/css" />
<LINK href="<%=rootPath%>/resource/css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" src="<%=rootPath%>/resource/js/page.js"></script> 

<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<LINK href="<%=rootPath%>/resource/css/itsm_style.css" type="text/css" rel="stylesheet">
<link href="<%=rootPath%>/resource/css/detail.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<link href="<%=rootPath%>/include/mainstyle.css" rel="stylesheet" type="text/css">
<script language="javascript">	
  var curpage= <%=jp.getCurrentPage()%>;
  var totalpages = <%=jp.getPageTotal()%>;
  var delAction = "<%=rootPath%>/ipmac.do?action=delete";
  var listAction = "<%=rootPath%>/ipmac.do?action=find";
  
  function doQuery()
  {  
     if(mainForm.key.value=="")
     {
     	alert("请输入查询条件");
     	return false;
     }
     mainForm.action = "<%=rootPath%>/ipmac.do?action=find";
     mainForm.submit();
  }
  
  function doChange()
  {
     if(mainForm.view_type.value==1)
        window.location = "<%=rootPath%>/topology/network/index.jsp";
     else
        window.location = "<%=rootPath%>/topology/network/port.jsp";
  }

  function toAdd()
  {
      mainForm.action = "<%=rootPath%>/network.do?action=ready_add";
      mainForm.submit();
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
<BODY leftmargin="0" topmargin="0" bgcolor="#cedefa"  onload="initmenu();">
<form method="post" name="mainForm">
<table border="0" id="table1" cellpadding="0" cellspacing="0" width=100%>
	<tr>
		<td width="200" valign=top align=center>
	
	<%=menuTable%>
            				
	</td>
		<td bgcolor="#cedefa" align="center" valign="top">
			<table width="98%" style="BORDER-COLLAPSE: collapse" borderColor=#397DBD cellPadding=0 rules=none align=center border=1 algin="center">
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=3>&nbsp;&nbsp;资源管理 >> 设备维护 >> IP/MAC</td>
				</tr>
				<tr>
		
					<td height=300 bgcolor="#FFFFFF" valign="top" align=center>
					<br>
						<table cellSpacing="1" cellPadding="0" width="100%" border="0">
        						<TR>
        							<TD width="50%"><B>查询:</B>
        								<SELECT name="key" style="width=100"> 
          									<OPTION value="relateipaddr" <%=relateipaddrchecked%>>网络设备IP</OPTION>
          									<OPTION value="ipaddress" <%=ipaddresschecked%>>IP地址</OPTION>
          									<OPTION value="mac" <%=macchecked%>>MAC</OPTION>          
          									
          								</SELECT>&nbsp;<b>=</b>&nbsp; 
          								<INPUT type="text" name="value" width="15" class="formStyle" value="<%=valuestr%>">
          								<INPUT type="button" class="formStyle" value="查询" onclick=" return doQuery()">
          							</td> 
          							<td width="50%" align='right'>
									
									<a href="#" onclick="toDelete()">删除</a>&nbsp;&nbsp;&nbsp;
		  						</td>            
								                      
        						</tr>						
						    <tr>
						    <td colspan="2" width="80%" align="center">
    <jsp:include page="../../common/page.jsp">
     <jsp:param name="curpage" value="<%=jp.getCurrentPage()%>"/>
     <jsp:param name="pagetotal" value="<%=jp.getPageTotal()%>"/>
   </jsp:include>
        </td></tr> 						

        						
							<tr>
								<td colspan="2">
									<table cellspacing="1" cellpadding="0" width="100%">
	  									<tr class="microsoftLook0">
      											<th width='5%'><INPUT type="checkbox" class=noborder name="checkall" onclick="javascript:chkall()">序号</th>				
      											<th width='20%'>服务器名称(ip)</th>
      											<th width='5%'>端口</th>
      											<th width='15%'>IP地址</th>      
      											<th width='20%'>MAC</th>
      											<th width='10%'>基线</th>
      											
										</tr>
<%
    IpMac vo = null;
    int startRow = jp.getStartRow();
    for(int i=0;i<rc;i++)
    {
       vo = (IpMac)list.get(i);

          
%>
   										<tr bgcolor="DEEBF7" <%=onmouseoverstyle%> height=25>  
    											<td ><INPUT type="checkbox" class=noborder name=checkbox value="<%=vo.getId()%>">
    												<font color='blue'><%=startRow + i%></font></td>
    											<td  align='center' style="cursor:hand"><%=vo.getRelateipaddr()%></td>
    											<td  align='center'><%=vo.getIfindex()%></td>
    											<td  align='center'><%=vo.getIpaddress()%></td>
    											<td  align='center'><%=vo.getMac()%></td>
    											<%
    												int baseflag = 0;
    												String band="<a href="+rootPath+"/ipmac.do?action=update&id="+vo.getId()+"&ifband=0>取消绑定</a>";
    												if(vo.getIfband().equals("0")){
    													band="<a href="+rootPath+"/ipmac.do?action=update&id="+vo.getId()+"&ifband=1>绑定</a>";
    												}
    												String sms = "<a href="+rootPath+"/ipmac.do?action=update&id="+vo.getId()+"&ifsms=0>取消发送</a>";
    												if(vo.getIfsms().equals("0")){
    													sms="<a href="+rootPath+"/ipmac.do?action=update&id="+vo.getId()+"&ifsms=1>发送</a>";
    												}
    												if(macbaseHash != null && macbaseHash.size()>0){
    													if(macbaseHash.containsKey(vo.getMac())){
    														baseflag = 1;
    													}
    												}
    												if(baseflag == 0){
    											%>
    											<td  align='left' bgcolor=#99CC00><a href="<%=rootPath%>/ipmac.do?action=selsetmacbase&relateip=<%=vo.getRelateipaddr()%>&ifindex=<%=vo.getIfindex()%>&macip=<%=vo.getIpaddress()%>&mac=<%=vo.getMac()%>&key=<%=key%>&value=<%=value%>"><font color=blue>设为基线</font></a></td>
    											<%
    												}else{
    											%>
    											<td  align='left'><a href="<%=rootPath%>/ipmac.do?action=selcancelmacbase&relateip=<%=vo.getRelateipaddr()%>&index=<%=vo.getIfindex()%>&ip=<%=vo.getIpaddress()%>&mac=<%=vo.getMac()%>&key=<%=key%>&value=<%=value%>"><font color=blue>取消基线</font></a></td>
    											<%
    												}
    											%>
        										
  										</tr>			
<% }%>
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
</form>
</BODY>
</HTML>
