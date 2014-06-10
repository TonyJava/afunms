var basePath = null;
var id = null;

var connGrid = null;
var sqlGrid = null;

var cacheGrid = null;
var scanGrid = null;

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
	connGrid = $("#connGrid").ligerGrid({
		columns : [ {
			display : '数据库连接',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'connRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	sqlGrid = $("#sqlGrid").ligerGrid({
		columns : [ {
			display : '数据库SQL',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'sqlRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	cacheGrid = $("#cacheGrid").ligerGrid({
		columns : [ {
			display : '数据库缓存',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'cacheRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	scanGrid = $("#scanGrid").ligerGrid({
		columns : [ {
			display : '数据库扫描信息',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'scanRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	setSqlserverDb();
});

function setSqlserverDb() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath + "dbPerformanceAjaxManager.ajax?action=getSqlserverDb",
		// 参数
		data : {
			id : id
		},
		dataType : "json",
		success : function(array) {
			if (array) {
				if (array.connRows.length > 0) {
					connGrid.set({
						data : array
					});
				}
				if (array.sqlRows.length > 0) {
					sqlGrid.set({
						data : array
					});
				}

				if (array.cacheRows.length > 0) {
					cacheGrid.set({
						data : array
					});
				}

				if (array.scanRows.length > 0) {
					scanGrid.set({
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