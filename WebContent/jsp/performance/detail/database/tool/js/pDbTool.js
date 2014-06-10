var basePath = null;
var imgFolder = null;
var nodeId = null;
var type = null;
var subType = null;
$(function() {
			basePath = $("#basePath").attr("value");
			nodeId = $("#nodeId").attr("value");
			type = $("#type").attr("value");
			subType = $("#subType").attr("value");
			imgFolder = basePath + "css/icons/"

			$("#accordion").ligerAccordion({
						height : 320
					});
			$("li").each(function() {
				$(this).css({
							"height" : "20px",
							"background" : "url('" + imgFolder + $(this).attr('bi')
									+ "') no-repeat",
							"padding-left" : "25px",
							"background-position" : "5px 0px",
							"cursor" : "pointer",
							"margin-top" : "5px"
						});
			});

			$("li").live('click', function() {
						eval($(this).attr("fn"));
					});

		});
function cancleMonitor() {
	$.ajax({
				type : "POST",
				async : false,
				url : basePath + "dbPerformanceAjaxManager.ajax?action=batchCancleMonitor",
				// 参数
				data : {
					string : getUrlParam("nodeId")
				},
				dataType : "text",
				success : function(array) {
					alert(array);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert(errorThrown);
				}
			});

}

function indicatorDetail() {
	openParentDlg(basePath + "jsp/performance/nodeIndicator/nodeIndicatorList.jsp?nodeId=" + nodeId
					+ "&type=" + type + "&subType=" + subType, 600, 1000, "网元指标列表");
}

function thresholdDetail() {
	openParentDlg(basePath + "jsp/performance/nodeThresHold/nodeThresHoldList.jsp?nodeId=" + nodeId
					+ "&type=" + type + "&subType=" + subType, 700, 1200, "网元阈值列表");
}

function closeParentDlg() {
	window.parent.closeDlg();
}

function openParentDlg(href, h, w, t) {
	window.parent.openDlgWindow(href, h, w, t);
}