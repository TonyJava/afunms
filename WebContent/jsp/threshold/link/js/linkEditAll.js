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
				url : basePath + "thresHoldAjaxManager.ajax?action=linkEditAll",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					linkIdString : $("#linkIdString").attr("value"),
					maxSpeed : liger.get('maxSpeed').getValue(),
					maxPer : liger.get('maxPer').getValue()
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								// 刷新列表
						window.parent.refresh();
						closeWindow();
							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

function closeWindow(){
	var dialog = frameElement.dialog;
	dialog.close();
}
