$(function(){
      //  alert("In script");
    $('.tool-tip-hyperlink').click(function(event){
        event.stopPropagation();
        $('.tool-tip-hyperlink .toolTip-ContentShow').show();
        
        if($(this).children('.toolTip-Content').hasClass('toolTip-ContentShow')){
            $(this).children('.toolTip-Content').removeClass('toolTip-ContentShow');

        }else{
            $('.tool-tip-hyperlink').children('.toolTip-Content').removeClass('toolTip-ContentShow');
            $(this).children('.toolTip-Content').addClass('toolTip-ContentShow');
        }
    });

});

$(document).on('click', function(){
	$('.tool-tip-hyperlink').children('.toolTip-Content').removeClass('toolTip-ContentShow');
   
});
