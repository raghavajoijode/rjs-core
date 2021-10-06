$(function(){
	const _carouselBlock= $('.subra-carousel').get(0);
	const _bredcrumbBlock= $('.breadcrumb').get(0);
	
	//console.log(_carouselBlock.getBoundingClientRect());
	$(document).scroll(function(){
		//console.log(_carouselBlock.getBoundingClientRect().top , _bredcrumbBlock.getBoundingClientRect().top , $(window).scrollTop());
	});
});


//Back to top - Start COPIED
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
//Back to top - End

//Load data-htmlinclude - Start
$(function(){
	$('div[data-htmlinclude]').each(function() {
		let $this = $(this)
		$this.load($this.data('htmlinclude'))
		//$this.load($this);
	});
});
//Load data-htmlinclude - End

//Mansonry - Start
$(function(){
	$('.subra-masonry-grid').masonry({
	  itemSelector: '.masonry-item',
	  gutter: 16,
	  fitWidth: true
	});
});
//Mansonry - End

