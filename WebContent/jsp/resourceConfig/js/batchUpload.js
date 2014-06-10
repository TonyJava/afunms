var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("#uploadify").uploadify({
				// 是否自动上传
				'auto' : true,
				fileTypeExts :'*.xls',
				buttonText :'选择文件',
				height : 25,
				swf : 'uploadify.swf',
				uploader : basePath
						+ "resourceBatchOpAjaxManager.ajax?action=upLoadFile",
				width : 80
			});
		});