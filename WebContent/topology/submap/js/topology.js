// info 信息框样式
var infoBgColor     = "#F5F5F5";
var infoBgImg       = "";           // path to background image;
var infoBorder      = "solid black 1px";
var infoBorderColor = "#003399";
var infoBorderWidth = 1;
var infoDelay       = 500;          // time span until tooltip shows up
									// [milliseconds]
var infoFontColor   = "#000000";
var infoFontFace    = "宋体,arial,helvetica,sans-serif";
var infoFontSize    = "12px";
var infoFontWeight  = "normal";     // alternative: "bold";
var infoPadding     = 3;            // spacing between border and content
var infoShadowColor = "";
var infoShadowWidth = 0;   
var infoTextAlign   = "left";
var infoTitleColor  = "#ffffff";    // color of caption text
var infoWidth       = 180;

var isFF = true;
//检测浏览器类型
(function(){
	if(window.addEventListener){
		isFF = true;
	}
	else{
		isFF = false;
	}
})();
Array.prototype.contains = function()
{ 
	for (var i = 0; i < this.length; i++)
	{
		if (arguments[0] == this[i])
		{
			return true;
		}
	}
	return false;
}

// 全局变量
var xmldoc;						// xml全局对象
var xx;							// 鼠标当前焦点－偏移量
var yy;							// 鼠标当前焦点－偏移量
var obj = null;				// 触发事件的控件对象
var objStyle = null;  // 触发事件的控件对象,用于改变选中图片后样式表
var strSearch = null;			// 搜索的字符串
var line_ids = new Array();	// 存放当前事件控件的相关连线
var tempArray = new Array();	// 存放修改过的对象id的数组
var clickObj = null;
var clickLineObj = null;
var maxWidth = 0;				// 最大宽度
var maxHeight = 0;				// 最大高度
var minWidth = 0;				// 最小宽度
var minHeight = 0;				// 最小高度
var strArray = new Array();
var beginx = 0;
var beginy = 0;
var selectStatus = false; // 判断选框
var ctrlStatus = false;// yangjun add 判断ctrl
var objMoveAry = new Array();// 画框选中的图元数组
var objEntityAry = new Array();// yangjun add Ctrl键选中的图元
var objLeftAry = new Array();
var objTopAry = new Array();
var lineMoveAry = new Array();
var assLineMoveAry = new Array();// 辅助链路存放数组yangjun add
var demoLineMoveAry = new Array();// 示意链路存放数组 yangjun add
var relLineAry = new Array();
var isRemoved = false;
var movedObjFlag = false;		// FIXME: 该变量未定义
// 为搜索功能而设的数据------------改1---
var nodeCoorAry = new Array();	// 每个节点的坐标：ip,x,y
var nodeIdAry = new Array();	// 每个节点的坐标：id,x,y yangjun add
// ---------------------------------
window.document.onselectstart = selectStart;
// 实现xml装载功能
// url: xml文件
function loadXML(url)
{
	/*
	 * 异步请求xml格式的字符串
	 * 每次都创建一个新的xhr对象
	 * 
	 *
	 * */
	var http = XHR.getInstance();
	
	http.open("POST", url, false);  //同步
	http.send();
	try{
		var parser = new DOMParser();
		xmldoc = parser.parseFromString(http.responseText.replace('<?xml version="1.0" encoding="GB2312"?>',""),"text/xml");
	}catch(e){
		try{
			xmldoc = new ActiveXObject("Microsoft.XMLDOM");
			xmldoc.async = false;
			xmldoc.loadXML(http.responseText);
		}catch(e){
			alert("Error!无法创建Document对象！");
		}
	}
	
	//xmldoc.loadXML(http.responseText);
	//"/afunms/resource/xml/network.xml"
	//xmldoc.load("/afunms/resource/xml/network.xml");//直接加载xml文件
	maxWidth = maxHeight = minWidth = minHeight = 0;
	parseData();
}
function initXML(url)
{
	/*
	 * 异步请求xml格式的字符串
	 * 
	 *
	 * */
	var http = XHR.getInstanceBy("POST", url, false);  //同步
	http.send();
	try{
		var parser = new DOMParser();
		xmldoc = parser.parseFromString(http.responseText.replace('<?xml version="1.0" encoding="GB2312"?>',""),"text/xml");
	}catch(e){
		try{
			xmldoc = new ActiveXObject("Microsoft.XMLDOM");
			xmldoc.async = false;
			xmldoc.loadXML(http.responseText);
		}catch(e){
			alert("Error!无法创建Document对象！");
		}
	}
	maxWidth = maxHeight = minWidth = minHeight = 0;
	return xmldoc;
}
//删除节点
function removeNode(nodeid,url){
	var olddiv = document.getElementById("node_" + nodeid);
    document.all.divLayer.removeChild(olddiv);
    var divText =  document.getElementById("text_" + nodeid);
    document.all.divLayer.removeChild(divText);
    var divInfo =  document.getElementById("info_" + nodeid);
    document.all.divLayer.removeChild(divInfo);
    var divMenu =  document.getElementById("menu_" + nodeid);
    document.all.divLayer.removeChild(divMenu);
}
//添加节点
function addNode(nodeid,url){
	xmldoc = initXML(url);
	var nodes = xmldoc.getElementsByTagName("node");	
	
	for (var i = nodes.length-1; i >= 0; i--){
	    var node = nodes[i];
		var id = node.getElementsByTagName("id")[0].childNodes[0].nodeValue;
		//alert(id+"="+nodeid);
		if(id==nodeid){
			//alert(id);
		    var nodeType = node.getElementsByTagName("id")[0].getAttribute("category");
			var imgValue = "../../resource/"+node.getElementsByTagName("img")[0].childNodes[0].nodeValue;
			var x = node.getElementsByTagName("x")[0].childNodes[0].nodeValue;
			var y = node.getElementsByTagName("y")[0].childNodes[0].nodeValue;
			var ip = node.getElementsByTagName("ip")[0].childNodes[0].nodeValue;
			var alias = node.getElementsByTagName("alias")[0].childNodes[0].nodeValue;
			var info = node.getElementsByTagName("info")[0].childNodes[0].nodeValue;
			var menu = node.getElementsByTagName("menu")[0].childNodes[0]?node.getElementsByTagName("menu")[0].childNodes[0].nodeValue:"";
			var relationMap = node.getElementsByTagName("relationMap")[0].childNodes[0]?node.getElementsByTagName("relationMap")[0].childNodes[0].nodeValue:"";
			// 这是搜索用的数组，在这里初始化---------改2---
			nodeCoorAry.push(ip + "," + x + "," + y);
			nodeIdAry.push(id + "," + x + "," + y)
			// ---------------------------------------
			getConfine(x, y);		// 获取最大边界，给 maxWidth，maxHeight 赋值
			
			var img = createElementByUserAgentAnd("image");
			//var img = document.createElement("img");
			// img.id = "image_" + id;//yangjun 修改
			img.id = "node_" + id;
			if(info=="示意设备") {// yangjun add 区分示意设备
				img.name = relationMap+",1";// 节点关联的子图文件名和示意设备判断标记
			} else {
			    img.name = relationMap+",0";;// 节点关联的子图文件名和示意设备判断标记
			}
			img.style.position = "absolute";
			img.style.cursor = "hand";
			setImage(img,{'x':x,'y':y,'w':imgWidth,'h':imgHeight});
	
			if(nodeType == "net_server")
			{
				
				setImage(img,{'w':65,'h':26});
				aliasHSpace = 10;
				aliasVSpace = 26;			
			} else if(nodeType == "机柜")
			{
				
				setImage(img,{'w':95,'h':371});
				aliasHSpace = 10;
				aliasVSpace = 371;
			} else if(nodeType == "标题")
			{
				setImage(img,{'w':10,'h':10});
				
				aliasHSpace = 5;
				aliasVSpace = 5
			} else if(nodeType == "服务器")
			{
				setImage(img,{'w':65,'h':26});
				
				aliasHSpace = 5;
				aliasVSpace = 20
			} else if(nodeType == "ups")
			{
				setImage(img,{'w':68,'h':76});
				aliasHSpace = 10;
				aliasVSpace = 70
			} 
			else if(nodeType == "weblogic")
			{
			
				setImage(img,{'w':32,'h':21});
				aliasHSpace = 20;
				aliasVSpace = 22
			}
			else if(nodeType == "ftp")
			{
				
				setImage(img,{'w':32,'h':32});
				aliasHSpace = 20;
				aliasVSpace = 33
			}
			else if(nodeType == "web")
			{
				
				setImage(img,{'w':32,'h':32});
				aliasHSpace = 20;
				aliasVSpace = 34
			} 
			else {
			    img.style.width = 30;
				img.style.height = 30;
				setImage(img,{'w':30,'h':30});
				aliasHSpace = 24;
				aliasVSpace = 28;
			}
			setImage(img,{'src':imgValue});
			// divBackGround.appendChild(img);
			document.getElementById('divLayer').appendChild(img);
			// 显示设备文本
			var divText = document.createElement("div");
			divText.id = "text_" + id;
			divText.style.position = "absolute";
			divText.style.width = "80";
			divText.style.height = "20";
			divText.style.left = parseInt(x, 10) - aliasHSpace;
			divText.style.top = parseInt(y, 10) + aliasVSpace;
			divText.style.fontSize = "12px";
			divText.align = "center";
			if (g_viewFlag == 0)
				divText.innerHTML = alias;// 显示设备别名
			else
				divText.innerHTML = ip;// 显示设备IP
			appendChild(divText);
			// 鼠标移上显示设备信息
			var divInfo = document.createElement("div");
			divInfo.id = "info_" + id;
			divInfo.name = id;// alias+"("+ip+")";//yangjun add
			divInfo.style.position = "absolute";
			divInfo.style.border = infoBorder;
			divInfo.style.width = infoWidth;
			divInfo.style.height = "auto";
			divInfo.style.color = infoFontColor;
			divInfo.style.padding = infoPadding;
			divInfo.style.lineHeight = "120%";
			divInfo.style.zIndex = 2;
			divInfo.style.backgroundColor = infoBgColor;
			divInfo.style.left = parseInt(x, 10) + 32;
			divInfo.style.top = parseInt(y, 10);
			divInfo.style.visibility = "hidden";
			divInfo.style.fontSize = "12px";
			divInfo.innerHTML = info;
			appendChild(divInfo);
	
			// 增加"设备信息显示"事件
			document.getElementById("node_" + id).onmouseover = function() { document.getElementById(this.id.replace("node", "info")).style.visibility = "visible"; };
			document.getElementById("node_" + id).onmouseout = function() { document.getElementById(this.id.replace("node", "info")).style.visibility = "hidden"; };
	        // 双击图元事件yangjun
			document.getElementById("node_" + id).ondblclick = function() {
			                                         if(this.name.substring(0,this.name.lastIndexOf(","))!=""){
				                                     	 // parent.location =
															// "../submap/submap.jsp?submapXml="
															// +
															// this.name.substring(0,this.name.lastIndexOf(","));
			                                         	 if(this.name.substring(0,this.name.lastIndexOf(","))=="network.jsp"){
			                                         	     window.parent.parent.location = "../network/index.jsp";
			                                         	     // parent.mainFrame.location.reload();
			                                         	 }else{
			                                         	     window.parent.parent.location = "../submap/index.jsp?submapXml=" + this.name.substring(0,this.name.lastIndexOf(","));
			                                         	 }
				                                     	 
				                                     }else{
				                                     	 nodeId = this.id.replace("node_", "");
				                                         showalert(nodeId);
				                                         window.parent.parent.opener.focus();
				                                     }
			                                     };
			
			// 右键根据类型显示菜单
			var divMenu = document.createElement("div");
			divMenu.id = "menu_" + id;
			divMenu.style.position = "absolute";
			divMenu.style.width = 120;
			divMenu.style.height = "auto";
			divMenu.style.zIndex = 2;
			divMenu.style.left = parseInt(x, 10) + 28;
			divMenu.style.top = parseInt(y, 10);
			divMenu.style.visibility = "hidden";
			divMenu.style.border = "solid #000066 1px";
			divMenu.style.backgroundColor = "#F5F5F5";
			divMenu.style.padding = "1px";
			divMenu.style.lineHeight = "100%";
			divMenu.style.fontSize = "12px";
			divMenu.innerHTML = menu;
			appendChild(divMenu);
	
			// 增加菜单的触发事件
			document.getElementById("node_" + id).oncontextmenu = function()
			{ 
				if(clickObj != null)
				{
					document.all(clickObj.id.replace("node", "menu")).style.visibility = "hidden";
					clickObj = null;
				}	
				if(objStyle != null)
				{
				  	unSelectImg(objStyle);
				    objStyle = null;
				}	
				clickObj = this;
				objStyle = this;
				selectImg(objStyle);
				document.all(this.id.replace("node", "info")).style.visibility = "hidden";
				document.all(this.id.replace("node", "menu")).style.visibility = "visible"; 			
		   };
		   
			document.getElementById("menu_" + id).onclick = function() { this.style.visibility = "hidden"; };	
			// 增加"拖动"事件
			document.getElementById("node_" + id).onmousedown = down;
			document.onmousemove = null;
			document.getElementById("node_" + id).onmouseup = up;
			return;
		}
	}
}
//创建链路
function addlink(lineid,url){
	xmldoc = initXML(url);
    var lines = xmldoc.getElementsByTagName("line");
	for (var j = lines.length-1; j >= 0; j--)
	{
		var lineObj = lines[j];				
		var line_id = lineObj.getAttribute("id");
		if(line_id==lineid){
		    var a = lineObj.getElementsByTagName("a")[0].childNodes[0].nodeValue;
			var b = lineObj.getElementsByTagName("b")[0].childNodes[0].nodeValue;
			var color = lineObj.getElementsByTagName("color")[0].childNodes[0].nodeValue;
			var dash = lineObj.getElementsByTagName("dash")[0].text;
			var lineWidth = lineObj.getElementsByTagName("lineWidth")[0].childNodes[0].nodeValue;
			var lineInfo = lineObj.getElementsByTagName("lineInfo")[0].childNodes[0].nodeValue;
			var lineMenu = lineObj.getElementsByTagName("lineMenu")[0].childNodes[0].nodeValue;
	         
			var line = createElementByUserAgentAnd("line");
			line.lineid = lineObj.getAttribute("id");
			line.id = "line_" + a + "_"+ b;
			line.style.position = "absolute";
			line.style.zIndex = -1;
			setLine(line,getCoorObjectFrom(document.getElementById("node_" + a),document.getElementById("node_" + b)));
			setLine(line,{'stroke':color,'strokeWidth':lineWidth});
			
			/*line.from = (parseInt(document.all("node_" + a).style.left) + parseInt(document.all("node_" + a).style.width)/2) + "," + (parseInt(document.all("node_" + a).style.top) + parseInt(document.all("node_" + a).style.height)/2);
			line.to = (parseInt(document.all("node_" + b).style.left) + parseInt(document.all("node_" + b).style.width)/2) + "," + (parseInt(document.all("node_" + b).style.top) + parseInt(document.all("node_" + b).style.height)/2);
			line.strokecolor = color;
			line.strokeweight = lineWidth;*/// 1;
	        // ///////////////yangjun add begin
			// 显示链路信息
			var divLineInfo = document.createElement("div");
			divLineInfo.id = "info_" + a + "_"+ b;
			divLineInfo.style.position = "absolute";
			divLineInfo.style.border = infoBorder;
			divLineInfo.style.width = 220;
			divLineInfo.style.height = "auto";
			divLineInfo.style.color = infoFontColor;
			divLineInfo.style.padding = infoPadding;
			divLineInfo.style.display = "block";
			divLineInfo.style.lineHeight = "120%";
			divLineInfo.style.zIndex = 2;
			divLineInfo.style.backgroundColor = infoBgColor;
			divLineInfo.style.visibility = "hidden";
			divLineInfo.style.fontSize = "12px";
			divLineInfo.innerHTML = lineInfo;
			appendChild(divLineInfo);
			var getCoordInDocument = function(e) {// 获取鼠标当前位置
	             e = e || window.event; 
	             var x = e.pageX || (e.clientX + (document.documentElement.scrollLeft|| document.body.scrollLeft)); 
	             var y= e.pageY || (e.clientY + (document.documentElement.scrollTop || document.body.scrollTop));   
	             return {'x':x,'y':y}; 
	        }
			line.onmousemove = function(e) { 
				                   var pos=getCoordInDocument();
			                       // window.event.srcElement.strokeweight =
									// parseInt(lineWidth)+1;
			                       window.event.srcElement.style.cursor = "hand";
			                       // window.event.srcElement.style.filter="Alpha(Opacity=60);";
			                       document.all(this.id.replace("line","info")).style.left = parseInt(pos['x']);
			                       document.all(this.id.replace("line","info")).style.top = parseInt(pos['y']);
			                       document.all(this.id.replace("line","info")).style.visibility = "visible";
			                   };
			line.onmouseout = function() { 
			                      window.event.srcElement.style.cursor = "default"; 
			                      document.all(this.id.replace("line","info")).style.visibility = "hidden";
			                  };
			// ///////////////end
		
			document.getElementById('divLayer').appendChild(line);
			document.getElementById("node_" + a).lines += '&' + line.id;
			document.getElementById("node_" + b).lines += '&' + line.id;
			// 链路菜单
			var divMenu = document.createElement("div");
			divMenu.id = "menu_" + a + "_"+ b;
			divMenu.style.position = "absolute";
			divMenu.style.width = 120;
			divMenu.style.height = "auto";
			divMenu.style.zIndex = 2;
			divMenu.style.visibility = "hidden";
			divMenu.style.border = "solid #000066 1px";
			divMenu.style.backgroundColor = "#F5F5F5";
			divMenu.style.padding = "1px";
			divMenu.style.lineHeight = "100%";
			divMenu.style.fontSize = "12px";
			divMenu.innerHTML = lineMenu;
			appendChild(divMenu);
			// 增加链路菜单的触发事件
			addContextmenuEventListener(document.getElementById("line_" + a + "_"+ b),function(event){
				event = event || window.event;
				var that = event.target || event.srcElement;
				var pos=getCoordInDocument();
				document.getElementById(that.id.replace("line","menu")).style.left = parseInt(pos['x']);
			    document.getElementById(that.id.replace("line","menu")).style.top = parseInt(pos['y']);
				if(clickLineObj != null)
				{
					document.getElementById(clickLineObj.id.replace("line", "menu")).style.visibility = "hidden";
					clickLineObj = null;
				}
				clickLineObj = that;
				document.getElementById(that.id.replace("line", "info")).style.visibility = "hidden";
				document.getElementById(that.id.replace("line", "menu")).style.visibility = "visible"; 			
		    });
			
			document.getElementById("menu_" + a + "_"+ b).onclick = function() { this.style.visibility = "hidden"; };
			// 加入 stroke 标签
			// window.event.srcElement.stroke.dashstyle = "Solid";
			var stroke = document.createElement("div");
			
			stroke.style.backgroundColor = "white";
			//stroke.dashstyle = dash;
			document.getElementById(line.id).appendChild(stroke);
			return;
		}
	}
}
//
function addAssLink(lineid,url){
	xmldoc = initXML(url);
    var lines = xmldoc.getElementsByTagName("assistant_line");
	for (var j = lines.length-1; j >= 0; j--)
	{
		var lineObj = lines[j];
		var line_id = lineObj.getAttribute("id");
		if(line_id==lineid){
		    var a = lineObj.getElementsByTagName("a")[0].text;
			var b = lineObj.getElementsByTagName("b")[0].text;
			var color = lineObj.getElementsByTagName("color")[0].text;
			var dash = lineObj.getElementsByTagName("dash")[0].text;
			var lineWidth = lineObj.getElementsByTagName("lineWidth")[0].text;
			var lineInfo = lineObj.getElementsByTagName("lineInfo")[0].text;
			var lineMenu = lineObj.getElementsByTagName("lineMenu")[0].text;
	
			var line = document.createElement("v:line");
			line.lineid = lineObj.getAttribute("id");
			line.id = "line_" + a + "_"+ b + "#assistant";
			line.style.position = "absolute";
			line.style.zIndex = -1;
			line.from = (parseInt(document.all("node_" + a).style.left) + parseInt(document.all("node_" + a).style.width)/2 + 3) + "," + (parseInt(document.all("node_" + a).style.top) + parseInt(document.all("node_" + a).style.height)/2 + 3);
			line.to = (parseInt(document.all("node_" + b).style.left) + parseInt(document.all("node_" + b).style.width)/2 + 3) + "," + (parseInt(document.all("node_" + b).style.top) + parseInt(document.all("node_" + b).style.height)/2 + 3);
			line.strokecolor = color;
			line.strokeweight = lineWidth;
	
			// ///////////////yangjun add begin
			// 显示链路信息
			var divLineInfo = document.createElement("div");
			divLineInfo.id = "info_" + a + "_"+ b + "#assistant";
			divLineInfo.style.position = "absolute";
			divLineInfo.style.border = infoBorder;
			divLineInfo.style.width = 200;
			divLineInfo.style.height = "auto";
			divLineInfo.style.color = infoFontColor;
			divLineInfo.style.padding = infoPadding;
			divLineInfo.style.display = "block";
			divLineInfo.style.lineHeight = "120%";
			divLineInfo.style.zIndex = 2;
			divLineInfo.style.backgroundColor = infoBgColor;
			divLineInfo.style.visibility = "hidden";
			divLineInfo.style.fontSize = "12px";
			divLineInfo.innerHTML = lineInfo;
			document.all.divLayer.appendChild(divLineInfo);
			var getCoordInDocument = function(e) {// 获取鼠标当前位置
	             e = e || window.event; 
	             var x = e.pageX || (e.clientX + (document.documentElement.scrollLeft|| document.body.scrollLeft)); 
	             var y= e.pageY || (e.clientY + (document.documentElement.scrollTop || document.body.scrollTop));   
	             return {'x':x,'y':y}; 
	        }
			line.onmousemove = function(e) { 
				                   var pos=getCoordInDocument();
			                       // window.event.srcElement.strokeweight =
									// parseInt(lineWidth)+1;
			                       window.event.srcElement.style.cursor = "hand";
			                       document.all(this.id.replace("line","info")).style.left = parseInt(pos['x']);
			                       document.all(this.id.replace("line","info")).style.top = parseInt(pos['y']);
			                       document.all(this.id.replace("line","info")).style.visibility = "visible";
			                   };
			line.onmouseout = function() { 
			                      // window.event.srcElement.strokeweight =
									// lineWidth;
			                      window.event.srcElement.style.cursor = "default"; 
			                      document.all(this.id.replace("line","info")).style.visibility = "hidden";
			                  };
			// ///////////////end
			// line.onclick = function() { showLineInfo() };
	
			document.all.divLayer.appendChild(line);
			document.all("node_" + a).lines += '&' + line.id;
			document.all("node_" + b).lines += '&' + line.id;
			// 链路菜单
			var divMenu = document.createElement("div");
			divMenu.id = "menu_" + a + "_"+ b + "#assistant";
			divMenu.style.position = "absolute";
			divMenu.style.width = 120;
			divMenu.style.height = "auto";
			divMenu.style.zIndex = 2;
			divMenu.style.visibility = "hidden";
			divMenu.style.border = "solid #000066 1px";
			divMenu.style.backgroundColor = "#F5F5F5";
			divMenu.style.padding = "1px";
			divMenu.style.lineHeight = "100%";
			divMenu.style.fontSize = "12px";
			divMenu.innerHTML = lineMenu;
			document.all.divLayer.appendChild(divMenu);
			// 增加链路菜单的触发事件
			document.all("line_" + a + "_"+ b + "#assistant").oncontextmenu = function()
			{ 
				var pos=getCoordInDocument();
				document.all(this.id.replace("line","menu")).style.left = parseInt(pos['x']);
			    document.all(this.id.replace("line","menu")).style.top = parseInt(pos['y']);
				if(clickLineObj != null)
				{
					document.all(clickLineObj.id.replace("line", "menu")).style.visibility = "hidden";
					clickLineObj = null;
				}
				clickLineObj = this;
				document.all(this.id.replace("line", "info")).style.visibility = "hidden";
				document.all(this.id.replace("line", "menu")).style.visibility = "visible"; 			
		    };
			document.all("menu_" + a + "_"+ b + "#assistant").onclick = function() { this.style.visibility = "hidden"; };
			
			// 加入 stroke 标签
			// window.event.srcElement.stroke.dashstyle = "Solid";
			var stroke = document.createElement("v:stroke");
			stroke.dashstyle = dash;
			document.all(line.id).appendChild(stroke);
			return;
		}
	}
}
//示意链路
function addLine(lineid,url){
    // 分析lines节点
	xmldoc = initXML(url);
	var lines = xmldoc.getElementsByTagName("demoLine");
	//alert(lines+"="+lines.length);
	for (var j = lines.length-1; j >= 0; j--)
	{
		var lineObj = lines[j];
		var line_id = lineObj.getAttribute("id");
		if(line_id==lineid){
		    var a = lineObj.getElementsByTagName("a")[0].childNodes[0].nodeValue;
			var b = lineObj.getElementsByTagName("b")[0].childNodes[0].nodeValue;
			var color = lineObj.getElementsByTagName("color")[0].childNodes[0].nodeValue;
			var dash = lineObj.getElementsByTagName("dash")[0].childNodes[0].nodeValue;
			var lineWidth = lineObj.getElementsByTagName("lineWidth")[0].childNodes[0].nodeValue;
			var lineInfo = lineObj.getElementsByTagName("lineInfo")[0].childNodes[0].nodeValue;
			var lineMenu = lineObj.getElementsByTagName("lineMenu")[0].childNodes[0].nodeValue;
	
			var line = createElementByUserAgentAnd("line");
			line.lineid = lineObj.getAttribute("id");
			line.id = "line_" + a + "_"+ b + "#demoline";
			line.style.position = "absolute";
			line.style.zIndex = -1;
			setLine(line,getCoorObjectFrom(document.getElementById("node_" + a),document.getElementById("node_" + b)));
			setLine(line,{'stroke':color,'strokeWidth':lineWidth});
			
			/*line.from = (parseInt(document.all("node_" + a).style.left) + parseInt(document.all("node_" + a).style.width)/2 - 3) + "," + (parseInt(document.all("node_" + a).style.top) + parseInt(document.all("node_" + a).style.height)/2 - 3);
			line.to = (parseInt(document.all("node_" + b).style.left) + parseInt(document.all("node_" + b).style.width)/2 - 3) + "," + (parseInt(document.all("node_" + b).style.top) + parseInt(document.all("node_" + b).style.height)/2 - 3);
			line.strokecolor = color;
			line.strokeweight = lineWidth;
			*/
			// ///////////////yangjun add begin
			// 显示链路信息
			var divLineInfo = document.createElement("div");
			divLineInfo.id = "info_" + a + "_"+ b + "#demoline";
			divLineInfo.style.position = "absolute";
			divLineInfo.style.border = infoBorder;
			divLineInfo.style.width = 200;
			divLineInfo.style.height = "auto";
			divLineInfo.style.color = infoFontColor;
			divLineInfo.style.padding = infoPadding;
			divLineInfo.style.display = "block";
			divLineInfo.style.lineHeight = "120%";
			divLineInfo.style.zIndex = 2;
			divLineInfo.style.backgroundColor = infoBgColor;
			divLineInfo.style.visibility = "hidden";
			divLineInfo.style.fontSize = "12px";
			divLineInfo.innerHTML = lineInfo;
			appendChild(divLineInfo);
			var getCoordInDocument = function(e) {// 获取鼠标当前位置
	             e = e || window.event; 
	             var x = e.pageX || (e.clientX + (document.documentElement.scrollLeft|| document.body.scrollLeft)); 
	             var y= e.pageY || (e.clientY + (document.documentElement.scrollTop || document.body.scrollTop));   
	             return {'x':x,'y':y}; 
	        }
			line.onmousemove = function(e) { 
				                   var pos=getCoordInDocument();
			                       // window.event.srcElement.strokeweight =
									// parseInt(lineWidth)+1;
			                       window.event.srcElement.style.cursor = "hand";
			                       document.all(this.id.replace("line","info")).style.left = parseInt(pos['x']);
			                       document.all(this.id.replace("line","info")).style.top = parseInt(pos['y']);
			                       document.all(this.id.replace("line","info")).style.visibility = "visible";
			                   };
			line.onmouseout = function() { 
			                      // window.event.srcElement.strokeweight =
									// lineWidth;
			                      window.event.srcElement.style.cursor = "default"; 
			                      document.all(this.id.replace("line","info")).style.visibility = "hidden";
			                  };
			// ///////////////end
			// line.onclick = function() { showLineInfo() };
	
			document.getElementById('divLayer').appendChild(line);
			document.getElementById("node_" + a).lines += '&' + line.id;
			document.getElementById("node_" + b).lines += '&' + line.id;
			// 链路菜单
			var divMenu = document.createElement("div");
			divMenu.id = "menu_" + a + "_"+ b + "#demoline";
			divMenu.style.position = "absolute";
			divMenu.style.width = 120;
			divMenu.style.height = "auto";
			divMenu.style.zIndex = 2;
			divMenu.style.visibility = "hidden";
			divMenu.style.border = "solid #000066 1px";
			divMenu.style.backgroundColor = "#F5F5F5";
			divMenu.style.padding = "1px";
			divMenu.style.lineHeight = "100%";
			divMenu.style.fontSize = "12px";
			divMenu.innerHTML = lineMenu;
			appendChild(divMenu);
			// 增加链路菜单的触发事件
			addContextmenuEventListener(document.getElementById("line_" + a + "_"+ b + "#demoline"),function(){ 
				var pos=getCoordInDocument();
				document.getElementById(this.id.replace("line","menu")).style.left = parseInt(pos['x']);
			    document.getElementById(this.id.replace("line","menu")).style.top = parseInt(pos['y']);
				if(clickLineObj != null)
				{
					document.getElementById(clickLineObj.id.replace("line", "menu")).style.visibility = "hidden";
					clickLineObj = null;
				}
				clickLineObj = this;
				document.getElementById(this.id.replace("line", "info")).style.visibility = "hidden";
				document.getElementById(this.id.replace("line", "menu")).style.visibility = "visible"; 			
		    });
			document.all("menu_" + a + "_"+ b + "#demoline").onclick = function() { this.style.visibility = "hidden"; };
			
			// 加入 stroke 标签
			// window.event.srcElement.stroke.dashstyle = "Solid";
			/*var stroke = document.createElement("v:stroke");
			stroke.dashstyle = dash;
			document.getElementById(line.id).appendChild(stroke);*/
			return;
		}
	}
}

