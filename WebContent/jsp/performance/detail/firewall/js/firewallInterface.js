var grid = null;
var nodeId = null;
var basePath = null;
var ifIndex = null;
var ifName = null;
var alias = null;
var contextMenu = null;
$(function() {
			basePath = $("#basePath").attr("value");
			nodeId = $("#nodeId").attr("value");
			alias = $("#alias").attr("value");
			// 右键菜单项
			contextMenu = $.ligerMenu({
						width : 120,
						items : [{
									text : '详细信息',
									click : contextMenuItemClick,
									icon : 'if'
								}, {
									text : '流速实时',
									click : contextMenuItemClick,
									icon : 'flux'
								}, {
									text : '带宽实时',
									click : contextMenuItemClick,
									icon : 'bandwidth'
								}, {
									text : '广播包实时',
									click : contextMenuItemClick,
									icon : 'realtime'
								}, {
									text : '多播包实时',
									click : contextMenuItemClick,
									icon : 'realtime'
								}]
					});
			// grid
			grid = $("#firewallInterfaceGrid").ligerGrid({
				columns : [{
							display : '索引',
							name : 'ifIndex',
							minWidth : 60,
							width : 60
						}, {
							display : '名称',
							name : 'ifName',
							minWidth : 300
						}, {
							display : '速率(kb/s)',
							name : 'ifSpeed',
							minWidth : 100,
							width : 150
						}, {
							display : '状态',
							name : 'ifOperStatus',
							minWidth : 50,
							width : 50,
							render : function(item) {
								return "<img src='" + basePath + "css/img/pList/"
										+ item.ifOperStatus + ".gif'>";
							}
						}, {
							display : '带宽利用率(%)',
							columns : [{
										display : '出口',
										name : 'outBandwidthUtilHdxPerc',
										width : 150,
										minWidth : 80
									}, {
										display : '入口',
										name : 'inBandwidthUtilHdxPerc',
										width : 150,
										minWidth : 80
									}]
						}, {
							display : '流速(KB/s)',
							columns : [{
										display : '出口',
										name : 'outBandwidthUtilHdx',
										width : 150,
										minWidth : 80
									}, {
										display : '入口',
										name : 'inBandwidthUtilHdx',
										width : 150,
										minWidth : 80
									}]
						}],
				pageSize : 22,
				checkbox : true,
				data : getNetInterfaceDetail(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'ifIndex',
				width : '99.8%',
				height : '99.9%',

				// 右键菜单
				onContextmenu : function(parm, e) {
					ifIndex = parm.data.ifIndex;
					ifName = parm.data.ifName;
					contextMenu.show({
								top : e.pageY,
								left : e.pageX
							});
					return false;
				}

			});
		});

function contextMenuItemClick(item, i) {
	if (item.text == "详细信息") {
		openWindow(basePath + "jsp/performance/detail/firewall/goto/ifDetail.jsp?ip="
						+ getUrlParam("ip") + "&alias=" + alias + "&ifIndex="
						+ ifIndex + "&ifName=" + ifName, 450, 850, "端口详细");
	} else if (item.text == "流速实时") {
		openWindow(basePath + "jsp/performance/detail/firewall/goto/ifRealTimeFlux.jsp?ip="
						+ getUrlParam("ip") + "&id=" + getUrlParam("nodeId") + "&ifIndex="
						+ ifIndex, 485, 685, "端口详细");
	} else if (item.text == "带宽实时") {
		openWindow(basePath + "jsp/performance/detail/firewall/goto/ifRealTimeBandWidth.jsp?ip="
						+ getUrlParam("ip") + "&id=" + getUrlParam("nodeId") + "&ifIndex="
						+ ifIndex, 485, 685, "端口详细");
	} else if (item.text == "广播包实时") {
		openWindow(basePath + "jsp/performance/detail/firewall/goto/ifRealTimeBroadcastPkts.jsp?ip="
						+ getUrlParam("ip") + "&id=" + getUrlParam("nodeId") + "&ifIndex="
						+ ifIndex, 485, 685, "端口详细");
	} else if (item.text == "多播包实时") {
		openWindow(basePath + "jsp/performance/detail/firewall/goto/ifRealTimeMulticastPkts.jsp?ip="
						+ getUrlParam("ip") + "&id=" + getUrlParam("nodeId") + "&ifIndex="
						+ ifIndex, 485, 685, "端口详细");
	}
}


function getNetInterfaceDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "netPerformanceAjaxManager.ajax?action=getNetInterfaceDetail",
				// 参数
				data : {
					ip : getUrlParam("ip")
				},
				dataType : "json",
				success : function(array) {
					rs = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;
}
