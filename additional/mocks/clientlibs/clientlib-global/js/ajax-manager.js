var ajaxManager = {
	fire: function (url, type, params) {
		$.ajax({
			url: url,
			type: type,
			data: params,
			contentType: 'application/json;',
			dataType: 'json'
		})
		.done (this.done)
		.fail (this.fail)
		.always (this.always);
		
		this.resetAjaxMgr();
	},
	
	defaultDone: function(data, textStatus, jqXHR) { 
		console.log('Done ', textStatus, ':: No Custom Done Handler Found');
	},
	
	defaultAlways: function(jqXHROrData, textStatus, jqXHROrErrorThrown) { 
		console.log('Ajax completed with status : ', textStatus, ':: No Custom Always Handler Found');
	},
	
	defaultFail: function(jqXHR, textStatus, errorThrown) { 
		console.log('Failed with error ', errorThrown, ':: No Custom Fail Handler Found');
	},
	
	resetAjaxMgr : function () {
		this.done = this.defaultDone;
		this.always = this.defaultAlways;
		this.fail = this.defaultFail;
	}
	
};
ajaxManager.resetAjaxMgr();