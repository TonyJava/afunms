<%@page contentType="text/html; charset=GB2312"%>
<?xml version="1.0" encoding="GB2312"?>
<root>
	<nodes>
		<node>
			<id category="路由器">hin1</id>
			<img>image/topo/router/1.gif</img>
			<x>461px</x>
			<y>178px</y>
			<width>32</width>
			<height>32</height>
			<ip>111</ip>
			<alias>111</alias>
			<info>示意设备</info>
			<menu>&lt;a class="deleteline_menu_out" onmouseover="deleteMenuOver();" onmouseout="deleteMenuOut();" onclick="deleteHintMeta('hin1')"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;删除设备&lt;/a&gt;&lt;br/&gt;&lt;a class="relationmap_menu_out" onmouseover="relationMapMenuOver();" onmouseout="relationMapMenuOut();" onclick="javascript:window.showModalDialog('/afunms/submap.do?action=relationList&amp;nodeId=hin1', window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;关联拓扑图 &lt;/a&gt;&lt;br/&gt;</menu>
			<relationMap/>
		</node>
		<node>
			<id category="交换机">hin2</id>
			<img>image/topo/switch/1.gif</img>
			<x>772px</x>
			<y>265px</y>
			<width>32</width>
			<height>29</height>
			<ip>77</ip>
			<alias>77</alias>
			<info>示意设备</info>
			<menu>&lt;a class="deleteline_menu_out" onmouseover="deleteMenuOver();" onmouseout="deleteMenuOut();" onclick="deleteHintMeta('hin2')"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;删除设备&lt;/a&gt;&lt;br/&gt;&lt;a class="relationmap_menu_out" onmouseover="relationMapMenuOver();" onmouseout="relationMapMenuOut();" onclick="javascript:window.showModalDialog('/afunms/submap.do?action=relationList&amp;nodeId=hin2', window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;关联拓扑图 &lt;/a&gt;&lt;br/&gt;</menu>
			<relationMap/>
		</node>
	</nodes>
	<lines/>
	<assistant_lines/>
	<demoLines>
		<demoLine id="hl1">
			<a>hin1</a>
			<b>hin2</b>
			<color>blue</color>
			<dash>Solid</dash>
			<lineWidth>1</lineWidth>
			<lineInfo>示意链路</lineInfo>
			<lineMenu>&lt;a class="property_menu_out" onmouseover="propertyMenuOver();" onmouseout="propertyMenuOut();" onclick="javascript:window.showModalDialog('/afunms/link.do?action=readyEditLine&amp;id=1', window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;链路属性 &lt;/a&gt;&lt;br/&gt;&lt;a class="deleteline_menu_out" onmouseover="deleteLineMenuOver();" onmouseout="deleteLineMenuOut();" onclick="deleteLine('hl1')"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;删除链路&lt;/a&gt;&lt;br/&gt;</lineMenu>
		</demoLine>
	</demoLines>
</root>
