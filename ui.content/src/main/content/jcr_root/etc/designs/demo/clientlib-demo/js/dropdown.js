$(document).ready(function () {
        $('.dropdown-button-content:first-child').addClass('visible'); 
    $('.selectpicker').selectpicker();
$('.dropdown-menu .inner li').click(function(){
var indexa = $('.dropdown-menu .inner li').index(this);
$('.dropdown-button-content').each(function(){
var indexb = $('.dropdown-button-content').index(this);
if(indexa == indexb){
$('.dropdown-button-content').removeClass('visible');
$(this).addClass('visible');
}
});
});
 $('.dropdown-button-content-news:gt(3)').hide(); 
	 	 function myFunction() {
			 for(var count = 4 ; count <= $('.dropdown-button-content-news').length; count++){
    if (document.body.scrollTop > $('.dropdown-button-content').height() - 150 || document.documentElement.scrollTop > $('.dropdown-button-content').height() - 150) {
	   $('.dropdown-button-content-news:lt(' + count + ')').fadeIn();
    }
}
 }
 	window.onscroll = function() {myFunction()}; 

});