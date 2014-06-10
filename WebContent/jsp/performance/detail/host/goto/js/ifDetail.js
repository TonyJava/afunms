var basePath = null;
var alias = null;
$(function() {
			basePath = $("#basePath").attr("value");
			alias = $("#alias").attr("value");
			var so = new SWFObject(basePath + "flex/Show_port.swf?ipadress="
							+ getUrlParam("ip") + "&ifindex="
							+ getUrlParam("ifIndex") + "&ifname="
							+ getUrlParam("ifName") + "&hostname="
							+ alias, "Show_port", "100%", "400",
					"8", "#ffffff");
			so.write("flashcontent");
		});