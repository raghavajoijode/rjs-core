$(document).ready(function () {
var videoShare ="",imgShare="",options="";
    $(window).scrollTop(0);

    $(document).on('click', '.figure, .show-popup', function () {
        $('.popup .popup-image').hide();
        $('.popup .popup-video').hide();
//$('.social-container-case').removeClass('social-container-case-open');

        $('.popup .popup-title').html($(this).attr('data-title'));
        $('.popup .popup-description p').html($(this).attr('data-desc'));

        if($('.popup .popup-description p').length == 0){
            $('.popup .popup-description').hide();
        }

        var play_button = $(this).find('.play-button');

        if (play_button.length == 1) {
            var thumbnail = play_button.attr('thumbnail');
            var source_hd = play_button.attr('source-hd');
            var source_sd = play_button.attr('source-sd');
            videoShare = play_button.attr('source-external');

            jwplayer("popup-player").setup({
                image: thumbnail,
                autostart: true,
                sources: [{
                    file: source_hd,
                    label: "720p HD"
                }, {
                    file: source_sd,
                    label: "360p SD",
                    "default": "true"
                }],
                width: "100%",
                height: "100%"
            });

            $('.popup .popup-video').show();
            $('body').css('overflow', 'hidden');
options="video";
        }
        else {
            imgsrc = $(this).find('img').attr('src');
            imgShare = $(this).find('img').attr('data-external');
            $(".popup .popup-image").find('img').attr('src', imgsrc);
            $('.popup .popup-image').show();
            $('body,html').css('overflow', 'hidden');
options="image";
        }
        $('.popup').show();
        $('body,html').css('overflow', 'hidden');
        function flexHeight() {
            setTimeout(function () {
                $('.transparent-image').each(function () {
                    var wh = $(window).height();
                    if ($(this).height() > 650) {
                        $(this).addClass('fixed-top');
                        $('.transparent').css('overflow-y', 'auto');
                    }
                    else {
                        $(this).removeClass('fixed-top');
                        $('.transparent').css('overflow-y', 'hidden');
                    }
                    if ($(this).height() > wh) {
                        $(this).addClass('fixed-top');
                        $('.transparent').css('overflow-y', 'auto');
                    }
                    else {
                        $(this).removeClass('fixed-top');
                        $('.transparent').css('overflow-y', 'hidden');
                    }
                });
            }, 100);
        }
        flexHeight();
        $(window).resize(function () {
            flexHeight();
        });

        $('.transparent').on('click', function (e) {
            if (e.target !== this)
                return;
            $(this).parent().hide();
$('.social-container-case').removeClass('social-container-case-open');
            $('body,html').css('overflow', 'auto');
            jwplayer().stop();
        });
//alert(this);
$('.share-icon-img-popup').unbind().click(function(event){
            event.stopPropagation();
       $('.social-container-case.video-share-container').toggleClass('social-container-case-open');
});
 $('.social-image-popup').click(function(){
            //alert(videoShare);
        
var media = $(this).attr('data-media');
var mediaTitle = $(this).attr('data-media-title');
if(options == "video")
{
urlShare(media,videoShare,mediaTitle);
}
else if(options=="image")
{
        //    alert(imgShare);
urlShare(media,imgShare,mediaTitle);
}
});
    });
});

$(document).keydown(function (e) {
    // ESCAPE key pressed
    if (e.keyCode == 27) {
        $('.popup, .plus-popup ').hide();
      jwplayer().stop();
//  $('.social-container-case').removeClass('social-container-case-open');
    }
});

//
$(".show-more-popup").on("shown.bs.modal",function(){
    $("html").css("overflow","hidden")
});
$(".show-more-popup").on("hidden.bs.modal",function(){
    $("html").css("overflow","auto")
});