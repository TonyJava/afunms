var grid = null;
var lockInfogrid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#oracleLockGrid").ligerGrid({
						columns : [{
									display : '用户名',
									name : 'username',
									minWidth : 150,
									width : 150
								}, {
									display : '状态',
									name : 'status',
									minWidth : 150,
									width : 150
								}, {
									display : '机器',
									name : 'machine',
									minWidth : 200
								}, {
									display : '会话类型',
									name : 'sessiontype',
									minWidth : 100,
									width : 150
								}, {
									display : '登陆时间',
									name : 'logontime',
									minWidth : 100,
									width : 200
								}, {
									display : '程序名',
									name : 'program',
									minWidth : 150,
									width : 180
								}, {
									display : '锁类型',
									name : 'locktype',
									minWidth : 80,
									width : 80
								}, {
									display : 'LMODE',
									name : 'lmode',
									minWidth : 80,
									width : 80
								}, {
									display : '请求',
									name : 'requeststr',
									minWidth : 70,
									width : 80
								}],
						pageSize : 15,
						checkbox : true,
						data : getOracleLockDetail("lock"),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'username',
						width : '99.8%',
						height : '70%'

					});
			lockInfogrid = $("#oracleLockInfoGrid").ligerGrid({
				columns : [{
							display : '死锁数',
							name : 'deadlockcount',
							minWidth : 100,
							width : 120
						}, {
							display : '锁等待数',
							name : 'lockwaitcount',
							minWidth : 100,
							width : 120
						}, {
							display : '最大连接数',
							name : 'maxprocesscount',
							minWidth : 100,
							width : 120
						}, {
							display : '当前连接数',
							name : 'processcount',
							minWidth : 100,
							width : 120
						}, {
							display : '当前会话数',
							name : 'currentsessioncount',
							minWidth : 100,
							width : 120
						}, {
							display : '最大允许会话数',
							name : 'useablesessioncount',
							minWidth : 100,
							width : 120
						}, {
							display : '可用会话数百分比(%)',
							name : 'useablesessionpercent',
							minWidth : 100,
							width : 150
						}, {
							display : '等待解锁的会话数',
							name : 'lockdsessioncount',
							minWidth : 100,
							width : 130
						}, {
							display : '回滚数',
							name : 'rollbacks',
							minWidth : 100,
							width : 130
						}, {
							display : '回滚与提交之比',
							name : 'rollbackcommitpercent',
							minWidth : 100,
							width : 130
						}],
				pageSize : 15,
				checkbox : true,
				data : getOracleLockDetail("lockInfo"),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'username',
				width : '99.8%',
				height : '29.9%'
			});
		});

function getOracleLockDetail(type) {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleLockDetail",
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
