<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.system.model.User"%>
<%@page import="com.afunms.common.base.JspPage"%>
<%@page import="java.util.List"%>
<%@page import="com.afunms.system.util.UserView"%>
<%@ include file="/include/globe.inc"%>
<%@page import="java.util.*"%>
<%@page import="com.afunms.topology.util.NodeHelper"%>
<%@page import="com.afunms.application.model.*"%>
<%@page import="com.afunms.application.dao.*"%>
<%@page import="com.afunms.config.model.*"%>
<%@page import="com.afunms.config.dao.*"%>
<%
  String rootPath = request.getContextPath();
  BusinessDao bussdao = new BusinessDao();
  RadarConfig vo = (RadarConfig)request.getAttribute("vo");
  System.out.println(vo.getInter()+"===========");
   List allbuss = bussdao.loadAll();  
  String bid = vo.getNetid();
  String id[] = bid.split(",");
  List bidlist = new ArrayList();
  if(id != null &&id.length>0){
	for(int i=0;i<id.length;i++){
		bidlist.add(id[i]);
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
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<link rel="stylesheet" type="text/css" 	href="<%=rootPath%>/js/ext/lib/resources/css/ext-all.css" charset="gb2312" />
<link rel="stylesheet" type="text/css" href="<%=rootPath%>/js/ext/css/common.css" charset="gb2312"/>
<script type="text/javascript" 	src="<%=rootPath%>/js/ext/lib/adapter/ext/ext-base.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/ext-all.js" charset="gb2312"></script>
<script type="text/javascript" src="<%=rootPath%>/js/ext/lib/locale/ext-lang-zh_CN.js" charset="gb2312"></script>
<script language="JavaScript" type="text/javascript">
  var delAction = "<%=rootPath%>/db.do?action=delete";
  var listAction = "<%=rootPath%>/db.do?action=list";

  function toAdd()
  {
     mainForm.action = "<%=rootPath%>/radar.do?action=ready_add";
     mainForm.submit();
  }
  function toDelete()
  {
     mainForm.action = "<%=rootPath%>/radar.do?action=delete";
     mainForm.submit();
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
        mainForm.action = "<%=rootPath%>/radar.do?action=update";
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
<script language="JavaScript">

	//公共变量
	var node="";
	var ipaddress="";
	var operate="";
	/**
	*根据传入的id显示右键菜单
	*/
	function showMenu(id,nodeid,ip)
	{	
		ipaddress=ip;
		node=nodeid;
		//operate=oper;
	    if("" == id)
	    {
	        popMenu(itemMenu,100,"100");
	    }
	    else
	    {
	        popMenu(itemMenu,100,"1111");
	    }
	    event.returnValue=false;
	    event.cancelBubble=true;
	    return false;
	}
	/**
	*显示弹出菜单
	*menuDiv:右键菜单的内容
	*width:行显示的宽度
	*rowControlString:行控制字符串，0表示不显示，1表示显示，如“101”，则表示第1、3行显示，第2行不显示
	*/
	function popMenu(menuDiv,width,rowControlString)
	{
	    //创建弹出菜单
	    var pop=window.createPopup();
	    //设置弹出菜单的内容
	    pop.document.body.innerHTML=menuDiv.innerHTML;
	    var rowObjs=pop.document.body.all[0].rows;
	    //获得弹出菜单的行数
	    var rowCount=rowObjs.length;
	    //alert("rowCount==>"+rowCount+",rowControlString==>"+rowControlString);
	    //循环设置每行的属性
	    for(var i=0;i<rowObjs.length;i++)
	    {
	        //如果设置该行不显示，则行数减一
	        var hide=rowControlString.charAt(i)!='1';
	        if(hide){
	            rowCount--;
	        }
	        //设置是否显示该行
	        rowObjs[i].style.display=(hide)?"none":"";
	        //设置鼠标滑入该行时的效果
	        rowObjs[i].cells[0].onmouseover=function()
	        {
	            this.style.background="#397DBD";
	            this.style.color="white";
	        }
	        //设置鼠标滑出该行时的效果
	        rowObjs[i].cells[0].onmouseout=function(){
	            this.style.background="#F1F1F1";
	            this.style.color="black";
	        }
	    }
	    //屏蔽菜单的菜单
	    pop.document.oncontextmenu=function()
	    {
	            return false; 
	    }
	    //选择右键菜单的一项后，菜单隐藏
	    pop.document.onclick=function()
	    {
	        pop.hide();
	    }
	    //显示菜单
	    pop.show(event.clientX-1,event.clientY,width,rowCount*25,document.body);
	    return true;
	}
	function detail()
	{
	    location.href="<%=rootPath%>/radar.do?action=check&id="+node;
	}
	function edit()
	{
		location.href="<%=rootPath%>/radar.do?action=ready_edit&id="+node;
	}
	function cancelmanage()
	{
		location.href="<%=rootPath%>/radar.do?action=cancelmanage&id="+node;
	}
	function addmanage()
	{
		location.href="<%=rootPath%>/radar.do?action=addmanage&id="+node;
	}	
	function clickMenu()
	{
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

<div id="loading">
	<div class="loading-indicator">
	<img src="<%=rootPath%>/js/ext/lib/resources/extanim64.gif" width="32" height="32" style="margin-right: 8px;" align="middle" />Loading...</div>
</div>
<!-- 这里用来定义需要显示的右键菜单 -->
	<div id="itemMenu" style="display: none";>
	<table border="1" width="100%" height="100%" bgcolor="#F1F1F1"
		style="border: thin;font-size: 12px" cellspacing="0">
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.edit()">修改信息</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.detail();">监视信息</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.cancelmanage()">取消监视</td>
		</tr>
		<tr>
			<td style="cursor: default; border: outset 1;" align="center"
				onclick="parent.addmanage()">添加监视</td>
		</tr>		
	</table>
	</div>
	<!-- 右键菜单结束-->
<form method="post" name="mainForm">
<input type=hidden name="id" value="<%=vo.getId()%>">
<table id="body-container" class="body-container">
	<tr>
		<td width="200" valign=top align=center>
			<%=menuTable%>
		
		</td>
		<td bgcolor="#ffffff" align="center" valign=top>
				
				<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%">
					<TBODY>
				<tr>
					<td height="28" align="left" bordercolor="#397DBD" bgcolor="#397DBD" class="txtGlobalBold" colspan=4>&nbsp;&nbsp;<font color=#ffffff>应用 >> 中间件管理 >> 修改MQ</font></td>
				</tr>
						<tr style="background-color: #ECECEC;">						
							<TD nowrap align="right" height="24" width="10%"><font color="red">*&nbsp;</font>名称&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="name" maxlength="50" size="20" class="formStyle" value="<%=vo.getName()%>">
							</TD>
							<TD nowrap align="right" height="24" width="10%"><font color="red">*&nbsp;</font>IP地址&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="ipaddress" maxlength="50" size="20" class="formStyle" value="<%=vo.getIpaddress()%>">
							</TD>
						</tr>
						<tr >						
							<TD nowrap align="right" height="24" width="10%"><font color="red">*&nbsp;</font>总路径&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="supperdir" maxlength="50" size="40" class="formStyle" value="<%=vo.getSupperdir()%>">
							</TD>
							<TD nowrap align="right" height="24" width="10%"><font color="red">*&nbsp;</font>子路径&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="subdir" maxlength="50" size="40" class="formStyle" value="<%=vo.getSubdir()%>" >
							</TD>
						</tr>
						<tr style="background-color: #ECECEC;">						
							<TD nowrap align="right" height="24" width="10%"><font color="red">*&nbsp;</font>文件产生时间间隔(分钟)&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="inter" maxlength="50" size="40" class="formStyle" value="<%=vo.getInter()%>">
							</TD>
							<TD nowrap align="right" height="24" width="10%"><font color="red">*&nbsp;</font>文件大小阀值（KB）&nbsp;</TD>				
							<TD nowrap width="40%">&nbsp;
								<input type="text" name="filesize" maxlength="50" size="20" class="formStyle" value="<%=vo.getFilesize()%>">
							</TD>
						</tr>
						
						<tr style="background-color: #ECECEC;">					
							<TD nowrap align="right" height="24" width="10%">是否监控&nbsp;</TD>				
							<TD nowrap width="40%" colspan=3>&nbsp;
								<select   name="mon_flag"  class="formStyle">
								<%
									if(vo.getMon_flag() == 0){
								%>
									<option value=0 selected>否</option>
									<option value=1>是</option>
								<%
									}else{
								%>
									<option value=0 >否</option>
									<option value=1 selected>是</option>								
								<%
									}
								%>
								</select>
							</TD>														
						</tr>
						<tr> 
  							<TD nowrap align="right" height="24" width="10%">短信接收人&nbsp;</TD>       
							<TD nowrap width="40%">&nbsp;
							<input type="text" name="sendmobiles" maxlength="32" size="50" value="<%=vo.getSendmobiles()%>">&nbsp;&nbsp; <input type=button value="设置短信接收人" onclick="javascript:return CreateWindow('<%=rootPath%>/user.do?action=setgrapesmobiles&id=<%=vo.getId()%>')"> 
							</td>
 						</tr>
    						<tr style="background-color: #ECECEC;"> 
  							<TD nowrap align="right" height="24" width="10%">邮件接收人&nbsp;</TD>       
							<TD nowrap width="40%">&nbsp;
							<input type="text" name="sendemail" maxlength="32" size="50" value="<%=vo.getSendemail()%>">&nbsp;&nbsp;<input type=button value="设置邮件接收人" onclick="javascript:return CreateWindow('<%=rootPath%>/user.do?action=setgrapesemail&id=<%=vo.getId()%>')"> 
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
           													String checkflag = "";
           													if(bidlist.contains(buss.getId()+""))checkflag="checked";
           													
                        									%>                  										                        								
                    										<tr align="left" valign="center"> 
                    											<td height="28" align="left">&nbsp;<INPUT type="checkbox" class=noborder name=checkbox value="<%=buss.getId()%>" <%=checkflag%>>&nbsp;<%=buss.getName()%></td>
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
							<td nowrap colspan="4" height="1" bgcolor="#8EADD5"></td>
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
</BODY>
</HTML>
