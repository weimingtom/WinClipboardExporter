/*
chrome.extension.onMessage.addListener(function(message, sender) {
    if(message.action == 'sendSource'){
		Copy(message.source);
		myWindow = window.open("siwuprinter://aHRtYWRkcmVzcw==/");
        setTimeout(function() {
           myWindow.close();
        }, 800);
        return false;    
     }
})

function Copy(str)
{	
    var sandbox = document.getElementById('sandbox');
    sandbox.value = str;
    sandbox.select();
    document.execCommand('copy');
    sandbox.value = "";
}
*/
		/*
		chrome.extension.sendMessage({action:'sendSource',source:"sss"});
			
		(function() {
			var MAGIC = ">>>>WinClipboardExporter<<<<";
			var kkk = document.querySelectorAll('[node-type="feed_list_content"]');
			var sss=MAGIC;
			sss += "href=" + window.location.href + ",";
			sss += "\n";
			for(var i=0;i<kkk.length;i++){sss+=(kkk[i].innerHTML.trim() + "\n");}
			
			chrome.extension.sendMessage({action:'sendSource',source:sss});
		})();*/
		//copy();



function queryWord(info, tab) {
	chrome.tabs.executeScript(null, {
		allFrames: false,
		file: "inc/jquery-2.1.0.min.js"
	}, function() {
		console.log('jquery executed');
		chrome.tabs.executeScript(null, {
			allFrames: false,
			file: "translate.js"
		}, function() {
			console.log('translate executed')
			
			chrome.tabs.query({
				active: true
			}, function(tab) {
				
			})
		});
	});
}

/*
使用%s显示选定的文本
"contexts": ["selection"],
*/
chrome.contextMenus.create({
    "title": "处理此页微博内容",
    "contexts": ["all"],
    "onclick": queryWord
});