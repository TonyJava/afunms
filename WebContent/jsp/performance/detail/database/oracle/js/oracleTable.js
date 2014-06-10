var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#oracleTableGrid").ligerGrid({
						columns : [{
									display : '名称',
									name : 'tablename',
									minWidth : 150
								}, {
									display : '占用空间(单位:M)',
									name : 'spaces',
									minWidth : 150,
									width : 200
								}],
						pageSize : 15,
						checkbox : false,
						data : getOracleTableDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'machine',
						width : '99.8%',
						height : '99.9%'

					});
			
		});

function getOracleTableDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleTableDetail",
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
