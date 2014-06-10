var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#oracleUserGrid").ligerGrid({
						columns : [{
									display : '用户名',
									name : 'username',
									minWidth : 150
								}, {
									display : '状态',
									name : 'status',
									minWidth : 140,
									width : 130
								}, {
									display : '使用CPU的时间',
									name : 'cpu_time',
									minWidth : 100,
									width : 130
								}, {
									display : '磁盘排序次数',
									name : 'sorts',
									minWidth : 100,
									width : 130
								}, {
									display : '逻辑读的次数',
									name : 'buffer_gets',
									minWidth : 100,
									width : 130
								}, {
									display : '使用的内存数',
									name : 'runtime_mem',
									minWidth : 100,
									width : 130
								}, {
									display : '打开的游标数',
									name : 'cursor',
									minWidth : 100,
									width : 130
								}, {
									display : '物理读的次数',
									name : 'disk_reads',
									minWidth : 100,
									width : 130
								}, {
									display : '物理写的次数',
									name : 'disk_write',
									minWidth : 100,
									width : 130
								}],
						pageSize : 15,
						checkbox : false,
						data : getOracleUserDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'username',
						width : '99.8%',
						height : '99.9%'

					});
			
		});

function getOracleUserDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleUserDetail",
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
