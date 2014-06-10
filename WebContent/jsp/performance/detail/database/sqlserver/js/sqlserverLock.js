var basePath = null;
var id = null;

var lockGrid = null;
var lockDetailGrid = null;

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
	lockGrid = $("#lockGrid").ligerGrid({
		columns : [ {
			display : '数据库锁信息',
			name : 'key',
			minWidth : 350
		}, {
			display : '值',
			name : 'value',
			minWidth : 150
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'lockRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	lockDetailGrid = $("#lockDetailGrid").ligerGrid({
		columns : [ {
			display : '锁资源',
			name : 'lockResource',
			minWidth : 100
		}, {
			display : '数据库',
			name : 'dataBase',
			minWidth : 80
		}, {
			display : '资源类型',
			name : 'resourceType',
			minWidth : 80
		}, {
			display : '锁请求模式',
			name : 'requestType',
			minWidth : 80
		}, {
			display : '锁请求状态',
			name : 'requestStatus',
			minWidth : 80
		}, {
			display : '锁引用次数',
			name : 'useCount',
			minWidth : 80
		}, {
			display : '锁生存周期',
			name : 'lockTTL',
			minWidth : 80
		}, {
			display : '进程ID',
			name : 'processId',
			minWidth : 80
		}, {
			display : '对象类型',
			name : 'objectType',
			minWidth : 80
		} ],
		allowHideColumn : false,
		rownumbers : true,
		root : 'lockDetailRows',
		colDraggable : true,
		rowDraggable : true,
		width : '99.8%',
		height : 320
	});

	setSqlserverLock();
});

function setSqlserverLock() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "dbPerformanceAjaxManager.ajax?action=getSqlserverLock",
		// 参数
		data : {
			id : id
		},
		dataType : "json",
		success : function(array) {
			if (array) {
				if (array.lockRows.length > 0) {
					lockGrid.set({
						data : array
					});
				}
				if (array.lockDetailRows.length > 0) {
					lockDetailGrid.set({
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