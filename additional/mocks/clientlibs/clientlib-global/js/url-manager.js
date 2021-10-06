var urlManager = {
  getParam: function(url, param){
    return decodeURI(url.replace(new RegExp("^(?:.*[&\\?]" + encodeURI(param).replace(/[\.\+\*]/g, "\\$&") + "(?:\\=([^&#]*))+)?.*$", "i"), "$1"));
  },
  replaceParam:function(url, param, value){
    var arr = url.split(/[&\\?]/);
    url = "";
    for(var i =0;i<arr.length;i++){
      var pair = arr[i].split('='); 
      if(pair[0].toLowerCase() === param.toLowerCase()){
        arr[i] = pair[0]+"="+value;
      }
      if(url !==""){
        url += (url.indexOf('?')<0 ? '?' : '&');
      }
      url+=arr[i];
    }
    return url;
  },
  addParam: function(url, param, value){
    if (typeof value !== "undefined" && typeof param === "string" && value !== null && value!=="") {
        value = encodeURIComponent(value);
        if (urlManager.getParam(url, param) === "" ) {
            url+=(url.indexOf('?')<0 ? '?' : '&')+param+"="+value;
        }else{
            url = urlManager.replaceParam(url, param, value);
        }
    }
    return url;
  }
};