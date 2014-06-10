var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#oracleJobGrid").ligerGrid({
						columns : [{
									display : '作业',
									name : 'job',
									minWidth : 150
								}, {
									display : 'LOG用户',
									name : 'loguser',
									minWidth : 150
								}, {
									display : '最后日期',
									name : 'lastdate',
									minWidth : 150
								}, {
									display : '状态',
									name : 'failures',
									minWidth : 150
								}],
						pageSize : 15,
						checkbox : false,
						data : getOracleJobDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'job',
						width : '99.8%',
						height : '99.9%'

					});
			
		});

function getOracleJobDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleJobDetail",
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
