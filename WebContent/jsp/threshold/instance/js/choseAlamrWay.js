var grid = null;
var key = null;
var alarmWayData = null;
var basePath = null;
$(function() {
	basePath = $("#basePath").attr("value");
	alarmWayData = f_setAlarmWayData();
	key = $("#key").ligerTextBox({});
	grid = $("#alarmWayGrid").ligerGrid({
		columns : [ {
			name : 'alarmWayId',
			hide : true,
			width : 0.1
		}, {
			display : '名称',
			name : 'name',
			align : 'left',
			width : 80
		}, {
			display : '描述',
			name : 'remark',
			align : 'left'
		}, {
			display : '默认',
			name : 'isDefault',
			width : 100,
			render : function(item) {
				if (item.isDefault == 0) {
					return "否";
				} else {
					return "是";
				}
			}
		} ],
		pageSize : 30,
		rownumbers : true,
		allowHideColumn : false,
		checkbox : true,
		where : f_getWhere(),
		data : $.extend(true, {}, alarmWayData),
		width : '100%',
		height : 380,
		onReload : function() {
			alarmWayData = f_setAlarmWayData();
			grid.set({
				data : $.extend(true, {}, alarmWayData)
			});
		}
	});
	$("#key").keyup(function() {
		f_search();
	});

});
function f_search() {
	grid.options.data = $.extend(true, {}, alarmWayData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#key").val();
		return rowdata.name.indexOf(key) > -1;
	};
	return clause;
}

function f_setAlarmWayData() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "alarmWayAjaxManager.ajax?action=getAlarmWayList",
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