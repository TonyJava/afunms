var grid = null;
var ifIndexKey = null;
var ifIndexData = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			ifIndexData = f_setifIndexData();
			ifIndexKey = $("#ifIndexKey").ligerTextBox({});
			grid = $("#ifIndexGrid").ligerGrid({
						columns : [{
									display : '端口索引',
									name : 'ifIndex',
									align : 'left',
									width : 100
								}, {
									display : '端口描述',
									name : 'ifDescr',
									align : 'left',
									width : 350
								}],
						pageSize : 30,
						rownumbers : true,
						allowHideColumn : false,
						where : f_getWhere(),
						data : $.extend(true, {}, ifIndexData),
						width : '100%',
						height : 300
					});
			$("#ifIndexKey").keyup(function() {
						f_search();
					});

		});
function f_search() {
	grid.options.data = $.extend(true, {}, ifIndexData);
	grid.loadData(f_getWhere());
}
function f_getWhere() {
	if (!grid)
		return null;
	var clause = function(rowdata, rowindex) {
		var key = $("#ifIndexKey").val();
		if(rowdata.ifIndex.indexOf(key) > -1 || rowdata.ifDescr.indexOf(key) > -1){
			return true;
		}
	};
	return clause;
}

function f_setifIndexData() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "thresHoldAjaxManager.ajax?action=getifIndexListById",
				// 参数
				data : {
					id : getUrlParam("id")
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