//可以打包为js文件;
var infoindex=0;
var x0 = 0, y0 = 0, x1 = 0, y1 = 0;
var offx = 6, offy=6;
var moveable = false;
var hover = 'orange', normal = '#336699';//color;
var index = 10000;//z-index;
//若Client浏览器为IE5.0以上版本的
var ie5=document.all&&document.getElementById;
//若Client浏览器为NetsCape6。0版本以上的
var ns6=document.getElementById&&!document.all;
//边界大小
var borderSize;
//开始拖动;
function startDrag(obj,e)
{

  if (ie5&&e.button == 1 ||ns6&&e.button == 0)
  {
    //锁定标题栏;
    
    if(obj.setCapture)
        obj.setCapture();
    else if(window.captureEvents)
        window.captureEvents(Event.MOUSEMOVE|Event.MOUSEDOWN);
   
    //定义对象;
    var win = obj.parentNode;
    
    var sha = win.nextSibling;
    
    //记录鼠标和层位置;
    x0 = e.clientX;
    y0 = e.clientY;
    x1 = parseInt(win.style.left);
    y1 = parseInt(win.style.top);
    //记录颜色;
    normal = obj.style.backgroundColor;
    //改变风格;
    obj.style.backgroundColor = hover;
    win.style.backgroundColor = hover;
    win.style.borderColor = hover;
    obj.nextSibling.style.color = hover;
    sha.style.left = x1 + offx;
    sha.style.top = y1 + offy;
    moveable = true;
    
  }
}


function subStr(str)
{
 if(str!=null&&str.length>31)
 {
  str=str.substring(0, 31)+"...";
 }
 return str;
}


function infoAutomorphism()
{
	
 if(infoindex>0)
	       {
	        for(var i=1; i<=infoindex; i++)
	        {
	        var infotable=document.getElementById("infotable"+i);
		    var infodiv=document.getElementById("infodiv"+i);
	        if(infodiv!=null&&infotable!=null)
	        {
	        	infodiv.style.width=infotable.clientWidth+6;
	        }
			
			}	
	       }   
}

//拖动;
function drag(obj,e)
{
  if (moveable)
  {
    var win = obj.parentNode;
    var sha = win.nextSibling;
    win.style.left = x1 + e.clientX - x0;
    win.style.top = y1 + e.clientY - y0;
    sha.style.left = parseInt(win.style.left) + offx;
    sha.style.top = parseInt(win.style.top) + offy;
  }
}
//停止拖动;
function stopDrag(obj)
{
  if (moveable)
  {
    var win = obj.parentNode;
    var sha = win.nextSibling;
    var msg = obj.nextSibling;
    win.style.borderColor = normal;
    win.style.backgroundColor = normal;
    obj.style.backgroundColor = normal;
    msg.style.color = normal;
    sha.style.left = obj.parentNode.style.left;
    sha.style.top = obj.parentNode.style.top;
   
    if(obj.releaseCapture)
        obj.releaseCapture();
       else if(window.captureEvents)
        window.captureEvents(Event.MOUSEMOVE|Event.MOUSEUP);
    
    moveable = false;
  }
}
//获得焦点;
function getFocus(obj)
{
  if (obj.style.zIndex != index)
  {
    index = index + 2;
    var idx = index;
    obj.style.zIndex = idx;
    obj.nextSibling.style.zIndex = idx - 1;
  }
}

function minInfo(obj)
{
 var win = obj.parentNode.parentNode;
  var sha = win.nextSibling;
  var tit = obj.parentNode;
  var msg = tit.nextSibling;
  var flg = msg.style.display=="none";
  if (flg)
  {
    //win.style.height = parseInt(msg.style.height) + parseInt(tit.style.height) + 2 * 2;
    //sha.style.height = win.style.height;
    msg.style.display = "block";
    obj.innerHTML = "0";
  }
  else
  {
    win.style.height = parseInt(tit.style.height) + 2 * 2;
    sha.style.height = win.style.height;
    obj.innerHTML = "2";
    msg.style.display = "none";
  }
}


