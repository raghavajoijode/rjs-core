
/* Scroll component Starts */

	function setCookie(cname,cvalue,exdays) {
    	var d = new Date();
    	d.setTime(d.getTime() + (exdays*24*60*60*1000));
    	var expires = "expires=" + d.toGMTString();
    	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    	//alert("added");
		$(".add-cookie").hide();
		$(".delete-cookie").show();
		}
    function deleteCookie(cname) {
    	var d = new Date();
    	d.setTime(d.getTime() - (30*24*60*60*1000));
    	var expires = "expires=" + d.toGMTString();
    	document.cookie = cname + "= ;" + expires + ";path=/";
    	//alert("deleted");
        $(".delete-cookie").hide();
		$(".add-cookie").show();
		}	 


    function getCookie(cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for(var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        return "";
    }
    function checkCookie(checkCname) {
        var user = getCookie(checkCname);
        if (user != "") {
            $(".add-cookie").hide();
        } else {
           $(".delete-cookie").hide();
        	}
        }


$(document).ready(function () {
	var cookieVal;
	$(".share-this-icons").hide();

   	$(".share-button").click(function(){
        $(".share-this-icons").toggle();
    });

    $('.add-cookie').each(function (index, element) {
        cookieVal = $(this).attr('data-cookie');
	});
    checkCookie(cookieVal);
});
/* Scroll component Ends */
/* Menu JavaScript Starts*/
// JavaScript Document
$(document).ready(function(e) {
	if($(window).width() > 767){
        $('.sub-menu-content .black-background').css('opacity', 0);
        $('.sub-menu-content .sub-menu-part p').css('opacity', 0);
		$(document).on('scroll', function (e) {
			$('.sub-menu-content .black-background').css('opacity', ($(document).scrollTop() / 570));
			$('.sub-menu-content .sub-menu-part p').css('opacity', ($(document).scrollTop() / 570));
		});
	}
});
/* Menu JavaScript Ends*/
