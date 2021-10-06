
$(document).ready(function(e) {
  var USER_CHANNEL_NAME =  $('#get-youtube-channel-name').attr('data-channelName');
  var YOUTUBE_API_KEY =  $('#get-youtube-api-key').attr('data-apiKey');
  var linkedInCompanyID = 1318;//$('#get-linkedIn-company-id').attr('data-companyID');
  var LinkedIN_ACCESS_TOKEN ="AQXwXMgIcNX43TSy5tV-hvH58niWqYsju8mTtfeVtPveQce8h-HlUCf50ej_SJhgizQYf5NsRtI9hLCfHzNzNLpKgzF4qZg3rEJohry8BMchpvZPGz_sAg0rEqFp7Y_u406A1ijIKWZl7XqnI3Kh2O0Db8d2HOsBngSvGePBA5SdUfUH4D8";//$('#get-linkedIn-access-token').attr('data-accessToken');
  var facebookCompanyID = $('#get-facebook-company-name').attr('data-companyID');
  var faceBook_ACCESS_TOKEN ="EAAAALNXcWO0BAO5zLscZBALyrzD4yR5UUfUJi5OI9pvKMs1WFscD4qHXMcZCZAhOwbuADVu7Fdk4kDFZCAW9q9ikQqdw6Go0UpWD2dZCZCYXqkMMTwvyD8CUAKY6gNiZCIgaAqtDgvxOygM59EFN0T4HxWoVhOlxr1mGMEILUFKTwZDZD";//$('#get-facebook-access-token').attr('data-accessToken');
  var monthNames = ["January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
           ];
  $.ajax({
          type:'GET', 
          dataType: "jsonp",
          url: 'https://graph.facebook.com/'+facebookCompanyID,
          async: true,
          data: {
                fields : 'feed.limit(5)',
                access_token : faceBook_ACCESS_TOKEN
              },
          success: function(data){     
         // console.log("data::", data);
          //console.log("data.feed.data::",data.feed.data);
          $.each(data.feed.data, function(i,item){
          //console.log(item);
          var fbDate = new Date(item.created_time);
          var fbUpdateDate = fbDate.getDate();
          var fbUpdateSubMonth = fbDate.getMonth();
          var fbUpdateMonth = monthNames[fbUpdateSubMonth];
          facebookResults = ' <div class="social-slide"> <div class="header-content"> <a target="_blank" href="https://www.facebook.com/'+facebookCompanyID+'\"><div class="header-content-text line"><h3>Facebook</h3></div></a><div class="header-content-img"><a target="_blank" href="https://www.facebook.com/'+facebookCompanyID+'\"><img src="/etc/designs/demo/images/social-media/slide-fb.png"/></a></div></div> <div class="header-content-text"><span class="dark-blue"><a target="_blank" href="https://www.facebook.com/'+facebookCompanyID+'\">Subra</a></span> <span class="date">'+fbUpdateMonth+' '+fbUpdateDate+'</span></div><a href="'+item.link+'" target="_blank"><div class="social-slide-main-content">'+item.message+'</div></a></div>';
          $(facebookResults).appendTo('#facebookResults');
          });
          },
          error:function(jqXHR, textStatus, errorThrown){
          //alert("Error::Unable to request FAcebook API");
                                        /* 
                                           alert("jqXHR: for linked in"+jqXHR.responseText);
                                           alert("textStatus:for linked in "+textStatus);
                                           alert("errorThrown: for linked in "+errorThrown);
                                           */
              }
            });
        $.ajax({
            type:'GET',  
            url :'https://api.linkedin.com/v1/companies/'+linkedInCompanyID+'/updates',
            data:'oauth2_access_token='+LinkedIN_ACCESS_TOKEN+'&event-type=status-update&count=5',
            dataType: 'jsonp',
            async: true,
            success:function(data){
            //console.log("Success");
            //console.log(data.values);
            $.each(data.values, function(i,item){
            var logoImg = './images/slide-linkedin.png';
            var companyname = item.updateContent.company.name;
            var title = item.updateContent.companyStatusUpdate.share.content.title;
            var description = item.updateContent.companyStatusUpdate.share.content.description;
            var comment= item.updateContent.companyStatusUpdate.share.comment;
            var contentToShow = comment;
            if (comment == undefined)   
            {
              contentToShow = title+description;
            }else {
              contentToShow = comment;
            }
            var timestamp = item.timestamp;
            var jsDate = new Date(timestamp);
            var updateDate = jsDate.getDate();
            var updateSubMonth = jsDate.getMonth();
            var updateMonth = monthNames[updateSubMonth];
          //alert("timestamp:"+timestamp);
          
          //console.log(item);                 
            linkedInresults = ' <div class="social-slide"><div class="header-content"><a target="_blank" href="https://www.linkedin.com/company/'+linkedInCompanyID+'\"><div class="header-content-text line"><h3>LinkedIn</h3></div></a><a target="_blank" href="https://www.linkedin.com/company/'+linkedInCompanyID+'\"><div class="header-content-img"><img src="/etc/designs/demo/images/social-media/slide-linkedin.png"/></div></a></div><div class="header-content-text"><span class="blue-header h4-akkurat-mono"><span class="dark-blue"><a target="_blank" href="https://www.linkedin.com/company/'+linkedInCompanyID+'">'+companyname+'</a></span><span class="date">'+updateMonth+' '+updateDate+'</span></div><a target="_blank" href="https://www.linkedin.com/company/'+linkedInCompanyID+'"><div class="social-slide-main-content">'+contentToShow+'</div></a></div>';
            $(linkedInresults).appendTo('#linkedInResults');

     setTimeout(function() { 
                      $('.Subra-social-media-content-box').slick({
                        dots: true,
                        infinite: true,
                        slidesToShow: 1,
                        slidesToScroll: 1,
                        prevArrow: false,
                        nextArrow: false,
            autoplay: true,
            autoplaySpeed: 6000
                });
                     function flex1(){
                    $('.social-slide-main-content').each(function(){
                    if($(this).height() > 130){
                    $(this).css('height','63%');
                    $(this).css('overflow-y','scroll');
                    }
                        });
                    }
                    flex1();
                    
                    $(window).resize(function() {
                    flex1();
                    });
                    }, 1500);
        })
            },
            error:function(jqXHR, textStatus, errorThrown){
                      //  alert("Error::Unable to request Linkedin API");
                                          /* 
                                           alert("jqXHR: for linked in"+jqXHR.responseText);
                                           alert("textStatus:for linked in "+textStatus);
                                           alert("errorThrown: for linked in "+errorThrown);
                                           */
              }
        });
$.get(
   "https://www.googleapis.com/youtube/v3/channels",{
   part : 'contentDetails',
   mime:true,
   async: true,
   forUsername : USER_CHANNEL_NAME,
   key: 'AIzaSyBYQ2VpPKcg2IZxsOL1W-hj2uXCAJ5pqv0'}, 
            function(data){
                $.each(data.items, function(i,item){

                   // console.log(item);
                    pid = item.contentDetails.relatedPlaylists.uploads;
                    getVids(pid);
                })
            }
    );

//Get Videos
function getVids(pid){
    $.get(
        "https://www.googleapis.com/youtube/v3/playlistItems",{
        part : 'snippet',
        maxResults : 5,
        playlistId : pid,
        key: 'AIzaSyBYQ2VpPKcg2IZxsOL1W-hj2uXCAJ5pqv0'},
        function(data) {
            var results;
            //console.log(data);
            $.each( data.items, function( i, item ) {
              // console.log(item);
                videoTitle =  item.snippet.title;
                videoId =  item.snippet.resourceId.videoId;
                publishedAt = item.snippet.publishedAt;
        var publishDate = new Date(publishedAt);

        var videoSubMonth = publishDate.getMonth();
        var videoMonth =  monthNames[videoSubMonth];
        //alert(videoMonth);
        var videoDate = publishDate.getDate();;
                youtuberesults = ' <div class="social-slide less-padding"> <div class="header-content"><a target="_blank" href="https://www.youtube.com/watch?v='+videoId+'\"><div class="header-content-text line"><h3>YouTube</h3></div></a><div class="header-content-img"><a target="_blank" href="https://www.youtube.com/watch?v='+videoId+'\"><img src="/etc/designs/demo/images/social-media/slide-youtube.png"/></a></div></div> <div class="header-content-text no-space"><span class="dark-blue"><a target="_blank" href="https://www.youtube.com/user/'+USER_CHANNEL_NAME+'">'+USER_CHANNEL_NAME+'</a></span> <span class="date">'+videoMonth+' '+videoDate+'</span> </div> <iframe src="https://www.youtube.com/embed/'+videoId+'\"></iframe> </div>';
                $(youtuberesults).appendTo('#youtuberesults');
        setTimeout(function() {   //calls click event after a certain time
      $('.Subra-social-media-content-box').slick({
        dots: true,
        infinite: false,
        slidesToShow: 1,
        slidesToScroll: 1,
        prevArrow: false,
        nextArrow: false,
        autoplay: true,
        autoplaySpeed: 6000
});
}, 1400);
            });
        }
    );
}
  function flex1(){
    $('.social-slide-main-content').each(function(){
  if($(this).height() > 230){
  $(this).css('height','63%');
  $(this).css('overflow-y','scroll');
  }
    });
  }
  flex1();
  $(window).resize(function() {
  flex1();
  });
});

