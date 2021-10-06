const setCookie = function(cname, cvalue, expdays) {
	var date = new Date();
	date.setTime(date.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + date.toGMTString();
	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}
const getCookie = function(cname) {
	var name = cname + "=";
	var decodedCookie = decodeURIComponent(document.cookie);
	var cookieArray = decodedCookie.split(';');
	for (var i = 0; i < cookieArray.length; i++) {
		var cookie = cookieArray[i];
		while (cookie.charAt(0) == ' ') {
			cookie = cookie.substring(1);
		}
		if (cookie.indexOf(name) === 0) {
			return cookie.substring(name.length, cookie.length);
		}
	}
	return "";
}
const formValidation = function(username, password, c) {
	if (username.length > 0 && password.length > 0) {
		if (c) {
			setCookie("user_name", username, 30);
		}
		return true;
	} else {
		return false;
	}
}
const checkCookie = function() {
	var user = getCookie("user_name");
	if (user !== "") {
		$('.subra-login-form input[name="username"]').val(user);
	}
}

(function($, window) {
	$(function() {
		checkCookie();
		let loginForm = $('.subra-login-form');
		loginForm.on('click', 'input[type="submit"]', function(e) {
			e.preventDefault();
			const userName = loginForm.find('input[name="username"]').val();
			const password = loginForm.find('input[name="password"]').val();
			const loginPagePath = loginForm.find('#loginPagePath').val();
			const targetPagePath = loginForm.find('#succesRedirectPage').val();
			const isChecked = loginForm.find('input[name="remember-me"]').length;
			const validInputs = formValidation(userName, password, isChecked);
			const errorMSG = $("#errorDiv").html("<p>Invalid User Name or Password</p>")
					.find('p').css({
						"color" : "red",
						"font-weight" : "bold"
					});
			if (validInputs) {
				const loginAjax = $.ajax({
					type : "POST",
					url : loginPagePath + "/j_security_check",
					data : {
						j_username : userName,
						j_password : password,
						j_validate : validInputs
					}
				});
				loginAjax
						.done(function(data, textStatus, jqXHR) {
							window.location.href = targetPagePath
									+ ".html";
						});
				loginAjax.fail(function(XMLHttpRequest,
						textStatus, errorThrown) {
					errorMSG;
				});

			} else {
				errorMSG;
			}
		});

	})
})($, window);