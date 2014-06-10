var ip = null;
var nodeId = null;
var basePath = null;
$(function() {
			ip = $("#ip").attr("value");
			nodeId = $("#nodeId").attr("value");
			basePath = $("#basePath").attr("value");
			$("#configDiv").ligerPanel({
						title : '参数信息',
						width : 1000,
						height : 480
					});

			f_getTomcatParameter();

			$('#configTable tbody tr:odd').css("backgroundColor", "#F0F0F0");

		});

function f_getTomcatParameter() {
	$.ajax({
		type : "POST",
		async : false,
		url : basePath
				+ "tomcatPerformanceAjaxManager.ajax?action=getTomcatParameter",
		// 参数
		data : {
			ip : ip,
			nodeId : nodeId
		},
		dataType : "json",
		success : function(array) {
			if (array.Rows.length > 0) {
				$("#InputArguments").text(array.Rows[0].InputArguments);
				$("#classPath").text(array.Rows[0].classPath);
				$("#libraryPath").text(array.Rows[0].libraryPath);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});

}
