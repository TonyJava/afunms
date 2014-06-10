var grid = null;
var basePath = null;
var ip = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ip = $("#ip").attr("value");
			id = $("#id").attr("value");
			// grid
			grid = $("#oracleSpacesGrid").ligerGrid({
				columns : [{
							display : '文件名',
							name : 'filename',
							minWidth : 200
						}, {
							display : '表空间',
							name : 'tablespace',
							minWidth : 100,
							width : 110
						}, {
							display : '空间大小(MB)',
							name : 'size',
							minWidth : 100,
							width : 100
						}, {
							display : '空闲大小啊(MB)',
							name : 'free',
							minWidth : 100,
							width : 100
						}, {
							display : '空闲比例',
							name : 'percent',
							minWidth : 100,
							width : 100
						}, {
							display : '物理读',
							name : 'pyr',
							minWidth : 100,
							width : 100
						}, {
							display : '物理块读',
							name : 'pbr',
							minWidth : 100,
							width : 100
						}, {
							display : '物理写',
							name : 'pyw',
							minWidth : 100,
							width : 100
						}, {
							display : '物理块写',
							name : 'pbw',
							minWidth : 100,
							width : 100
						}, {
							display : '文件状态',
							name : 'status',
							minWidth : 100,
							width : 120
						}],
				pageSize : 15,
				checkbox : false,
				data : getOracleSpacesDetail(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'filename',
				width : '99.8%',
				height : '99.9%'
			});
		});

function getOracleSpacesDetail(type) {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "dbPerformanceAjaxManager.ajax?action=getOracleSpacesDetail",
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
