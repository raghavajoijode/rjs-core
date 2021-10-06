$(function(){
	$('.subra-scroll-top').click(function(){
		$('html,body').animate({
			scrollTop:0
		}, 800, 'swing')
		return false
	});
	$(window).scroll(function() {
		if ($(this).scrollTop() > 50 ) {
			$('.subra-scroll-top').removeClass('d-none');
		} else {
			$('.subra-scroll-top').addClass('d-none');
		}
	});
});