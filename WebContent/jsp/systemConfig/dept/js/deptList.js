var grid = null;
var basePath = null;

$(function() {
			basePath = $("#basePath").attr("value");
			$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
					"like", "equal", "notequal", "greater", "less", "startwith", "endwith"];
			// grid
			grid = $("#deptGrid").ligerGrid({
				columns : [{
							name : 'deptId',
							hide : true
						}, {
							display : '部门',
							name : 'dept',
							Width : 50
						}, {
							display : '联系人',
							name : 'man',
							Width : 50
						}, {
							display : '电话',
							name : 'tel',
							Width : 50
						}],
				pageSize : 30,
				checkbox : true,
				data : listDept(),
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
								data : listDept()
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
								href : basePath + 'jsp/systemConfig/dept/deptAdd.jsp',
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
					openDlgWindow(basePath + "jsp/systemConfig/dept/deptEdit.jsp?id=" + data.deptId,
							300, 400, "部门修改");
				}
			});
		});

function sItemclick() {
	grid.options.data = $.extend(true, {}, listDept());
	grid.showFilter();
}

function itemclick(item) {
	if (item.text == "增加") {
		openDlgWindow(item.href, 300, 400, item.text + "部门");
	}  else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.deptId + ";";
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
				url : basePath + "userAjaxManager.ajax?action=deleteDept",
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

function listDept() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=getDept",
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
				data : listDept()
			});
}
