LogLevel = 2
function log (level, message) {
	if (level >= LogLevel) {
		console.log(level + ": " + message)
	}
}

function doAjax(url, callback) {
	log(1, "Call doAjax(" + url + ")");
	$.getJSON(url, function(data) {
		if (!data.success) {
			// FIXME 
			alert("response: " + data.messages);
		}
		else {
			log(2, "Success: doAjax(" + url + ")");
		}
		if (callback) {
			callback(data.success);
		}
	});
}