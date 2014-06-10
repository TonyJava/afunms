var basePath = null;
var grid = null;
$(function() {
	basePath = $("#basePath").attr("value");
	// 创建表单结构
	var mainform = $("form");
	mainform.ligerForm({});

	// grid
	grid = $("#alarmGrid").ligerGrid({
		columns : [ {
			name : 'id',
			hide : true,
			width : 0.1
		}, {
			display : '等级',
			name : 'level',
			minWidth : 50,
			width : 50,
			render : function(item) {
				return createDiv(50, item.level);
			}
		}, {
			display : '网元名称',
			name : 'alias',
			align : 'left',
			minWidth : 150,
			width : 200
		}, {
			display : '网元IP',
			name : 'ip',
			minWidth : 100,
			width : 120
		}, {
			display : '子类型',
			name : 'subType',
			minWidth : 50,
			width : 60
		}, {
			display : '指标',
			align : 'left',
			name : 'subEntity',
			minWidth : 50,
			width : 140
		}, {
			display : '告警描述',
			align : 'left',
			name : 'alarmInfo',
			minWidth : 200
		}, {
			display : '发生次数',
			name : 'times',
			minWidth : 50,
			width : 60
		}, {
			display : '发起时间',
			name : 'startTime',
			minWidth : 80,
			width : 140
		}, {
			display : '更新时间',
			name : 'updateTime',
			minWidth : 80,
			width : 140
		} ],
		pageSize : 30,
		checkbox : true,
		data : f_getAlarmListByDate(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'ip',
		width : '99.8%',
		height : '100%',
		heightDiff :-4,
		onReload : function() {
			grid.set({
				data : f_getAlarmListByDate()
			});
		},
		// 工具栏
		toolbar : {
			items : [ {
				text : '删除',
				click : tbItemclick,
				icon : 'delete'
			}, {
				line : true
			} ]
		},
		// 双击
		onDblClickRow : function(data, rowindex, rowobj) {
		}
	});

	$("#bt").click(function() {
		refresh();
	});

});

// 获取数据方法
function f_getAlarmListByDate() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "alarmAjaxManager.ajax?action=getAlarmListByDate",
		// 参数
		data : {
			beginDate : $("#startDate").val(),
			endDate : $("#endDate").val()
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
function f_deleteThresHolds(string) {
	var rs = "删除错误";
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "alarmAjaxManager.ajax?action=deleteEvents",
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

// 打开新的窗口方法
function openWindows(href, h, w, t) {
	var win = $.ligerDialog.open({
		title : t,
		height : h,
		url : href,
		width : w,
		showMax : true,
		showToggle : true,
		showMin : true,
		isResize : true,
		slide : false
	});
}

// 工具栏功能定义
function tbItemclick(item) {
	if (item.text == "增加") {
		if ($("#subType").attr("value").length == 0) {
			$.ligerDialog.error("子类型不能为空");
		} else {
			openWindows(basePath + 'jsp/threshold/instance/showAdd.jsp?type='
					+ $("#type").attr("value") + "&subType="
					+ $("#subType").attr("value"), 520, 900, "增加阈值");
		}

	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
				idString += this.id + ";";
			});
			$.ligerDialog.success(f_deleteThresHolds(idString));
		}
	} else if (item.text == "查询") {
		refresh();
	}
}

// 刷新列表
function refresh() {
	grid.set({
		data : f_getAlarmListByDate()
	});
}

function itemclick(item, i) {
	refresh();
}

function createDiv(pw, value) {
	var text = "提示";
	var color = "blue";
	if (value == 1) {
		text = "普通";
		color = "yellow";
	} else if (value == 2) {
		text = "严重";
		color = "orange";
	} else if (value == 3) {
		text = "紧急";
		color = "red";
	}
	var divString = "<div style='margin-top:3px;background:" + color
			+ "'><div>" + text + "</div></div>";
	return divString;
}