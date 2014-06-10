var grid = null;
var basePath = null;

$(function() {
			basePath = $("#basePath").attr("value");
			$.ligerDefaults.Filter.operators['string'] = $.ligerDefaults.Filter.operators['text'] = [
					"like", "equal", "notequal", "greater", "less", "startwith", "endwith"];
			// grid
			grid = $("#userGrid").ligerGrid({
				columns : [{
							display : '登录名',
							name : 'userId',
							width : 50
						}, {
							display : '用户名',
							name : 'name',
							Width : 50
						}, {
							display : '角色',
							name : 'role',
							Width : 50
						}, {
							display : '部门',
							name : 'dept',
							Width : 50
						}, {
							display : '职位',
							name : 'position',
							Width : 50
						}, {
							display : '手机',
							name : 'mobile',
							Width : 50
						}, {
							display : '邮箱',
							name : 'email',
							Width : 50
						}, {
							name : 'id',
							hide : true,
							width : 0.1
						}],
				pageSize : 30,
				checkbox : true,
				data : listUser(),
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
								data : listUser()
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
								href : basePath + 'jsp/systemConfig/user/userAdd.jsp',
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
					openDlgWindow(basePath + "jsp/systemConfig/user/userEdit.jsp?id=" + data.id,
							450, 610, "用户修改");
				}
			});
		});

function sItemclick() {
	grid.options.data = $.extend(true, {}, listUser());
	grid.showFilter();
}
function itemclick(item) {
	if (item.text == "增加") {
		openDlgWindow(item.href, 450, 540, item.text + "用户");
	} else if (item.text == "删除") {
		var rows = grid.getSelectedRows();
		var idString = "";
		if (rows.length == 0) {
			$.ligerDialog.error("请选择删除项");
		} else {
			$(rows).each(function() {
						idString += this.id + ";";
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
				url : basePath + "userAjaxManager.ajax?action=delete",
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

function listUser() {
	var rs = null;
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=list",
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
				data : listUser()
			});
}
