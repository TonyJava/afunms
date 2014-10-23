// Node object
function Node(id, name, pid , isSelected) {
	
	// id
	this.id = id;
	
	// name
	this.name = name;

	// 父 id
	this.pid = pid;
	
	// 是否具有子节点
	this.isHadChild = false;
	
	// 是否展开
	this.isOpen = false;
	
	// 是否选中
	this.isSelected = isSelected;
	
	// 所有的子节点
	this.childNodes = null;
	
	// 节点的等级
	this.level = null;
}

// Tree object
function Tree(){
	// 数据数组
	this.dataArray = new Array();

	// 所有节点
	this.nodeArray = new Array();
	
	// 

	// 树所在DIV
	this.treeDiv = null;
	
	// 根节点
	this.rootNode = null;
	

}

Tree.prototype = {
	// 初始化方法
	init:function(dataArray){
		
		this.dataArray = dataArray;
		
		
	
		for(var i = 0 ; i < this.dataArray.length ; i++){
		
			var data = this.dataArray[i];
			
			var node = new Node(data[0] , data[1] , data[2] , data[3]);
			
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
		this.rootNode = this.getRootNode();
		this.recursiveCreateTree(this.rootNode , null);
	},
	
	// 使用  id , 以及 tagName 来创建一个element
	creaateElement: function (id , tagName){
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
	
	// 获取所有子节点
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
		var rootNode = null;
		for(var i = 0 ; i < this.nodeArray.length ; i++){
			var node = this.nodeArray[i];
			if(node.pid == '0'){   // 如果 PID 为 null 则为空节点
				rootNode = node;
				break;
			}
		}
		
		return rootNode;
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
		var nodeDiv = this.creaateElement('nodeDiv-' + node.id , 'DIV');
		var nodeDataDiv = this.createNodeDataDiv(nodeDiv , node);
		var nodeChildDiv = this.createNodeChildDiv(nodeDiv, node);
		nodeDiv.appendChild(nodeDataDiv);
		nodeDiv.appendChild(nodeChildDiv);
		return nodeDiv;
	},
	
	// 创建节点数据 DIV
	createNodeDataDiv:function(nodeDiv , node){
		var nodeDataDiv = this.creaateElement('nodeDataDiv-' + node.id , 'DIV');
		
		if(node.isHadChild){
			// 如果有子节点  创建显示子节点的图标
			var showNodeChildDivIcon = this.createShowNodeChildDivIcon(node,this.showOrHideNodeChildDiv);
			nodeDataDiv.appendChild(showNodeChildDivIcon);
			
			// 创建 文件夹图标 
			var nodeFolderIcon = this.createNodeFolderIcon(node);
			nodeDataDiv.appendChild(nodeFolderIcon);
		}else{
			// 如果没有子节点 创建   checkbox 
			var nodeCheckbox = this.createNodeCheckboxIcon(node);
			nodeDataDiv.appendChild(nodeCheckbox);
		}
		
		// 创建节点的Text
		var nodeDataName = this.creaateElement('nodeDataDiv-name-' + node.id, 'A');
		nodeDataName.innerText = node.name;
		nodeDataDiv.appendChild(nodeDataName);
		
		
		return nodeDataDiv;
	},
	
	// 创建节点的子节点 DIV
	createNodeChildDiv:function(nodeDiv , node){
		var nodeChildDiv = this.creaateElement('nodeChildDiv-' + node.id , 'DIV');
		nodeChildDiv.className = "nodeChildDiv";
		if(node.isOpen){
			nodeChildDiv.style.display = "inline";
		}else{
			nodeChildDiv.style.display = "none";
		}
		return nodeChildDiv;
	},
	
	// 创建节点的显示子节点图标
	createShowNodeChildDivIcon:function(node,callback){
		// 创建节点的图标
		var img = this.creaateElement('showNodeChildDivIcon-' + node.id, 'img');
		if(node.isOpen){
			img.src = "/afunms/config/business/img/minus5.gif";
		}else{
			img.src = "/afunms/config/business/img/plus5.gif";
		}
		img.onclick = function(){
			callback.call(img,node);
		};
		return img;
	},
	
	// 显示或隐藏子节点DIV
	showOrHideNodeChildDiv:function(node){
		var nodeChildDiv = document.getElementById("nodeChildDiv-" + node.id);
		var nodeFolderIcon = document.getElementById("nodeFolderIcon-" + node.id);
		if(node.isOpen){
			// 如果打开 则隐藏
			node.isOpen = false;
			if(nodeChildDiv){
				nodeChildDiv.style.display = "none";
			}
			if(nodeFolderIcon){
				nodeFolderIcon.src = "/afunms/config/business/img/folderClosed.gif";
			}
			this.src = "/afunms/config/business/img/plus5.gif";
		}else{
			// 如果隐藏 则打开
			node.isOpen = true;
			if(nodeChildDiv){
				nodeChildDiv.style.display = "inline";
			}
			if(nodeFolderIcon){
				nodeFolderIcon.src = "/afunms/config/business/img/folderOpen.gif";
			}
			this.src = "/afunms/config/business/img/minus5.gif";
		}
	},
	
	// 创建文件夹图标
	createNodeFolderIcon:function(node){
		var nodeFolderIcon = this.creaateElement("nodeFolderIcon-" + node.id, "img");
		if(node.isOpen){
			nodeFolderIcon.src = "/afunms/config/business/img/folderOpen.gif";
		}else{
			nodeFolderIcon.src = "/afunms/config/business/img/folderClosed.gif";
		}
		return nodeFolderIcon;
	},
	
	// 创建节点  checkbox 图标
	createNodeCheckboxIcon:function(node){
		var img = this.creaateElement('nodeCheckboxIcon-' + node.id, 'img');
		if(node.isSelected){
			img.src = "/afunms/config/business/img/iconCheckDis.gif";
		}else{
			img.src = "/afunms/config/business/img/iconUncheckDis.gif";
		}
		img.onclick = function(){
			if(node.isSelected){
				// 如'选中' 则变为 '未选中'
				node.isSelected = false;
				img.src = "/afunms/config/business/img/iconUncheckDis.gif";
			}else{
				// 如果 '未选中'  则变为 '选中'
				node.isSelected = true;
				img.src = "/afunms/config/business/img/iconCheckDis.gif";
			}
		};
		return img;
	},
	
	// 执行是否选中
	isSelected:function(node){
		if(node.isSelected){
			this.choose(node);
		}else{
			this.noChoose(node);
		}
	},
	
	// 选中
	choose:function(node){
		var checkimage = document.getElementById("nodeCheckboxIcon-" + node.id);
		checkimage.src = "/afunms/config/business/img/iconCheckDis.gif";
		node.isSelected = true;
	},
	
	noChoose:function(node){
		var checkimage = document.getElementById("nodeCheckboxIcon-" + node.id);
		checkimage.src = "/afunms/config/business/img/iconUncheckDis.gif";
		node.isSelected = false;
	},
	
	// 全选
	allChoose:function(){
		for(var i = 0 ; i<this.nodeArray.length;i++){
			node = this.nodeArray[i];
			if(!node.isHadChild){
				this.choose(node);
			}
			
		}
		
	},
	
	// 全不选
	allNotChoose:function(){
		var node;
		var checkimage;
		for(var i = 0 ; i<this.nodeArray.length;i++){
			node = this.nodeArray[i];
			if(!node.isHadChild){
				this.noChoose(node);
			}
			
		}
		
	},
	
	antiChoose:function(){
		for(var i = 0 ; i<this.nodeArray.length;i++){
			node = this.nodeArray[i];
			if(!node.isHadChild){
				if(node.isSelected){
					this.noChoose(node);
				}else{
					this.choose(node);
				}
			}
		}
	},
	
	// 根据 isSelected 获取所有选中的节点
	getIsSelectedNode:function(isSelected){
		var isSelectedNodeArray = new Array();
		for(var i = 0 ; i < this.nodeArray.length ; i ++){
			var pernode = this.nodeArray[i];
			if(!pernode.isHadChild && pernode.isSelected == isSelected){
				isSelectedNodeArray.push(pernode);
			}
		}
		
		return isSelectedNodeArray;
	},
	
	// 重置
	reset:function(){
		for(var i = 0 ; i < this.nodeArray.length ; i++){
			var pernode = this.nodeArray[i];
			if(!pernode.isHadChild){
				pernode.isSelected = this.dataArray[i][3];
				this.isSelected(pernode);
			}
		
		}
	}
};





