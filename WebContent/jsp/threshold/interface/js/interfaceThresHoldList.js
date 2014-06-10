var basePath = null;
var grid = null;
var key = null;
var interfaceThresHoldData = null;
$(function() {
	basePath = $("#basePath").attr("value");
	interfaceThresHoldData = f_getInterfaceThresHoldList();
	key = $("#key").ligerTextBox({});
	// grid
	grid = $("#interfaceThresHoldGrid").ligerGrid({
		columns : [{
					display : '网元名称',
					name : 'alias',
					align : 'left',
					minWidth : 180
				}, {
					display : '网元IP',
					name : 'ip',
					minWidth : 100,
					width : 110
				}, {
					display : '告警阈值参数',
					columns : [{
								display : '接口名称',
								name : 'interfaceName',
								minWidth : 120
							}, {
								display : '启用',
								name : 'isA',
								minWidth : 10,
								width : 60,
								render : function(item) {
									if (item.isA == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}, {
								display : '报表',
								name : 'isRPT',
								minWidth : 10,
								width : 60,
								render : function(item) {
									if (item.isRPT == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}, {
								display : '短信',
								name : 'isSM',
								minWidth : 10,
								width : 60,
								render : function(item) {
									if (item.isSM == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}, {
								display : '告警级别',
								name : 'alarmLevel',
								minWidth : 30,
								width : 80,
								render : function(item) {
									if (item.alarmLevel == 1) {
										return "普通";
									} else if (item.alarmLevel == 2) {
										return "严重";
									} else {
										return "紧急";
									}
								}
							}, {
								display : '速率(KB/s)',
								name : 'speed',
								minWidth : 30,
								width : 100
							}, {
								display : '出口(KB/s)',
								name : 'outAlarmVlaue',
								minWidth : 30,
								width : 100
							}, {
								display : '入口(KB/s)',
								name : 'inAlarmValue',
								minWidth : 30,
								width : 100
							}, {
								display : '描述',
								name : 'remark',
								minWidth : 100,
								width : 100
							}, {
								name : 'interfaceThresHoldId',
								hide : true,
								width : 1
							}]
				}],
		pageSize : 30,
		checkbox : true,
		data : $.extend(true, {}, interfaceThresHoldData),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'interfaceThresHoldId',
		width : '99.8%',
		height : '100%',
		heightDiff :-4,
		onReload : function() {
			refresh();
		},
		// 工具栏
		toolbar : {
			items : [{
						text : '启用(是)',
						click : tbItemclick,
						icon : 'true'
					}, {
						text : '启用(否)',
						click : tbItemclick,
						icon : 'delete'
					}, {
						text : '报表(是)',
						click : tbItemclick,
						icon : 'report'
					}, {
						line : true
					}, {
						text : '报表(否)',
						click : tbItemclick,
						icon : 'busy'
					}, {
						text : '短信(是)',
						click : tbItemclick,
						icon : 'sms'
					}, {
						line : true
					}, {
						text : '短信(否)',
						click : tbItemclick,
						icon : 'busy'
					}]
		},
		// 双击
		onDblClickRow : function(data, rowindex, rowobj) {
			openWindows(
					basePath
							+ "jsp/threshold/interface/interfaceThresHoldEdit.jsp?interfaceThresHoldId="
							+ data.interfaceThresHoldId + "&alias="
							+ encodeURI(encodeURI( data.alias)), 400, 520, "接口阈值编辑");
		}
	});

	$("#key").keyup(function() {
				f_search();
			});

});

// 查询过滤方法
function f_search() {
	grid.options.data = $.extend(true, {}, interfaceThresHoldData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#key").val();
		if (rowdata.ip.indexOf(key) > -1 || rowdata.alias.indexOf(key) > -1) {
			return true;
		}
	};
	return clause;
}
// 获取阈值数据方法
function f_getInterfaceThresHoldList() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "interfaceThresHoldAjaxManager.ajax?action=getInterfaceThresHoldList",
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

// 打开新的窗口方法
function openWindows(href, h, w, t) {
	var win = $.ligerDialog.open({
				title : t,
				height : h,
				url : href,
				width : w,
				slide : false
			});
}

// 工具栏功能定义
function tbItemclick(item) {
	if (item.text == "启用(是)") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择更改项");
		} else {
			$(rows).each(function() {
						idString += this.interfaceThresHoldId + ";";
					});
			$.ligerDialog.success(batchEable(idString));
		}
	} else if (item.text == "启用(否)") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择更改项");
		} else {
			$(rows).each(function() {
						idString += this.interfaceThresHoldId + ";";
					});
			$.ligerDialog.success(batchDisable(idString));
		}
	} else if (item.text == "报表(是)") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择更改项");
		} else {
			$(rows).each(function() {
						idString += this.interfaceThresHoldId + ";";
					});
			$.ligerDialog.success(batchReport(idString));
		}
	} else if (item.text == "报表(否)") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择更改项");
		} else {
			$(rows).each(function() {
						idString += this.interfaceThresHoldId + ";";
					});
			$.ligerDialog.success(batchDisReport(idString));
		}
	} else if (item.text == "短信(是)") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择更改项");
		} else {
			$(rows).each(function() {
						idString += this.interfaceThresHoldId + ";";
					});
			$.ligerDialog.success(batchSms(idString));
		}
	} else if (item.text == "短信(否)") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择更改项");
		} else {
			$(rows).each(function() {
						idString += this.interfaceThresHoldId + ";";
					});
			$.ligerDialog.success(batchDisSms(idString));
		}
	}
	refresh();
}

function batchEable(string) {
	var rs = "启用阈值错误";
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "interfaceThresHoldAjaxManager.ajax?action=batchEable",
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
function batchDisable(string) {
	var rs = "禁用阈值错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "interfaceThresHoldAjaxManager.ajax?action=batchDisable",
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

function batchReport(string) {
	var rs = "启用显示于报表错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "interfaceThresHoldAjaxManager.ajax?action=batchReport",
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

function batchDisReport(string) {
	var rs = "禁用显示于报表错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "interfaceThresHoldAjaxManager.ajax?action=batchDisReport",
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

function batchSms(string) {
	var rs = "启用短信告警错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "interfaceThresHoldAjaxManager.ajax?action=batchSms",
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

function batchDisSms(string) {
	var rs = "禁用短信告警错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "interfaceThresHoldAjaxManager.ajax?action=batchDisSms",
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

// 刷新列表
function refresh() {
	interfaceThresHoldData = f_getInterfaceThresHoldList();
	grid.set({
				data : $.extend(true, {}, interfaceThresHoldData)
			});
	f_search();
}
