var manager = null;
var accordion = null;
var tab = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("#systemConfigLayout").ligerLayout({
						leftWidth : 190,
						height : '100%',
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
			$("#sysFramecenter").ligerTab({
						height : height
					});

			$("#sysAccordion").ligerAccordion({
						height : height - 24,
						speed : null
					});

			// tree
			$("#systemTree").ligerTree({
						data : systemTreeData,
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

			tab = $("#sysFramecenter").ligerGetTabManager();
			manager = $("#systemTree").ligerGetTreeManager();
			accordion = $("#sysAccordion").ligerGetAccordionManager();

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