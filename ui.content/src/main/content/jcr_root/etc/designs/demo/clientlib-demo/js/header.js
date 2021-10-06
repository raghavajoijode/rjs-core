/* Header component Starts */

/* Scroll component Starts */

	function setCookie(cname,cvalue,exdays) {
    	var d = new Date();

    	d.setTime(d.getTime() + (exdays*24*60*60*1000));
    	var expires = "expires=" + d.toGMTString();
        var shortPathval = window.location.pathname;
        var cookieval = JSON.stringify({fullPath: cvalue, shortPath: shortPathval});
    	document.cookie = cname + "=" + cookieval + ";" + expires + ";path=/";
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

	function getAllCookies() {
        var cam=[];
        var ca = document.cookie.split(';');
        for(var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf("NEX") == 0) {
                var x= c.substring(c.indexOf('=')+1);
				//alert("pushihng X "+x);
				cam.push(x);
            }
        }
		$.ajax({
                 type:'GET',  
                 url :'/bin/mylikedarticlesservlet',
                data:{
                    likedarray : cam.toString()

                },
                success:function(response){

                               //var json = JSON.stringify(response);  
                var data=$.parseJSON(response);
              //  loadTable(data);

                               console.log(" Response ____ "+data);

                               document.getElementById("likecount").innerHTML = data.likedcount;
                    result = 	data.resultData;
					$( "#likeResult" ).empty();
                  //  result = result.replace(/[]/g, "");
                  //  var starting='<div class="search"><div class="Subra-showmore-section  bg-color-white"><div class="container"><div class="row">';
                    var title='<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 show-more-title"><div class="Subra-section-head-wrapper"><div class="innerpage"><h2><span class="content-sec-header-h2-akkurat-mono">Favourite Articles</span></h2></div></div></div>';
                   // var finalResult=[];
                   // $(starting).appendTo('#likeResult');
                   $(title).appendTo('#likeResult');
					//finalResult.push(starting);
                   // finalResult.push(title);
                    for(i=0; i<result.length; i++){
                        var myDesc=result[i].pageDesc;
                       // alert(myDesc);
                        if(myDesc==null){
								myDesc="";
                         //   alert(myDesc);
                        }
                       var singleResult= '<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12"><div class="showmore"><div class="showmore-image"><a href="'+result[i].pagePath+'"><img alt="" src="'+result[i].pageImage+'" class="multi-image" data-hd="'+result[i].pageImage+'" data-sd="'+result[i].pageImage+'" data-mob="'+result[i].pageImage+'" /></a></div><div class="showmore-textBox"><div class="showmore-textBox-head" style="background-color:'+result[i].tagColor+';">'+result[i].tagTitle+'</div><div class="showmore-textBox-text text-akkurat-light"><a href="'+result[i].pagePath+'"><h4>'+result[i].pageTitle+'</h4><p>'+myDesc+'</p></a></div></div></div></div>';
						//finalResult.push(singleResult);
						$(singleResult).appendTo('#likeResult');
                      //  alert("aaaaaeeeee "+result[i].pageTitle);
                    }
                   // var ending='</div></div></div></div>';
                   // finalResult.push(ending);
                   // $(ending).appendTo('#likeResult');
                    //document.getElementById("likeResult").innerHTML=finalResult;

                },
                            error:function(jqXHR, textStatus, errorThrown){
                                                           //alert("inside error like servlet servlet");

                              }
                        });

        return "";
    }
// function loadTable(data) {
            // Grab the template script
  //          var theTemplateScript = $("#favourite-articles-template").html();
