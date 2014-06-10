var basePath = null;
var id = null;

var pageGrid = null;
var memoryGrid = null;

var avgWaitGrid = null;
var processWaitGrid = null;

var startUpWaitGrid = null;
var countWaitGrid = null;

$(function() {
	basePath = $("#basePath").attr("value");
	id = $("#id").attr("value");

	$("div.titleDiv").each(function() {
		$(this).click(function() {
			var isShowDiv = $(this).parent().find("div.ContentDiv").eq(0);
			$(isShowDiv).slideToggle(500);
			$(isShowDiv).find(".gridDiv").each(function() {
				liger.get($(this).find("div").attr("id")).reload();
			});
		});
	});

	$(".ContentDiv").ligerPanel({
		showToggle : true,
		width : '100%',
		height : 300
	});

	// grid
	pageGrid = $("#pageGrid").ligerGrid({
		columns : [ {
			display : '数据库分页、错误、游标',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'pageRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	memoryGrid = $("#memoryGrid").ligerGrid({
		columns : [ {
			display : '数据库内存',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'memoryRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	avgWaitGrid = $("#avgWaitGrid").ligerGrid({
		columns : [ {
			display : '平均等待锁',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'avgWaitRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	processWaitGrid = $("#processWaitGrid").ligerGrid({
		columns : [ {
			display : '进行等待锁',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'processWaitRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	startUpWaitGrid = $("#startUpWaitGrid").ligerGrid({
		columns : [ {
			display : '启动等待锁',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'startUpWaitRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	countWaitGrid = $("#countWaitGrid").ligerGrid({
		columns : [ {
			display : '累计等待锁',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'countWaitRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	setSqlserverSystem();
});

function setSqlserverSystem() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "dbPerformanceAjaxManager.ajax?action=getSqlserverSystem",
		// 参数
		data : {
			id : id
		},
		dataType : "json",
		success : function(array) {
			if (array) {
				if (array.pageRows.length > 0) {
					pageGrid.set({
						data : array
					});
				}
				if (array.memoryRows.length > 0) {
					memoryGrid.set({
						data : array
					});
				}

				if (array.avgWaitRows.length > 0) {
					avgWaitGrid.set({
						data : array
					});
				}

				if (array.processWaitRows.length > 0) {
					processWaitGrid.set({
						data : array
					});
				}

				if (array.startUpWaitRows.length > 0) {
					startUpWaitGrid.set({
						data : array
					});
				}

				if (array.countWaitRows.length > 0) {
					countWaitGrid.set({
						data : array
					});
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});

}