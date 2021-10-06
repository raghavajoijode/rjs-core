var cookieManager = {
	
	createCookie: function (name,value,days) {
		if (days) {
			var date = new Date();
			date.setTime(date.getTime()+(days*24*60*60*1000));
			var expires = "; expires="+date.toUTCString();
		}
		else var expires = "";
		document.cookie = name+"="+value+expires+"; path=/; sameSite=None; Secure";
		console.log(document.cookie);
	},
	
	getCookieList: function () {
		return decodeURIComponent(document.cookie).split(';');
	},
	
	readCookie: function (name) {
		var nameEq = name + "=";
		var cookieList = this.getCookieList();
		for(var i=0;i < cookieList.length;i++) {
			var cookie = cookieList[i];
			while (cookie.charAt(0)==' ') 
				cookie = cookie.substring(1,cookie.length);
			if (cookie.indexOf(nameEq) == 0) 
				return cookie.substring(nameEq.length,cookie.length);
		}
		return null;
	},
	
	eraseCookie: function(name) {
		createCookie(name,"",-1);
	}
}