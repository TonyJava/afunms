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
						labelWidth : 40,
						space : 40,
						fields : [{
									name : "positionId",
									type : "hidden"
								}, {
									display : "职位",
									name : "position",
									newline : true,
									type : "text",
									group : "职位信息",
									groupicon : groupicon
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
				url : basePath + "userAjaxManager.ajax?action=beforeEditPosition",
				// 参数
				data : {
					id : getUrlParam("id")
				},
				dataType : "json",
				success : function(array) {
					$(array.Rows).each(function() {
								var form = liger.get("form");
								form.setData({
											positionId : this.positionId,
											position : this.position
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
				url : basePath + "userAjaxManager.ajax?action=editPosition",
				// 参数
				contentType : "application/x-www-form-urlencoded; charset=UTF-8",
				data : {
					positionId : $("#positionId").attr("value"),
					position : liger.get("position").getValue()
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
