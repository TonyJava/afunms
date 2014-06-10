var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("form").ligerForm();
			$("#submit").click(function() {
						save();
					});
		});

function save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "userAjaxManager.ajax?action=addDept",
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					dept : liger.get('dept').getValue(),
					man : liger.get('man').getValue(),
					tel : liger.get('tel').getValue()
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								window.parent.refresh();
								closeDlgWindow();
							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

