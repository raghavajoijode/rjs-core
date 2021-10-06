customDone = function(data, textStatus, jqXHR) { 
	console.log('Custim');
	console.log('data : ', data);
	console.log('textStatus : ', textStatus);
	console.log('jqXHR : ', jqXHR);
	console.log('---');
};

//Using Ajax Manager
ajaxManager.done = customDone; // Setting custum done handler
ajaxManager.always=null;
ajaxManager.fire('https://reqres.in/api/users', 'GET', {'page':1, 'a':'b'});



//ajaxManager.fire('http://localhost:8080/subra/api/v1/users/register', 'POST', {'email':'test1','password':'Abcd@1234'});

//var oldCount = parseInt(cookieManager.readCookie('hitCount'), 10) || 0;
cookieManager.createCookie('raghava', 'test', 7);
console.log('c: ',cookieManager.readCookie('raghava'));