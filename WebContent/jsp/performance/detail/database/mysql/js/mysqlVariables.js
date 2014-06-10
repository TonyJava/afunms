var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#mysqlVariablesGrid").ligerGrid({
						columns : [{
									display : '配置项',
									name : 'variablesname',
									minWidth : 200,
									width : 650
								}, {
									display : '值',
									name : 'variablesvalue',
									minWidth : 120,
									width : 650
								}],
						pageSize : 30,
						checkbox : false,
						data : f_getMysqlVariablesDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'variablesname',
						width : '99.8%',
						height : '99.9%'

					});
		});

function f_getMysqlVariablesDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getMysqlVariablesDetail",
				// 参数
				data : {
					ip : ip,
					id : id
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
