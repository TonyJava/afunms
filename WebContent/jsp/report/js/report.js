var manager = null;
var accordion = null;
var tab = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("#reportLayout").ligerLayout({
						leftWidth : 190,
						height : '100%',
						heightDiff : -34,
						space : 4,
						onHeightChanged : f_heightChanged,
						allowLeftResize : false,
						allowLeftCollapse : true
					});

			var height = $(".l-layout-center").height();

			$(".l-link").hover(function() {
						$(this).addClass("l-link-over");
					}, function() {
						$(this).removeClass("l-link-over");
					});

			// Tab
			$("#reportFramecenter").ligerTab({
						height : height
					});

			// 面板
			$("#reportAccordion").ligerAccordion({
						height : height - 24,
						speed : null
					});

			// tree
			$("#reportTree").ligerTree({
						data : reportTreeData,
						attribute : ['nodename', 'url'],
						checkbox : false,
						slide : false,
						onSelect : function(node) {
							if (!node.data.url)
								return;
							var tabid = $(node.target).attr("tabid");
							if (!tabid) {
								tabid = node.data.text;
								$(node.target).attr("tabid", tabid);
							}
							f_addTab(tabid, node.data.text, node.data.url);
						}
					});

			tab = $("#reportFramecenter").ligerGetTabManager();
			manager = $("#reportTree").ligerGetTreeManager();
			accordion = $("#reportAccordion").ligerGetAccordionManager();

		});

function f_heightChanged(options) {
	if (tab)
		tab.addHeight(options.diff);
	if (accordion && options.middleHeight - 24 > 0)
		accordion.setHeight(options.middleHeight - 24);
}

function f_addTab(tabid, text, url) {
	tab.addTabItem({
				tabid : tabid,
				text : text,
				url : url
			});
}