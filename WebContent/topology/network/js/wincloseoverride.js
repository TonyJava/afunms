//���ֵ�������ر�����      �������ڹرյ�������
		 	var winC = window.close;
			window.close = function(){
				 if(parent.AlphaLayerTool){
					parent.AlphaLayerTool.hideOverlay();
					return;
				}
				winC();
			};