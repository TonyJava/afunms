var grid = null;
var basePath = null;
$(function() {
	basePath = $("#basePath").attr("value");

	$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
			"like", "equal", "notequal", "greater", "less", "startwith",
			"endwith" ];

	// grid
	grid = $("#firewallEventGrid").ligerGrid({
		columns : [ {
			display : '事件ID',
			name : 'eventId',
			minWidth : 60,
			width : 80
		}, {
			display : '事件等级',
			name : 'eventLevel',
			minWidth : 60,
			width : 80
		}, {
			display : '事件描述',
			name : 'eventContent',
			minWidth : 60
		}, {
			display : '登记日期',
			name : 'rptTime',
			minWidth : 60,
			width : 150
		}, {
			display : '登记人',
			name : 'rptMan',
			minWidth : 100,
			width : 100
		}, {
			display : '处理状态',
			name : 'eventStatus',
			minWidth : 100,
			width : 100
		} ],
		pageSize : 22,
		checkbox : true,
		data : getFirewallEventDetail(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'rptTime',
		width : '99.8%',
		height : '99.9%',

		// 工具栏
		toolbar : {
			items : [ {
				text : '高级自定义查询',
				click : sItemclick,
				icon : 'search2'
			}, {
				text : '删除',
				click : itemclick,
				icon : 'delete'
			} ]
		}

	});
});


function getFirewallEventDetail() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "netPerformanceAjaxManager.ajax?action=getNetEventDetail",
		// 参数
		data : {
			ip : getUrlParam("ip")
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

function sItemclick() {
	grid.options.data = $.extend(true, {}, getFirewallEventDetail());
	grid.showFilter();
}

function itemclick(item) {
	var rows = grid.getSelectedRows();
	var idString = "";
	if (rows.length == 0) {
		$.ligerDialog.error("请选择删除项");
	} else {
		$(rows).each(function() {
					idString += this.eventId + ";";
				});
		$.ligerDialog.success(f_deleteEvent(idString));
	}
}

function f_deleteEvent(string) {
	var rs = "删除错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "alarmAjaxManager.ajax?action=deleteEvents",
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
