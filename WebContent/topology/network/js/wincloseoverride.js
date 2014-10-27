//遮罩的情况，关闭遮罩      弹出窗口关闭弹出窗口
		 	var winC = window.close;
			window.close = function(){
				 if(parent.AlphaLayerTool){
					parent.AlphaLayerTool.hideOverlay();
					return;
				}
				winC();
			};