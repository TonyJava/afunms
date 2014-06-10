var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#netSyslogGrid").ligerGrid({
						columns : [{
									display : '类型',
									name : 'priorityName',
									minWidth : 60,
									width : 80
								}, {
									display : '日期',
									name : 'cTime',
									minWidth : 60,
									width : 100
								}, {
									display : '来源',
									name : 'processName',
									minWidth : 60,
									width : 100
								}, {
									display : '分类',
									name : 'processName',
									minWidth : 100,
									width : 200
								}, {
									display : '事件',
									name : 'eventid',
									minWidth : 200
								}, {
									display : '用户',
									name : 'userName',
									minWidth : 100,
									width : 200
								}, {
									display : '计算机',
									name : 'hostName',
									minWidth : 100,
									width : 200
								}],
						pageSize : 22,
						checkbox : true,
						data : getHostSyslogDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'cTime',
						width : '99.8%',
						height : '99.9%'

					});
		});

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

function getHostSyslogDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "netPerformanceAjaxManager.ajax?action=getNetSyslogDetail",
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
