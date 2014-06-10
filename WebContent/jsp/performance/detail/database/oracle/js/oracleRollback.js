var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#oracleRollbackGrid").ligerGrid({
						columns : [{
									display : '回滚段',
									name : 'rollback',
									minWidth : 150
								}, {
									display : '包装',
									name : 'wraps',
									minWidth : 150,
									width : 200
								}, {
									display : '回滚',
									name : 'shrink',
									minWidth : 150,
									width : 200
								}, {
									display : '平均回滚',
									name : 'ashrink',
									minWidth : 100,
									width : 200
								}, {
									display : '扩扩展',
									name : 'extend',
									minWidth : 100,
									width : 200
								}],
						pageSize : 15,
						checkbox : false,
						data : getOracleRollbackDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'machine',
						width : '99.8%',
						height : '99.9%'

					});
			
		});

function getOracleRollbackDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleRollbackDetail",
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
