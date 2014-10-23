// Node object
function TreeNode(id, name , descr , pid) {
	
	// id
	this.id = id;
	
	// name
	this.name = name;
	
	// descr 描述
	this.descr = descr;

	// 父 id
	this.pid = pid;
	
	// 是否具有子节点
	this.isHadChild = false;
	
	// 是否展开
	this.isOpen = false;
	
	// 是否选中
	this.isSelected = false;
	
	// 所有的子节点
	this.childNodes = null;
	
	// 节点的等级
	this.level = null;
	
	// 是否为父节点的最后子节点
	this.isEndNode = true;
	
}

// 右键菜单 object
function MenuItem(id , name , img , onclick){
	// id
	this.id = id;
	
	// name
	this.name = name;
	
	// 图片
	this.img = img;
	
	// 点击事件
	this.onclick = onclick;
}


// Tree object
function Tree(){
	// 数据数组
	this.dataArray = new Array();

	// 所有节点
	this.nodeArray = new Array();
	
	// 所有右键菜单的列表
	this.menuItemArray = new Array();
	
	// 树所在DIV
	this.treeDiv = null;
	
	// 根节点
	this.rootNode = null;
	
	// 节点click事件
	this.nodeClick = null;
	
	// 节点contextmenu事件
	this.nodeContextmenu = null;
	
	// 右键菜单
	this.contextMenuItemDiv = null;
	
	// 图片路径
	this.imPath = '';

}

