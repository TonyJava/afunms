var navtab = null;
var basePath = null;
$(function() {
	basePath = $("#basePath").attr("value");
	$("#navtab").ligerTab({
		onBeforeSelectTabItem : function(tabid) {
			$("div").each(function() {
				if ($(this).attr("tabid") == tabid) {
					$(this).find("iframe").attr("src", $(this).attr("toHref"));
				}
			});
		},
		onAfterSelectTabItem : function(tabid) {
			navtab.reload(tabid);
		}
	});
	navtab = $("#navtab").ligerGetTabManager();

	// 最外层iframe高度
	var outIframeHeight = $("#hFrame", top.document).height();
	// 设置面板高度
	$(".tabC").height(outIframeHeight - 60);

	// 初始化第一个iframe值
	$("#nowIframe").attr("src", "topNShowDetail.jsp?dateType=now");
});
