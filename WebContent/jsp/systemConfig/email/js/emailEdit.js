var basePath = null;
var id = null;
$(function() {
			basePath = $("#basePath").attr("value");
			id = $("#id").attr("value");
			$("form").ligerForm();
			
			// 初始化是否启用
			liger.get('usedflag').set({
						data : usedflag,
						valueField : 'value',
						textField : 'text'
					});
			initData();
			$("#submit").click(function() {
						save();
					});
		});

function initData() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "emailAjaxManager.ajax?action=beforeEditEmail",
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					id : id,
				},
				dataType : "json",
				success : function(array) {
					$(array.Rows).each(function() {
						liger.get("name").setValue(this.username);
						liger.get("smtp").setValue(this.smtp);
						liger.get("pwd").setValue(this.pwd);
						liger.get("Repwd").setValue(this.pwd);
						liger.get("usedflag").setValue(this.usedflagvalue);
						liger.get("usedflag").setText(this.usedflag);
						liger.get("emailaddress").setValue(this.emailaddress);
					});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

function save() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "emailAjaxManager.ajax?action=updateEmail",
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					id : id,
					name : liger.get('name').getValue(),
					smtp : liger.get('smtp').getValue(),
					pwd : liger.get('pwd').getValue(),
					usedflag : liger.get('usedflag').getValue(),
					emailaddress : liger.get('emailaddress').getValue()
				},
				dataType : "text",
				success : function(array) {
					$.ligerDialog.success(array, '提示', function(yes) {
								window.parent.refresh();
								closeWindows();
							});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}

function closeWindows() {
	var dialog = frameElement.dialog;
	dialog.close();
}

function refreshParent(){
	window.opener.refresh();
}

var usedflag = [{
				value : "0",
				text : "否"
		},{
			value : "1",
			text : "是"
	}];