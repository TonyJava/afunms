var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#hostProcessGrid").ligerGrid({
						columns : [{
									display : '进程ID',
									name : 'processId',
									minWidth : 60,
									width : 80
								}, {
									display : '进程名称',
									name : 'processName',
									minWidth : 60
								}, {
									display : '进程个数',
									name : 'processNumber',
									minWidth : 60,
									width : 60
								}, {
									display : '类型',
									name : 'processType',
									minWidth : 100,
									width : 150
								}, {
									display : 'CPU时间(秒)',
									name : 'cpuKeepTime',
									minWidth : 100,
									width : 150
								}, {
									display : '内存占用率(%)',
									name : 'memorySpendRate',
									minWidth : 100,
									width : 150
								}, {
									display : '内存占用量(KB)',
									name : 'memorySpendValue',
									minWidth : 100,
									width : 150
								}, {
									display : '状态',
									name : 'processStatus',
									minWidth : 100,
									width : 150
								}],
						pageSize : 22,
						checkbox : true,
						data : getHostProcessDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'memorySpendRate',
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

function getHostProcessDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostProcessDetail",
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
