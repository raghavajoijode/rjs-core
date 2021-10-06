	var selected;

function showCode(elem){
   var str  = elem.value;
   var i = str.indexOf("_");
    var country = str.substring(0,i);
    var code = str.substring(i+1 ,str.length);
     document.contactus.ctry.value = country;
     document.contactus.code.value ="+" + code ;
}
            function correctCaptcha (response){
			$('.recaptcha').find('.error').hide();
            };
     //for UTM cookies

function readCookie(name) {

    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}
var counter=0;
createCookie('contact_utm_preferrer',"",-1);
var prevurl=document.referrer;
var furl,fname,purl;
if(prevurl!="")
{
	//alert(prevurl);
	var hposition=prevurl.indexOf("Subra.com");
    console.log('position'+hposition);
	if(hposition=="0" || hposition<"0")
	{
		
		furl=prevurl;
		fname=window.location;
		var fullname=furl+","+fname;
		createCookie('contact_utm_cookie',fullname,1);
	}
	else{	
        var myCookie = checkEmptyCookie("contact_utm_cookie");

        if (myCookie == null) {
            furl='';
            fname=window.location;
            var fullname=furl+","+fname;
            createCookie('contact_utm_cookie',fullname,1);
        }

		createCookie('contact_utm_preferrer',prevurl,1);
	}

}
else
{
    console.log('else');
	furl='';
    fname=window.location;
	var fullname=furl+","+fname;
	createCookie('contact_utm_cookie',fullname,1);
	createCookie('contact_utm_preferrer','',1)
	
	
}
purl=prevurl;

function checkEmptyCookie(name) {
    var dc = document.cookie;
    var prefix = name + "=";
    var begin = dc.indexOf("; " + prefix);
    if (begin == -1) {
        begin = dc.indexOf(prefix);
        if (begin != 0) return null;
    }
    else
    {
        begin += 2;
        var end = document.cookie.indexOf(";", begin);
        if (end == -1) {
        end = dc.length;
        }
    }
    return decodeURI(dc.substring(begin + prefix.length, end));
} 


function createCookie(name,value,days) {

    if (days) {
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        var expires = "; expires="+date.toGMTString();
    }
    else var expires = "";
    document.cookie = name+"="+value+expires+"; path=/";
}

