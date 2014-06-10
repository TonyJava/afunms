var grid = null;
var processKey = null;
var processData = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			processData = f_setProcessData();
			processKey = $("#processKey").ligerTextBox({});
			grid = $("#processGrid").ligerGrid({
						columns : [{
									display : '进程名',
									name : 'name',
									align : 'left',
									width : 230
								}, {
									display : '运行状态',
									name : 'state',
									align : 'left'
								}],
						pageSize : 30,
						rownumbers : true,
						allowHideColumn : false,
						where : f_getWhere(),
						data : $.extend(true, {}, processData),
						width : '100%',
						height : 380
					});
			$("#processKey").keyup(function() {
						f_search();
					});

		});
function f_search() {
	grid.options.data = $.extend(true, {}, processData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#processKey").val();
		return rowdata.name.indexOf(key) > -1;
	};
	return clause;
}

function f_setProcessData() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "processMonitorAjaxManager.ajax?action=getProcessListByIp",
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