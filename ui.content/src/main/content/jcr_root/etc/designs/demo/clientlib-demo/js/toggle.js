$(document).ready(function(){
var number = $('.toggle-button-content-button').data('filter');
$('.toggle-button-content-button').click(function(){
$('.toggle-button-content-button').removeClass('active');
$(this).addClass('active');
});

//$('.toggle-button-content-button').click(function(){
                //if($(this).hasClass('active')){
                //$('.director-content').children().removeClass('fade-in');
                //$('.executive-director-cotent').addClass('fade-in');
                //}
//});
$('.toggle-button-content-button').click(function(){
                if($(this).data("filter") === 'one'){
                $('.director-content').children().toggleClass('fade-in');
                $('.director-content').children().removeClass('fade-in');
                $('.executive-director-content').addClass('fade-in');
                }
                else if($(this).data("filter") === 'two'){
                $('.director-content').children().toggleClass('fade-in');
                $('.director-content').children().removeClass('fade-in');
                $('.executive-director-content').css('display','none');
                $('.group-executive-council-cotent').addClass('fade-in');
                }
                else if($(this).data("filter") === 'three'){
                $('.director-content').children().toggleClass('fade-in');    
                $('.director-content').children().removeClass('fade-in');
                $('.executive-director-content').css('display','none');
                $('.executive-committe-cotent').addClass('fade-in');        
                }
                });
//$('.toggle-button-content-button').click(function(){
                //if($(this).hasClass('active')){
                //$('.director-content').children().removeClass('fade-in');
                //$('.executive-committe-cotent').addClass('fade-in');
                //}
//});
 
 
});