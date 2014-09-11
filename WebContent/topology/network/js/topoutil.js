/**
 * 判断两个设备之间是否有链路
 */
function isExistLineBetween(objEntityAry,lineArr){
	//链路集合中没有任何链路，则这两个节点之间也肯定没有链路
	if(lineArr ==null || lineArr.length==0){
		return false;
	}
	var start_id = objEntityAry[0].id.replace("node_","");
	var end_id = objEntityAry[1].id.replace("node_","");
	var line_id1 = 'line_'+start_id+'_'+end_id+'#demoline',line_id2 = 'line_'+end_id+'_'+start_id+'#demoline';
	//链路集合中有链路
	if(objEntityAry != null &&objEntityAry.length ==2){
		for(var i=0; i < lineArr.length;i++){
			var line_id = lineArr[i].id;
			if(line_id1 === line_id || line_id2 === line_id){
				return true;
			}
		}
	}else{
		alert("请选择两个节点设备！");
	}
	return false;
}

//用ps属性集设置img图片
var XHR = new function(){
	this.getInstance = function(){
		var http;
		try{
			http = new ActiveXObject("Microsoft.XMLHTTP");
		}catch(e){
			try{
				http = new XMLHttpRequest();
			}catch(e){
				alert("Error!无法创建XHR对象！");
			}
		}
		return http;
	}
	this.getInstanceBy = function(method,url,isAsyn){
		var http;
		try{
			http = new ActiveXObject("Microsoft.XMLHTTP");
		}catch(e){
			try{
				http = new XMLHttpRequest();
			}catch(e){
				alert("Error!无法创建XHR对象！");
			}
		}
		http.open(method,url,isAsyn);
		return http;
	}
};
/**
 * 显示模态窗口 并处理返回值
 * @param url
 * @param arguments
 * @param features
 */
function showModalDialogAndDo(url,arguments,features){
	var returnValue = window.showModalDialog(url,arguments,features);
	if(returnValue){
		window.location.reload();
	}
}
/**
 * 计算text的宽度
 */
function calculateXYWHByUserAgentAnd(ps){
	var t = {};
	if(isFF){
		if(ps.x){
			
			var divTextWidth = ps.divText?ps.divText.getBBox().width:30;
			var imgXYWH = getImagePropertiesBy(ps.img);
			t.x = parseInt(ps.x,10)+(parseInt(imgXYWH.w,10) - parseInt(divTextWidth,10))/2;//水平居中
		}
		if(ps.y&&ps.fs){ //text文本元素x,y为左下角，Y轴需要加上text的高度
			t.y = parseInt(ps.y,10)+parseInt(imgXYWH.h)+parseInt(ps.fs,10);
		}else if(ps.y){
			t.y = parseInt(ps.y,10)+parseInt(imgXYWH.h);
		}
		
		if(ps.w){
			t.w = parseInt(ps.w,10);
		}
		if(ps.h){
			t.h = parseInt(ps.h,10);
		}
		
	}else{
		var imgXYWH = getImagePropertiesBy(ps.img);
		console.log(ps.divText.offsetWidth);
		var divTextWidth = ps.divText?ps.divText.offsetWidth:30;
		if(ps.x){
			t.x = parseInt(ps.x,10)+(parseInt(imgXYWH.w,10) - parseInt(divTextWidth,10))/2;//水平居中
		}
		if(ps.y){
			t.y = parseInt(ps.y,10)+parseInt(imgXYWH.h,10);	
		}
		if(ps.w){
			t.w = parseInt(ps.w,10);
		}
		if(ps.h){
			t.h = parseInt(ps.h,10);
		}
		
	}
	return t;
}
/**
 * 依据平台设置元素在画布中的位置和它的长宽
 */