//end of utm cookies
    $(document).ready(function () {
		
    $('.selectpicker').selectpicker();
		$('.submit').click(function(e) {
           document.contactus.ucookie.value = readCookie('contact_utm_cookie');
          document.contactus.pcookie.value = readCookie('contact_utm_preferrer');
            var utmCookie= $('#utmCookie').val(); 
            var preferrerCookie= $('#preferrerCookie').val(); 
            var lastLandPage = preferrerCookie ;
           //alert("utmcookie:"+utmCookie);
             //alert("preferrerCookie:"+preferrerCookie);
       var refUrl;
     var iPage;
           var utpage;var initPage;var refParam;
      var uca = utmCookie.split(',');
     if( uca.length >0){
           //alert("length:"+ uca.length);
           refUrl = uca[0];
           //alert("refurl:"+refUrl);
            iPage = uca[1];
           //alert("iPage:"+iPage);
           if(iPage.indexOf('?') > 0){
             utpage = iPage.split('?');
             initPage = utpage[0];
             refParam =  utpage[1];
               //alert("initPage:"+initPage);
               //alert("refParam:"+refParam);
          }else{

              initPage = iPage;
              refParam = "";
               //alert("initPage:"+initPage);
           }
       }

     else{

         refurl = "";
         initPage = "";
         refParam = "";
       }
			var noError = true;

			$('#contact-form input[type="text"],#contact-form input[type="email"],#contact-form textarea').each(function (){
			if(!$(this).hasClass('not-mandatory')){
				if(!$(this).is(':disabled')){
					if($(this).val() == ''){
					$(this).next().find('.error').show();
					noError = false;
					}
				}
            }
			});

			$('.btn-group.bootstrap-select .dropdown-toggle').each(function (){
				if ($(this).children('.filter-option').text() == $(this).parent().find('.dropdown-menu.inner li:first-child').text()){
					$(this).parents('.Subra-contact-us-form-input').find('.error').show();
					noError = false;
				}
                });
        var recapresp= jQuery.trim($('#g-recaptcha-response').val());
        if (!recapresp) {
      e.preventDefault();
            $('.recaptcha').find('.error').show();
     }
      else if(noError && recapresp){
     $("#loading_gif").css("display", "block");
     $('.recaptcha').find('.error').hide();
    var myFirst= $('#FirstName').val() ; 
    var myLast= $('#LastName').val() ; 
    var email=$('#Email').val();
    var jobTit=$('#job').val();
    var company= $('#enter-name').val() ; 
    var compName= $('#companyName').val() ; 
    var inqType= $('#select-others').val() ; 
    var othInq=$('#othInq').val();
    var code= $('#code').val() ; 
    var country=  $('#fctry').val() ; 
    var phone= $('#number').val() ;
    var indBlg= $('#indBlg').val() ;
    var descInq=$('#descriptionofinquiry').val();



                        //Use JQuery AJAX request to post data to a Sling Servlet



           $.ajax({
         type: 'GET',    
         url:'/bin/contactform',
         data:'firstName='+myFirst+'&lastName='+ myLast+'&organization='+ company+'&g_recaptcha_response='+recapresp+'&email='+ email+'&jobTit='+ jobTit+'&compName='+ compName
               +'&inqType='+ inqType+'&othInq='+ othInq+'&code='+ encodeURIComponent(code)+'&country='+ country+'&phone='+ phone+'&indBlg='+ encodeURIComponent(indBlg)+'&descInq='+ descInq+'&refUrl='+encodeURIComponent(refUrl)+'&iPage='+encodeURIComponent(initPage)+'&refParam='+encodeURIComponent(refParam)+'&lastLandingPage='+encodeURIComponent(lastLandPage),
         dataType: "json",
         success: function(msg){
               $("#loading_gif").css("display", "none");

          // var json = jQuery.parseJSON(msg); 

            var status = msg.status;


        if(status == true){
             window.location.href= '/content/demo/en/successpage.html';
            }
         else{
             //var errormsg = json.error;
               var errormsg = msg.error;
              alert(errormsg);
           }
         },
         error:function(jqXHR, textStatus, errorThrown){
                                           alert("inside Contact US error");
                                           alert("Contact US jqXHR: "+jqXHR.responseText);
                                           alert("Contact US textStatus: "+textStatus);
                                           alert("Contact US errorThrown: "+errorThrown);
              }
     });
}
			if(!noError){
				e.preventDefault();
			}

		});

		$('#select-others').on('changed.bs.select', function (e, clickedIndex, newValue, oldValue) {
		var selected = $(e.currentTarget).val();
		if(selected == 'Others'){
		$('.others').removeAttr("disabled");
		}
		else if(selected != 'Others'){
		$('.others').prop('disabled', true);
	}
	});
		$('#enter-name').on('changed.bs.select', function (e, clickedIndex, newValue, oldValue) {
		var selected = $(e.currentTarget).val();
		$('.hidden-input span').html('*Enter ' + selected + ' Name');
		$('.hidden-input input').attr('name', selected );
		$('.hidden-input').show(); 

		if(selected == ''){
			$('.hidden-input').hide(); 
		}
	});
	$('.reset').click(function (){
        grecaptcha.reset();
	$('.error').hide();
	$(".selectpicker").val('default');
	$(".selectpicker").selectpicker("refresh");
	});
	$('input, textarea').focus(function(){
	 $(this).next().find('.error').hide();
	});
	$('.btn-group.bootstrap-select').click(function (){
	$(this).parents('.Subra-contact-us-form-input').find('.error').hide();
	});
	/*     $('.selectpicker').on('shown.bs.select', function () {
			$('html, body').css('overflow','hidden');
		});
		$('.selectpicker').on('hide.bs.select', function () {
			$('html, body').css('overflow-y','auto');
		}); */
		function trimspaces(el) {
		el.value = el.value.
		   replace (/(^\s*)|(\s*$)/gi, ""). // removes leading and trailing spaces
		   replace (/[ ]{2,}/gi," ").       // replaces multiple spaces with one space 
		   replace (/\n +/,"\n");           // Removes spaces after newlines
		return;
	}
	$('input, textarea').change(function(){
		trimspaces(this);
	});
	
	$('input[type=email]').on('keypress', function(e)
      {
      if (e.which == 32){
         return false;
      }
      else{
         return true;
      }
  });

	$('input[type=email]').change(function(e) {
		  var filter = /^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
			var sEmail = $('input[type=email]').val();
			if ($.trim(sEmail).length == 0) {
			   
				e.preventDefault();
			}
			if (filter.test(sEmail)) {
			}
			else {
			  $(this).parents('.Subra-contact-us-form-input').find('.error').show();
				e.preventDefault();
			}
		});
           $('.inline-input').keydown(function(event) {
                // Allow special chars + arrows 
                if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 
                    || event.keyCode == 27 || event.keyCode == 13 
                    || (event.keyCode == 65 && event.ctrlKey === true) 
                    || (event.keyCode >= 35 && event.keyCode <= 39)){
                        return;
                }else {
                    // If it's not a number stop the keypress
                    if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 )) {
                        event.preventDefault(); 
                    }   
                }
            });
});