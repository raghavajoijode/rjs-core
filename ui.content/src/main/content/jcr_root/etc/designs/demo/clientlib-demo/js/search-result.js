$(document).ready(function () {
  var filtercategory = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace(),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: '/bin/demoSearchSuggestionServlet?query=%QUERY',
            wildcard: '%QUERY'
        }
    });
    $('.search-block .search-box .input-field input').typeahead({
        hint: false,
        highlight: true,
        minLength: 3
    }, {
        source: filtercategory,
    });

    $('.search-block .search-box .input-field input').on('typeahead:selected', function (e, item) {
        getResults(e);
    })

    $(".mobile-devices .menu-icon").click(function () {
        $(".mobile-devices .menu-icon").toggle();
        $("#search-devices").slideToggle("slow");
    });

    $('.mobile-devices .search-devices ul li a').click(function (e) {
        e.preventDefault();
        $(".mobile-devices .menu-icon").toggle();
        $("#search-devices").slideToggle("slow");
        showResults(sortResults(getFilteredResults(), $(this).data('key'), $(this).data('order')));
    });

    $('.search-box .filter-tag').hide();
    $('.search-result-tag-second').hide();

    $('.search-box').on('click', '.search-result-tag-first .filter-tag-list a', function (e) {
        $('.search-result-tag-first .filter-tag-list a').removeClass('active');
        $(this).addClass('active');

        var index = $(this).parent().index() + 1;
        $('.search-result-tag-second').hide();
        $('.search-result-tag-second:nth-child(' + index + ')').show();
    });

    $('.search-box').on('click', '.search-result-tag-second .filter-tag-list a', function (e) {
        e.preventDefault();
        $(this).toggleClass('active');

        var tagid = $(this).data('tagid');
        var tagcategory = $(this).data('tagcategory');

        var filtertagitem = $('.filter-tag .filter-tags-container .filter-tag-list a[data-tagid="' + tagid + '"][data-tagcategory="' + tagcategory + '"]');
        if (filtertagitem.length > 0) {
            filtertagitem.parent().remove();
            if ($('.filter-tag .filter-tags-container .filter-tag-list').length === 0) {
                $('.filter-tag').hide();
            }
        } else {
            $('.filter-tag').show();
            $('.filter-tag .filter-tags-container').append($(this).parent().clone());
        }
        showResults(getFilteredResults());
    });

    $('.filter-tag .filter-tags-container').on('click', '.filter-tag-list a', function (e) {
        if ($(e.target).parent().hasClass("tag-close")) {
            var tagid = $(this).data('tagid');
            var tagcategory = $(this).data('tagcategory');
            $('.search-result-tag-second .filter-tag-list a[data-tagid="' + tagid + '"][data-tagcategory="' + tagcategory + '"]').removeClass('active');
            $(this).parent().remove();
            if ($('.filter-tag .filter-tags-container .filter-tag-list').length === 0) {
                $('.filter-tag').hide();
            }
            showResults(getFilteredResults());
        }
    });

    $('.filter-tag .filter-tags-clear-all .filter-tag-list a').click(function () {
        $('.filter-tag').hide();
        $('.filter-tag .filter-tags-container .filter-tag-list a .tag-close img').trigger("click");
    });

    $('.search-block .search-box .input-field button').click(function (e) {
        getResults(e);
    });

    $('.search-block .search-box .input-field input').on('click focus', function () {
        if ($(window).width() > 1025) {
            $('.search-block .search-box').animate({
                "padding-top": "140px"
            });
            $(document).scrollTop('0px');
        }
    });
});

var keyword = "";
$(document).keypress(function (e) {
    /*
    keyword = keyword + String.fromCharCode(e.which);
    */
	if (e.keyCode === 13) {
        getResults(e);
    } else {
        var key = String.fromCharCode(e.which);
        var allowedCharacters = /[A-Za-z0-9]/g;
        if (allowedCharacters.test(key)) {
            setTimeout(function(){
                $('.search-block .search-box .input-field input:not(:focus)').focus().val(keyword);
            }, 250);
        }
        return true;
    }
});

