var basePath = null;
$(function() {
	basePath = $("#basePath").attr("value");

	$("#uploadResource").ligerTip({
				content : '导入设备',
				width : 60,
				auto : true
			});

	$("#downloadResource").ligerTip({
				content : '导出设备',
				width : 60,
				auto : true
			});

	$("img#uploadResource").bind("click", function() {
				choseFile(basePath + "jsp/resourceConfig/batchUpload.jsp");
			});

	$("img#downloadResource").bind("click", function() {
				downloadFile(basePath + "jsp/resourceConfig/batchDownload.jsp");
			});
});

// 选择文件方法
function choseFile(path) {
	$.ligerDialog.open({
				url : path,
				height : 280,
				width : 550,
				title : "请选择",
				buttons : [{
							text : '确定',
							onclick : function(item, dialog) {
								batchOp(dialog);
							},
							cls : 'l-dialog-btn-highlight'
						}, {
							text : '取消',
							onclick : function(item, dialog) {
								dialog.close();
							}
						}],
				isResize : true
			});
}

function batchOp(dialog) {
	dialog.close();
	var waitDlg = $.ligerDialog.waitting("正在添加,请等待...");
	$.ajax({
				type : "POST",
				async : true,
				url : basePath
						+ "resourceBatchOpAjaxManager.ajax?action=resourceBatchAdd",
				dataType : "text",
				success : function(array) {
					if (waitDlg)
						waitDlg.close();
					$.ligerDialog.success(array);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}
// 选择文件方法
function downloadFile(path) {
	$.ligerDialog.open({
				url : path,
				height : 600,
				width : 550,
				title : "批量导出设备",
				isResize : true
			});
}
