var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#oracleWaitGrid").ligerGrid({
						columns : [{
									display : '事例',
									name : 'event',
									minWidth : 150
								}, {
									display : '之前',
									name : 'prev',
									minWidth : 150,
									width : 200
								}, {
									display : '当前',
									name : 'curr',
									minWidth : 150,
									width : 200
								}, {
									display : '总共',
									name : 'total',
									minWidth : 100,
									width : 200
								}],
						pageSize : 15,
						checkbox : false,
						data : getOracleWaitDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'event',
						width : '99.8%',
						height : '99.9%'

					});
			
		});

function getOracleWaitDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleWaitDetail",
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