Tree.prototype = {
	// 初始化方法
	init:function(dataArray){
		
		this.dataArray = dataArray;
	
		// 创建所有节点
		for(var i = 0 ; i < this.dataArray.length ; i++){
		
			var data = this.dataArray[i];
			
			var node = new TreeNode(data[0] , data[1] , data[2] , data[3]);
			
			this.nodeArray[i] = node;
			
		}
		
		for(var i = 0 ; i < this.nodeArray.length ; i++){
			var perNode = this.nodeArray[i];
			perNode.childNodes = this.getChildNodes(perNode);
			perNode.isHadChild = this.isHadChild(perNode);
		}
		
		
		
	},

	
	show: function(treeDivId){
		if(document.getElementById){
			this.treeDiv = document.getElementById(treeDivId);
		}else{
			alert('浏览器不支持');
			return;
		}
		
		if(!this.treeDiv){
			alert('该DIV不存在 , 其提供一个DIV');
			return;
		}
		this.treeDiv.className = 'businessTree';
		this.rootNode = this.getRootNode();
		this.recursiveCreateTree(this.rootNode , null);
	},
	
	// 使用  id , 以及 tagName 来创建一个element
	createElement: function (id , tagName){
		var element = document.createElement(tagName);
		element.id = id;
		return element;
	},
	
	

	// 是否具有子节点
	isHadChild:function(node){
		var result = false;
		for(var i = 0 ; i< this.nodeArray.length ; i++){
			var perNode = this.nodeArray[i];
			if(node.id == perNode.pid) {
				result = true;
				break;
			}
		}
		return result;
	},
	
	// 获取节点的父节点
	getFatherNode:function(node){
		var fatherNode = null;
		for(var i = 0 ; i < this.nodeArray.length ; i++){
			var perNode = this.nodeArray[i];
			if(node.pid == perNode.id){
				fatherNode = perNode;
			}
		}
		
		return fatherNode;
	},
	
	// 获取节点的所有子节点
	getChildNodes: function(node){
		var childNodes = new Array();
		for(var i = 0 ; i< this.nodeArray.length ; i++){
			var perNode = this.nodeArray[i];
			if(node.id == perNode.pid) {
				childNodes.push(perNode);
			}
		}
		return childNodes;
	},
	
	
	// 获取根节点
	getRootNode: function(){
		var node = null;
		for(var i = 0 ; i < this.nodeArray.length ; i++){
			var perNode = this.nodeArray[i];
			if(perNode.pid == '0'){   // 如果 PID 为 0 则为根节点
				node = perNode;
				break;
			}
		}
		return node;
	},
	
	// 递归创建树 需要 节点 和  父节点
	recursiveCreateTree:function(node , parentNode){
		
		if(node == null){
			return;
		}
		// 创建节点 DIV
		var nodeDiv = this.createNodeDiv(node);
		// 获取父节点的 nodeChildDiv
		var parentNodeChildDiv = this.getParentNodeChildDiv(parentNode);
		// 添加节点 DIV
		this.addNodeDiv(nodeDiv, parentNodeChildDiv);
		var childNodes = node.childNodes;
		if(childNodes){
			for(var i = 0 ; i < childNodes.length ; i++){
				var perChildNode = childNodes[i];
				
				if( i < childNodes.length -1){
					perChildNode.isEndNode = false;
				}
				this.recursiveCreateTree(perChildNode , node);
			}
		}
	},
	
	// 获取父节点的 nodeChildDiv
	getParentNodeChildDiv:function(parentNode){
		var parentNodeChildDiv = null;
		if(parentNode){
			parentNodeChildDiv = document.getElementById('nodeChildDiv-' + parentNode.id);
		}else{
			parentNodeChildDiv = this.treeDiv;
		}
		return parentNodeChildDiv;
	},
	
	// 添加节点 DIV
	addNodeDiv:function(nodeDiv, parentDiv){
		if(parentDiv){
			// 如果父节点 DIV 存在,则将节点的 DIV 加入到parentDiv
			parentDiv.appendChild(nodeDiv);
		}else{
			// 否则加入 treeDiv
			this.treeDiv.appendChild(nodeDiv);
		}
		
		
	},
	
	// 创建节点 DIV
	createNodeDiv:function(node){
		var nodeDiv = this.createElement('nodeDiv-' + node.id , 'DIV');
		var nodeDataDiv = this.createNodeDataDiv(nodeDiv , node);
		var nodeChildDiv = this.createNodeChildDiv(nodeDiv, node);
		nodeDiv.appendChild(nodeDataDiv);
		nodeDiv.appendChild(nodeChildDiv);
		
		nodeDiv.className = this.getNodeDivClassName();
		return nodeDiv;
	},
	
	
	
	// 创建节点数据 DIV
	createNodeDataDiv:function(nodeDiv , node){
		var nodeDataDiv = this.createElement('nodeDataDiv-' + node.id , 'DIV');
		
		var imgDiv = this.createNodeDataDivImgDiv(node);
		
		if(imgDiv){
			nodeDataDiv.appendChild(imgDiv);
		}
		
		if(node.isHadChild){
			// 如果有子节点 则创建含有子节点的节点图标  其中包括显示和隐藏子节点的图标  文件夹打开和关闭的图标
			var imgDiv2 = this.createHadChildNodeDivImgDiv(node);
			if(imgDiv2){
				nodeDataDiv.appendChild(imgDiv2);
			}
		}else{
			// 如果没有子节点 则创建没有含有子节点的图标 其中包括折线 和 文件图标
			var imgDiv2 =this.createHadNoChildNodeDivImgDiv(node);
			if(imgDiv2){
				nodeDataDiv.appendChild(imgDiv2);
			}
			
		}
		
		// 创建节点的Name的Div
		var nodeNameDiv = this.createNodeNameDiv(node);
		nodeDataDiv.appendChild(nodeNameDiv);
		
		nodeDataDiv.className = this.getNodeDataDivClassName();
		
		return nodeDataDiv;
	},
	
	// 创建节点的子节点 DIV
	createNodeChildDiv:function(nodeDiv , node){
		var nodeChildDiv = this.createElement('nodeChildDiv-' + node.id , 'DIV');
		if(node.isOpen){
			nodeChildDiv.style.display = "inline";
		}else{
			nodeChildDiv.style.display = "none";
		}
		
		nodeChildDiv.className = this.getNodeChildDivClassName();
		return nodeChildDiv;
	},
	
	// 创建节点名称Div
	createNodeNameDiv:function(node){
		var nodeNameDiv = this.createElement('nodeDataDiv-name-' + node.id, 'DIV');
		var nodeNameA = this.createElement('nodeDataDiv-name-A-' + node.id , 'A')
		nodeNameA.innerText = node.name;
		nodeNameA.name = "nodeDataDiv-name-A";
		
		
		var nodeClick = this.getNodeClick();
		
		var tree = this;
		
		if(nodeClick){
			nodeNameA.onclick = function(){
				nodeClick.call(tree , nodeNameA , node);
			};
		}
		
		var nodeContextmenu = this.getNodeContextmenu();
		
		var tree = this;
		
		if(nodeContextmenu){
			nodeNameA.oncontextmenu = function(){
				nodeContextmenu.call(nodeNameA , tree , node);
				return false;
			}
			
		}
		
		
		nodeNameDiv.appendChild(nodeNameA);
		
		
		
		return nodeNameDiv;
	},
	
	// 创建节点数据前竖线和空白的图片div
	createNodeDataDivImgDiv:function(node){
		
		var fatherNode = this.getFatherNode(node);
		if(!fatherNode){
			// 如果 fatherNode 为空 则是根节点 不需要创建
			return null;
		}
		
		var imgDiv = this.createElement('nodeDataDiv-imgDiv-' + node.id , 'DIV');
		
		// 获取父节点的竖线和空白图片的div
		var fatherNodeImgDiv = document.getElementById('nodeDataDiv-imgDiv-' + node.pid);
		if(fatherNodeImgDiv){
			// 将父节点的竖线和空白图片的div 进行复制
			imgDiv.innerHTML = fatherNodeImgDiv.innerHTML;
		}
		
		var img = this.createElement(null , 'img');
		if(!fatherNode.isEndNode){
			// 如果 父节点不是最后一个 即此节点有叔叔节点 则创建 竖线图片
			img.src = this.getLineImg();
		}else{
			// 如果 父节点是最后一个节点 则创建 空白图片
			img.src = this.getBlankImg();
		}
		imgDiv.appendChild(img);
		
		imgDiv.className = this.getNodeDataDivImgDivClassName();
		return imgDiv;
		
		
	},
	
	// 创建含有子节点的节点图标  其中包括显示和隐藏子节点的图标  文件夹打开和关闭的图标
	createHadChildNodeDivImgDiv:function(node){
		var imgDiv = this.createElement('nodeDataDiv-imgDiv-hadChildNode-' + node.id , 'DIV');
		
		// 如果有子节点  创建显示子节点的图标
		var showOrHideNodeChildDivImg = this.createShowOrHideNodeChildDivImg(node,this.showOrHideNodeChildDiv);
		imgDiv.appendChild(showOrHideNodeChildDivImg);
		
		// 创建 文件夹图标 
		var nodeFolderImg = this.createNodeFolderImg(node);
		imgDiv.appendChild(nodeFolderImg);
		
		imgDiv.className = this.getHadChildNodeDivImgDivClassName();
		
		return imgDiv;
	},
	
	// 创建节点的显示子节点图标 参数： node 节点    onClickCallback 按钮点击事件处理的回调函数
	createShowOrHideNodeChildDivImg:function(node,onClickCallback){
		// 创建图标
		var img = this.createElement('showOrHideNodeChildDivImg-' + node.id, 'img');
		if(node.isOpen){
			if(node.id == this.rootNode.id){
				img.src = this.getRootNodeOpenImg();
			}else{
				if(node.isEndNode){
					img.src = this.getNotRootAndIsEndNodeOpenImg();
				}else{
					img.src = this.getNotRootAndNotEndNodeOpenImg();
				}
			}
		}else{
			if(node.id == this.rootNode.id){
				img.src = this.getRootNodeClosedImg();
			}else{
				if(node.isEndNode){
					img.src = this.getNotRootAndIsEndNodeClosedImg();
				}else{
					img.src = this.getNotRootAndNotEndNodeClosedImg();
				}
			}
		}
		var tree = this;
		img.onclick = function(){
			onClickCallback.call(img, node , tree);
		};
		return img;
	},
	
	// 显示或隐藏 子节点DIV
	showOrHideNodeChildDiv:function(node , tree){
		var nodeChildDiv = document.getElementById("nodeChildDiv-" + node.id);
		var nodeFolderImg = document.getElementById("nodeFolderImg-" + node.id);
		if(node.isOpen){
			// 如果打开 则隐藏
			node.isOpen = false;
			if(nodeChildDiv){
				nodeChildDiv.style.display = "none";
			}
			if(nodeFolderImg){
				nodeFolderImg.src = tree.getFolderClosedImg();
			}
			if(node.id == tree.rootNode.id){
				this.src = tree.getRootNodeClosedImg();
			}else{
				if(node.isEndNode){
					this.src = tree.getNotRootAndIsEndNodeClosedImg();
				}else{
					this.src = tree.getNotRootAndNotEndNodeClosedImg();
				}
			}
			
		}else{
			// 如果隐藏 则打开
			node.isOpen = true;
			if(nodeChildDiv){
				nodeChildDiv.style.display = "inline";
			}
			if(nodeFolderImg){
				nodeFolderImg.src = tree.getFolderOpenImg();
			}
			if(node.id == tree.rootNode.id){
				this.src = tree.getRootNodeOpenImg();
			}else{
				if(node.isEndNode){
					this.src = tree.getNotRootAndIsEndNodeOpenImg();
				}else{
					this.src = tree.getNotRootAndNotEndNodeOpenImg();
				}
			}
		}
	},
	
	// 创建文件夹图标
	createNodeFolderImg:function(node){
		var nodeFolderImg = this.createElement("nodeFolderImg-" + node.id, "img");
		if(node.isOpen){
			nodeFolderImg.src = this.getFolderOpenImg();
		}else{
			nodeFolderImg.src = this.getFolderClosedImg();
		}
		return nodeFolderImg;
	},
	
	
	createHadNoChildNodeDivImgDiv:function(node){
		var imgDiv = this.createElement('nodeDataDiv-imgDiv-noChildNode-' + node.id , 'DIV');
		
		// 创建 折线图标
		var brokenLineImg = this.createBrokeLineImg(node);
		imgDiv.appendChild(brokenLineImg);
		
		// 创建 文件图标
		var leafImg = this.createLeafImg(node);
		imgDiv.appendChild(leafImg);
		
		imgDiv.className = this.getHadNoChildNodeDivImgDivClassName();
		
		return imgDiv;
	},
	
	// 创建 折线图标
	createBrokeLineImg:function(node){
		var brokenLineImg = this.createElement('brokenLineImg-' + node.id , 'img');
		if(node.isEndNode){
			// 如果为最后节点
			brokenLineImg.src = this.getIsEndBrokenLineImg();
		}else{
			// 否则
			brokenLineImg.src = this.getNotEndBrokenLineImg();
		}
		
		return brokenLineImg;
	},
	
	// 创建 文件图标
	createLeafImg:function(node){
		var leafImg = this.createElement('leafImg-' + node.id , 'img');
		leafImg.src = this.getLeafImg();
		return leafImg;
	},
	
	setImgPath:function(imPath){
		this.imPath = imPath;
	},
	
	getImgPath:function(){
		return this.imPath;
	},
	
	// 获取竖线图标
	getLineImg:function(){
		return this.getImgPath() + "line1.gif";
	},
	
	// 获取竖线图标
	getBlankImg:function(){
		return this.getImgPath() + "blank.gif";
	},
	
	// 获取根节点打开的图标
	getRootNodeOpenImg:function(){
		return  this.getImgPath() + "minus5.gif";
	},
	
	// 获取根节点关闭的图标
	getRootNodeClosedImg:function(){
		return  this.getImgPath() + "plus5.gif";
	},
	
	// 获取不是最后节点的非根节点打开的图标
	getNotRootAndNotEndNodeOpenImg:function(){
		return  this.getImgPath() + "minus3.gif";
	},
	
	// 获取是最后节点但不是根节点打开的图标
	getNotRootAndIsEndNodeOpenImg:function(){
		return  this.getImgPath() + "minus2.gif";
	},
	
	// 获取不是最后节点的非根节点关闭的图标
	getNotRootAndNotEndNodeClosedImg:function(){
		return  this.getImgPath() + "plus3.gif";
	},
	
	// 获取是最后节点但不是根节点关闭的图标
	getNotRootAndIsEndNodeClosedImg:function(){
		return  this.getImgPath() + "plus2.gif";
	},
	
	// 获取文件夹打开的图标
	getFolderOpenImg:function(){
		return  this.getImgPath() + "folderOpen.gif";
	},
	
	// 获取文件夹关闭的图标
	getFolderClosedImg:function(){
		return  this.getImgPath() + "folderClosed.gif";
	},
	
	// 获取不是最后节点的无子节点的折线图标
	getNotEndBrokenLineImg:function(){
		return  this.getImgPath() + "line3.gif";
	},
	
	// 获取是最后节点的无子节点的折线图标
	getIsEndBrokenLineImg:function(){
		return  this.getImgPath() + "line2.gif";
	},
	
	// 获取文件图标
	getLeafImg:function(){
		return this.getImgPath() + "leaf.gif";
	},
	
	// 获取文件图标
	getMenuItemBackgroundImage:function(){
		return this.getImgPath() + "menubg.jpg";
	},
	
	getImgSrcByImgName:function(name){
		return this.getImgPath() +　name;
	},
	
	// 获取节点Div的样式  nodeDiv
	getNodeDivClassName:function(){
		return 'nodeDiv';
	},
	
	// 获取节点数据的Div的样式
	getNodeDataDivClassName:function(){
		return 'nodeDataDiv';
	},
	
	// 获取节点的子节点的Div的样式
	getNodeChildDivClassName:function(){
		return 'nodeChildDiv';
	},
	
	// 获取节点数据节点的图片Div的样式
	getNodeDataDivImgDivClassName:function(){
		return 'floatLeft';
	},
	
	// 获取有子节点的节点图片Div的样式
	getHadChildNodeDivImgDivClassName:function(){
		return 'floatLeft';
	},
	
	// 获取没有有子节点的节点图片Div的样式
	getHadNoChildNodeDivImgDivClassName:function(){
		return 'floatLeft';
	},
	
	// 获取节点onclick事件
	getNodeClick:function(){
		return this.nodeClick;
	},
	
	// 设置节点onclick事件
	setNodeClick:function(nodeClick){
		this.nodeClick = nodeClick;
	},
	
	// 获取节点oncontextmenu事件
	getNodeContextmenu:function(){
		return this.nodeContextmenu;
	},
	
	// 设置节点onclick事件
	setNodeContextmenu:function(nodeContextmenu){
		this.nodeContextmenu = nodeContextmenu;
	},
	
	addMenuItem:function(menuItem){
		this.menuItemArray.push(menuItem);
	},
	
	getMenuItemById:function(menuItemId){
		for(var i = 0 ; i < this.menuItemArray.length ; i++){
			var perMenuItem = this.menuItemArray[i];
			if(perMenuItem.id == menuItemId){
				return perMenuItem;
			}
		}
	},
	
	getAllMenuItem:function(){
		return this.menuItemArray;
	},
	
	createContextMenuItem:function(){
		this.contextMenuItemDiv = this.createElement("contextMenuItemDiv" , "DIV");
		this.contextMenuItemDiv.style.display = "none";
		this.contextMenuItemDiv.style.position = "absolute";
		this.contextMenuItemDiv.className = "contextMenuItem";
		if(this.menuItemArray){
			for(var i = 0 ; i < this.menuItemArray.length ; i ++){
				var perMenuItem = this.menuItemArray[i];
				var menuItemDiv = this.createElement("menuItemDiv-" + perMenuItem.id ,"DIV");
				var menuItemImg = this.createElement("menuItemDiv-img-" + perMenuItem.id, "img");
				menuItemImg.src = this.getImgSrcByImgName(perMenuItem.img);
				menuItemDiv.appendChild(menuItemImg);
				var menuItemA = this.createElement("menuItemDiv-A-" + perMenuItem.id, "A");
				menuItemA.innerText = perMenuItem.name;
				menuItemDiv.appendChild(menuItemA);
				menuItemDiv.className = "menuItem";
				
				var tree = this;
				menuItemDiv.onmouseover = function(){
					this.style.backgroundImage = 'url('+tree.getMenuItemBackgroundImage()+')';
				};
				
				menuItemDiv.onmouseout = function(){
					this.className = "menuItem";
					this.style.background='#F0F1F4';
				}
				this.contextMenuItemDiv.appendChild(menuItemDiv);
			}
		}
		
		if(this.treeDiv){
			this.treeDiv.appendChild(this.contextMenuItemDiv);
		}
		
		return this.contextMenuItemDiv;
	},
	
	getContextMenuItem:function(){
		return this.contextMenuItemDiv;
	},
	
	setOnClickForMenuItemById:function(menuItemId , onclick , node){
		var menuItem = this.getMenuItemById(menuItemId);
		
		var menuItemDiv = document.getElementById("menuItemDiv-" + menuItem.id);
		
		var tree = this;
		menuItemDiv.onclick = function(){
			onclick.call(tree , node);
		};
	},
	
	showContextMenuItem:function(pixelTop , pixelLeft){
		this.contextMenuItemDiv.style.pixelTop = pixelTop; 
		this.contextMenuItemDiv.style.pixelLeft = pixelLeft;
		this.contextMenuItemDiv.style.display = "inline";
		
	},
	
	closeContextMenuItem:function(){
		this.contextMenuItemDiv.style.display = "none";
	}
};





