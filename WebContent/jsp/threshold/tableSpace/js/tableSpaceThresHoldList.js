var basePath = null;
var grid = null;
var key = null;
var tableSpaceThresHoldData = null;
$(function() {
	basePath = $("#basePath").attr("value");
	tableSpaceThresHoldData = f_getTableSpaceThresHoldList();
	key = $("#key").ligerTextBox({});
	// grid
	grid = $("#tableSpaceThresHoldGrid").ligerGrid({
		columns : [{
					display : '网元名称',
					name : 'alias',
					align : 'left',
					minWidth : 200
				}, {
					display : '网元IP',
					name : 'ip',
					minWidth : 100
				}, {
					display : '阈值参数',
					columns : [{
								display : '表空间',
								name : 'tableSpaceName',
								minWidth : 160
							}, {
								display : '启用',
								name : 'isA',
								minWidth : 50,
								width : 100,
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
								minWidth : 50,
								width : 100,
								render : function(item) {
									if (item.isRPT == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}, {
								display : '阈值(%)',
								name : 'alarmValue',
								minWidth : 30,
								width : 100
							}, {
								display : '描述',
								name : 'remark',
								minWidth : 100
							}, {
								name : 'tableSpaceThresHoldId',
								hide : true,
								width : 1
							}]
				}],
		pageSize : 30,
		checkbox : true,
		data : $.extend(true, {}, tableSpaceThresHoldData),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'tableSpaceThresHoldId',
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
						line : true
					}, {
						text : '报表(是)',
						click : tbItemclick,
						icon : 'report'
					}, {
						text : '报表(否)',
						click : tbItemclick,
						icon : 'busy'
					}]
		},
		// 双击
		onDblClickRow : function(data, rowindex, rowobj) {
			openWindows(
					basePath
							+ "jsp/threshold/tableSpace/tableSpaceThresHoldEdit.jsp?tableSpaceThresHoldId="
							+ data.tableSpaceThresHoldId + "&alias="
							+ data.alias+"&ip="+data.ip, 300, 450, "表空间阈值编辑");
		}
	});

	$("#key").keyup(function() {
				f_search();
			});

});

// 查询过滤方法
function f_search() {
	grid.options.data = $.extend(true, {}, tableSpaceThresHoldData);
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
function f_getTableSpaceThresHoldList() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "tableSpaceThresHoldAjaxManager.ajax?action=getTableSpaceThresHoldList",
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
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.tableSpaceThresHoldId + ";";
					});
			$.ligerDialog.success(batchEable(idString));
		}
	} else if (item.text == "启用(否)") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.tableSpaceThresHoldId + ";";
					});
			$.ligerDialog.success(batchDisable(idString));
		}
	} else if (item.text == "报表(是)") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.tableSpaceThresHoldId + ";";
					});
			$.ligerDialog.success(batchReport(idString));
		}
	} else if (item.text == "报表(否)") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.tableSpaceThresHoldId + ";";
					});
			$.ligerDialog.success(batchDisReport(idString));
		}
	}
	refresh();
}

function batchEable(string) {
	var rs = "启用阈值错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "tableSpaceThresHoldAjaxManager.ajax?action=batchEable",
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
						+ "tableSpaceThresHoldAjaxManager.ajax?action=batchDisable",
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
						+ "tableSpaceThresHoldAjaxManager.ajax?action=batchReport",
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
						+ "tableSpaceThresHoldAjaxManager.ajax?action=batchDisReport",
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
	tableSpaceThresHoldData = f_getTableSpaceThresHoldList();
	grid.set({
				data : $.extend(true, {}, tableSpaceThresHoldData)
			});
	f_search();
}
