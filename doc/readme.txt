https://github.com/kingzone/ClipboardMonitor

--------------------

kkk = document.querySelectorAll('[node-type="feed_list_content"]')
//for(var i=0; i < kkk.length; i++) {console.log(kkk[i].innerHTML);}
for(var i=0; i < kkk.length; i++) {sss+=(kkk[i].innerHTML);}
copy(sss)

--------------------

(function() {
	var kkk = document.querySelectorAll('[node-type="feed_list_content"]');
	var sss="";
	for(var i=0;i<kkk.length;i++){sss+=(kkk[i].innerHTML.trim() + "\n");}
	copy(sss);
})();