//最小化;
function min(obj)
{
  var win = obj.parentNode.parentNode;
  var sha = win.nextSibling;
  var tit = obj.parentNode;
  var msg = tit.nextSibling;
  var flg = msg.style.display=="none";
  alert(flg);
  if (flg)
  {
    win.style.height = parseInt(msg.style.height) + parseInt(tit.style.height) + 2 * 2;
    sha.style.height = win.style.height;
    alert("trueend");
    msg.style.display = "block";
    obj.innerHTML = "0";
    
  }
  else
  {
    win.style.height = parseInt(tit.style.height) + 2 * 2;
    sha.style.height = win.style.height;
     alert("falseend1");
    obj.innerHTML = "2";
    msg.style.display = "none";
   
  }
}
//创建一个对象;
function xWin(id, w, h, l, t, tit, msg)
{
  index = index + 2;
  this.id = id;
  this.width = w;
  this.height = h;
  this.left = l;
  this.top = t;
  this.zIndex = index;
  this.title = tit;
  this.message = msg;
  this.obj = null;
  if (ie5)
      borderSize = 0;
  else
      borderSize = 6;
  
  this.bulid = bulid;
  this.bulid();
}

function hiddenInfo(itemId)
{
	//infoindex--;
var infoid="";
 for(var i=0; i<infoArray.length; i++)
 {
  if(infoArray[i][0]==itemId)
  {
   infoArray[i][1]='false';
   infoid=infoArray[i][2];
   break;
  }
 }
 document.getElementById(infoid).style.display="none";
 var temp=new ActiveXObject('Microsoft.XMLDOM');
	  temp.load(Topology.doc);
	  var tempdoc=temp.documentElement;
	  var info=tempdoc.childNodes(3);
	  for(var k=0;k<info.childNodes.length; k++)
	  {
	   var tempid=info.childNodes[k].getAttribute("neId");
	   if(tempid==infoid)
	   {
	    info.removeChild(info.childNodes[k]);
	    Topology.doc=temp;
	    break;
	   }
	  }
}

function recreatInfo(root1)
{
	var choldinfo=root1.childNodes;

	for(var k=0;k<choldinfo.length; k++)
	{
		var root=choldinfo[k];
	infoindex++;
  
      var neid=root.getAttribute("neId");
     
     var parentItemid=root.getAttribute("parentId");
    
     var equipOfLink="";
     if(topology.findEquip(parentItemid)!=null)
     {
      equipOfLink="equip";
     }else
     {
      equipOfLink="link";
     }
      var temp=new ActiveXObject('Microsoft.XMLDOM');
	  temp.load(Topology.doc);
	  var tempdoc=temp.documentElement;
	  var info=tempdoc.childNodes(3);
      var tempi = temp.createElement("i");
	  tempi.setAttribute("neId", neid);
	  tempi.setAttribute("parentId", parentItemid);
	  info.appendChild(tempi);
	  Topology.doc=temp;
     for(var i=0; i<infoArray.length; i++)
     {
      if(infoArray[i][0]==parentItemid)
     {
      infoArray[i][1]='true';
      infoArray[i][2]=neid;
      break;
     }
     }
     var tempequip=document.getElementById(parentItemid);
     var tempx=tempequip.style.left;
     var tempy=tempequip.style.top;
     var objX=parseInt(tempx.replace("px",""));
     var objY=parseInt(tempy.replace("px",""));
     objX+=50;
     objY+=50;
     if(isNaN(objX))
     {
      var from=tempequip.from;
      var to=tempequip.to;
      objX=(from.x+to.x)/2+80;
      objY=(from.y+to.y)/2+50;
     }
     
     var indicators=root.childNodes[0].childNodes;
	 var pros=root.childNodes[1].childNodes;
     var proInfo="";
	 var indicatorInfo="";
	 
     for(var i=0;i<indicators.length;i++)
	 {
		var indicatorName=indicators[i].getAttribute('name');
		var indicatorValue=indicators[i].getAttribute('value');
		indicatorInfo+='<tr><td align="left" nowrap>'+indicatorName+'&nbsp;</td><td nowrap>'+indicatorValue+'</td></tr>\n';	
	 } 
		
     for(var i=0;i<pros.length;i++)
	 {
			 var proshow=pros[i].getAttribute('show');
			
			 if(proshow=='true')
			 {
			   var proName=pros[i].getAttribute('name');
			   var proValue=pros[i].getAttribute('value');
		       proInfo+='<tr><td align="left" nowrap>'+proName+'&nbsp;</td><td nowrap>'+proValue+'</td></tr>\n';
			 }
			 	
	}
	if(equipOfLink=="equip")
	{
	 closeinfostr='closeEquipInfo(\''+parentItemid+'\')';
	}else if(equipOfLink=="link")
	{
	 closeinfostr='closeLinkInfo(\''+parentItemid+'\')';
	}
	var massage="<table id='infotable"+infoindex+"'>";
	massage+=proInfo+indicatorInfo+"</table>";     
     var html="";
     html+="<div id="+neid+" name="+parentItemid+" style='"
     + "z-index:10002;left:"+objX+";"
     +"top:"+objY+";"
     +"background-color:#336699;"
     +"color:#336699;"
     +"font-size:8pt;"
     +"font-family:Tahoma;"
     +"position:absolute;"
     +"cursor:default;"
     +"border:2px solid #336699;"
     + "' "
     + "onmousedown='getFocus(this)'>"
      + "<div id='infodiv"+infoindex+"'"
     + "style='"
     + "background-color:#336699;"
     //+ "width:280;"
     + "height:20;"
     + "color:white;"  
     + "' "
     + "onmousedown='startDrag(this,event)' "
     + "onmouseup='stopDrag(this)' "
     + "onmousemove='drag(this,event)' "
     + ">"
     + "<span style='width:12px;position:relative;border-width:0px;color:white;font-family:webdings;' onclick='minInfo(this)'>0</span>"
    + "<span style='width:12px;position:relative;border-width:0px;color:white;font-family:webdings;' onclick="+closeinfostr+">r</span>"
     + "</div>"
     + "<div id=inner"+neid+" style='"
     + "width:" + (this.width-borderSize) + ";"
     + "height:" + (this.height-20-borderSize) + ";"
     + "background-color:white;"
     + "line-height:14px;"
     + "word-break:break-all;"
     + "padding:3px;overflow: auto;    scrollbar-3dlight-color : #dddddd;"
     + "scrollbar-arrow-color :#363636;"
     + "scrollbar-base-color : #333333;"
     + "scrollbar-darkshadow-color : #dddddd;"
     + "scrollbar-face-color : #eeeeee;"
     + "scrollbar-shadow-color : #999999;"
     + "scrollbar-track-color : #dddddd;"
     + "'>" + massage + "</div>"
     + "</div>"
     + "<div bg style='"
     + "width:" + this.width + ";"
     + "height:" + this.height + ";"
     + "top:" + this.top + ";"
     + "left:" + this.left + ";"
     + "z-index:" + (this.zIndex-1) + ";"
     + "position:absolute;"
     + "background-color:black;"
     + "filter:alpha(opacity=40);"
     + "'></div>";
     var printArea=document.getElementById("printArea");
		        printArea.innerHTML+=html;
		        infoAutomorphism();  
	}
		      
}