// 载入xml数据流并进行展现
function parseData()
{	
	//console.log(xmldoc);
	// 分析root节点
	var dateInfo = xmldoc.getElementsByTagName("root")[0].getAttribute("time");
	
	// 分析nodes节点
	var nodes = xmldoc.getElementsByTagName("node");	
	for (var i = 0; i < nodes.length; i += 1)
	{
		var node = nodes[i];
		//获取最内层文本节点的值
		var id = node.getElementsByTagName("id")[0].childNodes[0].nodeValue;
		var nodeType = node.getElementsByTagName("id")[0].getAttribute("category");
		var imgValue = "../../resource/"+node.getElementsByTagName("img")[0].childNodes[0].nodeValue;
		var x = node.getElementsByTagName("x")[0].childNodes[0].nodeValue;
		var y = node.getElementsByTagName("y")[0].childNodes[0].nodeValue;
		var ip = node.getElementsByTagName("ip")[0].childNodes[0].nodeValue;
		var alias = node.getElementsByTagName("alias")[0].childNodes[0].nodeValue;
		var info = node.getElementsByTagName("info")[0].childNodes[0].nodeValue;
		var menu = node.getElementsByTagName("menu")[0].childNodes[0]?node.getElementsByTagName("menu")[0].childNodes[0].nodeValue:"";
		var relationMap = node.getElementsByTagName("relationMap")[0].childNodes[0]?node.getElementsByTagName("relationMap")[0].childNodes[0].nodeValue:"";
		// 这是搜索用的数组，在这里初始化---------改2---
		nodeCoorAry.push(ip + "," + x + "," + y);
		
		nodeIdAry.push(id + "," + x + "," + y);
		// ---------------------------------------
		
		getConfine(x, y);		// 获取最大边界，给 maxWidth，maxHeight 赋值

		// 显示设备
	
		// end
		var img = createElementByUserAgentAnd("image");
		
		//var img = document.createElementNS("http://www.w3.org/2000/svg","image");
		// img.id = "image_" + id;//yangjun 修改
		img.id = "node_" + id;
		if(info=="示意设备") {// yangjun add 区分示意设备
			img.name = relationMap+",1";// 节点关联的子图文件名和示意设备判断标记
		} else {
		    img.name = relationMap+",0";;// 节点关联的子图文件名和示意设备判断标记
		}
		/*img.style.width = imgWidth;
		img.style.height = imgHeight;
		*/img.style.position = "absolute";
		img.style.cursor = "hand";
		/*img.style.left = x;
		img.style.top = y;*/
		setImage(img,{'x':x,'y':y,'w':imgWidth,'h':imgHeight});
		if(nodeType == "net_server")
		{
			setImage(img,{'w':65,'h':26});
			
			aliasHSpace = 10;
			aliasVSpace = 26;			
		} else if(nodeType == "机柜")
		{
			
			setImage(img,{'w':95,'h':371});
			aliasHSpace = 10;
			aliasVSpace = 371;
		} else if(nodeType == "标题")
		{
			setImage(img,{'w':10,'h':10});
			aliasHSpace = 5;
			aliasVSpace = 5
		} else if(nodeType == "服务器")
		{
			setImage(img,{'w':65,'h':26});
			aliasHSpace = 5;
			aliasVSpace = 20
		} else if(nodeType == "ups")
		{
			setImage(img,{'w':68,'h':76});
			aliasHSpace = 10;
			aliasVSpace = 70
		} 
		else if(nodeType == "weblogic")
		{
			setImage(img,{'w':32,'h':21});
			
			aliasHSpace = 20;
			aliasVSpace = 22
		}
		else if(nodeType == "ftp")
		{
			setImage(img,{'w':32,'h':32});
			
			aliasHSpace = 20;
			aliasVSpace = 33
		}
		else if(nodeType == "web")
		{
			setImage(img,{'w':32,'h':32});
			aliasHSpace = 20;
			aliasVSpace = 34
		}
		else if(nodeType == "iis")
		{
			setImage(img,{'w':30,'h':14});
			aliasHSpace = 30;
			aliasVSpace = 16
		}
		else if(nodeType == "mail")
		{
			setImage(img,{'w':30,'h':20});
			aliasHSpace = 15;
			aliasVSpace = 21
		}
		else if(nodeType == "机柜")
		{
			setImage(img,{'w':95,'h':371});
			aliasHSpace = 10;
			aliasVSpace = 371;
		}
		else if(nodeType == "标题")
		{
			setImage(img,{'w':10,'h':10});
			aliasHSpace = 5;
			aliasVSpace = 5
		}
		else
		{
			setImage(img,{'w':30,'h':30});
			aliasHSpace = 24;
			aliasVSpace = 28;
		}

		setImage(img,{'src':imgValue});
		//img.src = imgValue;
		// divBackGround.appendChild(img);
		document.getElementById('divLayer').appendChild(img);

		// 显示设备文本
		var divText = document.createElement("div");
		divText.id = "text_" + id;
		divText.style.position = "absolute";
		divText.style.width = "80";
		divText.style.height = "20";
		divText.style.left = parseInt(x, 10) - aliasHSpace;
		divText.style.top = parseInt(y, 10) + aliasVSpace;
		divText.style.fontSize = "12px";
		divText.align = "center";
		if (g_viewFlag == 0)
			divText.innerHTML = alias;// 显示设备别名
		else
			divText.innerHTML = ip;// 显示设备IP
		//document.getElementById('divLayer').appendChild(divText);
		//document.body.appendChild(divText);
		appendChild(divText);
		// 鼠标移上显示设备信息
		var divInfo = document.createElement("div");
		divInfo.id = "info_" + id;
		divInfo.name = id;// alias+"("+ip+")";//yangjun add
		divInfo.style.position = "absolute";
		divInfo.style.border = infoBorder;
		divInfo.style.width = infoWidth;
		divInfo.style.height = "auto";
		divInfo.style.color = infoFontColor;
		divInfo.style.padding = infoPadding;
		divInfo.style.lineHeight = "120%";
		divInfo.style.zIndex = 2;
		divInfo.style.backgroundColor = infoBgColor;
		divInfo.style.left = parseInt(x, 10) + 32;
		divInfo.style.top = parseInt(y, 10);
		divInfo.style.visibility = "hidden";
		divInfo.style.fontSize = "12px";
		divInfo.innerHTML = info;
		
		//document.getElementById("divLayer").appendChild(divInfo);
		appendChild(divInfo);
		// 增加"设备信息显示"事件
		document.getElementById("node_" + id).onmouseover = function() { 
			document.getElementById(this.id.replace("node", "info")).style.visibility = "visible"; 
		};
		document.getElementById("node_" + id).onmouseout = function() { document.getElementById(this.id.replace("node", "info")).style.visibility = "hidden"; };
		
		//#########增加“设备信息显示”时的越界处理，即超过边界的情况自动显示到可视范围之内   HONGLI ADD ########
		if(divInfo.clientHeight != 0){
			if(parseInt(x, 10) > document.body.clientWidth-infoWidth){
				divInfo.style.left =parseInt(x, 10)-infoWidth;
			} 
			if(parseInt(y, 10) > document.body.clientHeight-divInfo.clientHeight){
				divInfo.style.top = parseInt(y, 10)-divInfo.clientHeight;  
		    }
		}
	    //#############HONG ADD END##########
		
        // 双击图元事件yangjun
		document.getElementById("node_" + id).ondblclick = function() {
		                                         if(this.name.substring(0,this.name.lastIndexOf(","))!=""){
			                                     	 // parent.location =
														// "../submap/submap.jsp?submapXml="
														// +
														// this.name.substring(0,this.name.lastIndexOf(","));
		                                         	 if(this.name.substring(0,this.name.lastIndexOf(","))=="network.jsp"){
		                                         	     window.parent.parent.location = "../network/index.jsp";
		                                         	     // parent.mainFrame.location.reload();
		                                         	 }else{
		                                         	     window.parent.parent.location = "../submap/index.jsp?submapXml=" + this.name.substring(0,this.name.lastIndexOf(","));
		                                         	 }
			                                     	 
			                                     }else{
			                                     	 nodeId = this.id.replace("node_", "");
			                                         showalert(nodeId);
			                                         window.parent.parent.opener.focus();
			                                     }
		                                     };
		
		// 右键根据类型显示菜单
		var divMenu = document.createElement("div");
		divMenu.id = "menu_" + id;
		divMenu.style.position = "absolute";
		divMenu.style.width = 120;
		divMenu.style.height = "auto";
		divMenu.style.zIndex = 2;
		divMenu.style.left = parseInt(x, 10) + 28;
		divMenu.style.top = parseInt(y, 10);
		divMenu.style.visibility = "hidden";
		divMenu.style.border = "solid #000066 1px";
		divMenu.style.backgroundColor = "#F5F5F5";
		divMenu.style.padding = "1px";
		divMenu.style.lineHeight = "150%";
		divMenu.style.fontSize = "12px";
		divMenu.innerHTML = menu;
		//document.getElementById('divLayer').appendChild(divMenu);
		appendChild(divMenu);
		// 增加菜单的触发事件
		addContextmenuEventListener(document.getElementById("node_" + id),function(event){
			event = event || window.event;
			var that = event.target || event.srcElement;
			
			if(clickObj != null)
			{
				document.getElementById(clickObj.id.replace("node", "menu")).style.visibility = "hidden";
				clickObj = null;
			}	
			if(objStyle != null)
			{
			  	unSelectImg(objStyle);
			    objStyle = null;
			}
			clickObj = that;
			objStyle = that;
			selectImg(objStyle);
			document.getElementById(that.id.replace("node", "info")).style.visibility = "hidden";
			document.getElementById(that.id.replace("node", "menu")).style.visibility = "visible"; 
			
			return cancelPropagationAndDefaultOfEvent(event);
	   });
	
		document.getElementById("menu_" + id).onclick = function() { this.style.visibility = "hidden"; };	
		// 增加"拖动"事件
		document.getElementById("node_" + id).onmousedown = down;
		document.onmousemove = null;
		document.getElementById("node_" + id).onmouseup = up;
	}

	// 分析lines节点
	var lines = xmldoc.getElementsByTagName("line");
	for (var j = 0; j < lines.length; j += 1)
	{
		var lineObj = lines[j];				
		var a = lineObj.getElementsByTagName("a")[0].childNodes[0].nodeValue;
		var b = lineObj.getElementsByTagName("b")[0].childNodes[0].nodeValue;
		var color = lineObj.getElementsByTagName("color")[0].childNodes[0].nodeValue;
		var dash = lineObj.getElementsByTagName("dash")[0].childNodes[0].nodeValue;
		var lineWidth = lineObj.getElementsByTagName("lineWidth")[0].childNodes[0].nodeValue;
		var lineInfo = lineObj.getElementsByTagName("lineInfo")[0].childNodes[0].nodeValue;
		var lineMenu = lineObj.getElementsByTagName("lineMenu")[0].childNodes[0].nodeValue;
         
		var line = createElementByUserAgentAnd("line");
		line.lineid = lineObj.getAttribute("id");
		line.id = "line_" + a + "_"+ b;
		line.style.position = "absolute";
		line.style.zIndex = -1;
		setLine(line,getCoorObjectFrom(document.getElementById("node_" + a),document.getElementById("node_" + b)));
		setLine(line,{'stroke':color,'strokeWidth':lineWidth});
		/*line.from = (parseInt(document.all("node_" + a).style.left) + parseInt(document.all("node_" + a).style.width)/2) + "," + (parseInt(document.all("node_" + a).style.top) + parseInt(document.all("node_" + a).style.height)/2);
		line.to = (parseInt(document.all("node_" + b).style.left) + parseInt(document.all("node_" + b).style.width)/2) + "," + (parseInt(document.all("node_" + b).style.top) + parseInt(document.all("node_" + b).style.height)/2);
		line.strokecolor = color;
		line.strokeweight = lineWidth;*/// 1;
        // ///////////////yangjun add begin
		// 显示链路信息
		var divLineInfo = document.createElement("div");
		divLineInfo.id = "info_" + a + "_"+ b;
		divLineInfo.style.position = "absolute";
		divLineInfo.style.border = infoBorder;
		divLineInfo.style.width = 220;
		divLineInfo.style.height = "auto";
		divLineInfo.style.color = infoFontColor;
		divLineInfo.style.padding = infoPadding;
		divLineInfo.style.display = "block";
		divLineInfo.style.lineHeight = "120%";
		divLineInfo.style.zIndex = 2;
		divLineInfo.style.backgroundColor = infoBgColor;
		divLineInfo.style.visibility = "hidden";
		divLineInfo.style.fontSize = "12px";
		divLineInfo.innerHTML = lineInfo;
		appendChild(divLineInfo);
		var getCoordInDocument = function(e) {// 获取鼠标当前位置
             e = e || window.event; 
             var x = e.pageX || (e.clientX + (document.documentElement.scrollLeft|| document.body.scrollLeft)); 
             var y= e.pageY || (e.clientY + (document.documentElement.scrollTop || document.body.scrollTop));   
             return {'x':x,'y':y}; 
        }
		line.onmousemove = function(e) { 
			                   var pos=getCoordInDocument();
		                       // window.event.srcElement.strokeweight =
								// parseInt(lineWidth)+1;
		                       this.style.cursor = "hand";
		                       // window.event.srcElement.style.filter="Alpha(Opacity=60);";
		                       //TODO chrome 信息框由于坐标问题 闪烁 暂时x,y位置+1
		                       document.getElementById(this.id.replace("line","info")).style.left = parseInt(pos['x'])+1;
		                       document.getElementById(this.id.replace("line","info")).style.top = parseInt(pos['y'])+1;
		                       document.getElementById(this.id.replace("line","info")).style.visibility = "visible";
		                   };
		line.onmouseout = function() { 
			                  
		                      this.style.cursor = "default"; 
		                      document.getElementById(this.id.replace("line","info")).style.visibility = "hidden";
		                  };
		// ///////////////end

		document.getElementById('divLayer').appendChild(line);
		document.getElementById("node_" + a).lines += '&' + line.id;
		document.getElementById("node_" + b).lines += '&' + line.id;
		// 链路菜单
		var divMenu = document.createElement("div");
		divMenu.id = "menu_" + a + "_"+ b;
		divMenu.style.position = "absolute";
		divMenu.style.width = 120;
		divMenu.style.height = "auto";
		divMenu.style.zIndex = 2;
		divMenu.style.visibility = "hidden";
		divMenu.style.border = "solid #000066 1px";
		divMenu.style.backgroundColor = "#F5F5F5";
		divMenu.style.padding = "1px";
		divMenu.style.lineHeight = "150%";
		divMenu.style.fontSize = "12px";
		divMenu.innerHTML = lineMenu;
		appendChild(divMenu);
		// 增加右键单击链路弹出链路菜单
		addContextmenuEventListener(document.getElementById("line_" + a + "_"+ b),function(event){
			event = event || window.event;
			var that = event.target || event.srcElement;
			var pos=getCoordInDocument();
			document.getElementById(that.id.replace("line","menu")).style.left = parseInt(pos['x']);
		    document.getElementById(that.id.replace("line","menu")).style.top = parseInt(pos['y']);
			if(clickLineObj != null)
			{
				document.getElementById(clickLineObj.id.replace("line", "menu")).style.visibility = "hidden";
				clickLineObj = null;
			}
			clickLineObj = that;
			document.getElementById(that.id.replace("line", "info")).style.visibility = "hidden";
			document.getElementById(that.id.replace("line", "menu")).style.visibility = "visible"; 			
	    });
		document.getElementById("menu_" + a + "_"+ b).onclick = function() { this.style.visibility = "hidden"; };
		// 加入 stroke 标签
		// window.event.srcElement.stroke.dashstyle = "Solid";
		var stroke = document.createElement("v:stroke");
		stroke.dashstyle = dash;
		document.getElementById(line.id).appendChild(stroke);
	}
	
	// 分析lines节点
	var lines = xmldoc.getElementsByTagName("assistant_line");
	for (var j = 0; j < lines.length; j += 1)
	{
		var lineObj = lines[j];
		var a = lineObj.getElementsByTagName("a")[0].text;
		var b = lineObj.getElementsByTagName("b")[0].text;
		var color = lineObj.getElementsByTagName("color")[0].text;
		var dash = lineObj.getElementsByTagName("dash")[0].text;
		var lineWidth = lineObj.getElementsByTagName("lineWidth")[0].text;
		var lineInfo = lineObj.getElementsByTagName("lineInfo")[0].text;
		var lineMenu = lineObj.getElementsByTagName("lineMenu")[0].text;

		var line = document.createElement("v:line");
		line.lineid = lineObj.getAttribute("id");
		line.id = "line_" + a + "_"+ b + "#assistant";
		line.style.position = "absolute";
		line.style.zIndex = -1;
		//line.from = (parseInt(document.all("node_" + a).style.left) + 20) + "," + (parseInt(document.all("node_" + a).style.top) + 10);
		//line.to = (parseInt(document.all("node_" + b).style.left) + 20) + "," + (parseInt(document.all("node_" + b).style.top) + 10);
		line.from = (parseInt(document.all("node_" + a).style.left) + parseInt(document.all("node_" + a).style.width)/2 + 3) + "," + (parseInt(document.all("node_" + a).style.top) + parseInt(document.all("node_" + a).style.height)/2 + 3);
		line.to = (parseInt(document.all("node_" + b).style.left) + parseInt(document.all("node_" + b).style.width)/2 + 3) + "," + (parseInt(document.all("node_" + b).style.top) + parseInt(document.all("node_" + b).style.height)/2 + 3);
		line.strokecolor = color;
		line.strokeweight = lineWidth;

		// ///////////////yangjun add begin
		// 显示链路信息
		var divLineInfo = document.createElement("div");
		divLineInfo.id = "info_" + a + "_"+ b + "#assistant";
		divLineInfo.style.position = "absolute";
		divLineInfo.style.border = infoBorder;
		divLineInfo.style.width = 200;
		divLineInfo.style.height = "auto";
		divLineInfo.style.color = infoFontColor;
		divLineInfo.style.padding = infoPadding;
		divLineInfo.style.display = "block";
		divLineInfo.style.lineHeight = "120%";
		divLineInfo.style.zIndex = 2;
		divLineInfo.style.backgroundColor = infoBgColor;
		divLineInfo.style.visibility = "hidden";
		divLineInfo.style.fontSize = "12px";
		divLineInfo.innerHTML = lineInfo;
		document.all.divLayer.appendChild(divLineInfo);
		var getCoordInDocument = function(e) {// 获取鼠标当前位置
             e = e || window.event; 
             var x = e.pageX || (e.clientX + (document.documentElement.scrollLeft|| document.body.scrollLeft)); 
             var y= e.pageY || (e.clientY + (document.documentElement.scrollTop || document.body.scrollTop));   
             return {'x':x,'y':y}; 
        }
		line.onmousemove = function(e) { 
			                   var pos=getCoordInDocument();
		                       // window.event.srcElement.strokeweight =
								// parseInt(lineWidth)+1;
		                       window.event.srcElement.style.cursor = "hand";
		                       document.all(this.id.replace("line","info")).style.left = parseInt(pos['x']);
		                       document.all(this.id.replace("line","info")).style.top = parseInt(pos['y']);
		                       document.all(this.id.replace("line","info")).style.visibility = "visible";
		                   };
		line.onmouseout = function() { 
		                      // window.event.srcElement.strokeweight =
								// lineWidth;
		                      window.event.srcElement.style.cursor = "default"; 
		                      document.all(this.id.replace("line","info")).style.visibility = "hidden";
		                  };
		// ///////////////end
		// line.onclick = function() { showLineInfo() };

		document.all.divLayer.appendChild(line);
		document.all("node_" + a).lines += '&' + line.id;
		document.all("node_" + b).lines += '&' + line.id;
		// 链路菜单
		var divMenu = document.createElement("div");
		divMenu.id = "menu_" + a + "_"+ b + "#assistant";
		divMenu.style.position = "absolute";
		divMenu.style.width = 120;
		divMenu.style.height = "auto";
		divMenu.style.zIndex = 2;
		divMenu.style.visibility = "hidden";
		divMenu.style.border = "solid #000066 1px";
		divMenu.style.backgroundColor = "#F5F5F5";
		divMenu.style.padding = "1px";
		divMenu.style.lineHeight = "150%";
		divMenu.style.fontSize = "12px";
		divMenu.innerHTML = lineMenu;
		document.all.divLayer.appendChild(divMenu);
		// 增加链路菜单的触发事件
		document.all("line_" + a + "_"+ b + "#assistant").oncontextmenu = function()
		{ 
			var pos=getCoordInDocument();
			document.all(this.id.replace("line","menu")).style.left = parseInt(pos['x']);
		    document.all(this.id.replace("line","menu")).style.top = parseInt(pos['y']);
			if(clickLineObj != null)
			{
				document.all(clickLineObj.id.replace("line", "menu")).style.visibility = "hidden";
				clickLineObj = null;
			}
			clickLineObj = this;
			document.all(this.id.replace("line", "info")).style.visibility = "hidden";
			document.all(this.id.replace("line", "menu")).style.visibility = "visible"; 			
	    };
		document.all("menu_" + a + "_"+ b + "#assistant").onclick = function() { this.style.visibility = "hidden"; };
		
		// 加入 stroke 标签
		// window.event.srcElement.stroke.dashstyle = "Solid";
		var stroke = document.createElement("v:stroke");
		stroke.dashstyle = dash;
		document.all(line.id).appendChild(stroke);
	}
		
	// 分析lines节点
	var lines = xmldoc.getElementsByTagName("demoLine");
	
	for (var j = 0; j < lines.length; j += 1)
	{
		var lineObj = lines[j];
		//配置文件中示意链路的属性
		var a = lineObj.getElementsByTagName("a")[0].childNodes[0].nodeValue;
		var b = lineObj.getElementsByTagName("b")[0].childNodes[0].nodeValue;
		var color = lineObj.getElementsByTagName("color")[0].childNodes[0].nodeValue;
		var dash = lineObj.getElementsByTagName("dash")[0].childNodes[0].nodeValue;
		var lineWidth = lineObj.getElementsByTagName("lineWidth")[0].childNodes[0].nodeValue;
		var lineInfo = lineObj.getElementsByTagName("lineInfo")[0].childNodes[0].nodeValue;
		var lineMenu = lineObj.getElementsByTagName("lineMenu")[0].childNodes[0].nodeValue;

		//var line = document.createElement("v:line");
		var line = createElementByUserAgentAnd('line');
		line.lineid = lineObj.getAttribute("id");
		line.id = "line_" + a + "_"+ b + "#demoline";
		line.style.position = "absolute";
		line.style.zIndex = -1;
		 
		setLine(line,getCoorObjectFrom(document.getElementById("node_" + a),document.getElementById("node_" + b)));
		setLine(line,{'stroke':color,'strokeWidth':lineWidth});
		// ///////////////yangjun add begin
		// 显示链路信息
		var divLineInfo = document.createElement("div");
		divLineInfo.id = "info_" + a + "_"+ b + "#demoline";
		divLineInfo.style.position = "absolute";
		divLineInfo.style.border = infoBorder;
		divLineInfo.style.width = 200;
		divLineInfo.style.height = "auto";
		divLineInfo.style.color = infoFontColor;
		divLineInfo.style.padding = infoPadding;
		divLineInfo.style.display = "block";
		divLineInfo.style.lineHeight = "120%";
		divLineInfo.style.zIndex = 2;
		divLineInfo.style.backgroundColor = infoBgColor;
		divLineInfo.style.visibility = "hidden";
		divLineInfo.style.fontSize = "12px";
		divLineInfo.innerHTML = lineInfo;
		appendChild(divLineInfo);
		var getCoordInDocument = function(e) {// 获取鼠标当前位置
             e = e || window.event; 
             var x = e.pageX || (e.clientX + (document.documentElement.scrollLeft|| document.body.scrollLeft)); 
             var y= e.pageY || (e.clientY + (document.documentElement.scrollTop || document.body.scrollTop));   
             return {'x':x,'y':y}; 
        }
		
		// ///////////////end
		// line.onclick = function() { showLineInfo() };

		document.getElementById('divLayer').appendChild(line);
	
		document.getElementById("node_" + a).lines += '&' + line.id;
		document.getElementById("node_" + b).lines += '&' + line.id;
		// 链路菜单
		var divMenu = document.createElement("div");
		divMenu.id = "menu_" + a + "_"+ b + "#demoline";
		divMenu.style.position = "absolute";
		divMenu.style.width = 120;
		divMenu.style.height = "auto";
		divMenu.style.zIndex = 2;
		divMenu.style.visibility = "hidden";
		divMenu.style.border = "solid #000066 1px";
		divMenu.style.backgroundColor = "#F5F5F5";
		divMenu.style.padding = "1px";
		divMenu.style.lineHeight = "150%";
		divMenu.style.fontSize = "12px";
		divMenu.innerHTML = lineMenu;
		appendChild(divMenu);
		// 增加链路菜单的触发事件
		addContextmenuEventListener(document.getElementById("line_" + a + "_"+ b + "#demoline"),function(event){
				event = event || window.event;
				console.log('demoline');
				var that = event.target || event.srcElement;
			
				var pos=getCoordInDocument(event);
				
				document.getElementById(that.id.replace("line","menu")).style.left = parseInt(pos['x']);
			    document.getElementById(that.id.replace("line","menu")).style.top = parseInt(pos['y']);
				if(clickLineObj != null)
				{
					document.getElementById(clickLineObj.id.replace("line", "menu")).style.visibility = "hidden";
					clickLineObj = null;
				}
				clickLineObj = that;
				console.log('d2');
				document.getElementById(that.id.replace("line", "info")).style.visibility = "hidden";
				document.getElementById(that.id.replace("line", "menu")).style.visibility = "visible"; 
			
			//阻止默认事件和事件的传播
			
	    });
		
		document.getElementById("menu_" + a + "_"+ b + "#demoline").onclick = function() { this.style.visibility = "hidden"; };
		
		// 加入 stroke 标签
		// window.event.srcElement.stroke.dashstyle = "Solid";
		/*var stroke = document.createElement("v:stroke");
		stroke.dashstyle = dash;
		document.getElementById(line.id).appendChild(stroke);*/
	}
	
	var nodeNum = nodes.length;
	if (nodeNum == 0)
	{
		//document.write("<br/><br/><br/><br/><br/>");
		//document.write("<center>您没有选择观察的拓扑图，请选择正确的拓扑图</center>");
	}
	//zoomProcDlg("out");// 关闭闪屏
}
// end of ParseData();

