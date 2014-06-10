var grid = null;
var basePath = null;
var type = null;
var contextMenu = null;
var dbId = null;

$(function() {
	basePath = $("#basePath").attr("value");
	type = getUrlParam("type");
	$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
			"like", "equal", "notequal", "greater", "less", "startwith",
			"endwith" ];
	// 右键菜单项
	contextMenu = $.ligerMenu({
		width : 120,
		items : [ {
			text : '连接测试',
			click : contextMenuItemClick,
			icon : 'db'
		} ]
	});
	// grid
	grid = $("#dbGrid")
			.ligerGrid(
					{
						columns : [
								{
									display : 'ID',
									name : 'dbid',
									minWidth : 30,
									width : 50
								},
								{
									display : '名称',
									name : 'alias',
									minWidth : 100
								},
								{
									display : '类型',
									name : 'dbtype',
									minWidth : 50,
									width : 100
								},
								{
									display : '数据库名',
									name : 'dbname',
									minWidth : 100
								},
								{
									display : 'IP地址',
									name : 'ipaddress',
									minWidth : 80,
									width : 150
								},
								{
									display : '端口',
									name : 'port',
									minWidth : 50,
									width : 100
								},
								{
									display : '监视状态',
									name : 'managed',
									minWidth : 40,
									width : 100
								},
								{
									display : '状态',
									name : 'status',
									minWidth : 40,
									width : 100,
									render : function(item) {
										return "<img class='statePic' src='"
												+ basePath
												+ "resource/image/topo/"
												+ item.status + "'>";
									}
								}, {
									display : '告警',
									columns : [ {
										display : '普通',
										name : 'generalAlarm',
										minWidth : 40,
										width : 50
									}, {
										display : '严重',
										name : 'urgentAlarm',
										minWidth : 40,
										width : 50
									}, {
										display : '紧急',
										name : 'seriousAlarm',
										minWidth : 40,
										width : 50
									} ]
								}, {
									display : "可用性",
									name : 'pingvalue',
									minWidth : 50,
									width : 100
								} ],
						pageSize : 30,
						checkbox : true,
						data : getDbNodeDataByType(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'nodeId',
						width : '99.8%',
						height : '100%',
						heightDiff : -4,
						onReload : function() {
							grid.set({
								data : getDbNodeDataByType()
							});
						},
						// 工具栏
						toolbar : {
							items : [
									{
										text : '高级自定义查询',
										click : sItemclick,
										icon : 'search2'
									},
									{
										text : '增加',
										click : itemclick,
										href : basePath
												+ 'jsp/resourceManage/database/add.jsp',
										icon : 'add'
									},
									{
										line : true
									},
									{
										text : '修改',
										click : itemclick,
										href : basePath
												+ 'jsp/resourceManage/database/edit.jsp?id=',
										icon : 'modify'
									}, {
										line : true
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
									} ]
						},
						// 双击
						onDblClickRow : function(data, rowindex, rowobj) {
							var url = null;
							if (type == "mysql") {
								url = basePath
										+ "jsp/performance/detail/mysqlPerformanceDetail.jsp?ip="
										+ data.ipaddress + "&alias="
										+ data.alias + "&id=" + data.dbid
										+ "&type=db&subType=mysql";
							} else if (type == "oracle") {
								url = basePath
										+ "jsp/performance/detail/oraclePerformanceDetail.jsp?ip="
										+ data.ipaddress + "&alias="
										+ data.alias + "&id=" + data.dbid
										+ "&type=db&subType=oracle";
							} else if (type == "sqlserver") {
								url = basePath
										+ "jsp/performance/detail/sqlserverPerformanceDetail.jsp?ip="
										+ data.ipaddress + "&alias="
										+ data.alias + "&id=" + data.dbid
										+ "&type=db&subType=sqlserver";
							}
							openFullWindow(url, "数据库详细");
						},
						// 右键菜单
						onContextmenu : function(parm, e) {
							dbId = parm.data.dbid;
							contextMenu.show({
								top : e.pageY,
								left : e.pageX
							});
							return false;
						}
					});
});
function contextMenuItemClick(item, i) {
	if (item.text == "连接测试") {
		openDlgWindow(basePath + "jsp/tool/dbCheck.jsp?dbId=" + dbId, 200, 650,
				"数据库连接测试");
	}
}
function sItemclick() {
	grid.options.data = $.extend(true, {}, getDbNodeDataByType());
	grid.showFilter();
}
function itemclick(item) {
	if (item.text == "增加") {
		openWindow(item.href, 600, 650, item.text + "数据库");
	} else if (item.text == "修改") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0 || rows.length > 1) {
			$.ligerDialog.error("请选择一条记录进行修改");
		} else {
			$(rows).each(function() {
				idString = this.dbid;
			});
			openWindow(item.href + idString, 600, 650, item.text + "数据库");
		}
	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
				idString += this.dbid + ";";
			});
			f_deleteNodes(idString);
		}
	} else if (item.text == "开启监控") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择项");
		} else {
			$(rows).each(function() {
				idString += this.dbid + ";";
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
				idString += this.dbid + ";";
			});
			$.ligerDialog.success(batchCancleMonitor(idString));
		}
	}
	refresh();
}

function batchAddMonitor(string) {
	var rs = "启用监控错误";
	$
			.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=batchAddMonitor",
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
		url : basePath
				+ "dbPerformanceAjaxManager.ajax?action=batchCancleMonitor",
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

function f_deleteNodes(string) {
	var url = basePath + "dbPerformanceAjaxManager.ajax?action=deleteDbs";
	showDeleteDlg(url, string);
}

function getDbNodeDataByType() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "dbPerformanceAjaxManager.ajax?action=getDbNodeDataByType",
		// 参数
		data : {
			type : getUrlParam("type")
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

// 刷新列表
function refresh() {
	grid.set({
		data : getDbNodeDataByType()
	});
}
function createDiv(pw, value, color) {
	var divString = "<div style='float:left;margin-top:5px;width:" + pw
			+ "px;border:1px solid green;'><div style='height:12px;background:"
			+ color + ";width:" + value + "%;'></div></div>" + value;
	return divString;
}

function toDetail(href, h, w, t) {
	var imgString = "<img src='"
			+ basePath
			+ "css/img/pList/pDetail.gif' style='margin-top:5px;' class='pDetail' onclick='openWindow(\""
			+ href + "\"," + h + "," + w + ",\"" + t + "\")' />";
	return imgString;
}