$(window).on('load', function (e) {
	var queryval=getParameterByName('q');
	var query="";
	if(queryval!=null)
	{
    query = queryval.split('+').join(' ');
    }
   /* var query = getParameterByName('q').split('+').join(' ');
    */
	if (query !== "") {
        $('.search-block .search-box .input-field input').val(query);
        getResults(e);
    }
});

$(window).on('scroll', function () {
    var trigger = $(".search .Subra-search-section .container .row .items-container > div:visible").last();
    if (isScrolledIntoView(trigger)) {
        $(".search .Subra-search-section .container .row .items-container > div:hidden").first().show();
    }
});

var searchResult = [];
var searchKeyword = '';

function getResults(e) {
    var temp = trimSpaces($('.search-block .search-box .input-field input').val());
    if (temp === '' || temp === searchKeyword) {
        return false;
    }
    searchKeyword = temp;
    $('.search-block .search-box .input-field button').attr('disabled', 'disabled');
    $('.search-block .search-box .input-field input').attr('disabled', 'disabled');
    $('.search-block .search-box .input-field button img.search-btn').hide();
    $('.search-block .search-box .input-field button img.loading-icon').show();

    $('.tt-menu').hide();
    $('.search-box .filter-tag').hide();
    $('.search-result-tag-first').hide();
    $('.search-result-tag-second').hide();

    $.ajax({
        type: 'get',
               data: { searchKeyword: searchKeyword },
        url: '/bin/demosearchresultservlet',
        success: function (result) {
            searchResult = result;
			 if($.isEmptyObject(result[0].items))
            {
                $(".rep").text("No Result Found");
				$(".Subra-search-section.bg-color-12.bg-color-white").addClass('hidden');
                $(".Subra-search-section.bg-color-12.bg-color-white.hidden").css('display','none');
            }else{
					$(".rep").text("Refine search by:");	
            }
            $('.search-block .search-box .search-result-tag-first .categories-container').html('');
            $('.search-block .search-box .tags-container').html('');
            $('.search-block .search-box .filter-tag .filter-tags-container').html('');

            $.each(result[0].categories, function (c, category) {
                showFilterTags(c, category);
            });
            showFilterTags('contentTypes', result[0].contentTypes);
            showResults(result[0].items);

            $('.search-result-tag-first').show();
            $('.search .Subra-search-section').show();
            $('.filter-tag-list h3, .filter-tag-list-title h3').show();
            $('.search-block').css('height', 'auto');

            /*if ($(window).width() < 768) {
                $('.mobile-devices').show();
                $('.mobile-devices').css('position', 'relative');
            }*/

            $('.search-block .search-box .input-field button img.loading-icon').hide();
            $('.search-block .search-box .input-field button img.search-btn').show();
            $('.search-block .search-box .input-field input').removeAttr('disabled');
            $('.search-block .search-box .input-field button').removeAttr('disabled');
        },
        error: function (e) {
            $('.search-block .search-box .input-field button img.loading-icon').hide();
            $('.search-block .search-box .input-field button img.search-btn').show();
            $('.search-block .search-box .input-field input').removeAttr('disabled');
            $('.search-block .search-box .input-field button').removeAttr('disabled');

            searchKeyword = '';
            $('.search .Subra-search-section').hide();
        }
    });
}

function getFilteredResults() {
    var categoryPageIds = [];
    var contentTypePageIds = [];
    var commonPageIds = [];

    $('.filter-tag .filter-tags-container .filter-tag-list').each(function () {
        var tagCategory = $(this).find('a').data('tagcategory');
        var tagId = $(this).find('a').data('tagid');
        if (tagCategory == "contentTypes") {
            for (i in searchResult[0]['contentTypes']['tags'][tagId]['pages']) {
                contentTypePageIds.push(searchResult[0]['contentTypes']['tags'][tagId]['pages'][i]['id']);
            }
        } else {
            for (i in searchResult[0]['categories'][tagCategory]['tags'][tagId]['pages']) {
                categoryPageIds.push(searchResult[0]['categories'][tagCategory]['tags'][tagId]['pages'][i]['id']);
            }
        }
    });

    categoryPageIds = getUniqueItems(categoryPageIds);
    contentTypePageIds = getUniqueItems(contentTypePageIds);

    if (categoryPageIds.length == 0 && contentTypePageIds.length == 0) {
        return searchResult[0]['items'];
    } else if (categoryPageIds.length > 0 && contentTypePageIds.length > 0) {
        commonPageIds = getCommonItems(categoryPageIds, contentTypePageIds);
    } else {
        if (contentTypePageIds.length == 0) {
            commonPageIds = categoryPageIds;
        } else {
            commonPageIds = contentTypePageIds;
        }
    }

    var items = [];
    for (i in commonPageIds) {
        items.push(searchResult[0]['items'][commonPageIds[i]]);
    }
    return items;
}

