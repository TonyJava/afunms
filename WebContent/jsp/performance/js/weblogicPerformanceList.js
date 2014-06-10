var grid = null;
var basePath = null;
var contextMenu = null;
var nodeId = null;
$(function() {
	basePath = $("#basePath").attr("value");
	$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
			"like", "equal", "notequal", "greater", "less", "startwith", "endwith"];
	// 右键菜单项
	contextMenu = $.ligerMenu({
				width : 120,
				items : [{
							text : '连接测试',
							click : contextMenuItemClick,
							icon : 'common'
						}]
			});
	// grid
	grid = $("#weblogicGrid").ligerGrid({
		columns : [{
			display : '状态',
			name : 'status',
			width : 30,
			render : function(item) {
				return "<img class='statePic' src='" + basePath + "css/img/pList/level"
						+ item.status + ".gif'>";
			}
		}, {
			display : '网元名称',
			name : 'alias',
			minWidth : 200
		}, {
			display : '网元IP',
			name : 'ip',
			minWidth : 100
		}, {
			display : 'SNMP',
			columns : [{
						display : '团体名',
						name : 'community',
						minWidth : 60,
						width : 60
					}, {
						display : '端口',
						name : 'port',
						minWidth : 60,
						width : 60
					}]
		}, {
			display : '可用性(%)',
			columns : [{
						display : '连通率',
						name : 'pingValue',
						minWidth : 60,
						width : 80
					}]
		}, {
			display : '服务器',
			columns : [{
						display : '名称',
						name : 'serverName',
						minWidth : 80,
						width : 120
					}, {
						display : '监听地址',
						name : 'serverAddr',
						minWidth : 80,
						width : 190
					}, {
						display : '监听端口',
						name : 'listenPort',
						minWidth : 80,
						width : 60
					}]
		}, {
			display : '域',
			columns : [{
						display : '域名',
						name : 'domainName',
						minWidth : 40,
						width : 100
					}, {
						display : '端口',
						name : 'domainPort',
						minWidth : 40,
						width : 60
					}, {
						display : '版本',
						name : 'domainVersion',
						minWidth : 40,
						width : 80
					}]
		}, {
			display : '监控',
			name : 'isM',
			minWidth : 40,
			width : 60,
			render : function(item) {
				if (item.isM == 1) {
					return "是";
				} else {
					return "否";
				}
			}
		}, {
			name : 'nodeId',
			hide : true,
			width : 0.1
		}],
		pageSize : 30,
		checkbox : true,
		data : getWeblogicNodeData(),
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
						data : getWeblogicNodeData()
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
						href : basePath + 'jsp/resourceManage/middleware/weblogic/add.jsp',
						icon : 'add'
					}, {
						line : true
					}, {
						text : '修改',
						click : itemclick,
						href : basePath + 'jsp/resourceManage/middleware/weblogic/edit.jsp?nodeId=',
						icon : 'modify'
					}, {
						text : '删除',
						click : itemclick,
						icon : 'delete'
					}, {
						line : true
					}, {
						text : '开启监控',
						click : itemclick,
						icon : 'ok'
					}, {
						line : true
					}, {
						text : '取消监控',
						click : itemclick,
						icon : 'back'
					}]
		},
		// 双击
		onDblClickRow : function(data, rowindex, rowobj) {
			openFullWindow(basePath + "jsp/performance/detail/weblogicPerformanceDetail.jsp?ip="
							+ data.ip + "&alias=" + data.alias + "&nodeId=" + data.nodeId
							+ "&type=middleware&subType=weblogic", "Weblogic详细");
		},
		// 右键菜单
		onContextmenu : function(parm, e) {
			nodeId = parm.data.nodeId;
			contextMenu.show({
						top : e.pageY,
						left : e.pageX
					});
			return false;
		}
	});
});

function sItemclick() {
	grid.options.data = $.extend(true, {}, getWeblogicNodeData());
	grid.showFilter();
}
function itemclick(item) {
	if (item.text == "增加") {
		openWindow(item.href, 400, 500, item.text + "网元");
	} else if (item.text == "修改") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0 || rows.length > 1) {
			$.ligerDialog.error("请选择一条记录进行修改");
		} else {
			$(rows).each(function() {
						idString = this.nodeId;
					});
			openWindow(item.href + idString, 400, 550, item.text + "tomcat");
		}
	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.nodeId + ";";
					});
			f_deleteWeblogicNodes(idString);
		}
	} else if (item.text == "开启监控") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择项");
		} else {
			$(rows).each(function() {
						idString += this.nodeId + ";";
					});
			$.ligerDialog.success(batchAddMonitor(idString));
		}
	} else if (item.text == "取消监控") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择项");
		} else {
			$(rows).each(function() {
						idString += this.nodeId + ";";
					});
			$.ligerDialog.success(batchCancleMonitor(idString));
		}
	}
	refresh();
}

function batchAddMonitor(string) {
	var rs = "启用监控错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "weblogicPerformanceAjaxManager.ajax?action=batchAddMonitor",
				// 参数
				data : {
					string : string
				},
				dataType : "text",
				success : function(array) {
					rs = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;
}

function batchCancleMonitor(string) {
	var rs = "取消监控错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "weblogicPerformanceAjaxManager.ajax?action=batchCancleMonitor",
				// 参数
				data : {
					string : string
				},
				dataType : "text",
				success : function(array) {
					rs = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;
}
function f_deleteWeblogicNodes(string) {
	var url=basePath + "weblogicPerformanceAjaxManager.ajax?action=deleteWeblogicNodes";
	showDeleteDlg(url,string);
}

function getWeblogicNodeData() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "weblogicPerformanceAjaxManager.ajax?action=getWeblogicNodeData",
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
function contextMenuItemClick(item, i) {
	if (item.text == "连接测试") {
		openDlgWindow(basePath + 'jsp/tool/weblogicCheck.jsp?weblogicId=' + nodeId, 200, 650,
				"weblogic测试");
	}
}
// 刷新列表
function refresh() {
	grid.set({
				data : getWeblogicNodeData()
			});
}
function createDiv(pw, value, color) {
	var divString = "<div style='float:left;margin-top:5px;width:" + pw
			+ "px;border:1px solid green;'><div style='height:12px;background:" + color + ";width:"
			+ value + "%;'></div></div>" + value;
	return divString;
}

function toDetail(href, h, w, t) {
	var imgString = "<img src='"
			+ basePath
			+ "css/img/pList/pDetail.gif' style='margin-top:5px;' class='pDetail' onclick='openWindow(\""
			+ href + "\"," + h + "," + w + ",\"" + t + "\")' />";
	return imgString;
}
