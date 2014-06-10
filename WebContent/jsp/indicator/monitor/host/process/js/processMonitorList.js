var grid = null;
var basePath = null;
$(function() {
	basePath = $("#basePath").attr("value");
	$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
			"like", "equal", "notequal", "greater", "less", "startwith",
			"endwith"];
	// grid
	grid = $("#processGrid").ligerGrid({
		columns : [{
					display : '网元名称',
					name : 'alias',
					minWidth : 200
				}, {
					display : '网元IP',
					name : 'ip',
					minWidth : 100
				}, {
					display : '进程组',
					name : 'processGroup',
					minWidth : 100
				}, {
					display : '告警等级',
					name : 'level',
					minWidth : 80,
					render : function(item) {
						var levelString = "普通";
						if (item.level == "2") {
							levelString = "严重";
						} else if (item.level == "3") {
							levelString = "紧急";
						}
						return levelString;
					}
				}, {
					display : '监控',
					name : 'isM',
					minWidth : 50,
					render : function(item) {
						var isMString = "否";
						if (item.isM == "1") {
							isMString = "是";
						}
						return isMString;
					}
				}, {
					name : 'nodeId',
					hide : true,
					width : 0.1
				}, {
					name : 'groupId',
					hide : true,
					width : 0.1
				}],
		pageSize : 30,
		checkbox : true,
		data : getProcessMonitorList(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'nodeId',
		width : '99.8%',
		height : '100%',
		heightDiff :-4,
		onReload : function() {
			grid.set({
						data : getProcessMonitorList()
					});
		},
		// 工具栏
		toolbar : {
			items : [{
						text : '高级自定义查询',
						click : sItemclick,
						icon : 'search2'
					}, {
						text : '增加',
						click : itemclick,
						href : basePath
								+ 'jsp/indicator/monitor/host/process/processMonitorAdd.jsp',
						icon : 'add'
					}, {
						line : true
					}, {
						text : '删除',
						click : itemclick,
						icon : 'delete'
					}]
		},
		// 双击
		onDblClickRow : function(data, rowindex, rowobj) {
			openWindow(
					basePath
							+ "jsp/indicator/monitor/host/process/processMonitorEdit.jsp?groupId="
							+ data.groupId, 500, 650, "进程监控编辑");
		}
	});
});

function sItemclick() {
	grid.options.data = $.extend(true, {}, getProcessMonitorList());
	grid.showFilter();
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
function itemclick(item) {
	if (item.text == "增加") {
		openWindow(item.href, 500, 650, item.text + "进程监控");
	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.groupId + ";";
					});
			$.ligerDialog.success(f_deleteProcessMonitorConfig(idString));
		}
	}
}

function f_deleteProcessMonitorConfig(string) {
	var rs = "删除错误";
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "processMonitorAjaxManager.ajax?action=deleteProcessMonitorConfig",
		// 参数
		data : {
			string : string
		},
		dataType : "text",
		success : function(array) {
			// 成功删除则更新表格行
			grid.deleteSelectedRow();
			rs = array;
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
	return rs;
}

function getProcessMonitorList() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "processMonitorAjaxManager.ajax?action=getProcessMonitorList",
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
// 刷新列表
function refresh() {
	grid.set({
				data : getProcessMonitorList()
			});
}
