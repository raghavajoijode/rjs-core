$(document).ready(function () {

    $('.search-block .search-box .input-field input').on('click focus', function () {
        if ($(window).width() > 1025) {
            $('.search-block .search-box').animate({ "padding-top": "140px" });
            $(document).scrollTop('0px');
        }
    });

    $('.search-block .search-box .input-field input').on('typeahead:selected', function (e, item) {
        redirectToSearch();
    });

    $('.search-block .search-box .input-field button').click(function (e) {
        redirectToSearch();        
    });

    $('#search-overlay').on('shown.bs.modal', function () {
        if (keyword !== '') {
            $('.search-block .search-box .input-field input:not(:focus)').focus().val(keyword);
        }
    });

    $('#search-overlay').on('hidden.bs.modal', function () {
        keyword = "";
        $('.search-block .search-box .input-field input').val('');
    });

    $('#search-overlay').on('shown.bs.modal', function (){
    $('html').css('overflow','hidden');
});
 $('#search-overlay').on('hidden.bs.modal', function () {
    $('html').css('overflow','auto');
 });


});

var keyword = "";
$(document).keypress(function (e) {
   
    if ($('#search-overlay').is(':visible')) {
        if (e.keyCode === 13) {
            redirectToSearch();
        } else {
            if (!$('input, textarea').is(':focus')) {
                keyword = keyword + String.fromCharCode(e.which);
                var key = String.fromCharCode(e.which);
                var allowedCharacters = /[A-Za-z0-9]/g;
                if (allowedCharacters.test(key)) {
                    if ($('#search-overlay').is(':visible')) {
                        setTimeout(function(){
                            $('.search-block .search-box .input-field input:not(:focus)').focus().val(keyword);
                        }, 250);
                    } else {
                        $('#search-overlay:not(:visible)').modal('show');
                    }
                }
                return true;
            }
        }
    }       
});
$(document).keypress(function (e) {

var counter = true;
if($("input").is(':focus') || $("button").is(':focus') || $("select").is(':focus') || $("textarea").is(':focus') || $('div.dropdown-menu.open').is(':visible')){
    counter=false;
}else{
    counter=true;
}
    if (e.keyCode === 13) {
        redirectToSearch();        
    } else if(counter){
        $('#search-overlay:not(:visible)').modal('show');
        setTimeout(function(){
            $('.search-block .search-box .input-field input:not(:focus)').focus().val(String.fromCharCode(e.keyCode));
        },500);        
    }
});


function redirectToSearch(){
    var keyword = trimSpaces($('.search-block .search-box .input-field input').val());
    if(keyword === ""){
        return false;
    }else{
        keyword = keyword.split(' ').join('+');
        window.location.href = "/content/demo/en/search.html?q="+keyword;
    }    
}

function trimSpaces(text) {
    text = text.
	   replace(/(^\s*)|(\s*$)/gi, ""). // removes leading and trailing spaces
	   replace(/[ ]{2,}/gi, " ").      // replaces multiple spaces with one space 
	   replace(/\n +/, "\n");          // Removes spaces after newlines
    return text;
}