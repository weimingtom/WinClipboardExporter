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
*/
chrome.contextMenus.create({
    "title": "处理此页微博内容",
    "contexts": ["selection"],
    "onclick": queryWord
});