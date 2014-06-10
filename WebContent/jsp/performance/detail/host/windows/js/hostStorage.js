var grid = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			// grid
			grid = $("#hostStorageGrid").ligerGrid({
						columns : [{
									display : '介质索引',
									name : 'storageIndex',
									minWidth : 60,
									width : 80
								}, {
									display : '介质名称',
									name : 'storageName',
									minWidth : 60
								}, {
									display : '介质类型',
									name : 'storageType',
									minWidth : 60,
									width : 100
								}, {
									display : '存储介质空间大小',
									name : 'storageCap',
									minWidth : 100,
									width : 200
								}],
						pageSize : 22,
						checkbox : true,
						data : getHostStorageDetail(),
						allowHideColumn : false,
						rownumbers : true,
						colDraggable : true,
						rowDraggable : true,
						sortName : 'storageIndex',
						width : '99.8%',
						height : '99.9%'

					});
		});

function getHostStorageDetail() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "hostPerformanceAjaxManager.ajax?action=getHostStorageDetail",
				// 鍙傛暟
				data : {
					ip : getUrlParam("ip")
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
