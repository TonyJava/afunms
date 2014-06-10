var basePath = null;
var grid = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({});

			// grid
			grid = $("#smsAlarmGrid").ligerGrid({
						columns : [{
									display : '名称',
									name : 'name',
									minWidth : 150,
									width : 180
								}, {
									display : '手机号码',
									name : 'phone',
									minWidth : 150,
									width : 180
								}, {
									display : '告警描述',
									name : 'alarmDescr',
									minWidth : 200,
									width : 300
								}, {
									display : '告警时间',
									name : 'happenTime',
									minWidth : 150,
									width : 200
								}, {
									name : 'smsAlarmId',
									hide : true,
									width : 0.1
								}],
						pageSize : 30,
						checkbox : true,
						data : f_getSmsAlarmListByDate(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'happenTime',
						width : '99.8%',
						height : '100%',
						heightDiff :-5,
						onReload : function() {
							grid.set({
										data : f_getSmsAlarmListByDate()
									});
						},
						// 工具栏
						toolbar : {
							items : [{
										text : '删除',
										click : tbItemclick,
										icon : 'delete'
									}]
						}
					});

			$("#bt").click(function() {
						refresh();
					});

		});

// 获取数据方法
function f_getSmsAlarmListByDate() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "alarmAjaxManager.ajax?action=getSmsAlarmListByDate",
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
	if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.smsAlarmId + ";";
					});
			$.ligerDialog.success(f_deleteSmsAlarm(idString));
		}
	}
}

// 刷新列表
function refresh() {
	grid.set({
				data : f_getSmsAlarmListByDate()
			});
}

function f_deleteSmsAlarm(string) {
	var rs = "删除错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "alarmAjaxManager.ajax?action=deleteSmsAlarm",
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