//
            // Compile the template
    //        var theTemplate = Handlebars.compile(theTemplateScript);

            // Pass our data to the template
      //      var theCompiledHtml = theTemplate(data);
        //    console.log(' theCompiledHtml 5',theCompiledHtml);
          //   $('#favourite-articles').html(theCompiledHtml);

            //}

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

	$(document).ready(function() {
	function flex(){
	if($(window).width() >= 1025 && $(window).width() <= 1680){

    $('.menu-list-overlay-container-one').each(function(){
	var ab = $(this).children('.menu-list-overlay-content').length;
	if(ab > 30){
	//$(this).height(507);
	$(this).css('height','100vh');
	$(this).css('padding-bottom','120px');
	}
	});

    $('.menu-list-overlay-container-one').each(function(){
	var cde = $(this).children('.menu-list-overlay-sub-content').length;
    var fgh = $(this).children('.menu-list-overlay-sub-content-2').length;
	if(cde+fgh > 30){
    //$(this).height(507);
	$(this).css('max-height','100vh');
	$(this).css('padding-bottom','120px');
	}
	});

    $('.menu-list-overlay-container-one.ggr').each(function(){
	//$(this).height(507);
	$(this).css('max-height','100vh');
	$(this).css('padding-bottom','120px');
    });

	}
	if($(window).width() >= 1900){
	$('.menu-list-overlay-container-one').each(function(){
	var ab = $(this).children('.menu-list-overlay-content').length;
	if(ab > 20){
	//$(this).height(507);
	$(this).css('height','100vh');
	$(this).css('padding-bottom','150px');
	}
	});
	}
	if($(window).width() <= 1024){
	$('.menu-list-overlay-container-two').each(function(){
	var cb = $(this).children('a').length;
	var cbHeight = $(window).height() - 120;
	if(cb > 5){
	$(this).height(cbHeight);

	}
	});
	}
	}
	flex();
	$(window).resize(function() {
	flex();
	});
	$(document).on('scroll', function (e) {
	$('.sub-menu-content .black-background').css('opacity', (($(document).scrollTop() / 570) > 0.3 ? ($(document).scrollTop() / 570) : 0.3));
		});
	$(document).on('scroll', function (e) {
    if($(this).scrollTop() >= 570) {
	 $('.sub-menu-content .sub-menu-part p').css('opacity','1');
    }
	else{
		$('.sub-menu-content .sub-menu-part p').css('opacity','0');	
	}
	});
	});

	$(document).ready(function(){

	$(".menu-items table tr td a").click(function (event) {
		//alert(0);
		event.stopPropagation();
		var hrefVal = $(this).attr('href');
		//alert(hrefVal);
		if (hrefVal == "javascript:void(0)"){
			$(this).parent().toggleClass("active");
			$(".menu-items table tr td a").not(this).parent().removeClass('active');	
			if($(this).parent().hasClass('active')){
			$('.menu-list-overlay').slideDown();
			$(".fade-class-1").addClass("show-class");
			$('html').css('overflow','hidden');
			$('html').addClass('ipad_no_scroll');
			$('html').removeClass('ipad_scroll');

			}
			else{
			$('.menu-list-overlay').slideUp();	
			$('html').css('overflow','auto');
			$('html').addClass('ipad_scroll');
			$('html').removeClass('ipad_no_scroll');
			$(".fade-class-1").removeClass("show-class");
            $('.banner-container').slick('slickPlay');
			}
		}
	});
	$(document).click(function (event) {
			if($('.menu-items table tr td').hasClass('active')){
				$(".menu-list-overlay").slideUp(); 
				//alert("done3");
				$(".slide-down-menu").removeClass("active");
				$('html').css('overflow','auto');
				$(".fade-class-1").toggleClass("show-class");
				$(".menu-items table tr td a").removeClass('active');
                $('.banner-container').slick('slickPlay');
			}
	});
	$('.menu-list-overlay').click(function(event){
		event.stopPropagation()
	});
	$(".navbar-section-3 table tr td:last-child").click(function () {
	$(".dropdown-section").addClass("slideleft");
	$(".fade-class").css("display","block");
	$(".menu-list-overlay").slideUp();
	$(".fade-class-1").removeClass('show-class');
	$(".menu-items table tr td a").removeClass('active');
    //$('.banner-container').slick('slickPlay');
    $('.banner-container').slick('slickPause');

    $('html').css('overflow','hidden');
	$('html').addClass('ipad_no_scroll');
	$('html').removeClass('ipad_scroll');
			getAllCookies();
	});
	$(".close-button-sidebar").click(function () {
	$(".dropdown-section").removeClass("slideleft");
	$(".fade-class").css("display","none");
    $('.banner-container').slick('slickPlay');
	$('html').css('overflow','auto');
	$('html').removeClass('ipad_no_scroll');
	$('html').addClass('ipad_scroll');
	});
	$(".fade-class").click(function () {
	$(".dropdown-section").removeClass("slideleft");
	$(".fade-class").css("display","none");
    $('.banner-container').slick('slickPlay');
	$('html').css('overflow','auto');
	$('html').removeClass('ipad_no_scroll');
	$('html').addClass('ipad_scroll');
	});
	$(".dropdown-section-content-header a").click(function (){
		var hrefVal = $(this).attr('href');
		//alert(hrefVal);
		if(hrefVal == "javascript:void(0)"){
			if($(this).hasClass('slide-down-menu')){
			$(".menu-list-overlay").slideToggle();
			$(".dropdown-section").removeClass("slideleft");
			$(".fade-class").css("display","none");
			$(".fade-class-1").toggleClass("show-class");
			}
			else{
			$(".menu-list-overlay").slideUp();
			$(".fade-class-1").removeClass("show-class");
			}
		}
	});

	$(".menu-items table tr td a").click(function (event){
		event.stopPropagation();
		var hrefVal = $(this).attr('href');
		//alert(hrefVal);
		if (hrefVal == "javascript:void(0)"){
			var index = $(this).parent().index();
			$(".menu-list-options").each(function(){
			var index1 = $(this).index();
			if(index == index1){
			$(".menu-list-options").hide();
			$(this).show();	
			}
			});
		}
        //
   if($('.fade-class-1').hasClass('show-class')){
   $('.banner-container').slick('slickPause');
   }
   else {
   $('.banner-container').slick('slickPlay');
   }
});
	
	$(".dropdown-section-content-header a").click(function (){
		var hrefVal = $(this).attr('href');
		//alert(hrefVal);
		var index = $(this).index();
		$(".menu-list-options").each(function(){
		var index1 = $(this).index();
		if(index == index1){
		$(".menu-list-options").hide();
		$(this).show();	
		}
		});
	});

	$(".menu-list-overlay-sub-content").click(function(){
	$(".menu-list-overlay-sub-content").removeClass("active");	
	$(this).addClass("active");	
	});
	
	$(".cross-button").click(function () {
$(".menu-list-overlay").slideUp(); 
$(".fade-class-1").toggleClass("show-class");
$('.menu-items table tr td a').removeClass("active");
        $('.banner-container').slick('slickPlay');
});
$(".fade-class-1").click(function () {
$(".menu-list-overlay").slideUp(); 
//alert("done3");
	$(".slide-down-menu").removeClass("active");
$('html').css('overflow','auto');
$(".fade-class-1").toggleClass("show-class");
$(".menu-items table tr td a").removeClass('active');
    $('.banner-container').slick('slickPlay');
});
if($(".fade-class-1").hasClass("show-class")){
$("body").css("overflow","hidden");
}
$('.plus').click(function(){
$('.plus-popup').slideToggle('slow', function() {
if ($('.plus-popup').is(':hidden'))
{
$('.plus').removeClass('plusIcon');
}
else
{
$('.plus').addClass('plusIcon');

}
});             
$('body').css('overflow', 'hidden');
});
$('.plus-popup').click(function(e){
if(e.target == this){
$(this).hide();
$('body').css('overflow-y', 'scroll');
$('.plus').removeClass('plusIcon');
}
});
if($(window).width() <= 767){
if($(".sub-menu").is(":visible")){
$(".home-banner").css("padding-top","130px");
}

    var count_anchor = $(".col-lg-2.col-md-2.col-sm-2.col-xs-2 .sub-menu-part .sub-menu-logos>a").length
    var count_span = $(".sub-menu-part .sub-menu-logos>span").length 
    //alert(count_anchor);
    //alert(count_span);
    if(count_anchor == 1 && count_span == 0){
        $('.plus').css('display','none');
        //$(".only_share_icon").css('display','block');
        $(".sub-menu-content .sub-menu-logos").css('display','block');
        $(".sub-menu-content .sub-menu-logos").css('float','right');
        
    }
    else {
        $('.plus').css('display','block');
    }

}

});

document.onkeydown = function(evt) {
    evt = evt || window.event;
    if (evt.keyCode == 27) {
        	$('.menu-list-overlay').slideUp();
			$(".slide-down-menu").removeClass("active");
			$('html').css('overflow','auto');
			$('html').addClass('ipad_scroll');
			$('html').removeClass('ipad_no_scroll');
			$(".fade-class-1").removeClass("show-class");
            $('.banner-container').slick('slickPlay');
    }
}


$('.slide-down-menu.ggr2 a').click(function(){
	//$( '.menu-list-overlay-container-one.ggr' ).scrollTop( 300 );
    $(".menu-list-overlay-container-one.ggr").animate({
            scrollTop: 0
        }, 300);
        return false;
});

$(document).ready(function() {
    	$(".menu-list-overlay-container-one").niceScroll({
    		cursorwidth: '10px', 
    		autohidemode: false, 
    		zindex: 999,
    		cursorborder : 'none',
    		cursorborderradius : '3px',
            cursorcolor : '#4b5a71'
    		});
});


/* burger menu last child line 

$(".dropdown-section-content-text.dropdown-section-content-text-h10:last").addClass('dropdown-section-content-text-h10-last');
 burger menu last child line */


/* Header component Ends */