function getUniqueItems(items) {
    var result = [];
    $.each(items, function (i, item) {
        if (result.indexOf(item) === -1) {
            result.push(item);
        }
    });
    return result;
}

function getCommonItems(items1, items2) {
    var temp = [];
    if (items1.length > items2.length) {
        temp = items1;
        items1 = items2;
        items2 = temp;
    }

    var result = [];
    $.each(items1, function (i, item) {
        if (items2.indexOf(item) > -1) {
            result.push(item);
        }
    });
    return result;
}

function showFilterTags(c, category) {
    var catTemplate = $('.templates-container .category-template > div').clone();
    catTemplate.children('a').html(category.name);
    $('.search-block .search-box .search-result-tag-first .categories-container').append(catTemplate);

    var tagsContainerTemplate = $('.templates-container .tags-container-template > div').clone();
    $.each(category.tags, function (t, tag) {
        var tagsTemplate = $('.templates-container .tag-template > div').clone();
        tagsTemplate.find('a').attr('data-tagcategory', c);
        tagsTemplate.find('a').attr('data-tagid', t);
        tagsTemplate.find('a span.name').html(tag.name);
        tagsTemplate.find('a').css('background-color', tag.color);
        if(tagsTemplate.find('a').css('background-color').length == 0){
           // alert(0);
            tagsTemplate.find('a').addClass('filter-no-color');

        }
        tagsContainerTemplate.append(tagsTemplate);
    });
    $('.search-block .search-box .tags-container').append(tagsContainerTemplate);
}

function showResults(items) {

    $('.search-block .search-box .input-field input').val(searchKeyword);
    $('.search > .Subra-search-section > .container > .row > .search-result-title > h1 > .search-text > .search-result-count').html(Object.keys(items).length);
    $('.search > .Subra-search-section > .container > .row > .search-result-title > h1 > .search-keyword').html(searchKeyword);
    $('.search > .Subra-search-section > .container > .row > .items-container').html('');

    $.each(items, function (i, item) {
        var itemTemplate = $('.templates-container .result-template > div').clone();
        itemTemplate.find('a').attr('href', item.itemPath);
        itemTemplate.find('a > img').attr('src', item.imagePath).attr('alt', item.title);
        itemTemplate.find('.search-result-textBox-head').html(item.masterTag).css('background-color', item.masterTagColor);
        itemTemplate.find('.search-result-textBox-head')
        itemTemplate.find('.search-result-textBox-text > a > h4').html(item.title);
        itemTemplate.find('.search-result-textBox-text > a > p').html(item.description);
        itemTemplate.find('.search-result').addClass(item.itemType);
        itemTemplate.find('.search-result.video a').attr('href', 'javascript:void(0);');
        itemTemplate.find('.search-result.pdf a').attr('target', '_blank');
        itemTemplate.find('.search-result .show-popup .play-button').attr('source-hd', item.itemPath);
        itemTemplate.find('.search-result .show-popup .play-button').attr('source-sd', item.itemPath);
        $('.search > .Subra-search-section > .container > .row > .items-container').append(itemTemplate);
    });
}

function sortResults(items, key, order) {
    if (key === '') {
        return items;
    }
    var result = [];
    $.each(items, function (i, item) {
        result.push(item);
    });
    result.sort(function (a, b) {
        return order * (a[key] - b[key]);
    });
    return result;
}

function isScrolledIntoView(elem) {
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

function trimSpaces(text) {
    text = text.
	   replace(/(^\s*)|(\s*$)/gi, ""). // removes leading and trailing spaces
	   replace(/[ ]{2,}/gi, " ").      // replaces multiple spaces with one space 
	   replace(/\n +/, "\n");          // Removes spaces after newlines
    return text;
}

function getParameterByName(name, url) {
    if (!url) {
        url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}