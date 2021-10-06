$(document).ready(function () {
    if ($(window).width() > 1599) {
        // large crisp images for HD screens
        $('img.multi-image').each(function (index, element) {
            var url = $(this).attr('data-hd');
            $(this).attr('src', url);
        });
    } else if ($(window).width() > 960) {
        // standard images for regular screens
        $('img.multi-image').each(function (index, element) {
            var url = $(this).attr('data-sd');
            $(this).attr('src', url);
        });
    } else {
        // small sized images for mobile screens
        $('img.multi-image').each(function (index, element) {
            var url = $(this).attr('data-mob');
            $(this).attr('src', url);
        });
    }
});
/* Multi Image Starts */

/* Scroll to top */
$(document).ready(function () {

    $(window).scroll(function () {
        if ($(this).scrollTop() > 100) {
            $('.scrollup').fadeIn();
        } else {
            $('.scrollup').fadeOut();
        }
    });

    $('.scrollup').click(function () {
        $("html, body").animate({
            scrollTop: 0
        }, 600);
        return false;
    });

});



/* Equal Height */
$(document).ready(function() {

        //equal height script
equalheight = function(eHeight){
var currentTallest = 0,
     currentRowStart = 0,
     rowDivs = new Array(),
     $el,
     topPosition = 0;
 $(eHeight).each(function() {

   $el = $(this);
   $($el).height('auto')
   topPostion = $el.position().top;

   if (currentRowStart != topPostion) {
     for (currentDiv = 0 ; currentDiv < rowDivs.length ; currentDiv++) {
       rowDivs[currentDiv].height(currentTallest);
     }
     rowDivs.length = 0; // empty the array
     currentRowStart = topPostion;
     currentTallest = $el.height();
     rowDivs.push($el);
   } else {
     rowDivs.push($el);
     currentTallest = (currentTallest < $el.height()) ? ($el.height()) : (currentTallest);
  }
   for (currentDiv = 0 ; currentDiv < rowDivs.length ; currentDiv++) {
     rowDivs[currentDiv].height(currentTallest);
   }
 });
}
/* Link Rel */
$(document).ready(function(){
    if($('.hyperlink .innerpage a').attr('href').includes("https") ||$('.hyperlink .innerpage a').attr('href').includes("http"))
    {
      $('.hyperlink .innerpage a').attr("rel","nofollow"); 
    }
});
/*Link Rel */
/*Subra TV */
$(window).on('load', function() {
  equalheight('.Subra-tv .Subra-solutions-squares-content-text h4');
});

$(window).resize(function(){
  equalheight('.Subra-tv .Subra-solutions-squares-content-text h4');
});
/*Subra TV */
$(document).ready(function() {
    equalheight('.eqheightcontainer-col-3'); 

});

$(window).resize(function(){
    equalheight('.eqheightcontainer-col-3');
});



$(window).resize(function(){
  equalheight('.eqheightcontainer-col-2');
});
$(document).ready(function() {
  equalheight('.eqheightcontainer-col-2');
});
/*$(window).on('click', function() {
  equalheight('.menu-list-overlay .menu-list-overlay-sub-content');
});
$(window).on('load', function() {
  equalheight('.menu-list-overlay .menu-list-overlay-sub-content');
});
$(window).resize(function(){
  equalheight('.menu-list-overlay .menu-list-overlay-sub-content');
});*/

/*$(window).on('load', function() {
  equalheight('.related-links-content-box');
});

$(window).resize(function(){related-links-content-box media-content-box
  equalheight('.related-links-content-box');
});*/

/*
$(window).on('load', function() {
  equalheight('.related-links-content-box ul');
});

$(window).resize(function(){
  equalheight('.related-links-content-box ul');
});

$(window).on('load', function() {
  equalheight('.Subra-solutions-squares-content-text h4');
});

$(window).resize(function(){
  equalheight('.Subra-solutions-squares-content-text h4');
});


$(window).on('load', function() {
  equalheight('.article-section .content-h13-akkurat-mono');
});

$(window).resize(function(){
  equalheight('.article-section .content-h13-akkurat-mono');
});*/
});
/* segments solutions */
$(function(){
  $(".Subra-solutions-squares-content-text p").each(function(i){
    len=$(this).text().length;
    if(len>200)
    {
      $(this).text($(this).text().substr(0,200)+'...');
    }
  });       
});

/*$(function(){
  $(".Subra-solutions-squares-content-text h4").each(function(i){
    len=$(this).text().length;
    if(len>10)
    {
      $(this).text($(this).text().substr(0,20)+'...');
    }
  });       
});*/
$(function(){
  $(".article-section p").each(function(i){
    len=$(this).text().length;
    if(len>10)
    {
      $(this).text($(this).text().substr(0,120)+' ');
    }
  });       
});
/*$(function(){
  $(".Subra-case-studies-bg-textBox-text h4").each(function(i){
    len=$(this).text().length;
    if(len>40)
    {
      $(this).text($(this).text().substr(0,60)+'...');
    }
  });       
});*/

$(function(){
  $(".Subra-case-studies-bg-textBox-text p").each(function(i){
    len=$(this).text().length;
    if(len>120)
    {
      $(this).text($(this).text().substr(0,120)+' ');
    }
  });       
});
$('.slide .slide-content h1').filter( function(){ return $(this).text().length<8; }).addClass('background-color');

$(function(){
  $(".drop-down-selections .related-links-content-box p").each(function(i){
    len=$(this).text().length;
    if(len>30)
    {
      $(this).css('display','block');
    }
  });       
});
$(document).ready(function(){
   $(".aem-GridColumn.leadership").parent().parent().addClass('leadership-section');
   $(".aem-GridColumn.segments").parent().parent().parent().css('margin-bottom', '25px');
   $(".aem-GridColumn.solution-offering").parent().parent().parent().css('margin-bottom', '25px');
          
		/*  if($('.fade-class-1').hasClass('show-class')){
             $('.banner-container').slick('slickPause');
           }
           else{
             $('.banner-container').slick('slickPlay');  
                  } */
				

});

/*
$(document).ready(function(){

  $('.tab-link').click(function(){
  var tabLinkId = $(this).children('a').attr("href");

      var selector = tabLinkId +" "+".selectpicker";
       // alert(selector +"dsds --- "+ $(selector).children('option').first().attr("value"));
  var firstValue = $(selector).children('option').first().attr("value");
  //alert(firstValue);
  if(firstValue != undefined){
      $('.selectpicker').val(firstValue).change();
      }
     }); 

});
*/
