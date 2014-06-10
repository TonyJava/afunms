var basePath = null;
var grid = null;
var key = null;
var diskThresHoldData = null;
$(function() {
	basePath = $("#basePath").attr("value");
	diskThresHoldData = f_getDiskThresHoldList();
	key = $("#key").ligerTextBox({});
	// grid
	grid = $("#diskThresHoldGrid").ligerGrid({
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
								display : '磁盘名称',
								name : 'diskName',
								minWidth : 160
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
								display : '一级',
								name : 'firstLevelValue',
								minWidth : 30,
								width : 100
							}, {
								display : '短信',
								name : 'firstIsSM',
								minWidth : 10,
								width : 60,
								render : function(item) {
									if (item.firstIsSM == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}, {
								display : '二级',
								name : 'secondLevelValue',
								minWidth : 30,
								width : 100
							}, {
								display : '短信',
								name : 'secondIsSM',
								minWidth : 10,
								width : 60,
								render : function(item) {
									if (item.secondIsSM == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}, {
								display : '三级',
								name : 'thirdLevelValue',
								minWidth : 30,
								width : 100
							}, {
								display : '短信',
								name : 'thirdIsSM',
								minWidth : 10,
								width : 60,
								render : function(item) {
									if (item.thirdIsSM == 0) {
										return "否";
									} else {
										return "是";
									}
								}
							}, {
								display : '描述',
								name : 'remark',
								minWidth : 100
							}, {
								name : 'diskThresHoldId',
								hide : true,
								width : 1
							}]
				}],
		pageSize : 30,
		checkbox : true,
		data : $.extend(true, {}, diskThresHoldData),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'diskThresHoldId',
		width : '99.8%',
		height : '100%',
		heightDiff :-5,
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
					}]
		},
		// 双击
		onDblClickRow : function(data, rowindex, rowobj) {
			openDlgWindow(
					basePath
							+ "jsp/threshold/disk/diskThresHoldEdit.jsp?diskThresHoldId="
							+ data.diskThresHoldId + "&alias=" +encodeURI(encodeURI( data.alias)),
					520, 450, "磁盘阈值编辑");
		}
	});

	$("#key").keyup(function() {
				f_search();
			});

});

// 查询过滤方法
function f_search() {
	grid.options.data = $.extend(true, {}, diskThresHoldData);
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
function f_getDiskThresHoldList() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "diskThresHoldAjaxManager.ajax?action=getDiskThresHoldList",
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

// 删除阈值方法(保留,不应该有这个方法的)
function f_deleteDiskThresHolds(string) {
	var rs = "删除错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "diskThresHoldAjaxManager.ajax?action=deleteDiskThresHolds",
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

// 工具栏功能定义
function tbItemclick(item) {
	if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.diskThresHoldId + ";";
					});
			$.ligerDialog.success(f_deleteDiskThresHolds(idString));
		}
	} else if (item.text == "启用(是)") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.diskThresHoldId + ";";
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
						idString += this.diskThresHoldId + ";";
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
						idString += this.diskThresHoldId + ";";
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
						idString += this.diskThresHoldId + ";";
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
						+ "diskThresHoldAjaxManager.ajax?action=batchEable",
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
						+ "diskThresHoldAjaxManager.ajax?action=batchDisable",
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
						+ "diskThresHoldAjaxManager.ajax?action=batchReport",
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
						+ "diskThresHoldAjaxManager.ajax?action=batchDisReport",
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
	diskThresHoldData = f_getDiskThresHoldList();
	grid.set({
				data : $.extend(true, {}, diskThresHoldData)
			});
	f_search();
}
