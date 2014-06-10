var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			configFilePath();
			$('a[rel*=downloadr]').downloadr();
		});

function configFilePath() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath
						+ "resourceBatchOpAjaxManager.ajax?action=downLoadFile",
				dataType : "text",
				success : function(array) {
					$('a[rel*=downloadr]').attr("href", basePath+array);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});
}