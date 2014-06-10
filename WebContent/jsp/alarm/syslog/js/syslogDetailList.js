var basePath = null;
var content = null;
var grid = null;
$(function() {
	basePath = $("#basePath").attr("value");
	content = $("#content").val();
	// 创建表单结构
	var mainform = $("form");
	mainform.ligerForm({});

	liger.get('type').set({
		data : typeItem,
		valueField : 'value',
		textField : 'text'
	});
	liger.get('type').setValue("-1");
	// grid
	grid = $("#syslogDetailGrid").ligerGrid({
		columns : [ {
			name : 'id',
			hide : true,
			width : 0.1
		}, {
			display : '名称',
			name : 'hostname',
			minWidth : 50,
			width : 100
		}, {
			display : 'ip地址',
			name : 'ipaddress',
			minWidth : 100,
			width : 150
		}, {
			display : '设备类型',
			name : 'category',
			minWidth : 150
		}, {
			display : '状态',
			name : 'status',
			minWidth : 50,
			width : 100,
			render : function(item) {
				return createDiv(50, item.level);
			}
		}, {
			display : '错误',
			name : 'errors',
			minWidth : 50,
			width : 80
		}, {
			display : '警告',
			name : 'warnings',
			minWidth : 50,
			width : 80
		}, {
			display : '失败',
			name : 'failures',
			minWidth : 50,
			width : 80
		}, {
			display : '其他',
			name : 'others',
			minWidth : 50,
			width : 80
		}, {
			display : '全部',
			name : 'all',
			minWidth : 50,
			width : 80
		} ],
		pageSize : 30,
		checkbox : true,
		data : f_getSyslogAlarmListByDate(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'hostname',
		width : '99.8%',
		height : '100%',
		heightDiff : 0,
		onReload : function() {
			grid.set({
				data : f_getSyslogAlarmListByDate()
			});
		}
	});

	$("#bt").click(function() {
		grid.set({
			data : f_getSyslogAlarmListByDate()
		});
	});

});

// 获取数据方法
function f_getSyslogAlarmListByDate() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "syslogAjaxManager.ajax?action=getSyslogDetailForNodes",
		// 参数
		data : {
			beginDate : $("#startDate").val(),
			endDate : $("#endDate").val(),
			ipaddress : $("#ipaddress").val(),
			strclass : liger.get("type").getValue()
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
function f_deleteSyslogNode(string) {
	var rs = "删除错误";
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "syslogAjaxManager.ajax?action=deleteSyslogNodes",
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
				idString += this.id + ";";
			});
			$.ligerDialog.success(f_deleteSyslogNode(idString));
		}
	} else if (item.text == "查询") {
		refresh();
	}
}

// 刷新列表
function refresh() {
	grid.set({
		data : f_getSyslogAlarmListByDate()
	});
}

function itemclick(item, i) {
	refresh();
}

function createDiv(pw, value) {
	var divString = "<img class='statePic' src=" + basePath
			+ "resource/image/statusCancelled.gif />";
	if (value == "0") {
		divString = "<img class='statePic' src=" + basePath
				+ "resource/image/statusCancelled.gif />";
	} else {
		divString = "<img class='statePic' src=" + basePath
				+ "resource/image/statusOK.gif />";
	}
	return divString;
}

var typeItem = [ {
	value : "-1",
	text : "不限"
}, {
	value : "1",
	text : "主机"
}, {
	value : "2",
	text : "网络"
} ];