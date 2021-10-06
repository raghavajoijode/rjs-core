$(function(){
  var windowWidth = $(window).width();
  $(".Subra-case-studies-bg-textBox-text p").each(function(i){
    var len=$(this).text().length;
    if(windowWidth>=1500 && len>160)
    {
      $(this).text($(this).text().substr(0,160)+'...');
    } else if(len>120){
      $(this).text($(this).text().substr(0,120)+'...');
    }
  });       
});

//For share in breadcrumb//
$(document).on('click', function(){

   
    if($('.social-container-case').hasClass('social-container-case-open')){
    	$('.social-container-case').toggleClass('social-container-case-open');
    }
});
$(document).ready(function () {
	$('.share-icon-img').click(function(event){
        event.stopPropagation();
       
		$('.social-container-case').toggleClass('social-container-case-open');
	});
});

function urlShare(media,url,title){
		  //alert("title: "+url);
          var shareURL="";
          if(media=="email"){
                   //shareURL="http://api.addthis.com/oexchange/0.8/forward/email/offer?pco=tbx32nj-1.0&pubid=ra-5319414c5f12b007&url="+url+"&title="+title;
                   //window.open(shareURL,"_blank","height=485,width=700");
				   
				   shareURL="mailto:?subject="+title+"&body="+title+" "+url;
                   window.open(shareURL,"_self");
				   
          }else if(media=="facebook"){
                   shareURL="https://www.facebook.com/sharer/sharer.php?u="+url;
                   window.open(shareURL,"_blank","height=485,width=700");
          }else if(media=="linkedin"){
                   shareURL="https://www.linkedin.com/shareArticle?mini=true&url="+url+"&amp;title="+title;
                   window.open(shareURL,"_blank","height=485,width=700");
          }else if(media=="twitter"){
                   shareURL="https://twitter.com/intent/tweet?text="+title+"&amp;url="+url+"&amp;via=Subra;";
                   window.open(shareURL,"_blank","height=485,width=700");
          }
	};