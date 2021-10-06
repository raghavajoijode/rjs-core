// JavaScript Document
$(document).ready(function () {
    $(window).scrollTop(0);
});
$(document).ready(function (e) {
    $('.banner-tags-content').click(function () {
        $('.banner-tags-content').removeClass('active');
        $(this).addClass('active');
    });
    $('.pause').on('click', function () {
        $('.banner-container')
            .slick('slickPause')
            .slick('slickSetOption', 'pauseOnDotsHover', false);
    });
    $('.play').on('click', function () {
        $('.banner-container')
            .slick('slickPlay')
            .slick('slickSetOption', 'pauseOnDotsHover', true);
    });
    $('.banner-container').slick({
        dots: true,
        autoplay: true,
        autoplaySpeed: 5000,
        infinite: true,
        slidesToShow: 1,
        slidesToScroll: 1,
        accessibility: false,
        prevArrow: false,
		pauseOnHover:false,
        nextArrow: false,
        draggable: false
    });
	if($( ".slide" ).length == 1) {
		$('.play-pause').hide();
	}
	$('.banner-container').on('afterChange', function(event,slick,i){
  $('.banner-tags-content').removeClass('active');
  $('.banner-tags-content').eq(i).addClass('active');        
});

// remember document ready on this
$('.banner-tags-content').eq(0).addClass('active');
    $('.banner-tags-content[data-slide]').click(function (e) {
        e.preventDefault();
        var slideno = $(this).data('slide');
        $('.banner-container').slick('slickGoTo', slideno - 1);
    });
    $('.banner-container').mousemove(function (e) {
        var amountMovedX = (e.pageX * -1 / 6);
        var amountMovedY = (e.pageY * -1 / 6);
        $(this).find('.slide-content').css('background-position', amountMovedX + 'px ' + amountMovedY + 'px');
    });
    $(".play").click(function () {
        $('.play').hide();
        $('.pause').show();
    });
    $(".pause").click(function () {
        $('.pause').hide();
        $('.play').show();
    });
    
    setTimeout(function(){
    if($("#video_player")[0] == undefined){

    }else{
     $('#video_player')[0].play();
    }
                         
    },100);
});