function creatInfo(temp)
{

  var dom = new ActiveXObject("microsoft.XMLDOM");
      dom.loadXML(temp);
       var root=dom.documentElement;
      
     if(root==null)
     {
     	alert("该资源已经打开过文本框");
     	return false;
     }
     infoindex++;
     var neid=root.getAttribute("neId");
     var parentItemid=root.getAttribute("itemId");
     
     var equipOfLink="";
     if(topology.findEquip(parentItemid)!=null)
     {
      equipOfLink="equip";
     }else
     {
      equipOfLink="link";
     }
     
     
      var temp=new ActiveXObject('Microsoft.XMLDOM');
	  temp.load(Topology.doc);
	  var tempdoc=temp.documentElement;
	  var info=tempdoc.childNodes(3);
      var tempi = temp.createElement("i");
      
	  tempi.setAttribute("neId", neid);
	  tempi.setAttribute("parentId", parentItemid);
	  
	  info.appendChild(tempi);
	  Topology.doc=temp;

      
      
    
     
     for(var i=0; i<infoArray.length; i++)
     {
      if(infoArray[i][0]==parentItemid)
     {
      infoArray[i][1]='true';
      infoArray[i][2]=neid;
      break;
     }
     }
     var tempequip=document.getElementById(parentItemid);
     var tempx=tempequip.style.left;
     var tempy=tempequip.style.top;
     var objX=parseInt(tempx.replace("px",""));
     var objY=parseInt(tempy.replace("px",""));
     objX+=50;
     objY+=50;
     if(isNaN(objX))
     {
      var from=tempequip.from;
      var to=tempequip.to;
      objX=(from.x+to.x)/2+80;
      objY=(from.y+to.y)/2+50;
     }
     
     var indicators=root.childNodes[0].childNodes;
	 var pros=root.childNodes[1].childNodes;
     var proInfo="";
	 var indicatorInfo="";
	 
     for(var i=0;i<indicators.length;i++)
	 {
		var indicatorName=indicators[i].getAttribute('name');
		var indicatorValue=indicators[i].getAttribute('value');
		indicatorInfo+='<tr><td align="left" nowrap>'+indicatorName+'&nbsp;</td><td nowrap>'+indicatorValue+'</td></tr>\n';	
	 } 
		
     for(var i=0;i<pros.length;i++)
	 {
			 var proshow=pros[i].getAttribute('show');
			
			 if(proshow=='true')
			 {
			   var proName=pros[i].getAttribute('name');
			   var proValue=pros[i].getAttribute('value');
		       proInfo+='<tr><td align="left" nowrap>'+proName+'&nbsp;</td><td nowrap>'+proValue+'</td></tr>\n';
			 }
			 	
	}
	if(equipOfLink=="equip")
	{
	 closeinfostr='closeEquipInfo(\''+parentItemid+'\')';
	}else if(equipOfLink=="link")
	{
	 closeinfostr='closeLinkInfo(\''+parentItemid+'\')';
	}
	var massage="<table id='infotable"+infoindex+"'>";
	massage+=proInfo+indicatorInfo+"</table>";     
     var html="";
     html+="<div id="+neid+" name="+parentItemid+" style='"
     + "z-index:10002;left:"+objX+";"
     +"top:"+objY+";"
     +"background-color:#336699;"
     +"color:#336699;"
     +"font-size:8pt;"
     +"font-family:Tahoma;"
     +"position:absolute;"
     +"cursor:default;"
     +"border:2px solid #336699;"
     + "' "
     + "onmousedown='getFocus(this)'>"
      + "<div id='infodiv"+infoindex+"'"
     + "style='"
     + "background-color:#336699;"
     //+ "width:280;"
     + "height:20;"
     + "color:white;"  
     + "' "
     + "onmousedown='startDrag(this,event)' "
     + "onmouseup='stopDrag(this)' "
     + "onmousemove='drag(this,event)' "
     + ">"
     + "<span style='width:12px;position:relative;border-width:0px;color:white;font-family:webdings;' onclick='minInfo(this)'>0</span>"
    + "<span style='width:12px;position:relative;border-width:0px;color:white;font-family:webdings;' onclick="+closeinfostr+">r</span>"
     + "</div>"
     + "<div id=inner"+neid+" style='"
     + "width:" + (this.width-borderSize) + ";"
     + "height:" + (this.height-20-borderSize) + ";"
     + "background-color:white;"
     + "line-height:14px;"
     + "word-break:break-all;"
     + "padding:3px;overflow: auto;    scrollbar-3dlight-color : #dddddd;"
     + "scrollbar-arrow-color :#363636;"
     + "scrollbar-base-color : #333333;"
     + "scrollbar-darkshadow-color : #dddddd;"
     + "scrollbar-face-color : #eeeeee;"
     + "scrollbar-shadow-color : #999999;"
     + "scrollbar-track-color : #dddddd;"
     + "'>" + massage + "</div>"
     + "</div>"
     + "<div bg style='"
     + "width:" + this.width + ";"
     + "height:" + this.height + ";"
     + "top:" + this.top + ";"
     + "left:" + this.left + ";"
     + "z-index:" + (this.zIndex-1) + ";"
     + "position:absolute;"
     + "background-color:black;"
     + "filter:alpha(opacity=40);"
     + "'></div>";
     var printArea=document.getElementById("printArea");
		        printArea.innerHTML+=html;
		infoAutomorphism();        
} 


