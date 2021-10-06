jQuery.expr[":"].Contains = jQuery.expr.createPseudo(function (arg) {
    return function (elem) {
        return jQuery(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});

$(document).ready(function () {
    $('#btnSearch').click(function () {
        $('.highlight').removeClass('highlight');
        var keyword = $('#txtSearch').val().trim();
        if (keyword !== '') {
            $("li:Contains('" + keyword + "')").parents('.location-content-box').addClass('highlight').parents('.tab').each(function () {
                $('.tabs .tab-link:nth-of-type(' + ($(this).index() + 1) + ')').addClass('highlight');
            });

            $('.tabs .tab-link').hide();
            $('.tabs .tab-link.highlight').show();

            $('.location-content-box').hide();
            $('.location-content-box.highlight').show();
            $('.location-content-box').parents('.address').hide();
            $('.location-content-box.highlight').parents('.address').show();
            $('.location-content-box').parents('.tab').hide();
            $('.location-content-box.highlight').parents('.tab').show();
		}
    });

    $('#btnReset').click(function () {
        $('#txtSearch').val('');
        $('.highlight').removeClass('highlight');

        $('.tabs .tab-link').show();
        $('.location-content-box').show();
        $('.location-content-box').parents('.address').show();
        $('.location-content-box').parents('.tab').show();

        $('.tabs .tab-links .tab-link:nth-of-type(1) > a .toggle-button-content-button').trigger("click");
    });
});

$(document).ready(function(){
        $('#btnSearch').click(function(){
             $(".search-location-content-box").each(function(){
                if(!$(this).hasClass('highlight')) {
                      $(this).parent().addClass('removeTD');
                }
             });

        });
        $('#btnReset').click(function(){
              $(".search-location-content-box").each(function(){
                if(!$(this).hasClass('highlight')) {
                      $(this).parent().removeClass('removeTD');
                }
             });
        });
});
