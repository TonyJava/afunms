
String.prototype.trim = function() {													
    return this.replace(/(^\s*)|(\s*$)/g, "");
}

//1 载入XML文件
function load4AddNodes(url)
{
	var http = new ActiveXObject("Microsoft.XMLHTTP");
	xmldoc = new ActiveXObject("Microsoft.XMLDOM");
	http.open("POST", url, false);
	http.send();
	xmldoc.async = false;
	xmldoc.loadXML(http.responseText);

	// nodes节点
	var nodes = xmldoc.getElementsByTagName("node");	
	for (var i = 0; i < nodes.length; i += 1)
	{
		var node = nodes[i];
		var id = node.getElementsByTagName("id")[0].text;
		var ip = node.getElementsByTagName("ip")[0].text
		var alias = node.getElementsByTagName("alias")[0].text;
		var category = node.getElementsByTagName("id")[0].getAttribute("category");
        
		var opt = new Option();    	
		if(category== "router")		
		   category = "路由器";
		else if(category== "switch_router")		
		   category = "路由交换机";
		else if(category== "switch")		
		   category = "交换机";
		else if(category== "server")	
		   category = "服务器";	
		else if(category== "ip_node")	
		   category = "IP地址";	
		else if(category== "ip_node")	
		   category = "IP地址";	
		else if(category== "tomcat")	
		   category = "Tomcat";	
		else if(category== "mysql")	
		   category = "MySQL";	
		else if(category== "oracle")	
		   category = "Oracle";	

       	opt.text = category + "[" + ip + "_" + alias + "]";
	    opt.value = id;
		mainForm.viewnodes.options.add(opt);
	}	
}

function add()
{
	if ( mainForm.node_list.selectedIndex == -1 ) return;
    	
	var tempId = mainForm.node_list.options[mainForm.node_list.selectedIndex].value;
	var tempText = mainForm.node_list.options[mainForm.node_list.selectedIndex].text;
    var bExist = false;
    for ( i=0; i < mainForm.viewnodes.options.length; i++ )
    {
        if ( tempId == mainForm.viewnodes.options[i].value )
        {
            bExist = true;
            break;
        }
    }
    if ( bExist )
    {
        alert( tempText + "已经存在!" );
        return;
    }

    var opt = new Option();
    opt.text = tempText;
    opt.value = tempId;
    mainForm.viewnodes.options.add(opt);
}
  
//删除一个结点
function del()
{
    if ( mainForm.viewnodes.selectedIndex !=-1 )
    {
      mainForm.viewnodes.remove(mainForm.viewnodes.selectedIndex);
    }
}
  
  function addALine()
  {
  	var opt = new Option();
    var optText;
    var optIp;
    var bExist;
    if (( mainForm.startnodes.value == "" )||(mainForm.endnodes.value  == "")) 
    {
    	alert("必须有起点和终点！");
    	return;
    }
    
    if(mainForm.startnodes.value == mainForm.endnodes.value)
    {
        alert("不能是同一个节点");
        return;
    }
	optText = mainForm.startnodes.options[mainForm.startnodes.selectedIndex].text  + "<----->"+
			  mainForm.endnodes.options[mainForm.endnodes.selectedIndex].text;
	optText2 = mainForm.endnodes.options[mainForm.endnodes.selectedIndex].text  + "<----->" +
			   mainForm.startnodes.options[mainForm.startnodes.selectedIndex].text;

    bExist = false;
    for ( i=0; i < mainForm.viewlines.options.length; i++ )
    {
        oldText = mainForm.viewlines.options[i].text;
        if ((optText == oldText)||(optText2 == oldText))
        {
            bExist = true;
            break;
        }
    }
    if ( bExist )
    {
       alert( "该连线已经存在!" );
       return;
    }
    opt.text = optText;
    opt.value = mainForm.startnodes.value + "-" + mainForm.endnodes.value;
    mainForm.viewlines.options.add(opt);  
  }
  
  
  function delALine()
  {
  	if ( mainForm.viewlines.selectedIndex !=-1 )
    {
      mainForm.viewlines.remove(mainForm.viewlines.selectedIndex);
    }
  } 

//1 载入XML文件
function load4AddLines(url)
{
	var http = new ActiveXObject("Microsoft.XMLHTTP");
	xmldoc = new ActiveXObject("Microsoft.XMLDOM");
	http.open("POST", url, false);
	http.send();
	xmldoc.async = false;
	xmldoc.loadXML(http.responseText);

	// nodes节点
	var nodes = xmldoc.getElementsByTagName("node");	
	for (var i = 0; i < nodes.length; i += 1)
	{
		var node = nodes[i];
		var id = node.getElementsByTagName("id")[0].text;
		var ip = node.getElementsByTagName("ip")[0].text
		var alias = node.getElementsByTagName("alias")[0].text;

		var opt1 = new Option();
		var opt2 = new Option();		
    	var optText;
    	
    	optText = ip + "_" + alias;
    	opt1.text = optText;
		opt1.value = id;
		opt2.text = optText;
		opt2.value = id;
		mainForm.startnodes.options.add(opt1);
		mainForm.endnodes.options.add(opt2);
	}

	var lines = xmldoc.getElementsByTagName("line");	
	for (var i = 0; i < lines.length; i+=1)
	{
		var line = lines[i];
		var id1 = line.getElementsByTagName("a")[0].text;
		var id2 = line.getElementsByTagName("b")[0].text;
		
		var tmpIp,tmpAlias,tmpId,tmpNode;
		var ip1,ip2;
		var alias1,alias2;
		
		for (var j = 0; j < nodes.length; j+=1)
		{
			tmpNode = nodes[j];
			tmpId = tmpNode.getElementsByTagName("id")[0].text;
			tmpIp = tmpNode.getElementsByTagName("ip")[0].text
			tmpAlias = tmpNode.getElementsByTagName("alias")[0].text;
			if(id1==tmpId)
			{
				ip1 = tmpIp;
				alias1 = tmpAlias;
			}
			else if(id2==tmpId)
			{
				ip2 = tmpIp;
				alias2 = tmpAlias;
			}
		}		
		var tmpOpt = new Option();
    	var tmpText = ip1 + "_" + alias1 + "<----->" + ip2 + "_" + alias2;    	
    	tmpOpt.text = tmpText;
		tmpOpt.value = id1 + "-" + id2;
		mainForm.viewlines.options.add(tmpOpt);
	}	
}

function sendURL(url)
{
	var http = new ActiveXObject("Microsoft.XMLHTTP");
	http.open("POST", url, false);
	http.send();
}