function setElementXYWH(elem,ps){
	if(isFF){
		if(ps.x){
			
			
			elem.setAttribute('x',parseInt(ps.x,10));
		}
		if(ps.y){ //text文本元素x,y为左下角，Y轴需要加上text的高度
			elem.setAttribute('y',parseInt(ps.y,10));
		}else if(ps.y){
			elem.setAttribute('y',parseInt(ps.y,10));
		}
		
		if(ps.w){
			elem.setAttribute('width',parseInt(ps.w,10)+'px');
		}
		if(ps.h){
			elem.setAttribute('height',parseInt(ps.h,10)+'px');
		}
		
	}else{
		if(ps.x){
			elem.style.left = parseInt(ps.x,10)+'px';
		}
		if(ps.y){
			elem.style.top = parseInt(ps.y,10)+'px';	
		}
		if(ps.w){
			elem.style.width = parseInt(ps.w,10)+'px';
		}
		if(ps.h){
			elem.style.height = parseInt(ps.h,10)+'px';
		}
		
	}
}
function setImage(img,ps){
	if(isFF){
		setElementXYWH(img,ps);
		if(ps.src){
			img.setAttributeNS('http://www.w3.org/1999/xlink','xlink:href',ps.src);
		}
	}else{
		setElementXYWH(img,ps);
		
		if(ps.src){
			img.src = ps.src;
		}
	}
}
function getImagePropertiesBy(img){
	var ps ={};
	if(isFF){
		
			ps.x = img.getAttribute('x');
		
		
			ps.y = img.getAttribute('y');
		
		
			ps.w = img.getAttribute('width');
		
		
			ps.h = img.getAttribute('height');
		
		
	}else{
		
			ps.x = img.offsetLeft;
		
		
			ps.y = img.offsetTop;	
	
		
			ps.w = img.offsetWidth;
		
		
			 ps.h = img.offsetHeight;
		
	}
	return ps;
}
//设置连线的位置、颜色、粗细
function setLine(line,p){
	if(isFF){
		if(p.x1){
			line.setAttribute("x1",p.x1);
		}
		if(p.y1){
			line.setAttribute("y1",p.y1);
		}
		if(p.x2){
			line.setAttribute("x2",p.x2);
		}
		if(p.y2){
			line.setAttribute("y2",p.y2);
		}
		if(p.stroke){
			line.setAttribute("stroke",p.stroke);
		}
		if(p.strokeWidth){
			line.setAttribute("stroke-width",p.strokeWidth);
		}
		
	}else{
		if(p.x1&&p.y1){
			line.from = p.x1+','+p.y1;
		}
		
		if(p.x2&&p.y2){
			line.to = p.x2+','+p.y2;
		}
		if(p.stroke){
			line.strokecolor = p.stroke;
		}
		if(p.strokeWidth){
			line.strokeweight = p.strokeWidth;
		}
	}
}
//根据浏览器和label创建对象
function createElementByUserAgentAnd(label){
	var vmlTags = {
			'image':'v:image',
			'line':'v:line',
			'text':'div'
	};
	var svgTags = {
			'image':'image',
				'line':'line',
				'text':'text'
	};
	var o;
	if(isFF){
		o = document.createElementNS("http://www.w3.org/2000/svg",svgTags[label]);
	}else{
		o = document.createElement(vmlTags[label]);
	}
	return o;
}
//根据浏览器，将节点标签添加到画布
function appendChild(divText){
	if(isFF){
		document.body.appendChild(divText);
	}else{
		document.getElementById('divLayer').appendChild(divText);
	}
}
function removeChild(divText){
	if(isFF){
		document.body.removeChild(divText);
	}else{
		document.getElementById('divLayer').removeChild(divText);
	}
}

//由a,b节点得到线两端点的坐标
function getCoorObjectFrom(a,b){
	var x1,y1,x2,y2;
	if(isFF){
		if(a){
			x1 =(parseInt(a.getAttribute('x')) + parseInt(a.getAttribute('width'))/2 - 3),y1=(parseInt(a.getAttribute('y')) + parseInt(a.getAttribute('height'))/2 - 3);
				
		}
		if(b){
			x2 = (parseInt(b.getAttribute('x')) + parseInt(b.getAttribute('width'))/2 - 3),y2=(parseInt(b.getAttribute('y')) + parseInt(b.getAttribute('height'))/2 - 3);
				
		}
		
	}else{
		if(a){
			x1 =(parseInt(a.style.left) + parseInt(a.style.width)/2 - 3),y1=(parseInt(a.style.top) + parseInt(a.style.height)/2 - 3);
				
		}
		if(b){
			x2 = (parseInt(b.style.left) + parseInt(b.style.width)/2 - 3),y2=(parseInt(b.style.top) + parseInt(b.style.height)/2 - 3);
				
		}
		
	}
	return {'x1':x1,'y1':y1,'x2':x2,'y2':y2};
}

//由a,b节点  得到辅助链路线  两端点的坐标
function getAssLineCoorObjectFrom(a,b){
	var x1,y1,x2,y2;
	if(isFF){
		if(a){
			x1 =(parseInt(a.getAttribute('x')) + parseInt(a.getAttribute('width'))/2 + 3),y1=(parseInt(a.getAttribute('y')) + parseInt(a.getAttribute('height'))/2 + 3);
				
		}
		if(b){
			x2 = (parseInt(b.getAttribute('x')) + parseInt(b.getAttribute('width'))/2 + 3),y2=(parseInt(b.getAttribute('y')) + parseInt(b.getAttribute('height'))/2 + 3);
				
		}
		
	}else{
		if(a){
			x1 =(parseInt(a.style.left) + parseInt(a.style.width)/2 + 3),y1=(parseInt(a.style.top) + parseInt(a.style.height)/2 + 3);
				
		}
		if(b){
			x2 = (parseInt(b.style.left) + parseInt(b.style.width)/2 + 3),y2=(parseInt(b.style.top) + parseInt(b.style.height)/2 + 3);
				
		}
		
	}
	return {'x1':x1,'y1':y1,'x2':x2,'y2':y2};
}

function cancelPropagationAndDefaultOfEvent(event){
	 if(event.stopPropagation){
	    event.stopPropagation();
	}else{
		event.cancelBubble = true;
	}
	event.preventDefault?event.preventDefault():(event.returnValue=false);
	
	return false; 
}

