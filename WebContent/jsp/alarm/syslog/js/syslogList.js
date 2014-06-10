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
			grid = $("#syslogGrid").ligerGrid({
				columns : [{
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
							display : '来源',
							name : 'alias',
							align : 'left',
							minWidth : 200,
							width:300
						}, {
							display : '描述',
							align : 'left',
							name : 'content',
							minWidth : 200
						}, {
							display : '接收时间',
							name : 'rtime',
							minWidth : 50,
							width : 150
						}],
				pageSize : 30,
				checkbox : true,
				data : f_getSyslogAlarmListByDate(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'level',
				width : '99.8%',
				height : '100%',
				heightDiff :-4,
				onReload : function() {
					grid.set({
								data : f_getSyslogAlarmListByDate()
							});
				},
				// 工具栏
				toolbar : {
					items : [{
								text : '删除',
								click : tbItemclick,
								icon : 'delete'
							}]
				},

				// 双击
				onDblClickRow : function(data, rowindex, rowobj) {
					openDlgWindow(encodeURI(encodeURI(basePath
									+ "jsp/alarm/syslog/contentDetail.jsp?content='" + data.content
									+ "'")), 250, 520, "Syslog详细");
				}
			});

			$("#bt").click(function() {
						refresh();
					});

		});
// 刷新列表
function refresh() {
	grid.set({
				data : f_getSyslogAlarmListByDate()
			});
}

// 获取数据方法
function f_getSyslogAlarmListByDate() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				contentType : "application/x-www-form-urlencoded;charset=UTF-8",
				url : basePath + "syslogAjaxManager.ajax?action=getSyslogAlarmListByDate",
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
	var color=null;
	if (value == "error") {
		color = "#CC0000";
	} else if (value == "warning") {
		color = "#FFCC00";
	}
	var divString = "<span style=\"color:" + color + "\">" + value + "</span>";
	return divString;
}
