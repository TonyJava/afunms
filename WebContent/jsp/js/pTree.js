var pTreeData = [{
			text : '根',
			isexpand : true,
			icon : "../../css/img/pTree/base.gif",
			children : [{
						text : "网络设备",
						isexpand : true,
						icon : "../../css/img/pTree/net.gif",
						children : [{
									text : "交换机",
									url : "netPerformanceList.jsp?type=switch",
									icon : "../../css/img/pTree/switch.gif"
								}, {
									text : "路由器",
									url : "netPerformanceList.jsp?type=route",
									icon : "../../css/img/pTree/route.gif"
								}]
					}, {
						text : "服务器",
						isexpand : true,
						icon : "../../css/img/pTree/host.gif",
						children : [{
									text : "Windows",
									url : "hostPerformanceList.jsp?type=windows",
									icon : "../../css/img/pTree/windows.gif"
								}, {
									text : "Linux",
									url : "hostPerformanceList.jsp?type=linux",
									icon : "../../css/img/pTree/linux.gif"
								}, {
									text : "Aix",
									url : "hostPerformanceList.jsp?type=aix",
									icon : "../../css/img/pTree/aix.gif"
								}]
					}, {
						text : "安全设备",
						isexpand : true,
						icon : "../../css/img/pTree/safe.gif",
						children : [{
									text : "防火墙",
									url : "firewallPerformanceList.jsp?type=firewall",
									icon : "../../css/img/pTree/firewall.gif"

								}, {
									text : "入侵检测",
									url : "idsPerformance.jsp?type=linux",
									icon : "../../css/img/pTree/ids.gif"
								}]
					}, {
						text : "数据库",
						isexpand : true,
						icon : "../../css/img/pTree/db.gif",
						children : [{
									text : "Oracle",
									url : "dbPerformanceList.jsp?type=oracle",
									icon : "../../css/img/pTree/oracle.gif"
								}, {
									text : "SQLServer",
									url : "dbPerformanceList.jsp?type=sqlserver",
									icon : "../../css/img/pTree/sqlserver.gif"
								}, {
									text : "MySQL",
									url : "dbPerformanceList.jsp?type=mysql",
									icon : "../../css/img/pTree/mysql.gif"
								}]
					}, {
						text : "中间件",
						isexpand : true,
						icon : "../../css/img/pTree/mid.gif",
						children : [{
									text : "Tomcat",
									url : "tomcatPerformanceList.jsp",
									icon : "../../css/img/pTree/tomcat.gif"
								}, {
									text : "Weblogic",
									url : "weblogicPerformanceList.jsp",
									icon : "../../css/img/pTree/weblogic.gif"
								}]
					}]
		}]
