var cpugrid = null;
var memorygrid = null;
var usergrid = null;
var basePath = null;
$(function() {
			var h=$(window).height();
			basePath = $("#basePath").attr("value");
			$("div.divHead").each(function() {
						$(this).click(function() {
									var isShowDiv = $(this).parent()
											.find("div").eq(1);
									$(isShowDiv).slideToggle(500);
									liger.get($(isShowDiv).attr("id")).reload();
								});
					});

			// grid
			cpugrid = $("#cpuConfigGrid").ligerGrid({
						columns : [{
									display : '处理器ID',
									name : 'processorId',
									minWidth : 50,
									width : 100
								}, {
									display : '类型/主频',
									name : 'configName',
									minWidth : 300,
									width : 400
								}, {
									display : 'cpu MHz',
									name : 'cpuSpeed',
									minWidth : 350,
									width : 380
								}, {
									display : '二级缓存大小',
									name : 'cacheSize',
									minWidth : 200,
									width : 380
								}],
						pageSize : 10,
						checkbox : true,
						data : getHostConfigDetail("cpuconfig"),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'processorId',
						width : '99.8%',
						height : 350
					});

			memorygrid = $("#memoryConfigGrid").ligerGrid({
						columns : [{
									display : '物理内存',
									name : 'visibleMemorySize',
									minWidth : 200,
									width : 640
								}, {
									display : 'Swap内存',
									name : 'swapMemorySize',
									minWidth : 200,
									width : 640
								}],
						pageSize : 5,
						checkbox : true,
						data : getHostConfigDetail("memoryconfig"),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						width : '99.8%',
						height : 200
					});

			usergrid = $("#userConfigGrid").ligerGrid({
						columns : [{
									display : '名称',
									name : 'userName',
									minWidth : 200,
									width : 635
								}, {
									display : '用户组',
									name : 'userGroup',
									minWidth : 200,
									width : 635
								}],
						pageSize : 10,
						checkbox : true,
						data : getHostConfigDetail("userconfig"),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						width : '99.8%',
						height : 250
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

function getHostConfigDetail(type) {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostConfigDetail",
				// 参数
				data : {
					ip : getUrlParam("ip"),
					type : type
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
