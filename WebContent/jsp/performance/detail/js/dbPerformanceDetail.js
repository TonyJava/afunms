var navtab = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("#navtab").ligerTab({
						onBeforeSelectTabItem : function(tabid) {
							$("div").each(function() {
										if ($(this).attr("tabid") == tabid) {
											$(this).find("iframe").attr("src",
													$(this).attr("toHref"));
										};
									});

						},
						onAfterSelectTabItem : function(tabid) {
							navtab.reload(tabid);
						}
					});
			navtab = $("#navtab").ligerGetTabManager();
			var wHeight=$(window).height();
			// 重置面板高度
			$(".tabC").height(wHeight-50);
			
			$("img#toolImg").bind("click", function() {
				var offset = $(this).offset();
				toolDlg = openPnDlgWindow("database/tool/pDbTool.jsp?ip=" + getUrlParam("ip")
								+ "&nodeId=" + getUrlParam("id") + "&type="
								+ getUrlParam("type") + "&subType=" + getUrlParam("subType"), 400,
						185, offset.left - 175, offset.top + 15, "工具面板");
			});
		});
function closeDlg() {
	toolDlg.close();
}

// 对话框遮罩窗口
function openDlgWindow(href, h, w, t) {
	var win = $.ligerDialog.open({
				title : t,
				height : h,
				url : href,
				width : w,
				slide : false
			});
	return win;
}