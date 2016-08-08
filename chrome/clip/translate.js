var page = {

  messageListener: function() {
	(function() {
		var MAGIC = ">>>>WinClipboardExporter<<<<";
		var kkk = document.querySelectorAll('[node-type="feed_list_content"]');
		var sss=MAGIC;
		sss += "href=" + window.location.href + ",";
		sss += "\n";
		for(var i=0;i<kkk.length;i++){sss+=(kkk[i].innerHTML.trim() + "\n");}
		var aaa = $("a[action-type='fl_unfold']");
		x=aaa.toArray()
		for (i=0;i<x.length;i++)
		{
			console.log(x[i]);
			x[i].click()
		}
		//copy(sss);
	})();
  },

  init: function() {
    page.messageListener();
  }
}

page.init();