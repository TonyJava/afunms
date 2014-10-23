function test()
{
	alert("test");
}



function isAcrobatPluginInstall()
{
	var ua = navigator.userAgent.toLowerCase();

	var isIE  = (ua.indexOf("msie") != -1) ? true : false;
	var isFirefox = (ua.indexOf("firefox") != -1) ? true : false;
	var isChrome = (ua.indexOf("chrome") != -1) ? true : false;
	
	if(isIE)
    {
		var acrobat = new Object();
		var oAcro;
      	if(window.ActiveXObject)
      	{ 
	        for(x=2;  x<10;  x++)
	        {  
	            try
	            {  
                   oAcro=eval("new  ActiveXObject('PDF.PdfCtrl."+x+"');");  
                   if  (oAcro)
                   {  
                       return true; 
                   }  
	            }  
	            catch(e)  {}  
	        }  

        	try
        	{  
                var oAcro4=new  ActiveXObject('PDF.PdfCtrl.1');  
                if(oAcro4)  
                {  
                    return true;
                }  
	        }  
	        catch(e)  {}  

	        try{  
	             var oAcro7=new  ActiveXObject('AcroPDF.PDF.1');  
                 if(oAcro7)  
                 {  
                     return true;  
                 }  
	        }  
	        catch(e)  
	        {}  
     
     	}

   	}
   	else if(isFirefox)
   	{
   		if (navigator.plugins && navigator.plugins.length) 
   		{    
			for (x=0; x<navigator.plugins.length;x++) 
			{    
				if (navigator.plugins[x].name== 'Adobe Acrobat')    
					return true;  
			} 
		}
   	}

    return false;
}






