var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#oracleSessionGrid").ligerGrid({
						columns : [{
									display : '工作站',
									name : 'machine',
									minWidth : 100,
									width : 150
								}, {
									display : '用户',
									name : 'username',
									minWidth : 150,
									width : 150
								}, {
									display : '应用程序',
									name : 'program',
									minWidth : 150,
									width : 150
								}, {
									display : 'SESSION状态',
									name : 'status',
									minWidth : 100
								}, {
									display : 'SESSION类型',
									name : 'sessiontype',
									minWidth : 100,
									width : 200
								}, {
									display : '命令',
									name : 'command',
									minWidth : 150,
									width : 180
								}, {
									display : '登陆时间',
									name : 'logontime',
									minWidth : 100,
									width : 200
								}],
						pageSize : 15,
						checkbox : false,
						data : getOracleSessionDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'machine',
						width : '99.8%',
						height : '99.9%'

					});
			
		});

function getOracleSessionDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleSessionDetail",
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
