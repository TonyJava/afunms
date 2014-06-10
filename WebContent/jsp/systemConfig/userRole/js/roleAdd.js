var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("form").ligerForm();
			$("#submit").click(function() {
						save();
					});
		});

// 功能函数定义

function save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=addRole",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					role : liger.get('role').getValue()
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