//初始化;
function bulid()
{
  var str = ""
    + "<div id=xMsg" + this.id + " "
    + "style='"
    + "z-index:" + this.zIndex + ";"
    + "width:" + this.width + ";"
    //+ "height:" + this.height + ";"
    + "left:" + this.left + ";"
    + "top:" + this.top + ";"
    + "background-color:" + normal + ";"
    + "color:" + normal + ";"
    + "font-size:8pt;"
    + "font-family:Tahoma;"
    + "position:absolute;"
    + "cursor:default;"
    + "border:2px solid " + normal + ";"
    + "' "
    + "onmousedown='getFocus(this)'>"
    + "<div "
    + "style='"
    + "background-color:" + normal + ";"
    + "width:" + (this.width) + ";"
    + "height:20;"
    + "color:white;"
    //+ "text-align:right;"    
    + "' "
    + "onmousedown='startDrag(this,event)' "
    + "onmouseup='stopDrag(this)' "
    + "onmousemove='drag(this,event)' "
    + "ondblclick='minInfo(this.childNodes[1])'"
    + ">"
    //+ "<span style='width:" + (this.width-2*12-4) + "px;padding-left:3px;'>" + this.title + "</span>"
    + "<span style='width:50px;padding-left:3px;'>" + this.title + "</span>"    
    + "<span style='width:12px;position:relative;left:190px;border-width:0px;color:white;font-family:webdings;' onclick='minInfo(this)'>0</span>"
    + "<span style='width:12px;position:relative;left:205px;border-width:0px;color:white;font-family:webdings;' onclick='ShowHide(\""+this.id+"\",null)'>r</span>"
    + "</div>"
    + "<div style='"
   
    + "background-color:white;"
    + "line-height:14px;"
    + "word-break:break-all;"
    + "padding:3px;overflow: auto;    scrollbar-3dlight-color : #dddddd;"
    + "scrollbar-arrow-color :#363636;"
    + "scrollbar-base-color : #333333;"
    + "scrollbar-darkshadow-color : #dddddd;"
    + "scrollbar-face-color : #eeeeee;"
    + "scrollbar-shadow-color : #999999;"
    + "scrollbar-track-color : #dddddd;"
    + "'>" + this.message + "</div>"
    + "</div>"
    + "<div id=xMsg" + this.id + "bg style='"
  
    + "top:" + this.top + ";"
    + "left:" + this.left + ";"
    + "z-index:" + (this.zIndex-1) + ";"
    + "position:absolute;"
    + "background-color:black;"
    + "filter:alpha(opacity=40);"
    + "'></div>";
  document.body.innerHTML+= str;
}
//显示隐藏窗口
function ShowHide(id, dis)
{
  var bdisplay = (dis == null)?((document.getElementById("xMsg"+id).style.display=="")?"none":""):dis
  document.getElementById("xMsg"+id).style.display = bdisplay;
  document.getElementById("xMsg"+id+"bg").style.display = bdisplay;
}
function initialize()
{
  var content='<table ondragend="Topology.handleDropEvent(event)" ondrop="Topology.handleDropEvent(event)" class="table_List">'
		+ '<tr>'
		+ '<td align="left">图元类型：'
		+ '<select>'
		+ '<option>设备</option>'
		+ '<option>应用</option>'
		+ '<option>服务</option>'
		+ '<option>资源集</option>'
		+ '<option>业务</option>'
		+ '<option>监视器</option>'
		+ '</select>'
		+ '<td>'
		+ '</tr>'
		+ '<tr>'
		+ '<td>'
		+ '<table width="200">'
		+ '<tr>'
		+ '<td width="20%"><img src="../img/switch.gif"  alt="交换机"/></td>'
		+ '<td width="20%"><img src="../img/router.gif"  alt="路由器"/></td>'
		+ '<td width="20%"><img src="../img/3switch.gif"  alt="三层交换机"/></td>'
		+ '<td width="20%"><img   src="../img/pc.gif" alt="主机"/></td>'
		+ '<td width="20%"><img src="../img/server.gif"  alt="服务器"/></td>'
		+ '</tr>'	
		+ '</table>'
		+ '</td>'
		+ '</tr>'
		+ '</table>'
		+ '<br>'
		+ '<table width="200" align="center">'
		+ '<tr>'
		+ '<td align="center">'
		+ '<a class="button_default_long"  href="#" onclick="return false;">添 加</a>'	
		+ '&nbsp&nbsp'
		+ '<a class="button_default_long"  href="#" onclick=\'ShowHide("1","none");\'>取 消</a>'
		+ '<textarea  id="debug" name="textarea" style="display:none" rows="20"></textarea>'
		+ '</td>'
		+ '</tr>'
		+ '</table>';
			
  //var c = new xWin("3",750,700,140,10,"个人简介","Copyright<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/> by 网页一班!");
  var a = new xWin("1",280,180,200,200,"新增图元",gallery);
  //var b = new xWin("2",240,200,100,100,"窗口2","Welcome to visited my personal website:<br/><a href=http://www.skyapi.com target=_blank>http://www.skyapi.com</a><br/>anducanalsosignmyguestbookat:<br/><ahref=http://www.skyapi.com</a><br/><br/>thx!!!=)..."); 
  
  ShowHide("1","none");
  //ShowHide("2","none");
  //ShowHide("3","none");//隐藏窗口1
  //center('1');
  //center('2');center('3');
}
//window.onload = initialize;
function center(id)
{
xwin = document.getElementById("xMsg"+id);
var wleft = (document.body.clientWidth-xwin.style.width.replace("px",""))/2 < 0 ? 0 : (document.body.clientWidth-xwin.style.width.replace("px",""))/2;
var wtop = (document.body.clientHeight-xwin.style.height.replace("px",""))/2 < 0 ? 0 : (document.body.clientHeight-xwin.style.height.replace("px",""))/2;;

xwin.style.left = wleft;
xwin.style.top  = wtop;

xwinbg = document.getElementById("xMsg"+id+"bg");
xwinbg.style.left = wleft;
xwinbg.style.top  = wtop;
}