<%@page contentType="text/html; charset=GB2312"%>
<?xml version="1.0" encoding="GB2312"?>
<root>
	<nodes>
		<node>
			<id category="·����">hin1</id>
			<img>image/topo/router/1.gif</img>
			<x>517px</x>
			<y>135px</y>
			<width>32</width>
			<height>32</height>
			<ip>111</ip>
			<alias>111</alias>
			<info>ʾ���豸</info>
			<menu>&lt;a class="deleteline_menu_out" onmouseover="deleteMenuOver();" onmouseout="deleteMenuOut();" onclick="deleteHintMeta('hin1')"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;ɾ���豸&lt;/a&gt;&lt;br/&gt;&lt;a class="relationmap_menu_out" onmouseover="relationMapMenuOver();" onmouseout="relationMapMenuOut();" onclick="javascript:window.showModalDialog('/afunms/submap.do?action=relationList&amp;nodeId=hin1', window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;��������ͼ &lt;/a&gt;&lt;br/&gt;</menu>
			<relationMap/>
		</node>
		<node>
			<id category="������">hin2</id>
			<img>image/topo/switch/1.gif</img>
			<x>772px</x>
			<y>265px</y>
			<width>32</width>
			<height>29</height>
			<ip>77</ip>
			<alias>77</alias>
			<info>ʾ���豸</info>
			<menu>&lt;a class="deleteline_menu_out" onmouseover="deleteMenuOver();" onmouseout="deleteMenuOut();" onclick="deleteHintMeta('hin2')"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;ɾ���豸&lt;/a&gt;&lt;br/&gt;&lt;a class="relationmap_menu_out" onmouseover="relationMapMenuOver();" onmouseout="relationMapMenuOut();" onclick="javascript:window.showModalDialog('/afunms/submap.do?action=relationList&amp;nodeId=hin2', window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;��������ͼ &lt;/a&gt;&lt;br/&gt;</menu>
			<relationMap/>
		</node>
		<node>
			<id category="net_server">net208</id>
			<img>image/topo/win_xp.gif</img>
			<x>586px</x>
			<y>369px</y>
			<width>65</width>
			<height>26</height>
			<ip>127.0.0.1</ip>
			<alias>local</alias>
			<info>�豸��ǩ:local&lt;br&gt;IP��ַ:127.0.0.1</info>
			<menu/>
			<relationMap/>
		</node>
		<node>
			<id category="net_server">net209</id>
			<img>image/topo/win_xp.gif</img>
			<x>235px</x>
			<y>203px</y>
			<width>65</width>
			<height>26</height>
			<ip>192.168.1.113</ip>
			<alias>localhost</alias>
			<info>�豸��ǩ:localhost&lt;br&gt;IP��ַ:192.168.1.113</info>
			<menu/>
			<relationMap/>
		</node>
	</nodes>
	<lines>
		<line id="1">
			<a>net208</a>
			<b>net209</b>
			<color>green</color>
			<dash>Solid</dash>
			<alias>#.#</alias>
			<start>##</start>
			<end>##</end>
			<lineWidth>1</lineWidth>
			<lineInfo>��·����: 127.0.0.1_0/192.168.1.113_0&lt;br&gt;��Դ����: ��·&lt;br&gt;��·��������: ����ȡֵ&lt;br&gt;��·��������: ����ȡֵ&lt;br&gt;��·����������: ����ȡֵ&lt;br&gt;��·����������: ����ȡֵ</lineInfo>
			<lineMenu>&lt;a class="detail_menu_out" onmouseover="detailMenuOver();" onmouseout="detailMenuOut();" onclick="showLineInfo(1)"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;�鿴��Ϣ &lt;/a&gt;&lt;br/&gt;&lt;a class="property_menu_out" onmouseover="propertyMenuOver();" onmouseout="propertyMenuOut();" onclick="javascript:window.showModalDialog('/afunms/submap.do?action=linkProperty&amp;lineId=1', window, 'dialogwidth:350px; dialogheight:250px; status:no; help:no;resizable:0');"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;��·���� &lt;/a&gt;&lt;br/&gt;&lt;a class="deleteline_menu_out" onmouseover="deleteLineMenuOver();" onmouseout="deleteLineMenuOut();" onclick="deleteLink(1)"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;ɾ����·&lt;/a&gt;&lt;br/&gt;&lt;a class="editline_menu_out" onmouseover="editLineMenuOver();" onmouseout="editLineMenuOut();" onclick="editLink(1)"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;�޸���· &lt;/a&gt;&lt;br/&gt;&lt;a class="confirmAlarm_menu_out" onmouseover="confirmAlarmMenuOver();" onmouseout="confirmAlarmMenuOut();" onclick="confirmAlarmLink(1)"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;�澯ȷ��&lt;/a&gt;&lt;br/&gt;</lineMenu>
		</line>
	</lines>
	<assistant_lines>
		<assistant_line id="2">
			<a>net209</a>
			<b>net208</b>
			<color>blue</color>
			<dash>Solid</dash>
			<alias>#.#</alias>
			<start>##</start>
			<end>##</end>
			<lineWidth>1</lineWidth>
			<lineInfo>��·����: 192.168.1.113_5/127.0.0.1_0_18&lt;br&gt;��Դ����: ��·&lt;br&gt;��·��������: ����ȡֵ&lt;br&gt;��·��������: ����ȡֵ&lt;br&gt;��·����������: ����ȡֵ&lt;br&gt;��·����������: ����ȡֵ</lineInfo>
			<lineMenu>&lt;a class="detail_menu_out" onmouseover="detailMenuOver();" onmouseout="detailMenuOut();" onclick="showLineInfo(2)"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;�鿴��Ϣ &lt;/a&gt;&lt;br/&gt;&lt;a class="property_menu_out" onmouseover="propertyMenuOver();" onmouseout="propertyMenuOut();" onclick="javascript:window.showModalDialog('/afunms/submap.do?action=linkProperty&amp;lineId=2', window, 'dialogwidth:350px; dialogheight:250px; status:no; help:no;resizable:0');"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;��·���� &lt;/a&gt;&lt;br/&gt;&lt;a class="deleteline_menu_out" onmouseover="deleteLineMenuOver();" onmouseout="deleteLineMenuOut();" onclick="deleteLink(2)"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;ɾ����·&lt;/a&gt;&lt;br/&gt;&lt;a class="editline_menu_out" onmouseover="editLineMenuOver();" onmouseout="editLineMenuOut();" onclick="editLink(2)"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;�޸���· &lt;/a&gt;&lt;br/&gt;&lt;a class="confirmAlarm_menu_out" onmouseover="confirmAlarmMenuOver();" onmouseout="confirmAlarmMenuOut();" onclick="confirmAlarmLink(2)"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;�澯ȷ��&lt;/a&gt;&lt;br/&gt;</lineMenu>
		</assistant_line>
	</assistant_lines>
	<demoLines>
		<demoLine id="hl1">
			<a>hin1</a>
			<b>hin2</b>
			<color>blue</color>
			<dash>Solid</dash>
			<lineWidth>3</lineWidth>
			<lineInfo>ʾ����·</lineInfo>
			<lineMenu>&lt;a class="property_menu_out" onmouseover="propertyMenuOver();" onmouseout="propertyMenuOut();" onclick="javascript:window.showModalDialog('/afunms/link.do?action=readyEditLine&amp;id=5', window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;��·���� &lt;/a&gt;&lt;br/&gt;&lt;a class="deleteline_menu_out" onmouseover="deleteLineMenuOver();" onmouseout="deleteLineMenuOut();" onclick="deleteLine('hl1')"&gt;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;ɾ����·&lt;/a&gt;&lt;br/&gt;</lineMenu>
		</demoLine>
	</demoLines>
</root>