var page = {

  messageListener: function() {
	function Copy(str)
	{	
		if ($("body").find("#sandbox").size() == 0) {
			$("body").append("<textarea id='sandbox'></textarea>");
		}
		var sandbox = document.getElementById('sandbox');
		sandbox.value = str;
		sandbox.select();
		document.execCommand('copy');
		sandbox.value = "";
	}

	//(function() {
		var MAGIC = ">>>>WinClipboardExporter<<<<";
		var kkk = document.querySelectorAll('[node-type="feed_list_content"]');
		var sss=MAGIC;
		sss += "href=" + window.location.href + ",";
		sss += "\n";
		for(var i=0;i<kkk.length;i++){sss+=(kkk[i].innerHTML.trim() + "\n");}
		
		//copy(sss);
		//chrome.extension.sendMessage({action:'sendSource',source:sss});
		Copy(sss);
		
		var aaa = $("a[action-type='fl_unfold']");
		x=aaa.toArray()
		for (i=0;i<x.length;i++)
		{
			console.log(x[i]);
			x[i].click()
		}
		//copy(sss);
	//})();
  },

  init: function() {
    page.messageListener();
  }
}

page.init();