// 处理选框
function selectStart()
{
	if (window.event.srcElement.id == "divLayer" || 
		window.event.srcElement.tagName == "BODY")
	{
		obj = null;
		selectStatus = true;
		imgTop.style.height = imgTop.style.width = imgLeft.style.height = imgLeft.style.width = imgBottom.style.height = imgBottom.style.width = imgRight.style.height = imgRight.style.width = 1;
		imgTop.style.visibility = imgLeft.style.visibility = imgBottom.style.visibility = imgRight.style.visibility = "visible";
		imgTop.style.zIndex = imgLeft.style.zIndex = imgBottom.style.zIndex = imgRight.style.zIndex = 9999;
		beginx = event.x;
		beginy = event.y;
		document.onmousemove = move;
		document.onmouseup = up;
	}
}


function bodyDown()
{
	var event = window.event || arguments.callee.caller.arguments[0];
	var srcElement = event.target || event.srcElement;

	if (srcElement.tagName == "BODY") 
	{
		if (document.getElementById("ctrlImgDiv"))// yangjun add 取消ctrl选中的图元
		{
			if(isRemoved == false)
			{
				rmvCtrlImg();	
			}
			isRemoved = true;
		}
		if (clickObj != null )
		{
			document.getElementById(clickObj.id.replace("node", "menu")).style.visibility = "hidden"; 
			unSelectImg(clickObj);
			clickObj = null;
		}
		if (clickLineObj != null )
		{
			document.getElementById(clickLineObj.id.replace("line", "menu")).style.visibility = "hidden"; 
			clickLineObj = null;
		}
		if (objStyle != null)
		{
		   unSelectImg(objStyle);
		   objStyle = null;
		}
		if (document.getElementById("containImgDiv"))
		{
			if(isRemoved == false)
			{
				rmvContainedImg();	
			}
			isRemoved = true;
		}
	}
	else if (srcElement.tagName == "DIV" && 
			 srcElement.id == "containImgDiv")
	{
		if(isRemoved == false)
		{
			rmvContainedImg();	
		}
		isRemoved = true;
	}
	else if (srcElement.tagName == "DIV" && 
			 srcElement.id == "ctrlImgDiv")// yangjun add
														// 取消ctrl选中的图元
	{
		if(isRemoved == false)
		{
			rmvCtrlImg();	
		}
		isRemoved = true;
	}
}

