var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#mysqlStatusGrid").ligerGrid({
						columns : [{
									display : '标签',
									name : 'name',
									minWidth : 200,
									width : 620
								}, {
									display : '值',
									name : 'value',
									minWidth : 120,
									width : 650
								}],
						pageSize : 22,
						checkbox : true,
						data : getMysqlStatusDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'name',
						width : '99.8%',
						height : '99.9%'

					});
		});

function getMysqlStatusDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getMysqlStatusDetail",
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
