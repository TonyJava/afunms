var grid = null;
var basePath = null;

$(function() {
			basePath = $("#basePath").attr("value");
			$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
					"like", "equal", "notequal", "greater", "less", "startwith", "endwith"];
			// grid
			grid = $("#positionGrid").ligerGrid({
				columns : [{
							name : 'positionId',
							hide : true
						}, {
							display : '职位',
							name : 'position',
							Width : 50
						}],
				pageSize : 30,
				checkbox : true,
				data : listPosition(),
				allowHideColumn : false,
				rownumbers : true,
				colDraggable : true,
				rowDraggable : true,
				sortName : 'id',
				width : '99.8%',
				height : '100%',
				heightDiff :-4,
				onReload : function() {
					grid.set({
								data : listPosition()
							});
				},
				// 工具栏
				toolbar : {
					items : [{
								text : '高级自定义查询',
								click : sItemclick,
								icon : 'search2'
							}, {
								text : '增加',
								click : itemclick,
								href : basePath + 'jsp/systemConfig/position/positionAdd.jsp',
								icon : 'add'
							}, {
								line : true
							}, {
								text : '删除',
								click : itemclick,
								icon : 'delete'
							}]
				},
				// 双击
				onDblClickRow : function(data, rowindex, rowobj) {
					openDlgWindow(basePath + "jsp/systemConfig/position/positionEdit.jsp?id="
									+ data.positionId, 240, 400, "职位修改");
				}
			});
		});

function sItemclick() {
	grid.options.data = $.extend(true, {}, listPosition());
	grid.showFilter();
}
function itemclick(item) {
	if (item.text == "增加") {
		openDlgWindow(item.href, 240, 400, item.text + "职位");
	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.positionId + ";";
					});
			$.ligerDialog.success(f_deleteNodes(idString));
		}
	}
}

function f_deleteNodes(string) {
	var rs = "删除错误";
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=deletePosition",
				// 参数
				data : {
					string : string
				},
				dataType : "text",
				success : function(array) {
					// 成功删除则更新表格行
					grid.deleteSelectedRow();
					rs = array;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
	return rs;
}

function listPosition() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=getPosition",
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
				data : listPosition()
			});
}
function createDiv(pw, value, color) {
	var divString = "<div style='float:left;margin-top:5px;width:" + pw
			+ "px;border:1px solid green;'><div style='height:12px;background:" + color + ";width:"
			+ value + "%;'></div></div>" + value;
	return divString;
}

function toDetail(href, h, w, t) {
	var imgString = "<img src='"
			+ basePath
			+ "css/img/pList/pDetail.gif' style='margin-top:5px;' class='pDetail' onclick='openWindow(\""
			+ href + "\"," + h + "," + w + ",\"" + t + "\")' />";
	return imgString;
}
