https://github.com/kingzone/ClipboardMonitor

--------------------
(function() {
	var MAGIC = ">>>>WinClipboardExporter<<<<";
	var kkk = document.querySelectorAll('[node-type="feed_list_content"]');
	var sss=MAGIC;
	sss += "href=" + window.location.href + ",";
	sss += "\n";
	for(var i=0;i<kkk.length;i++){sss+=(kkk[i].innerHTML.trim() + "\n");}
	copy(sss);
})();

--------------------
