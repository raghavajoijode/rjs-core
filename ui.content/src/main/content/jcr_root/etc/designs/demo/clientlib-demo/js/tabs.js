//Tab container
/*$(document).ready(function() {
    $('.tabs .tab-links .toggle-button-content-button:first').addClass('active');
    $('.tabs .tab-content div:first').addClass('active');
    $('#one-0').show().siblings().hide(); 
	$('#two-0').show().siblings().hide(); 
    $('#three-0').show().siblings().hide(); 
    $('#four-0').show().siblings().hide(); 
    $('#five-0').show().siblings().hide(); 
    $('#six-0').show().siblings().hide(); 
    $('.toggle-button-content-button').on('click', function(e)  {
		$('.toggle-button-content-button').removeClass('active');
		$(this).addClass('active');
        var currentAttrValue = $(this).parent('a').attr('href');

        // Show/Hide Tabs
        $('.tabs ' + currentAttrValue).show().siblings().hide();

        // Change/remove current tab to active
        $(this).children('.toggle-button-content-button').addClass('active').siblings().removeClass('active');
        e.preventDefault();
    });
});
$(document).ready(function() {
    $('#one .tabs .tab-links .toggle-button-content-button:first').addClass('active');
     $('#one .toggle-button-content-button:first').trigger('click');
    $('#one .tabs .tab-content div:first').addClass('active');
    $('#one-0').show().siblings().hide();
    $('#one .toggle-button-content-button').on('click', function(e)  {
		$('#one .toggle-button-content-button').removeClass('active');
		$(this).addClass('active');
        var currentAttrValue = $(this).parent('a').attr('href');

        // Show/Hide Tabs
        $('#one .tabs ' + currentAttrValue).show().siblings().hide();

        // Change/remove current tab to active
        $(this).children('#one .toggle-button-content-button').addClass('active').siblings().removeClass('active');
        e.preventDefault();
    });
});

$(document).ready(function() {
    $('#two .tabs .tab-links .toggle-button-content-button:first').addClass('active');
    $('#two .tabs .tab-content div:first').addClass('active');
     $('#two .toggle-button-content-button:first').trigger('click');
    $('#two-0').show().siblings().hide();
    $('#two .toggle-button-content-button').on('click', function(e)  {
		$('#two .toggle-button-content-button').removeClass('active');
		$(this).addClass('active');
        var currentAttrValue = $(this).parent('a').attr('href');

        // Show/Hide Tabs
        $('#two .tabs ' + currentAttrValue).show().siblings().hide();

        // Change/remove current tab to active
        $(this).children('#two .toggle-button-content-button').addClass('active').siblings().removeClass('active');
        e.preventDefault();
    });
});

$(document).ready(function() {
    $('#three .tabs .tab-links .toggle-button-content-button:first').addClass('active');
     $('#three .toggle-button-content-button:first').trigger('click');
    $('#three .tabs .tab-content div:first').addClass('active');
    $('#three-0').show().siblings().hide();
    $('#three .toggle-button-content-button').on('click', function(e)  {
		$('#three .toggle-button-content-button').removeClass('active');
		$(this).addClass('active');
        var currentAttrValue = $(this).parent('a').attr('href');

        // Show/Hide Tabs
        $('#three .tabs ' + currentAttrValue).show().siblings().hide();

        // Change/remove current tab to active
        $(this).children('#three .toggle-button-content-button').addClass('active').siblings().removeClass('active');
        e.preventDefault();
    });
});

setTimeout(function(){
},100);*/