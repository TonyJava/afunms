var grid = null;
var basePath = null;
var ifIndex = null;
var ifName = null;
var contextMenu = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// 右键菜单项
			contextMenu = $.ligerMenu({
						width : 120,
						items : [{
									text : '详细信息',
									click : contextMenuItemClick,
									icon : 'if'
								}]
					});
			// grid
			grid = $("#hostInterfaceGrid").ligerGrid({
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
								return "<img src='" + basePath
										+ "css/img/pList/" + item.ifOperStatus
										+ ".gif'>";
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
				data : getHostInterfaceDetail(),
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
		openWindow(basePath
						+ "jsp/performance/detail/host/goto/ifDetail.jsp?ip="
						+ getUrlParam("ip") + "&alias=" + getUrlParam("alias")
						+ "&ifIndex=" + ifIndex + "&ifName=" + ifName, 450,
				850, "端口详细");
	}
}

// 打开新的窗口方法
function openWindow(href, h, w, t) {
	// href 转向网页的地址
	// t 网页名称，可为空
	// w 弹出窗口的宽度
	// h 弹出窗口的高度
	// window.screen.height获得屏幕的高，window.screen.width获得屏幕的宽
	var top = (window.screen.height - 30 - h) / 2; // 获得窗口的垂直位置;
	var left = (window.screen.width - 10 - w) / 2; // 获得窗口的水平位置;
	var features = "height="
			+ h
			+ ", width="
			+ w
			+ ",top="
			+ top
			+ ",left="
			+ left
			+ ",toolbar=no,menubar=no,scrollbars=no,resizable=yes,location=no,status=no";
	window.open(href, t, features);
}

function getHostInterfaceDetail() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "hostPerformanceAjaxManager.ajax?action=getHostInterfaceDetail",
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
