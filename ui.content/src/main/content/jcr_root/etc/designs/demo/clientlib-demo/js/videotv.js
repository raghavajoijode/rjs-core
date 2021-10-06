$(document).ready(function () {
    $('.popup.Subratv .transparent').on('click', function(e) {
 
        if (e.target == this)
        //return;

        parent.history.back();
        return false;
    });

    $('.popup.Subratv .popup-video').on('click', function(ev) {
    $('#stwrapper').css('display','none');
                });
 
    $('.popup.Subratv .sub-menu-part .sub-menu-logos a img').on('click', function(evt) {
    $('#stwrapper').css('display','block');
                });

    if ($('div').hasClass('Subratv')) {
        $( ".figure" ).trigger( "click" );
    }

    //$(window).scrollTop(0);
    //$(".popup.Subratv .play-button-1").trigger("click");
    //$( ".popup.Subratv .figure" ).trigger( "click" );


});

$(document).keydown(function (e) {
    // ESCAPE key pressed
    if ((e.keyCode == 27) && ($('div').hasClass('Subratv'))) {
        //$('.Subratv, .plus-popup').hide();
         //jwplayer().stop();
        parent.history.back();
        return false;
    }
});