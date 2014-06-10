<%@page language="java" contentType="text/html;charset=GB2312"%>
<%@page import="com.afunms.common.util.SessionConstant"%>
<%@page import="com.afunms.config.dao.IpaddressPanelDao"%>
<%@page import="com.afunms.config.model.IpaddressPanel"%>
<%@page import="com.afunms.common.util.SysUtil"%>
<%@page import="com.afunms.config.dao.PanelModelDao"%>
<%@page import="com.afunms.config.model.PanelModel"%>
<%@page import="com.afunms.topology.model.HostNode"%>
<%@page import="com.afunms.topology.dao.HostNodeDao"%>
<%
	String rootPath = request.getContextPath();
	//String fileName = (String)session.getAttribute(SessionConstant.CURRENT_CUSTOM_VIEW);
	String fileName = (String)request.getParameter("filename");
	String fullScreen = (String)request.getParameter("fullScreen");
	String oid = (String)request.getParameter("oid");
	String sys_oid = oid;
	String ip = (String)request.getParameter("ip");
	oid = oid.replace(".","-");
	System.out.println(oid);
	IpaddressPanelDao dao = new IpaddressPanelDao();
	IpaddressPanel panel = dao.loadIpaddressPanel(ip);
	String filename = SysUtil.doip(ip);
	String imageType = panel.getImageType();
	PanelModelDao panelModelDao = new PanelModelDao();
	PanelModel panelModel = panelModelDao.loadPanelModel(sys_oid,imageType);
	String pheight = panelModel.getHeight();
	String pwidth = panelModel.getWidth();
	
%> 
<html xmlns:v="urn:schemas-microsoft-com:vml">
<head>
<meta http-equiv="content-type" content="text/html; charset=gb2312" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<title>显示服务器视图</title>
<link href="<%=rootPath%>/resource/css/topo_style.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="js/profile.js"></script>
<script type="text/javascript" src="js/global.js"></script>
<script type="text/javascript" src="js/disable.js"></script>
<script type="text/javascript" src="js/menu.js"></script>
<script type="text/javascript" src="js/map.js"></script>
<%
	//它控制设备名称显示信息（IP还是名字text）
	//g_viewflag在global.js中定义，默认为0，所以要在其后
	String viewflag = request.getParameter("viewflag");	
	if (viewflag == null) 
		out.print("<script type=\"text/javascript\">g_viewFlag = 0;</script>");
	else 
		out.print("<script type=\"text/javascript\">g_viewFlag = " + viewflag + ";</script>");	
%>

<script type="text/javascript" src="js/topology.js"></script>
<script type="text/javascript">
	window.onerror = new Function('return true;');		// 容错
	openProcDlg();  //显示闪屏
	function saveFile() {
		resetProcDlg();
		save();  //topoloty.js中的函数,用于保存图数据--->String串
	}
	
	function doInit()
	{
	//alert(<%=fileName%>);
		loadXML("<%=rootPath%>/panel/xml/<%=fileName%>.jsp");
		
		var autoR = setInterval(autoRefresh,1000*60*2);
	}
	
	function autoRefresh()
	{
		//alert("开始刷新！！！");
	   window.location = "<%=rootPath%>/panel/view/showMap.jsp?fullscreen=<%=fullScreen%>&filename=<%=fileName%>&oid=<%=sys_oid%>&imageType=<%=imageType%>&ip=<%=ip%>";
	   //window.location = "showMap.jsp?filename=<%=fileName%>";
	}
 
</script>
<style>
v\:*{ behavior:url(#default#VML); }
</style>
</head>

<!--画框选择时，用的上下左右四根彩线-->
<img src="<%=rootPath%>/resource/image/topo/line_top.gif" id="imgTop" class="tmpImg" style="width:10; height:10 " />
<img src="<%=rootPath%>/resource/image/topo/line_left.gif" id="imgLeft" class="tmpImg" style="width:10; height:10 "/>
<img src="<%=rootPath%>/resource/image/topo/line_bottom.gif" id="imgBottom" class="tmpImg" style="width:10; height:10 "/>
<img src="<%=rootPath%>/resource/image/topo/line_right.gif" id="imgRight" class="tmpImg" style="width:10; height:10 "/>

<script type="text/javascript">
<!--
document.write('<form name="frmMap" method="post" action="<%=rootPath%>/customview.do?action=save">');
document.write('<body  class="main_body" onLoad="doInit();" onmousewheel="window.parent.parent.document.body.scrollTop -= event.wheelDelta/2;" onmousedown="bodyDown()" onselectstart="return false" marginheight="0" marginwidth="0" topmargin="0" leftmargin="0">');	
//loadMoveController();		// 加载移动控制器
//loadSizeController();		// 加载大小控制器
document.write('<div id="divLayer" style="background-attachment:fixed;background-repeat: no-repeat;background-image:url(<%=rootPath%>/panel/view/image/<%=oid%><%="_"%><%=imageType%>.jpg);width:100%;height:100%;color:black;position:absolute;top:0px;left:0px;background-color:#F0F8FF;border:#F0F8FF; 1px solid;" onmousedown="divLayerDown()" onclick="javascript:closeLineFrame();"></div>');//#000066
document.write('<input type="hidden" name="hidXml"/>');
document.write('</body></form>');
//-->
</script>

<script type="text/javascript">
<!--
// 调整 divLayer 大小
function resizeTopDiv() {
		//alert("<%=pheight%>=============<%=pwidth%>"+" maxWidth: "+maxWidth);
		document.all.divLayer.style.width = maxWidth + <%=pwidth%>;
		document.all.divLayer.style.height = maxHeight + <%=pheight%>;
		//zoomProcDlg("out");
}
	
	setTimeout("resizeTopDiv()", 1000);	
	parent.topFrame.showController(false);
	function showDevice(action) {
		parent.location = action;
	}
	
//-->
</script>
</html>