// 按下divLayer空白处时
function divLayerDown(event)
{
	event = event ||window.event;
	var that = event.target || event.srcElement;
	
	if (that.id == "divLayer")
	{
		if (clickObj != null )
		{
			document.getElementById(clickObj.id.replace("node", "menu")).style.visibility = "hidden"; 
			unSelectImg(clickObj);
			clickObj = null;
		}
		if (clickLineObj != null )
		{
			document.getElementById(clickLineObj.id.replace("line", "menu")).style.visibility = "hidden"; 
			clickLineObj = null;
		}
		if (objStyle != null)
		{
		   unSelectImg(objStyle);
		   objStyle = null;
		}
		if (document.getElementById("containImgDiv"))
		{
			if(isRemoved == false)
			{
				rmvContainedImg();	
			}
			isRemoved = true;
		}
		if (document.getElementById("ctrlImgDiv"))// yangjun add 取消ctrl选中的图元
		{
			console.log("ctrlImgDiv is found");
			if(isRemoved == false)
			{
				rmvCtrlImg();	
			}
			isRemoved = true;
		}
    }
	else if (that.tagName == "DIV" && that.id == "containImgDiv")
	{
		if(isRemoved == false)
		{
			rmvContainedImg();	
		}
		isRemoved = true;
	}
	else if (that.tagName == "DIV" &&  that.id == "ctrlImgDiv")// yangjun add
														// 取消ctrl选中的图元
	{
		if(isRemoved == false)
		{
			rmvCtrlImg();	
		}
		isRemoved = true;
	}
}

