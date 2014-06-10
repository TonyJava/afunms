var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#oracleBaseInfoGrid").ligerGrid({
						columns : [{
									display : '属性名',
									name : 'subentity',
									minWidth : 150,
									width : 500
								}, {
									display : '属性值',
									name : 'thevalue',
									minWidth : 300
								}],
						pageSize : 15,
						checkbox : false,
						data : getOracleBaseInfoDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'subentity',
						width : '99.8%',
						height : '99.9%'

					});
			
		});

function getOracleBaseInfoDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleBaseInfoDetail",
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
