var grid = null;
var ipKey = null;
var hostData = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			hostData = f_setHostData();
			ipKey = $("#ipKey").ligerTextBox({});
			grid = $("#hostGrid").ligerGrid({
						columns : [{
									display : '网元IP',
									name : 'ip',
									align : 'left',
									width : 230
								}, {
									display : '网元别名',
									name : 'alias',
									align : 'left'
								}, {
									name : 'nodeId',
									hide : true,
									width : 0.1
								}, {
									name : 'isM',
									hide : true,
									width : 0.1
								}],
						pageSize : 30,
						rownumbers : true,
						allowHideColumn : false,
						where : f_getWhere(),
						data : $.extend(true, {}, hostData),
						width : '100%',
						height : 300
					});
			$("#ipKey").keyup(function() {
						f_search();

					});

		});
function f_search() {
	grid.options.data = $.extend(true, {}, hostData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#ipKey").val();
		return rowdata.ip.indexOf(key) > -1;
	};
	return clause;
}

function f_setHostData() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "processMonitorAjaxManager.ajax?action=getHostList",
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