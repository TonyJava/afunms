var basePath = null;
var content = null;
var grid = null;
$(function() {
	basePath = $("#basePath").attr("value");
	content = $("#content").val();
	// 创建表单结构
	var mainform = $("form");
	mainform.ligerForm({});

	// grid
	grid = $("#trapGrid").ligerGrid({
		columns : [ {
			name : 'id',
			hide : true,
			width : 0.1
		}, {
			display : '等级',
			name : 'level',
			minWidth : 50,
			width : 100,
			render : function(item) {
				return createDiv(50, item.level);
			}
		}, {
			display : '告警来源',
			name : 'alias',
			align : 'left',
			minWidth : 100,
			width : 150
		}, {
			display : 'Trap描述',
			align : 'left',
			name : 'content',
			minWidth : 200
		}, {
			display : '登记日期',
			name : 'rtime',
			minWidth : 50,
			width : 150
		}, {
			display : '登记人',
			name : 'rptman',
			minWidth : 80,
			width : 140
		} ],
		pageSize : 30,
		checkbox : true,
		data : f_getTrapAlarmListByDate(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'level',
		width : '99.8%',
		height : '100%',
		heightDiff : -4,
		onReload : function() {
			grid.set({
				data : f_getTrapAlarmListByDate()
			});
		},
		// 工具栏
		toolbar : {
			items : [ {
				text : '删除',
				click : tbItemclick,
				icon : 'delete'
			} ]
		}
	});

	$("#submit").click(function() {
		refresh();
	});

});

// 获取数据方法
function f_getTrapAlarmListByDate() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		contentType : "application/x-www-form-urlencoded;charset=UTF-8",
		url : basePath + "trapAjaxManager.ajax?action=getTrapAlarmListByDate",
		// 参数
		data : {
			beginDate : $("#startDate").val(),
			endDate : $("#endDate").val(),
			content : $("#content").val()
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

// 删除指标方法
function f_deleteTrapNode(string) {
	var rs = "删除错误";
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "trapAjaxManager.ajax?action=deleteTrapNodes",
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

// 工具栏功能定义
function tbItemclick(item) {
	if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
				idString += this.id + ";";
			});
			$.ligerDialog.success(f_deleteTrapNode(idString));
		}
	} else if (item.text == "查询") {
		refresh();
	}
}

// 刷新列表
function refresh() {
	grid.set({
		data : f_getTrapAlarmListByDate()
	});
}

function itemclick(item, i) {
	refresh();
}

function createDiv(pw, value) {
	var color = "blue";
	if (value == "普通事件") {
		color = "yellow";
	} else if (value == "严重事件") {
		color = "orange";
	} else if (value == "紧急事件") {
		color = "red";
	} else {
		value = "提示";
	}
	var divString = "<div style='margin-top:3px;background:" + color
			+ "'><div>" + value + "</div></div>";
	return divString;
}
