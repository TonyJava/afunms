(function($) {
  $.fn.downloadr = function() {
  	return this.each(function() {
  	
  	function returnBrowserTest(){
				
					var dlBrowser = $.browser.browser();
					
					var dlString = '';
					
					switch(dlBrowser){
					
						case "Safari":
						
						dlString = '右键图标 ,从菜单中选择<strong>另存为...</strong> 或者 <strong>链接另存为...</strong>.';
						
						break;
						
						case "Firefox":
						
						dlString = '右键图标 ,从菜单中选择 <strong>另存为...</strong>.'
						
						break;
						
						case "Msie":
						
						dlString = '右键图标 ,从菜单中选择 <strong>保存目标为...</strong>.';
						
						break;
						
						default:
						
						dlString = '右键图标 ,从菜单中选择 <strong>保存目标为...</strong>';
					}
					
					
					return dlString;
				}	
				
				var element = this;
			  
			  	$(element).addClass("download_link");
			  	
			  	var theTitle = $(element).attr('title');
			  				  	
				var theLink = $(element).attr('href');
	
			  	$(element).bind('click',function(e){
			  	
			  		e.preventDefault();

				  	var html = "";
				  	
				  	html += "<h2>下载 '" + theTitle + "'</h2>";
				  	html += "<p>下载 '" + theTitle + "', 只要 " + returnBrowserTest() + "</p>";
				  	html += "<p style='text-align:center;'><a href='" + theLink + "'><img src='downloadr/download.png' alt='右键下载保存' id='download_file'/></a></p>";
				  	html += "<p>点击连接打开文件 <strong><a href='" + theLink + "'>here</a></strong>.</p>";
				  	
				  	jQuery.facebox(html);
			  		
			  	});
			  	});

  }
})(jQuery);