var manager = null;
var accordion = null;
var tab = null;
var basePath = null;
$(function() {
			basePath = $("#basePath").attr("value");
			$("#resourceConfigLayout").ligerLayout({
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
			$("#rcFramecenter").ligerTab({
						height : height
					});

			// Ãæ°å
			$("#rcAccordion").ligerAccordion({
						height : height - 24,
						speed : null
					});

			// tree
			$("#rcTree").ligerTree({
						data : resTreeData,
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
							f_addTab(tabid, node.data.text, basePath
											+ node.data.url);
						}
					});

			tab = $("#rcFramecenter").ligerGetTabManager();
			manager = $("#rcTree").ligerGetTreeManager();
			accordion = $("#rcAccordion").ligerGetAccordionManager();

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