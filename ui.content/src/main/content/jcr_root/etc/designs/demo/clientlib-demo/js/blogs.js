

$(document).ready(function(){
    $('#get-list').hide();
    $('#set-list').hide();
    var lsitHtml = $('#get-list').html();
    $('#set-list').html(lsitHtml);
    $('#blog-year-select').addClass('selectpicker');
    $('#set-list').show();
    var currentPagePath = $("#blog-current-page-path").val();

    //Dorp down Change
    $("#blog-year-select").change(function(){
        $("#blog-search-text").val("");
        var year = $("#blog-year-select :selected").text();
        loadContent(year, currentPagePath, '/bin/blogservlet');
    });

    //Search Click
    $("#blog-search-button").click(function(){
        var year = $("#blog-search-text").val();
        if(year.length > 0){
            loadContent(year, currentPagePath, '/bin/blogsearchservlet');
        }else{
            $(".blog-noresult").empty();
            loadContent("Archives", currentPagePath, '/bin/blogservlet');
        }
    });
    //Search Click
    $("#blog-reset-button").click(function(){
            $("#blog-search-text").val('');
            $("#blog-year-select").val("Archives").change();
            $(".blog-noresult").empty();
            loadContent("Archives", currentPagePath, '/bin/blogservlet');
    });

    //Load Content from servlets
    function loadContent(filter, path, url){
        $(".blog-noresult").empty();
        $.ajax({
                type: "GET",  
                url : url,
                data:{ 
                     year: filter,
                     pagePath: encodeURI(path)
                },
                success: function(data){     
                    var b = $.parseJSON(data);
                    console.log("data::",b);
                    if(b.length > 0){
                        $(".blog-list-content").empty();
                       // alert("Emptied");
                        $.each(b, function(i,item){
                            //console.log("data::",item);
                            var result = '<div class="Subra-case-studies-bg"><div><div class="Subra-case-studies-bg-textBox-head" style="background-color:'+item.color+';">'+item.category+'</div><div class="row" style="margin:0;"><div class="text-akkurat-light text-akkurat-bold"><h4 class="blog-title"><a href="'+item.blogPageLink+'" target="_blank">'+item.title+'</a></h4><p class="blog-description">Posted by: <a href="'+item.authorPageLink+'" target="_blank">'+item.author+'</a> | '+item.publishDate+'</p><p class="blog-description">'+item.description+'</p></div></div></div></div>'
                            $(result).appendTo('.blog-list-content');
                        });
                    }else{

                        loadContent("Archives", currentPagePath, '/bin/blogservlet');
                        var noresult = '<h4 style="text-align: center;"><i>No Results Found</i></h4>'
                        $(noresult).appendTo('.blog-noresult');
                    }

                },
                error:function(jqXHR, textStatus, errorThrown){
                    alert("Error"); 
                    alert("jqXHR: for linked in"+jqXHR.responseText);
                    alert("textStatus:for linked in "+textStatus);
                    alert("errorThrown: for linked in "+errorThrown);
                }
            });
        }
    $(window).on('scroll', function () {
    var trigger = $(".blog-list-content > .Subra-case-studies-bg:visible").last();
        //alert(trigger);
    if (isScrolledIntoViewBlog(trigger)) {
        $(".blog-list-content > .Subra-case-studies-bg:hidden").first().show();
    }
});

function isScrolledIntoViewBlog(elem) {
    if ($(elem).length == 0) {
        return false;
    }
    var docViewTop = $(window).scrollTop();
    var docViewBottom = docViewTop + $(window).height();
    var elemTop = $(elem).offset().top + ($(elem).height() / 2);
    var elemBottom = elemTop + $(elem).height();
    //  return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop)); // it will only work for text
    return (docViewBottom >= elemTop && docViewTop <= elemBottom);
}
});