//IE10的oncontextmenu事件 
function addContextmenuEventListener(o,fn){
	if(isFF){//支持w3c标准
		o.addEventListener('contextmenu',fn,false);
	}else{
		o.attachEvent('oncontextmenu',fn);
	}
}

function serializeXmldocToString(){
	if(isFF){
		 return '<?xml version="1.0" encoding="GB2312"?>\n'+(new XMLSerializer()).serializeToString(xmldoc);
	}else{
		return xmldoc.xml;
	}
}

function discardSelectedLast(){
	if (objStyle != null)
	{
		unSelectImg(objStyle);
		objStyle = null;
	}
}

function zoomInit(){
	if(isFF){
		var transform = divLayer.getAttribute('transform');
		if( transform == null){
			divLayer.setAttribute('transform','scale(1.0)');
		}
	}
	else{
		if (divLayer.style.zoom == "") 
		{
			divLayer.style.zoom = 1.0;
		}
		
	}
}
function zoomPlus(){
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
}
function zoomIn(){
	
	if(isFF){
		zoomPlus();
		zoomOnFF();
		
	}
	else{
		// 放大
		if (divLayer.style.zoom != "") 
		{
			zoomPlus();
			divLayer.style.zoom = parseFloat(zoom);
		}
	}
	
}
function zoomOnFF(){
	var transform = divLayer.getAttribute('transform');
	//第一次放大，并且之前没有经过任何平移和缩放操作
	
	var  scale = 'scale('+parseFloat(zoom)+')';
	if( transform == null || transform === ""){
		divLayer.setAttribute('transform','scale('+parseFloat(zoom)+')');
	}else{
		var posReg = /scale\(.+\)/;  //提取平移的坐标  x,y
		
		var transform = posReg.test(transform)?transform.replace(posReg,scale):transform+' '+scale;
		divLayer.setAttribute('transform',transform);
	}
	
}
function zoomMinus(){
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
}
function zoomOut(){
	
	if(isFF){
			zoomMinus();
			zoomOnFF();
	}
	else{
		// 缩小
		if (divLayer.style.zoom != "") 
		{
			zoomMinus();
			divLayer.style.zoom = parseFloat(zoom);
		}
	}
	
}

function zoomRecovery(){
	if(isFF){
			
			zoom = 1.0;
			zoomOnFF();
	}
	else{
		// 复原
		if (divLayer.style.zoom != "") 
		{
			divLayer.style.zoom = 1.0;
			zoom = 1.0;
		}
	}
	
}

function currentPosition(){
	var p = {};
	if(isFF){
		var transform = divLayer.getAttribute('transform');
		//第一次移动并没有缩放操作时当前的位置
		if( transform == null || transform === ""){
			p.left = 0;
			p.top = 0;
		}else{
			var posReg = /translate\(([-]?\d+),([-]?\d+)\)/;  //提取平移的坐标  x,y
			var pos = transform.match(posReg);
			if(pos != null){
				p.left = parseInt(pos[1],10);
				p.top = parseInt(pos[2],10);
			}else{
				//移动之前，经行了放大操作
				p.left = 0;
				p.top = 0;
			}
		}
	}else{
		p.left  = parseInt(divLayer.style.left);
		p.top = parseInt(divLayer.style.top);
		
	}
	return p;
}

function moveRightByPlat(){
	if(isFF){
		var translate = 'translate('+(leftPos-speed)+','+topPos+')';
		moveOnFF(translate);
	}else{
		divLayer.style.left = (leftPos - speed)+'px';
	}
}

function moveLeftByPlat(){
	if(isFF){
		var translate = 'translate('+(leftPos + speed)+','+topPos+')';
		moveOnFF(translate);
	}else{
		divLayer.style.left = (leftPos + speed)+'px';
	}
}

function moveUpByPlat(){
	if(isFF){
		
		var translate = 'translate('+leftPos+','+(topPos + speed)+')';
		moveOnFF(translate);
	}else{
		divLayer.style.top = (topPos + speed)+'px';
	}
}
function  moveOnFF(translate){
	var transform = divLayer.getAttribute('transform');
	//第一次移动时当前的位置
	if(transform == null || transform === ""){
		divLayer.setAttribute('transform',translate);
	}else{
		var posReg = /translate\(([-]?\d+),([-]?\d+)\)/;  //提取平移的坐标  x,y
		
		var transform = posReg.test(transform)?transform.replace(posReg,translate):transform+' '+translate;
		divLayer.setAttribute('transform',transform);
	}
}
function moveDownByPlat(){
	if(isFF){
		var translate = 'translate('+leftPos+','+(topPos - speed)+')';
		moveOnFF(translate);
	}else{
		divLayer.style.top = (topPos - speed)+'px';
	}
}

function moveOriginByPlat(){
	if(isFF){
		var translate = 'translate(0,0)';
		moveOnFF(translate);
	}else{
		divLayer.style.left = '0px';// parseInt(mainX);--这是改后的，用于恢复原来的位置-----改5--
		divLayer.style.top = '0px';// parseInt(mainY);
	}
}