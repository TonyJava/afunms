var manager = null;
var accordion = null;
var tab = null;
var basePath = null;
var portalFlag = null;
$(function() {
	basePath = $("#basePath").attr("value");
	portalFlag = $("#portalFlag").attr("value");
	$("#performanceLayout").ligerLayout({
		leftWidth : 190,
		height : '100%',
		space : 4,
		onHeightChanged : f_heightChanged,
		allowLeftResize : false,
		allowLeftCollapse : true
	});

	var height = $(".l-layout-center").height();

	$(".l-link").hover(function() {
		$(this).addClass("l-link-over");
	}, function() {
		$(this).removeClass("l-link-over");
	});

	// Tab
	$("#pFramecenter").ligerTab({
		height : height
	});

	// 面板
	$("#pAccordion").ligerAccordion({
		height : height - 24,
		speed : null
	});

	// tree
	$("#pTree").ligerTree({
		data : pTreeData,
		attribute : [ 'nodename', 'url' ],
		checkbox : false,
		slide : false,
		onSelect : function(node) {
			if (!node.data.url)
				return;
			var tabid = $(node.target).attr("tabid");
			if (!tabid) {
				tabid = node.data.text;
				$(node.target).attr("tabid", tabid);
			}
			f_addTab(tabid, node.data.text, node.data.url);
		}
	});

	tab = $("#pFramecenter").ligerGetTabManager();
	manager = $("#pTree").ligerGetTreeManager();
	accordion = $("#pAccordion").ligerGetAccordionManager();

	if ("-1" != portalFlag) {
		var url = "";
		var tabid = "";
		if (portalFlag.indexOf("路由器") >= 0) {
			url = "netPerformanceList.jsp?type=route";
			tabid = "路由器";
		} else if (portalFlag.indexOf("交换机") >= 0) {
			url = "netPerformanceList.jsp?type=switch";
			tabid = "交换机";
		} else if (portalFlag.indexOf("服务器") >= 0) {
			url = "hostPerformanceList.jsp?type=windows";
			tabid = "Windows";
		} else if (portalFlag.indexOf("数据库") >= 0) {
			url = "dbPerformanceList.jsp?type=oracle";
			tabid = "Oracle";
		} else if (portalFlag.indexOf("安全") >= 0) {
			url = "firewallPerformanceList.jsp?type=firewall";
			tabid = "防火墙";
		} else if (portalFlag.indexOf("中间件") >= 0) {
			url = "tomcatPerformanceList.jsp";
			tabid = "Tomcat";
		}
		f_addTab(tabid, tabid, url);
	}
});

function f_heightChanged(options) {
	if (tab)
		tab.addHeight(options.diff);
	if (accordion && options.middleHeight - 24 > 0)
		accordion.setHeight(options.middleHeight - 24);
}

function f_addTab(tabid, text, url) {
	tab.addTabItem({
		tabid : tabid,
		text : text,
		url : url
	});
}