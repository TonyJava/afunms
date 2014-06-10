 <%@ page language="java" contentType="text/html; charset=gb2312" pageEncoding="gb2312"%>

 <%
     String rootPath = request.getContextPath();  
     String equipName = (String)request.getAttribute("equipName");
     String nodeId = (String)request.getAttribute("nodeId");
     String type = (String)request.getAttribute("type");
     String galleryPanel = (String)request.getAttribute("galleryPanel");
     String gallery = (String)request.getAttribute("gallery");
     String category = (String)request.getAttribute("category");
     
 %>
<html>
<META http-equiv="Content-Type" content="text/html; charset=gb2312" />
<META HTTP-EQUIV="pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate">
<META HTTP-EQUIV="expires" CONTENT="0">
<head>
<base target="_self">
<link rel="stylesheet" href="<%=rootPath%>/resource/css/style.css" type="text/css">
<script type="text/javascript" src="<%=rootPath%>/resource/js/wfm.js"></script>
<style type="text/css">
.layout_title {
	font-family:宋体, Arial, Helvetica, sans-serif;
	font-size:12px;
	font-weight:bold;
	color:#000000;
	text-align: center;
}
.bg_image{
	background-image:url("<%=rootPath%>/resource/image/bg.jpg");
}
</style>
<title>设备属性</title>
  <script   language="Javascript1.2">   
  ///<!--     
  var   xpos,ypos;   
    
  function   startDragme()   {   
    xpos   =   document.body.scrollLeft   +   event.clientX;   
    ypos   =   document.body.scrollTop   +   event.clientY;   
  }   
    
  function   moveWindow()   {   
    var   xmov   =   document.body.scrollLeft   +   event.clientX   -   xpos;   
    var   ymov   =   document.body.scrollTop   +   event.clientY   -   ypos;   
    window.moveTo(xmov,ymov);
  }   
  //--   The   End>   
</script>  
<script type="text/javascript">
var equipName="<%=equipName%>";
var typeName = "<%=category%>";
var galleryPanelStr = '<%=galleryPanel%>';//用单引号括，如果用双引号会报脚本错
var currentlyIconId=null;
var setting=new Array();

setting[0]=equipName;
setting[1]="";
setting[2]="";
setting[3]="";
function save()
{
    var args = window.dialogArguments;
	setting[0]=document.all.equipName.value;//设备名称	
	if(currentlyIconId!=null)
	{						
		setting[1]=currentlyIconId;
	}
	setting[2]=args.fatherXML;
	setting[3]="<%=type%>";
	var chk = checkinput("equipName","string","设备名称",30,false);
    if(chk){
	    mainForm.action = "<%=rootPath%>/submap.do?action=replaceEquipPic&returnValue="+setting;
        mainForm.submit();
	    window.close();
	    args.location.reload();
	}
}
function update()
{
	changeStyle(optionId);	
}
window.onload=function()
{
    document.getElementById("resTypeSort").value = typeName;
    document.getElementById("galleryPanel").innerHTML=galleryPanelStr;
}
function changeStyle(id)
{
    var galleryPanel=document.getElementById("galleryPanel");    
    var icons=galleryPanel.getElementsByTagName("img");
	for(var i=0;i<icons.length;i++)
	{
		if(icons[i].id==id)
		{
			icons[i].border=1;
			currentlyIconId=id;
		}
		else
		{
			icons[i].border=0;
		}	
	}
}

function updateGalleryPanel()
{	
    currentlyIconId=null;
	var category=document.getElementById("resTypeSort").value;	
	
	//提交请求		
    mainForm.action = "<%=rootPath%>/submap.do?action=equipProperty&category="+category;	
    mainForm.submit();   
    
}
</script>
</head>
<body id="body" class="bg_image">
<form name="mainForm" method="post" action="">
<input type="hidden" name='nodeId' value="<%=nodeId%>"/>
<input type="hidden" name='type' value="<%=type%>"/>
<table border="0" id="table1" class="body-container" cellpadding="0" cellspacing="0" width=100% >
<tr >
<td width=5px>&nbsp;</td>
<td bgcolor="#ffffff">


					<table id="detail-content" class="detail-content" width="100%" background="<%=rootPath%>/common/images/right_t_02.jpg" cellspacing="0" cellpadding="0">
					<td align="left"><img src="<%=rootPath%>/common/images/right_t_01.jpg" width="5" height="29" /></td>
					<td class="layout_title"><b>实体图元属性</b></td>
					<td align="right"><img src="<%=rootPath%>/common/images/right_t_03.jpg" width="5" height="29" /></td>
					</table>
        <table width="500px" border=0 cellpadding=0 cellspacing=0>
		
		<tr>
		<td height=250 valign="top">
		<table border="0" id="table1" cellpadding="0" cellspacing="1" width="100%" align="center">
			<TBODY>
			<tr>						
			<TD  nowrap align="right" height="24" width="20%">设备名称&nbsp;</TD>				
			<TD nowrap width="80%">
			   &nbsp; <input name="equipName" type="text" id="titleText"  value="<%=equipName%>" size="32" maxlength="50">
			</TD>
			<td height="20px"></td>
			</tr>
    		<%=gallery%></TBODY>
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