// 处理鼠标键按下事件
function down(event)
{
	event = event || window.event;
	console.log(event);
	 /***
	 * 测试的时候是基于鼠标为左手的测试
	 * 点击鼠标右键 event.button = 0或者1
	 */
	if (event.button == 0||event.button == 1 )
	{  // 禁用右键拖动
		
	}else{
		return false;
	}
	if (clickLineObj != null )
	{
		document.getElementById(clickLineObj.id.replace("line", "menu")).style.visibility = "hidden"; 
		clickLineObj = null;
	}
	if (clickObj != null)
	{ // 隐藏右键单击图标后的菜单
		document.getElementById(clickObj.id.replace("node", "menu")).style.visibility = "hidden";
		clickObj = null;
	}
	if(isFF){
		document.addEventListener('mousemove',move,true);
	}else{
		this.setCapture();
		this.attachEvent('onmousemove',move);	
	}
	
	// /////////////////////////////////yangjun 修改
	if(event.ctrlKey) {// 如果是ctrl选择事件 yangjun add
	
		if(document.getElementById("ctrlImgDiv") && objEntityAry.contains(this))
		{
			xx = event.clientX - document.getElementById("ctrlImgDiv").offsetLeft;
			yy = event.clientY - document.getElementById("ctrlImgDiv").offsetTop;
			this.setCapture();
			obj = this;
		}
		else
		{
			if(document.getElementById("ctrlImgDiv"))
			{
				removeChild(document.getElementById("ctrlImgDiv"));
				// rmvCtrlImg();
			}
			var p = getImagePropertiesBy(this,{'x':1,'y':1});
			//alert(p.x+"="+p.y+"this.offsetTop="+this.offsetTop);
			xx = event.clientX - parseInt(p.x,10);
			yy = event.clientY - parseInt(p.y,10);
			obj = this;
			objStyle = this;
			selectImg(objStyle);
			if (obj.lines != null)
			{
				line_ids = obj.lines.split("&");
			}
			strSearch = obj.id.replace(/^[^_]*/, '') + '_';
			objEntityAry.push(obj);// yangjun add
			ctrlStatus = true;
		}
	} 
	else 
	{
	    if (document.getElementById("containImgDiv") && objMoveAry.contains(this))
		{
			xx = event.clientX - document.getElementById("containImgDiv").offsetLeft;
			yy = event.clientY - document.getElementById("containImgDiv").offsetTop;
			this.setCapture();
			obj = this;
		}
		else
		{
			if(document.getElementById("containImgDiv"))
			{
				rmvContainedImg();
			}
			
			var p = getImagePropertiesBy(this,{'x':1,'y':1});
			
			//console.log(event.clientX+"===="+parseInt(p.x,10));
			 
			xx = event.clientX - parseInt(p.x,10);
			yy = event.clientY - parseInt(p.y,10);
			discardSelectedLast();
			obj = this;
			objStyle = this;
			selectImg(objStyle);
			objEntityAry.length=0;// yangjun add
			objEntityAry.push(obj);// yangjun add
			//ctrlStatus = true; // yangjun add
			if (obj.lines != null)
			{
				//alert(obj.lines);
				line_ids = obj.lines.split("&");
			}
			strSearch = obj.id.replace(/^[^_]*/, '') + '_';
			
		}
	}
	// //////////////////////////////////////////////////////
	//document.onmousemove = move;
	
	return cancelPropagationAndDefaultOfEvent(event);
}
// 处理鼠标移动事件
function move(event)
{
	event = event || window.event;
	var eventX = event.clientX;
	var eventY = event.clientY;
	if (eventX > 0 & eventY > 0 & eventX < window.screen.width & eventY < window.screen.height)
	{
		if(obj != null)
		{
			var tempX = eventX - xx;
			var tempY = eventY - yy;
			// /yangjun add ctrl选中后拖动
		 	if (document.getElementById("containImgDiv"))
			{
				document.getElementById("containImgDiv").style.left = tempX;
				document.getElementById("containImgDiv").style.top = tempY;
				if (line_ids.length > 0)
				{
					for (var i = 0; i < line_ids.length; i++)
					{
						for(var j = 0; j < line_ids[i].length-1; j++)
						{
							var iElem = document.all(line_ids[i][j]);
							if (iElem == null)
							{
								continue;
							}
							var lth = line_ids[i].length-1;
							var tmpStr = document.all(line_ids[i][lth]).id.replace(/[^_]*/, '') + '_';
							var iLeft = parseInt(document.all(line_ids[i][lth]).style.left) + parseInt(document.all("containImgDiv").style.left) + 15;
							var iTop = parseInt(document.all(line_ids[i][lth]).style.top) + parseInt(document.all("containImgDiv").style.top) + 8;
							if (line_ids[i][j].search(tmpStr) != -1)
							{
								iElem.from = iLeft + "," + iTop;
							}
							else
							{
								iElem.to = iLeft + "," + iTop;
							}
						}
					}
				}
			}
			else
			{
				var textStyle = document.getElementById(obj.id.replace("node", "text")).style;
				var infoStyle = document.getElementById(obj.id.replace("node", "info")).style;
				var menuStyle = document.getElementById(obj.id.replace("node", "menu")).style;
				
				setImage(obj,{'x':tempX+'px','y':tempY+'px'});
				textStyle.left = tempX - 24;
				textStyle.top = tempY + 28;
				infoStyle.left = tempX + 32;
				infoStyle.top = tempY;
				//#########增加拖拽设备时“设备信息显示”的越界处理，即超过边界的情况自动显示到可视范围之内   HONGLI ADD #########
				var divInfo = document.getElementById(obj.id.replace("node", "info"));
				if(parseInt(tempX, 10) > document.body.clientWidth-infoWidth-24){
					infoStyle.left =parseInt(tempX, 10)-infoWidth;
				}
				if(parseInt(tempY, 10) > document.body.clientHeight-divInfo.clientHeight){  
					infoStyle.top = parseInt(tempY, 10)-divInfo.clientHeight;  
			    }
		   		//#############HONG ADD END##########
				menuStyle.left = tempX + 28;
				menuStyle.top = tempY;
				var p = getImagePropertiesBy(obj,{'x':1,'y':1});
				var iLeft = parseInt( p.x ) + 15;
				var iTop = parseInt( p.y ) + 8;
				for (var i = 1; i < line_ids.length; i += 1)
				{
					var iElem = document.getElementById(line_ids[i]);
					if (line_ids[i].search(strSearch) != -1)
					{

						if(line_ids[i].search('#assistant') != -1){
							setLine(iElem,{'x1':(parseInt(iLeft)+5),'y1':iTop});
	
						} else if(line_ids[i].search('#demoline') != -1){
						    setLine(iElem,{'x1':(parseInt(iLeft)+8),'y1':iTop});
						} else {
							
						    setLine(iElem,{'x1':iLeft,'y1':iTop});
						}
					}
					else
					{

						if(line_ids[i].search('#assistant') != -1){
							
							setLine(iElem,{'x2':(parseInt(iLeft)+5),'y2':iTop});
						} else if(line_ids[i].search('#demoline') != -1){
							setLine(iElem,{'x2':(parseInt(iLeft)+8),'y2':iTop});
						} else{
							//iElem.to = iLeft + "," + iTop;
							setLine(iElem,{'x2':iLeft,'y2':iTop});
						}
					}
				}
			}
			movedObjFlag = true;
		}
	
		if (selectStatus)
		{  // 画框选择
			var tmpx = window.event.x;
			var tmpy = window.event.y;
			imgTop.style.left = Math.min(beginx,tmpx);
			imgTop.style.top = Math.min(beginy,tmpy);
			imgTop.style.width = Math.abs(beginx - tmpx);
			imgLeft.style.left = Math.min(beginx,tmpx);
			imgLeft.style.top = Math.min(beginy,tmpy);
			imgLeft.style.height = Math.abs(beginy - tmpy);
			imgBottom.style.left = Math.min(beginx,tmpx);
			imgBottom.style.top = Math.max(beginy,tmpy);
			imgBottom.style.width = Math.abs(beginx - tmpx);
			imgRight.style.left = Math.max(beginx,tmpx);
			imgRight.style.top = Math.min(beginy,tmpy);
			imgRight.style.height = Math.abs(beginy - tmpy);
		}
		
	}
	
	return cancelPropagationAndDefaultOfEvent(event);
}

