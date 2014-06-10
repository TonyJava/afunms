<%@page language="java" contentType="text/html;charset=GB2312"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<%@page import="java.util.List"%>
<%@page import="com.afunms.topology.model.CustomXml"%>
<%@page import="com.afunms.topology.dao.CustomXmlDao"%>
<%
   String rootPath = request.getContextPath();
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<link href="<%=rootPath%>/resource/css/topo_style.css" rel="stylesheet" type="text/css" />
<title>topFrame</title>
<script type="text/javascript" src="js/profile.js"></script>
<script type="text/javascript" src="js/toolbar.js"></script>
<script type="text/javascript" src="js/global.js"></script>
<script type="text/javascript" src="js/disable.js"></script>
<script type="text/javascript" src="js/edit.js"></script>
<script type="text/javascript" src="js/customview.js"></script>
<%
	out.println("<script type=\"text/javascript\">");
	// 判断全屏显示状态
	String fullscreen = request.getParameter("fullscreen");	
	if (fullscreen.equals("0")) 
	   fullscreen = "0";
	else 
	{
		fullscreen = "1";
		out.println("viewWidth = 0;");
	}

	// 取得用户权限---用来限制保存、刷新、编辑等操作
	boolean admin = false;
	String user = "admin";

	if (user.equalsIgnoreCase("admin") || user.equalsIgnoreCase("superuser")) {
		out.println("var admin = true;"); //为了－－编辑－－能正常使用
		admin = true;
	}
	else {
		out.println("var admin = false;");	
		admin = false;
	}
	out.println("</script>");
	
	String disable = "";//控制按钮是否激活
	if (!admin) {
		disable = "disabled=\"disabled\"";
	}
%>
<script type="text/javascript">
	var curTarget = "showMap.jsp?fullscreen=<%=fullscreen%>";
	var display = false;	    // 是否显示快捷列表
	var controller = false;		// 是否显示控制器
	
function searchNode()
{	
	var ip = window.prompt("请输入需要搜索的设备IP地址", "在此输入设备IP地址");
	if (ip == null)
		return true;
	else if (ip == "在此输入设备IP地址")
		return;

	if (!checkIPAddress(ip))
		searchNode();

	var coor = window.parent.mainFrame.getNodeCoor(ip);
	if (coor == null)
	{
		var msg = "没有在图中搜索到IP地址为 "+ ip +" 的设备。";
		window.alert(msg);
		return;
	}
	else if (typeof coor == "string")
	{
		window.alert(coor);
		return;
	}

	// 移动设备到中心标记处
	window.parent.mainFrame.moveMainLayer(coor);
}

// 保存拓扑图
function saveFile() {
	if (!admin) {
		window.alert("您没有保存视图的权限！");
		return;
	}
	parent.mainFrame.saveFile();
}

// 刷新拓扑图
function refreshFile() 
{
	if (window.confirm("“刷新”前是否需要保存当前拓扑图？")) {
		parent.topFrame.saveFile();
	}
	parent.mainFrame.location.reload();
}

// 全屏观看
function gotoFullScreen() {
	parent.mainFrame.resetProcDlg();
	var status = "toolbar=no,height="+ window.screen.height + ",";
	status += "width=" + (window.screen.width-8) + ",scrollbars=no";
	status += "screenX=0,screenY=0";
	window.open("custom.jsp?filename="+document.all.customview.value+"&fullscreen=yes&user=<%=user%>", "fullScreenWindow", status);
	parent.mainFrame.zoomProcDlg("out");
}

// 切换视图
function changeName() 
{
	// 之前提醒用户保存
	if (admin) {
		if (window.confirm("“切换视图”前是否需要保存当前拓扑图？")) {
			saveFile();
		}
	}
	
	if (g_viewFlag == 0) {
		g_viewFlag = 1;
		parent.mainFrame.location = curTarget+"&viewflag=1";
	}
	else if (g_viewFlag == 1) {
		g_viewFlag = 0;	
		parent.mainFrame.location = curTarget+"&viewflag=0";
	}
	else {
		window.alert("视图类型错误");
	}
}

// 显示视图控制器
function showController(flag) {

	var result;
	if (flag == false)
		controller = false;
	if (controller) {
		result = parent.mainFrame.showController(controller);
		
		if (result == false) {
			window.alert("您没有选择视图，无控制器可用");
			return;
		}
			
		//document.all.controller.value = "关闭控制器";
		//document.all.controller.title = "关闭显示框内的视图控制器";
		controller = false;
	}
	else {
		result = parent.mainFrame.showController(controller);
		
		if (result == false) {
			window.alert("您没有选择视图，无控制器可用");
			return;
		}

		//document.all.controller.value = "开启控制器";
		//document.all.controller.title = "开启显示框内的视图控制器";
		//controller = true;
		controller = false;
	}
}
	function autoRefresh() 
	{
		window.clearInterval(freshTimer);
		freshTimer = window.setInterval("refreshFile()",60000);
	}

// 交换图片
function swapImage(imageID, imageSrc) {
	document.all(imageID).src = imageSrc;
}

//选择视图
function changeView()
{
	if(document.all.customview.value == "")return;

	parent.mainFrame.location = "change.jsp?customview=" + document.all.customview.value;
}

</script>
</head>
<body topmargin="0" leftmargin="0" marginheight="0" marginwidth="0" bgcolor="#CEDFF6">
<form name="topviewForm">
</form>
</body>
</html>
