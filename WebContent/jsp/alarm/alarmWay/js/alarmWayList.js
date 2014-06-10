var grid = null;
var basePath = null;
$(function() {
	basePath = $("#basePath").attr("value");
	$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
			"like", "equal", "notequal", "startwith", "endwith"];
	// grid
	grid = $("#alarmWayGrid").ligerGrid({
		columns : [{
					display : 'alarmWayId',
					hide : true,
					width : 0.1
				}, {
					display : '名称',
					name : 'name',
					minWidth : 100
				}, {
					display : '默认',
					name : 'isDefault',
					render : function(item) {
						if (item.isDefault == 0) {
							return "否";
						} else {
							return "是";
						}
					}
				}, {
					display : '告警方式',
					columns : [{
								display : '系统',
								name : 'isSystem',
								minWidth : 50,
								render : function(item) {
									if (item.isSystem == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}, {
								display : '声音',
								name : 'isSound',
								minWidth : 50,
								render : function(item) {
									if (item.isSound == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}, {
								display : '短信',
								name : 'isSM',
								minWidth : 50,
								render : function(item) {
									if (item.isSM == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}, {
								display : '邮件',
								name : 'isMail',
								minWidth : 50,
								render : function(item) {
									if (item.isMail == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}]
				}, {
					display : '描述',
					name : 'remark',
					minWidth : 100
				}],
		pageSize : 30,
		checkbox : true,
		data : getAlarmWayList(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'alarmWayId',
		width : '99.8%',
		height : '100%',
		heightDiff :-4,
		onReload : function() {
			grid.set({
						data : getAlarmWayList()
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
						href : basePath + 'jsp/alarm/alarmWay/alarmWayAdd.jsp',
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
			openWindow(basePath
							+ "jsp/alarm/alarmWay/alarmWayEdit.jsp?alarmWayId="
							+ data.alarmWayId, 500, 650, "告警方式编辑");
		}
	});
});

function sItemclick() {
	grid.options.data = $.extend(true, {}, getAlarmWayList());
	grid.showFilter();
}

function itemclick(item) {
	if (item.text == "增加") {
		openWindow(item.href, 500, 650, item.text + "服务监控");
	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.alarmWayId + ";";
					});
			$.ligerDialog.success(f_deleteAlarmWayConfig(idString));
		}
	}
}

function f_deleteAlarmWayConfig(string) {
	var rs = "提示";
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "alarmWayAjaxManager.ajax?action=deleteAlarmWayConfig",
		// 参数
		data : {
			string : string
		},
		dataType : "text",
		success : function(array) {
			// 成功删除则更新表格行
			refresh();
			grid.deleteSelectedRow();
			rs = array;
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
	return rs;
}

function getAlarmWayList() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "alarmWayAjaxManager.ajax?action=getAlarmWayList",
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
				data : getAlarmWayList()
			});
}