// 处理鼠标键释放事件
function up()
{
	console.log("mouse up");
	if (obj != null)
	{
		if(isFF){
			document.removeEventListener('mousemove',move,true);
		}else{
			this.detachEvent('onmousemove',move);
			this.releaseCapture();
				
		}
		 cancelPropagationAndDefaultOfEvent(event);
		//obj.onmouseup = null
		if (movedObjFlag == true)
		{
			if (document.getElementById("containImgDiv"))
			{
				for (var i = 0; i < objMoveAry.length; i++)
				{
					if (!tempArray.contains(objMoveAry[i]))
					{
						tempArray.push(objMoveAry[i]);
					}
				}
			}
			else if (!tempArray.contains(this))
			{
				tempArray.push(this);
			}
		}
		obj = null;
		if(!document.getElementById("containImgDiv"))
		{
			line_ids.length = 0;
		}		
		strSearch = null;
	}
	if (selectStatus)
	{
		imgTopX = parseInt(imgTop.style.left);   // 取得彩框的大小位置，将所有位于这个框内的节点和文本标签放入objMoveAry数组
		imgTopY = parseInt(imgTop.style.top);
		imgBottomX = parseInt(imgTop.style.left) + parseInt(imgTop.style.width);
		imgBottomY = parseInt(imgTop.style.top) + parseInt(imgLeft.style.height);
		var allImgNodes = document.getElementsByTagName("image");// yangjun
																	// 将image修改为back
		var j = 0;
		objMoveAry.length = 0;
		for (var i = 0; i < allImgNodes.length; i++)
		{
			if (parseInt(allImgNodes[i].style.left) + parseInt(document.getElementById('divLayer').style.left) >= imgTopX 
			   && parseInt(allImgNodes[i].style.left) + parseInt(document.getElementById('divLayer').style.left) <= imgBottomX 
			   && parseInt(allImgNodes[i].style.top) + parseInt(document.getElementById('divLayer').style.top) >= imgTopY 
			   && parseInt(allImgNodes[i].style.top) + parseInt(document.getElementById('divLayer').style.top) <=imgBottomY
			   && parseInt(allImgNodes[i].style.left) + parseInt(allImgNodes[i].style.width) + parseInt(document.getElementById('divLayer').style.left) <= imgBottomX 
			   && parseInt(allImgNodes[i].style.top) + parseInt(allImgNodes[i].style.height) + parseInt(document.getElementById('divLayer').style.top) <= imgBottomY)
			{
				objMoveAry[j] = allImgNodes[i];
				selectImg(objMoveAry[j]);
				j++;
			}
		}
		if (objMoveAry.length > 0)
		{
			var containImgDiv = document.createElement("div");
			containImgDiv.id = "containImgDiv";
			containImgDiv.style.position = "absolute";
			containImgDiv.style.left = imgTopX;
			containImgDiv.style.top = imgTopY;
			containImgDiv.style.width = imgTop.style.width;
			containImgDiv.style.height = imgLeft.style.height;
			appendChild(containImgDiv);
			for (var i = 0; i < objMoveAry.length; i++)
			{
				var txtObj = document.getElementById(objMoveAry[i].id.replace("node","text"));
				var infoObj = document.getElementById(objMoveAry[i].id.replace("node","info"));
				var menuObj = document.getElementById(objMoveAry[i].id.replace("node","menu"));
				document.getElementById("containImgDiv").appendChild(objMoveAry[i]);
				document.getElementById("containImgDiv").appendChild(txtObj);
				document.getElementById("containImgDiv").appendChild(infoObj);
				document.getElementById("containImgDiv").appendChild(menuObj);
				objMoveAry[i].style.position = "absolute";
				objMoveAry[i].style.left = parseInt(objMoveAry[i].style.left) - parseInt(containImgDiv.style.left);
				objMoveAry[i].style.top = parseInt(objMoveAry[i].style.top) - parseInt(containImgDiv.style.top);
				txtObj.style.position = "absolute";
				txtObj.style.left = parseInt(txtObj.style.left) - parseInt(containImgDiv.style.left);
				txtObj.style.top = parseInt(txtObj.style.top) - parseInt(containImgDiv.style.top);
				infoObj.style.position = "absolute";
				infoObj.style.left = parseInt(infoObj.style.left) - parseInt(containImgDiv.style.left);
				infoObj.style.top = parseInt(infoObj.style.top) - parseInt(containImgDiv.style.top);
				menuObj.style.position = "absolute";
				menuObj.style.left = parseInt(menuObj.style.left) - parseInt(containImgDiv.style.left);
				menuObj.style.top = parseInt(menuObj.style.top) - parseInt(containImgDiv.style.top);
			}
			isRemoved = false;
			line_ids.length = 0;
			lineMoveAry.length = 0; // 存放加载到containImgDiv里面的连线对象
			assLineMoveAry.length = 0;
			demoLineMoveAry.length = 0;
			for (var i = 0; i < objMoveAry.length; i++)
			{
				if (objMoveAry[i].lines == null)
				{
					// 如果设备没有连线
					continue;
				}
				if (objMoveAry[i].lines.split("&").length > 0)
				{
					var twoDemAry = new Array();
					twoDemAry = objMoveAry[i].lines.split("&");
					var tmpRelAry = new Array();
					for (var j = 1; j < twoDemAry.length; j++)
					{
						if (! document.getElementById(twoDemAry[j]))
						{
							continue;
						}
						if(twoDemAry[j].search('#assistant') != -1){// 将辅助链路放入数组
							// alert(twoDemAry[j].split("#")[0]);
							var conObjAry1 = twoDemAry[j].split("#")[0].replace("line_","").split("_");
							//document.all("containImgDiv").all("node_" + conObjAry1[0])
							if ((document.getElementById("node_" + conObjAry1[0])) && (document.getElementById("node_" + conObjAry1[1]))) {
							    var addLineObj = document.getElementById(twoDemAry[j]);
							    assLineMoveAry.push(addLineObj);
							}
						}
						if(twoDemAry[j].search('#demoline') != -1){// 将示意链路放入数组
							// alert(twoDemAry[j].split("#")[0]);
							var conObjAry2 = twoDemAry[j].split("#")[0].replace("line_","").split("_");
							if ((document.getElementById("node_" + conObjAry2[0])) && (document.getElementById("node_" + conObjAry2[1]))) {
							    var addLineObj = document.getElementById(twoDemAry[j]);
							    demoLineMoveAry.push(addLineObj);
							}
						}
						var conObjAry = twoDemAry[j].replace("line_","").split("_");
						if ((document.getElementById("node_" + conObjAry[0])) && (document.getElementById("node_" + conObjAry[1]))) {
							var addLineObj = document.getElementById(twoDemAry[j]);
							lineMoveAry.push(addLineObj);		
							addLineObj.style.visibility = "hidden";
							addLineObj.style.position = "absolute";
							//addLineObj.from = (parseInt(document.all("node_" + conObjAry[0]).style.left) + 15) + "," + (parseInt(document.all("node_" + conObjAry[0]).style.top) + 8);
							//addLineObj.to = (parseInt(document.all("node_" + conObjAry[1]).style.left) + 15) + "," + (parseInt(document.all("node_" + conObjAry[1]).style.top) + 8);
						    
							addLineObj[i].from = (parseInt(document.getElementById("node_" + conObjAry[0]).style.left) + parseInt(document.getElementById("node_" + conObjAry[0]).style.width)/2) + "," + (parseInt(document.getElementById("node_" + conObjAry[0]).style.left) + parseInt(document.getElementById("node_" + conObjAry[0]).style.height)/2);
							addLineObj[i].to =  (parseInt(document.getElementById("node_" + conObjAry[1]).style.left) + parseInt(document.getElementById("node_" + conObjAry[1]).style.width)/2) + "," + (parseInt(document.getElementById("node_" + conObjAry[1]).style.left) + parseInt(document.getElementById("node_" + conObjAry[1]).style.height)/2);
						    
						    document.getElementById("containImgDiv").appendChild(addLineObj);
							addLineObj.style.visibility = "visible";
						}
						else
						{
							relLineAry.push(twoDemAry[j]);  // 把所有与该div相连的连线（非包含）都放到此数组中
							tmpRelAry.push(twoDemAry[j]);   // 把与当前节点相关的连线（没包含在当前div中）都放到此数组中
						}
					}
					if (tmpRelAry.length > 0)
					{
						tmpRelAry.push(objMoveAry[i].id);
						line_ids.push(tmpRelAry);	// line_ids中存放着所有与该div节点中相连的线对象(没包含在当前div中)id
					}
				}
			}
		}
		selectStatus = false;
		imgTop.style.visibility = "hidden";
		imgLeft.style.visibility = "hidden";
		imgBottom.style.visibility = "hidden";
		imgRight.style.visibility = "hidden";
	}
	console.log("ctrlStatus="+ctrlStatus);
	if (ctrlStatus) // 处理Ctrl+鼠标键释放事件yangjun
	{
		//console.log("ctrl press objEntityAry.length="+objEntityAry.length);
		if (objEntityAry.length > 0)
		{
     		var ctrlImgDiv = document.createElement("div");
			ctrlImgDiv.id = "ctrlImgDiv";
			ctrlImgDiv.style.position = "absolute";
			appendChild(ctrlImgDiv);
			/*for (var i = 0; i < objEntityAry.length; i++)
			{
				// ////////////////////////
				document.getElementById("ctrlImgDiv").appendChild(objEntityAry[i]);
				// ////////////////////////

			}*/
			//alert(ctrlStatus);
			isRemoved = false;
			line_ids.length = 0;
			lineMoveAry.length = 0; // 存放加载到containImgDiv里面的连线对象
			assLineMoveAry.length = 0;
			demoLineMoveAry.length = 0;
			for (var i = 0; i < objEntityAry.length; i++)
			{
				
				if (objEntityAry[i].lines == null)
				{
					// 如果设备没有连线
					continue;
				}
				if (objEntityAry[i].lines.split("&").length > 0)
				{
					var twoDemAry = new Array();
					twoDemAry = objEntityAry[i].lines.split("&");
					var tmpRelAry = new Array();
					for (var j = 1; j < twoDemAry.length; j++)
					{
						if (! document.getElementById(twoDemAry[j]))
						{
							continue;
						}
						if(twoDemAry[j].search('#assistant') != -1){// 将辅助链路放入数组
							// alert(twoDemAry[j].split("#")[0]);
							var conObjAry1 = twoDemAry[j].split("#")[0].replace("line_","").split("_");
							if ((document.getElementById("node_" + conObjAry1[0])) && (document.getElementById("node_" + conObjAry1[1]))) {
							    var addLineObj = document.getElementById(twoDemAry[j]);
							    assLineMoveAry.push(addLineObj);
							}
						}
						if(twoDemAry[j].search('#demoline') != -1){// 将示意链路放入数组
							// alert(twoDemAry[j].split("#")[0]);
							var conObjAry2 = twoDemAry[j].split("#")[0].replace("line_","").split("_");
							if ((document.getElementById("node_" + conObjAry2[0])) && (document.getElementById("node_" + conObjAry2[1]))) {
							    var addLineObj = document.getElementById(twoDemAry[j]);
							    demoLineMoveAry.push(addLineObj);
							}
						}
						var conObjAry = twoDemAry[j].replace("line_","").split("_");
						if ((document.getElementById("node_" + conObjAry[0])) && (document.getElementById("node_" + conObjAry[1]))) {
							var addLineObj = document.getElementById(twoDemAry[j]);
							lineMoveAry.push(addLineObj);	
							// /////////////////////////////
						}

					}

				}
			}
		}
		ctrlStatus = false;
	}
	//document.onmousemove = null;
	
	return cancelPropagationAndDefaultOfEvent(event);
}
// 清楚ctrl选中的图元
function rmvCtrlImg()
{
	console.log(objEntityAry.length);
	for (var i = 0; i < objEntityAry.length; i++)
	{
		unSelectImg(objEntityAry[i]);
		/*var txtObj = document.getElementById(objEntityAry[i].id.replace("node","text"));
		var infoObj = document.getElementById(objEntityAry[i].id.replace("node","info"));
		var menuObj = document.getElementById(objEntityAry[i].id.replace("node","menu"));
		document.getElementById('divLayer').appendChild(objEntityAry[i]);
		document.getElementById('divLayer').appendChild(txtObj);
		document.getElementById('divLayer').appendChild(infoObj);
		document.getElementById('divLayer').appendChild(menuObj);*/
		// ////
	}		
	if (lineMoveAry.length > 0)
	{
		for (var i = 0; i < lineMoveAry.length; i++)
		{
			document.getElementById('divLayer').appendChild(lineMoveAry[i]);
			lineMoveAry[i].style.visibility = "hidden";
			var conObjAry = lineMoveAry[i].id.replace("line_","").split("_");
			var fromObj = document.getElementById("node_" + conObjAry[0]);
			var toObj = document.getElementById("node_" + conObjAry[1]);
			//lineMoveAry[i].from = (parseInt(fromObj.style.left) + 15) + "," + (parseInt(fromObj.style.top) + 8);
			//lineMoveAry[i].to =  (parseInt(toObj.style.left) + 15) + "," + (parseInt(toObj.style.top) + 8);
			lineMoveAry[i].from = (parseInt(fromObj.style.left) + parseInt(fromObj.style.width)/2) + "," + (parseInt(fromObj.style.top) + parseInt(fromObj.style.height)/2);
			lineMoveAry[i].to =  (parseInt(toObj.style.left) + parseInt(toObj.style.width)/2) + "," + (parseInt(toObj.style.top) + parseInt(toObj.style.height)/2);
			lineMoveAry[i].style.visibility = "visible";
		}
	}
	lineMoveAry.length = 0;
	assLineMoveAry.length = 0;
	demoLineMoveAry.length = 0;
	objEntityAry.length = 0;
	line_ids.length = 0;
	removeChild(document.getElementById("ctrlImgDiv"));
}
function rmvContainedImg()
{
	var tmpDivLeft = document.all("containImgDiv").style.left;
	var tmpDivTop = document.all("containImgDiv").style.top;
	for (var i = 0; i < objMoveAry.length; i++)
	{
		unSelectImg(objMoveAry[i]);	
		var txtObj = document.all(objMoveAry[i].id.replace("node","text"));
		var infoObj = document.all(objMoveAry[i].id.replace("node","info"));
		var menuObj = document.all(objMoveAry[i].id.replace("node","menu"));
		document.all.divLayer.appendChild(objMoveAry[i]);
		document.all.divLayer.appendChild(txtObj);
		document.all.divLayer.appendChild(infoObj);
		document.all.divLayer.appendChild(menuObj);
		objMoveAry[i].style.left = parseInt(objMoveAry[i].style.left) + parseInt(tmpDivLeft);
		objMoveAry[i].style.top = parseInt(objMoveAry[i].style.top) + parseInt(tmpDivTop);
		txtObj.style.left = parseInt(txtObj.style.left) + parseInt(tmpDivLeft);
		txtObj.style.top = parseInt(txtObj.style.top) + parseInt(tmpDivTop);
		infoObj.style.left = parseInt(infoObj.style.left) + parseInt(tmpDivLeft);
		infoObj.style.top = parseInt(infoObj.style.top) + parseInt(tmpDivTop);
		menuObj.style.left = parseInt(menuObj.style.left) + parseInt(tmpDivLeft);
		menuObj.style.top = parseInt(menuObj.style.top) + parseInt(tmpDivTop);
	}			
	if (lineMoveAry.length > 0)
	{
		for (var i = 0; i < lineMoveAry.length; i++)
		{
			document.all.divLayer.appendChild(lineMoveAry[i]);
			lineMoveAry[i].style.visibility = "hidden";
			var conObjAry = lineMoveAry[i].id.replace("line_","").split("_");
			var fromObj = document.all("node_" + conObjAry[0]);
			var toObj = document.all("node_" + conObjAry[1]);
			//lineMoveAry[i].from = (parseInt(fromObj.style.left) + 15) + "," + (parseInt(fromObj.style.top) + 8);
			//lineMoveAry[i].to =  (parseInt(toObj.style.left) + 15) + "," + (parseInt(toObj.style.top) + 8);
			lineMoveAry[i].from = (parseInt(fromObj.style.left) + parseInt(fromObj.style.width)/2) + "," + (parseInt(fromObj.style.top) + parseInt(fromObj.style.height)/2);
			lineMoveAry[i].to =  (parseInt(toObj.style.left) + parseInt(toObj.style.width)/2) + "," + (parseInt(toObj.style.top) + parseInt(toObj.style.height)/2);
			lineMoveAry[i].style.visibility = "visible";
		}
	}
	lineMoveAry.length = 0;
	assLineMoveAry.length = 0;
	demoLineMoveAry.length = 0;
	objMoveAry.length = 0;
	line_ids.length = 0;
	removeChild(document.getElementById("containImgDiv"));
}

