var basePath = null;
var groupicon = null;
$(function() {
			basePath = $("#basePath").attr("value");
			groupicon = basePath + "css/icons/communication.gif";
			// 创建表单结构
			var mainform = $("form");
			mainform.ligerForm({
						width : 380,
						inputWidth : 170,
						labelWidth : 50,
						labelAlign : 'right',
						space : 40,
						fields : [{
									name : "deptId",
									type : "hidden"
								}, {
									display : "部门",
									name : "dept",
									newline : true,
									type : "text",
									group : "部门信息",
									groupicon : groupicon
								}, {
									display : "联系人",
									name : "man",
									newline : true,
									type : "text",
									group : "部门联系人",
									groupicon : groupicon
								}, {
									display : "电话",
									name : "tel",
									newline : true,
									type : "text"
								}],
						buttons : [{
									text : '修改',
									width : 60,
									click : f_save
								}, {
									text : '关闭',
									width : 60,
									click : closeDlgWindow
								}]
					});

			initData();
		});

function initData() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=beforeEditDept",
				// 参数
				data : {
					id : getUrlParam("id")
				},
				dataType : "json",
				success : function(array) {
					$(array.Rows).each(function() {
								var form = liger.get("form");
								form.setData({
											deptId : this.deptId,
											dept : this.dept,
											man : this.man,
											tel : this.tel
										});
							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

function f_save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=editDept",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					deptId : $("#deptId").attr("value"),
					dept : liger.get("dept").getValue(),
					man : liger.get("man").getValue(),
					tel : liger.get("tel").getValue()
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
								window.parent.refresh();
								closeDlgWindow();

							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}
