$(function () {
    $(".Subra-solutions-squares-content").slice(0, 8).show();
    $("#loadMore").on('click', function (e) {
        e.preventDefault();
        $(".Subra-solutions-squares-content:hidden").slice(0).show();
        //if ($(".Subra-solutions-squares-content:hidden").length == 0) {
            //$("#loadMore").html('Show less');
            $("#loadMore").css('display','none');
            $("#showless").css('display','inline-block');
        //}

        $('html,body').animate({
            scrollTop: ($('#showless').offset().top -550)
        }, 1000);
	$(".Subra-tv .Subra-solutions-squares-content-image .Subra-solutions-squares-content-text h4").css('height','auto');

    });
    $("#showless").on('click', function (e) {
        e.preventDefault(); 
        $(".Subra-solutions-squares-content").slice(8).hide();
        $("#loadMore").css('display','inline-block');
        $("#showless").css('display','none');

        $('html,body').animate({
            scrollTop: ($('#loadMore').offset().top - 550)
        }, 1000);

        $(".Subra-tv .Subra-solutions-squares-content-image .Subra-solutions-squares-content-text h4").css('height','auto');
    });
        
    $(".Subra-tv .Subra-solutions-squares-content-image .Subra-solutions-squares-content-text h4").css('height','auto');
});

// $('a[href=#top]').click(function () {
//     $('body,html').animate({
//         scrollTop: 0
//     }, 600);
//     return false;
// });

// $(window).scroll(function () {
//     if ($(this).scrollTop() > 50) {
//         $('.totop a').fadeIn();
//     } else {
//         $('.totop a').fadeOut();
//     }
// });