var memGrid = null;
var diskGrid = null;
var sortGrid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			
			$("div.divHead").each(function() {
				$(this).click(function() {
							var isShowDiv = $(this).parent()
									.find("div").eq(1);
							$(isShowDiv).slideToggle(500);
							liger.get($(isShowDiv).attr("id")).reload();
						});
			});

			// grid
			memGrid = $("#oracleMemsqlGrid").ligerGrid({
						columns : [{
									display : '占用内存最多的10条记录',
									name : 'memsql',
									minWidth : 150
								}, {
									display : '占内存读取的比列',
									name : 'pct_bufgets',
									minWidth : 150,
									width : 200
								}, {
									display : '用户',
									name : 'username',
									minWidth : 150,
									width : 200
								}],
						pageSize : 15,
						checkbox : false,
						data : getOracleTopsqlDetail("memsql"),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'memsql',
						width : '99.8%',
						height : '70.9%'
					});
			// grid
			diskGrid = $("#oracleDisksqlGrid").ligerGrid({
						columns : [{
									display : '磁盘读写最多的前10条sql语句',
									name : 'disksql',
									minWidth : 150
								}, {
									display : '总磁盘物理读数量',
									name : 'totaldisk',
									minWidth : 150,
									width : 150
								}, {
									display : '次数',
									name : 'totalexec',
									minWidth : 150,
									width : 150
								}, {
									display : '平均磁盘物理读数量',
									name : 'diskreads',
									minWidth : 150,
									width : 150
								}],
						pageSize : 15,
						checkbox : false,
						data : getOracleTopsqlDetail("disksql"),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'disksql',
						width : '99.8%',
						height : '70.9%'
					});
			
			// grid
			sortGrid = $("#oracleSortsqlGrid").ligerGrid({
						columns : [{
									display : '排序最多的前10条sql语句',
									name : 'sortsql',
									minWidth : 150
								}, {
									display : '总排序的次数',
									name : 'sorts',
									minWidth : 150,
									width : 150
								}, {
									display : '执行的次数',
									name : 'executions',
									minWidth : 150,
									width : 150
								}, {
									display : '平均每次的排序数',
									name : 'sortsexec',
									minWidth : 150,
									width : 150
								}],
						pageSize : 15,
						checkbox : false,
						data : getOracleTopsqlDetail("sortsql"),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'sortsql',
						width : '99.8%',
						height : '80%'
					});
		});

function getOracleTopsqlDetail(type) {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleTopsqlDetail",
				// 参数
				data : {
					ip : ip,
					id : id,
					type : type
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
