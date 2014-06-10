var grid = null;
var basePath = null;
var id = null;
$(function() {
	basePath = $("#basePath").attr("value");
	id = $("#id").attr("value");
	
	// grid
	grid = $("#processGrid").ligerGrid({
		columns : [ {
			display : '进程ID',
			name : 'processId',
			align : 'left',
			width:80
		}, {
			display : '数据库',
			name : 'dataBase',
			minWidth : 80
		}, {
			display : '用户',
			name : 'user',
			minWidth : 60
		}, {
			display : 'CPU时间(S)',
			name : 'cpuTime',
			minWidth : 60
		}, {
			display : '磁盘读写',
			name : 'diskIO',
			minWidth : 60
		}, {
			display : '页数',
			name : 'pages',
			minWidth : 60
		}, {
			display : '状态',
			name : 'status',
			minWidth : 60
		}, {
			display : '工作站',
			name : 'workStation',
			minWidth : 80
		}, {
			display : '应用程序',
			name : 'application',
			minWidth : 100
		}, {
			display : '开始时间',
			name : 'startTime',
			minWidth : 100
		} ],
		pageSize : 30,
		data : f_getSqlserverProcessList(),
		allowHideColumn : false,
		rownumbers : true,
		colDraggable : true,
		rowDraggable : true,
		sortName : 'processId',
		width : '99.8%',
		height : '100%',
		heightDiff : -5,
		onReload : function() {
			grid.set({
				data : f_getSqlserverProcessList()
			});
		}
	});

});

// 获取数据方法
function f_getSqlserverProcessList() {
	var rs = null;
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "dbPerformanceAjaxManager.ajax?action=getSqlserverProcess",
		// 参数
		data : {
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

// 刷新列表
function refresh() {
	grid.set({
		data : f_getSqlserverProcessList()
	});
}

function itemclick(item, i) {
	refresh();
}