// 选中节点后的样式
function selectImg(objSty)
{	
	var obj = document.getElementById(objSty.id);
	obj.style.background = "#9BFFAC";
	obj.style.border="1px solid #007314";
	if(isFF){
		obj.style.opacity="0.6";
	}else{

		obj.style.filter="Alpha(Opacity=60);";
	}
	
}

// 取消选中节点的样式
function unSelectImg(objSty)
{
	//TODO svg image不支持background和border  CSS2属性，后续实现选中边框和背景变色效果
	var obj = document.getElementById(objSty.id);
	
	if(isFF){
		obj.style.opacity="1";
		
	}else{
		obj.style.background = "";
		obj.style.border="0px solid #007314";
		obj.style.color = "#000000";
		obj.style.filter="";
	}
	console.log(obj);
}

// 保存
function save()
{
	if(document.getElementById("containImgDiv"))
	{
		rmvContainedImg();
	}
	var nodes = xmldoc.getElementsByTagName("node");
	for (var i = 0; i < tempArray.length; i += 1)
	{
		for (var j = 0; j < nodes.length; j += 1)
		{
			var node = nodes[j];
			var id = "node_" + node.getElementsByTagName("id")[0].childNodes[0].nodeValue;

			if (tempArray[i].id == id)
			{
				var p = getImagePropertiesBy(tempArray[i],{'x':1,'y':1});
				node.getElementsByTagName("x")[0].childNodes[0].nodeValue = p.x;
				node.getElementsByTagName("y")[0].childNodes[0].nodeValue = p.y;
			}
		}
	}
	document.getElementById("hidXml").value = serializeXmldocToString();
	document.frmMap.submit();
}

// 判断X, Y是否在所有节点坐标的最大值
function getConfine(x, y)
{
	var coorX;
	var coorY;
	
	if (x.indexOf("px") >= 0)
	{
		coorX = x.substring(0, x.length-2);
		coorX = parseInt(coorX);
	}
	else {
		coorX = parseInt(x);
	}
	
	if (y.indexOf("px") >= 0)
	{
		coorY = y.substring(0, y.lenght-2);
		coorY = parseInt(coorY);
	}
	else
	{
		coorY = parseInt(y);
	}
	
	if (coorX > maxWidth)
		maxWidth = coorX;
	if (coorY > maxHeight)
		maxHeight = coorY;
		
	if (coorX < minWidth)
		minWidth = coorX;
	if (coorY < minHeight)
		minHeight = coorY;
}

// 通过键盘的“上下左右”方向键控制视图位置
/*document.onkeydown = function ()
{
// -----先不用--------改3-------

 * try { if (document.all.blind.style.visibility == "visible") return; } catch
 * (exception) { }
 
// ----------------------
	var left = divLayer.style.left;
	var top = divLayer.style.top;
	
	// left = left.substring(0, left.length - 2);
	// top = top.substring(0, top.length - 2);

	left = parseInt(left);
	top = parseInt(top);
	console.log('event.keyCode='+event.keyCode);
	switch (event.keyCode)
	{
		
		 * case 81: // Q 键，切换视图 if (g_viewFlag == 0) { g_viewFlag = 1; var
		 * target = "showMap.jsp?filename=" + filename + "&viewflag=1";
		 * parent.mainFrame.location = target; } else if (g_viewFlag == 1) {
		 * g_viewFlag = 0; var target = "showMap.jsp?filename=" + filename +
		 * "&viewflag=0"; parent.mainFrame.location = target; } break;
		 
		case 35:		// End
		case 82:		// R
			moveAction();
			break;
		case 37:
		case 65:		// A
			moveAction('left');
			break;
		case 38:
		case 87:		// W
			moveAction('up');
			break;
		case 39:
		case 68:		// D
			moveAction('right');
			break;
		case 40:
		case 83:		// S
			moveAction('down');
			break;
			
		case 90:		// Z
			zoomAll('out');
			break;
		case 88:		// X
			zoomAll();
			break;
		case 67:		// C
			zoomAll('in');
			break;
			
		case 69:		// E 隐藏/显示控制面板
			// showController(!controllerState);
			break;

		default:
			break;
	}

}
*/

/** ** 控制移动方向的四个按钮事件和缩放按钮事件 -- 开始 *** */

var distance = 80;
var speed = 12;
var position;
var left;
var top;
var timer;

var zoom = 1.0;
var scale = 0.1;

var controllerState = true;

function moveAction(dir)
{

// /-----------------改4----
	closeAnchor();	// 关闭准星
// hideLineTip(); // 隐藏链路提示
// ------------------------

	clearTimer();
	updatePosition();
	
	if (dir == "left")
	{
		position = left + distance;
		timer = setInterval("moveLeft()", speed);
	}
	else if (dir == "up") 
	{
		position = top + distance;
		timer = setInterval("moveUp()", speed);
	}
	else if (dir == "right") 
	{
		position = left - distance;
		timer = setInterval("moveRight()", speed);
	}
	else if (dir == "down") 
	{
		position = top - distance;
		timer = setInterval("moveDown()", speed);
	}
	else
	{
		moveOrigin();
	}
}

function clearTimer()
{
	clearInterval(timer);
}

function moveLeft() 
{
	updatePosition();
	if (left >= position) 
	{
		clearTimer();
		return;
	}
	divLayer.style.left = (left + speed);
}

function moveUp() 
{
	updatePosition();
	if (top >= position)
	{
		clearTimer();
		return;
	}
	divLayer.style.top = (top + speed);
}

function moveRight()
{
	updatePosition();
	if (left <= position) 
	{
		clearTimer();
		return;
	}
	divLayer.style.left = (left - speed);
}

function moveDown() 
{
	updatePosition();
	if (top <= position) 
	{
		clearTimer();
		return;
	}
	divLayer.style.top = (top - speed);
}

function moveOrigin() 
{
	divLayer.style.left = 0;// parseInt(mainX);--这是改后的，用于恢复原来的位置-----改5--
	divLayer.style.top = 0;// parseInt(mainY);
}

function updatePosition() 
{
	var divLeft = parseInt(divLayer.style.left);
	var divTop = parseInt(divLayer.style.top);
	
	// divLeft = divLeft.substring(0, divLeft.length - 2);
	// divTop = divTop.substring(0, divTop.length - 2);

	left = parseInt(divLeft);
	top = parseInt(divTop);
}

function zoomAll(state) 
{
	closeAnchor();// ---------改后添加这个-
	if (divLayer.style.zoom == "") 
	{
		divLayer.style.zoom = 1.0;
	}
	
	if (state == "out") 
	{
		// 缩小
		if (divLayer.style.zoom != "") 
		{
			zoom = parseFloat(zoom) - scale;
			if (zoom <= 0) 
			{
				zoom = 0.9;
			}
			else if (zoom > 0 && zoom < 0.5) 
			{
				zoom = 0.5;
				return;
			}
			divLayer.style.zoom = parseFloat(zoom);
		}
	}
	else if (state == "in") 
	{
		// 放大
		if (divLayer.style.zoom != "") 
		{
			zoom = parseFloat(zoom) + scale;
			if (zoom > 2.0) 
			{
				zoom = 2.0;
				return;
			}
			else if (zoom == 0.2) 
			{
				zoom = 1.1;
			}
			divLayer.style.zoom = parseFloat(zoom);
		}
	}
	else {
		// 复原
		if (divLayer.style.zoom != "") 
		{
			divLayer.style.zoom = 1.0;
			zoom = 1.0;
		}
	}
}

