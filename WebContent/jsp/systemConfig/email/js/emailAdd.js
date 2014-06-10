var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("form").ligerForm();

			// 初始化是否启用
			liger.get('usedflag').set({
						data : usedflag,
						valueField : 'value',
						textField : 'text'
					});
			liger.get('usedflag').setValue('1');
			
			$("#submit").click(function() {
						save();
					});
		});

function save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "emailAjaxManager.ajax?action=addEmail",
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					name : liger.get('name').getValue(),
					smtp : liger.get('smtp').getValue(),
					pwd : liger.get('pwd').getValue(),
					repwd : liger.get('Repwd').getValue(),
					usedflag : liger.get('usedflag').getValue(),
					emailaddress : liger.get('emailaddress').getValue()
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

var usedflag = [{
			value : "0",
			text : "否"
		}, {
			value : "1",
			text : "是"
		}];