function showController(show) 
{
	if (show) 
	{
		document.all.moveController.style.visibility = "visible";
		document.all.sizeController.style.visibility = "visible";
		controllerState = true;
	}
	else 
	{
		document.all.moveController.style.visibility = "visible";
		document.all.sizeController.style.visibility = "visible";
		// controllerState = false;
		controllerState = true;
	}
	return true;
}

// ------ 图片交换效果函数 - 开始 ------

function swapImage(imageID, imageSrc) 
{
	document.all(imageID).src = imgPath+imageSrc;
}

// ------ 图片交换效果函数 - 结束 ------

function loadMoveController() 
{
	document.write('<div id="moveController" style="position:absolute;top:5px;left:5px;z-index:999;background-image:url(image/controller_bg.gif);">');
	document.write('<table width="66" border="0" cellspacing="0" cellpadding="0" style="font-size:9pt;"><tr><td height="19" width="19"></td><td width="28">');
	document.write('<img src="'+imgPath+'image/topo/arrow_up.gif" onmouseout="javascript:swapImage(\'image_up\', \'image/topo/arrow_up.gif\');" onmouseover="javascript:swapImage(\'image_up\', \'image/topo/arrow_up_over.gif\');" alt="向上移动 | W" width="28" height="19" onclick="javascript:moveAction(\'up\');" style="cursor:hand;" name="image_up" id="image_up" />');
	document.write('</td><td height="19" width="19"></td></tr><tr><td>');
	document.write('<img src="'+imgPath+'image/topo/arrow_left.gif" onmouseout="javascript:swapImage(\'image_left\', \'image/topo/arrow_left.gif\');" onmouseover="javascript:swapImage(\'image_left\', \'image/topo/arrow_left_over.gif\');" alt="向左移动 | A" width="19" height="28" onclick="javascript:moveAction(\'left\');" style="cursor:hand;" name="image_left" id="image_left" />');
	document.write('</td><td align="center" valign="middle" style="text-align:center;">');
	document.write('<img src="'+imgPath+'image/topo/arrow_center.gif" onmouseout="javascript:swapImage(\'image_center\', \'image/topo/arrow_center.gif\');" onmouseover="javascript:swapImage(\'image_center\', \'image/topo/arrow_center_over.gif\');" alt="复位 | R" width="19" height="19" onclick="javascript:moveAction(\'origin\');" style="cursor:hand;" name="image_center" id="image_center" />');
	document.write('</td><td>');
	document.write('<img src="'+imgPath+'image/topo/arrow_right.gif" onmouseout="javascript:swapImage(\'image_right\', \'image/topo/arrow_right.gif\');" onmouseover="javascript:swapImage(\'image_right\', \'image/topo/arrow_right_over.gif\');" alt="向右移动 | D" width="19" height="28" onclick="javascript:moveAction(\'right\');" style="cursor:hand;" name="image_right" id="image_right" />');
	document.write('</td></tr><tr><td></td><td>');
	document.write('<img src="'+imgPath+'image/topo/arrow_down.gif" onmouseout="javascript:swapImage(\'image_down\', \'image/topo/arrow_down.gif\');" onmouseover="javascript:swapImage(\'image_down\', \'image/topo/arrow_down_over.gif\');" alt="向下移动 | S" width="28" height="19" onclick="javascript:moveAction(\'down\');" style="cursor:hand;" name="image_down" id="image_down" />');
	document.write('</td><td></td></tr></table></div>');
}

function loadSizeController() 
{
	document.write('<div id="sizeController" style="position:absolute;top:80px;left:8px;z-index:998;background-image:url(image/controller_bg2.gif);width:60px;">');
	document.write('<table width="58" height="20" border="0" cellspacing="0" cellpadding="0"><tr><td width="18" height="20" style="padding-left:2px;">');
	document.write('<img src="'+imgPath+'image/topo/zoom_out.gif" onmouseout="javascript:swapImage(\'image_out\', \'image/topo/zoom_out.gif\');" onmouseover="javascript:swapImage(\'image_out\', \'image/topo/zoom_out_over.gif\');" alt="缩小视图 | Z" width="18" height="16" onclick="javascript:zoomAll(\'out\');" style="cursor:hand;" name="image_out" id="image_out" />');
	document.write('</td><td align="center" style="text-align:center;">');
	document.write('<img src="'+imgPath+'image/topo/zoom_reset.gif" onmouseout="javascript:swapImage(\'image_reset\', \'image/topo/zoom_reset.gif\');" onmouseover="javascript:swapImage(\'image_reset\', \'image/topo/zoom_reset_over.gif\');" alt="还原 | X" width="18" height="16" onclick="javascript:zoomAll();" style="cursor:hand;" name="image_reset" id="image_reset" />');
	document.write('</td><td width="18" height="20">');
	document.write('<img src="'+imgPath+'image/topo/zoom_in.gif" onmouseout="javascript:swapImage(\'image_in\', \'image/topo/zoom_in.gif\');" onmouseover="javascript:swapImage(\'image_in\', \'image/topo/zoom_in_over.gif\');" alt="放大视图 | C" width="18" height="16" onclick="javascript:zoomAll(\'in\');" style="cursor:hand;" name="image_in" id="image_in" />');
	document.write('</td></tr></table></div>');
}

/** ** 控制移动方向的四个按钮事件和缩放按钮事件 -- 结束 *** */


// /把原来的重新加上
/** ** 物理链路信息显示 -- 开始 *** */

var lastLine = "";
var parentScrollTop = 0;
var defaultY = 360;
var showFlag = false;
var frameHeight = 180;

// 右键连接线菜单yangjun修改
function showLineInfo(lineid)
{
	//document.all.linkedline.style.top = (defaultY + parentScrollTop + frameHeight) + "px";
	//document.all.linkedline.style.visibility = "visible";
	//showFlag = true;
	// lastLine = window.event.srcElement.lineid;
	//window.lineInfoFrame.location = "linkedline.jsp?line=" + lineid;
	window.open('linkedline.jsp?line='+ lineid,'window', 'toolbar=no,height=800,width=850,scrollbars=yes,center=yes,screenY=0');
	clearTimer();
	timer = setInterval("displayInfoFrame()", speed);
	return;
}

function loadLinkLineInfo()
{
	var x = y = 0;
	if (fullscreen == "0")
	{
		x = 0;// (parseInt(viewWidth) - 800) / 2;
		y = defaultY + parentScrollTop + frameHeight-100;
	}
	else
	{
		x = 0;// (parseInt(viewWidth) - 800) / 2;
		y = parseInt(document.body.clientHeight) - frameHeight;
		defaultY = y;
	}
	
	document.write('<div id="linkedline" style="position:absolute;left:'+ x +'px;top:'+ y +'px;width:100%;height:200px;z-index:999;visibility:hidden;">');
	document.write('<iframe id="lineInfoFrame" frameborder="0" marginheight="0" marginwidth="0" scrolling="no" width="100%" height="'+frameHeight+'" src="linkedline.jsp?line='+ lastLine +'"></iframe>');
	document.write('</div>');
}

// 滚屏控制，使链路信息始终保持在底部
if (fullscreen == "0")
{
	window.parent.parent.document.body.onscroll = screenScroll;
	defaultY = parseInt(window.parent.parent.document.body.clientHeight) - frameHeight  - 30;// - 35
																								// +
																								// 15-
																								// 90
}
else
{
	window.parent.document.body.onscroll = screenScroll;
	defaultY = parseInt(window.parent.document.body.clientHeight) - frameHeight;// - 30
																				// + 15
}

if (defaultY > 550)
{
	defaultY -= 35;
}

function screenScroll()
{	
	if (fullscreen == "0")
	{	
		parentScrollTop = parseInt(window.parent.parent.document.body.scrollTop);
	}
	else
	{
		parentScrollTop = parseInt(document.body.clientHeight) - 28;
		// parseInt(window.parent.document.body.scrollTop);
		// 全屏时始终置底
		return;
	}

	if (parentScrollTop >= 0)
	{
		var divTop = document.all.linkedline.style.top;
		// divTop = divTop.substring(0, divTop.length - 2);
		var top = parseInt(divTop);
		document.all.linkedline.style.top = (defaultY + parentScrollTop) + "px";
	}
	
	// 如果已经隐藏了信息框则不能再滚动信息框
	if (showFlag == false)
	{
		if (fullscreen == "0")
			document.all.linkedline.style.top = (defaultY + parentScrollTop + 600) + "px";
		else
			document.all.linkedline.style.top = (defaultY + parentScrollTop + 760) + "px";
	}
}

function displayInfoFrame()
{
	var divTop = document.all.linkedline.style.top;
	// divTop = divTop.substring(0, divTop.length - 2);
	var top = parseInt(divTop);
	if (top > (defaultY + parentScrollTop))
	{
		document.all.linkedline.style.top = (top - 4) + "px";
	}
	else
	{
		document.all.linkedline.style.top = (defaultY + parentScrollTop) + "px";
		clearTimer();
	}
}


function closeLineInfoFrame()
{
	if (fullscreen == "0")
		document.getElementById('linkedline').style.top = (defaultY + parentScrollTop + 300) + "px";// 600
	else
		document.getElementById('linkedline').style.top = (defaultY + parentScrollTop + 400) + "px";// 760
	document.getElementById('linkedline').style.visibility = "hidden";
	showFlag = false;
}

// 关闭链路信息框，由 divLayer 调用
function closeLineFrame()
{
	var divTop = document.getElementById('linkedline').style.top;
	// divTop = divTop.substring(0, divTop.length - 2);
	var top = parseInt(divTop);

	if (fullscreen == "0")
	{
		if (top <= (defaultY + parentScrollTop))
		{
			closeLineInfoFrame();
		}
	}
	else
	{
		if (top <= document.body.clientHeight - frameHeight)
		{
			closeLineInfoFrame();
		}
	}
}

/** ** 物理链路信息显示 -- 结束 *** */

/** ** 搜索面板 -- 开始 *** */
// 最重要的一步就是移动主层---如何计算移动的方向和大小

var anchorPos = new Array();
anchorPos[0] = (viewWidth - 88) / 2;
anchorPos[1] = 200;
var flashTimer;
var closeAncTimer;

// var mappingIP = window.parent.bottomFrame.getMappingIP();

document.write("<div id=\"postLayer\" style=\"position:absolute;left:"+ anchorPos[0] +"px;top:"+ anchorPos[1] +"px;visibility:hidden;z-index:993;width:88px;height:60px;\">");
document.write("<table width=\"100%\" height=\"60\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
document.write("<td style=\"border:#FF0000 2px dotted\">&nbsp;</td>");
document.write("</tr></table>");
document.write("</div>");


function getNodeCoor(ip)
{
	for (var i = 0; i < nodeCoorAry.length; ++i)
	{
		var tmp = nodeCoorAry[i].split(",");
		if (tmp[0] == ip)
		{
			var coor = new Array();
			coor[0] = tmp[1];
			coor[1] = tmp[2];
			return coor;
		}
	}
	return null;
}
//yangjun add 按照id查找设备节点
function getNodeId(id)
{
	//alert("id="+id+"nodeIdAry="+nodeIdAry);
	for (var i = 0; i < nodeIdAry.length; ++i)
	{
		var tmp = nodeIdAry[i].split(",");
		if (tmp[0] == id)
		{
			var coor = new Array();
			coor[0] = tmp[1];
			coor[1] = tmp[2];
			//alert("coor="+coor);

			return coor;
		}
	}
	//alert("coor="+coor);

	return null;
}
function setMainLayerPos(x, y)
{
	document.getElementById('divLayer').style.left = x;
	document.getElementById('divLayer').style.top = y;
}

function showAnchor()
{
	clearInterval(flashTimer);
	clearTimeout(closeAncTimer);
	document.all.postLayer.style.visibility = "visible";
	flashTimer = setInterval("flashAnchor()", 500);
	closeAncTimer = setTimeout("closeAnchor()", 30*1000);
}

function flashAnchor()
{
	var visi = document.all.postLayer.style.visibility;
	if (visi == "visible")
	{
		document.all.postLayer.style.visibility = "hidden";
	}
	else
	{
		document.all.postLayer.style.visibility = "visible";
	}
}

function closeAnchor()
{
	clearInterval(flashTimer);
	clearTimeout(closeAncTimer);
	document.all.postLayer.style.visibility = "hidden";
}

// 根据节点的坐标移动主层
// 输入变量是其上的一个节点坐标，移动之后使节点位于 Anchor 内
function moveMainLayer(coor)
{
	var x = y = 0;
	coor[0] = parseInt(coor[0]);
	coor[1] = parseInt(coor[1]);
	if (coor[0] > 0)
	{
		x = coor[0] - anchorPos[0];
		x = 0 - x;
	}
	else
	{
		x = anchorPos[0] - coor[0];
	}
	
	if (coor[1] > 0)
	{
		y = coor[1] - anchorPos[1];
		y = 0 - y;
	}
	else
	{
		y = anchorPos[1] - coor[1];
	}

	setMainLayerPos(x+23, y+10);
	
	showAnchor();
}


/* 搜索面板 -- 结束 */