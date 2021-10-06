
 $(document).ready(function(e) {

	var USER_CHANNEL_NAME =  $('#get-youtube-channel-name').attr('data-channelName');//'Subravideos';
	var YOUTUBE_API_KEY =  $('#get-youtube-api-key').attr('data-apiKey');//'AIzaSyDD_6agCrGHFhsPkaUz8X_V-A8CjzO4VEQ-8E';

	var linkedInCompanyID = $('#get-linkedIn-company-id').attr('data-companyID');//'2414183';
	var LinkedIN_ACCESS_TOKEN =$('#get-linkedIn-access-token').attr('data-accessToken');//'AQWSL8-7BI4PUuHTVSjRXPqg9oZ4AljGdkVTj2ihFqEGRHb9t9iF8oqh4vH0zzRH9F994VIrJZ-Gn6zPI4-njD__joFE_XAGyaqYOeZPZBcQHRJtrDvxPuSw8DkdNcB7llXbNy_1h8RxVMdFrxS37JJzVLu_ju_kt0ltaj19IlCjN8Y0q9g';
	var facebookCompanyID = $('#get-facebook-company-name').attr('data-companyID');
    var faceBook_ACCESS_TOKEN =$('#get-facebook-access-token').attr('data-accessToken');
	var monthNames = ["January", "February", "March", "April", "May", "June",
  				      "July", "August", "September", "October", "November", "December"
					 ];


     $.ajax({
         		type:'GET', 
                dataType: "jsonp",
                url: 'https://graph.facebook.com/'+facebookCompanyID,
         		data: {
                    fields : 'feed.limit(5)',
                    access_token : faceBook_ACCESS_TOKEN
                },
         		success: function(data){
               
//                    console.log(data.feed.data);
                    $.each(data.feed.data, function(i,item){
						console.log(item);

                        var fbDate = new Date(item.created_time);
					var fbUpdateDate = fbDate.getDate();
					var fbUpdateSubMonth = fbDate.getMonth();
					var fbUpdateMonth = monthNames[fbUpdateSubMonth];



                   facebookResults = ' <div class="social-slide"> <div class="header-content"> <div class="header-content-img"><a target="_blank" href="https://www.facebook.com/'+facebookCompanyID+'\"><img src="/etc/designs/demo/images/social-media/slide-fb.png"/></a></div> <div class="header-content-text"><span class="dark-blue"><a target="_blank" href="https://www.facebook.com/'+facebookCompanyID+'\">Subra</a></span> <span class="date">'+fbUpdateMonth+' '+fbUpdateDate+'</span> </div></div> <a target="_blank" href="'+item.link+'"><div class="social-slide-main-content">'+item.message+'</div></a></div> </div>';

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
			success:function(data){
				console.log("Success");
				console.log(data.values);
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
					
					console.log(item);
					
										
                    linkedInresults = ' <div class="social-slide"> <div class="header-content"> <div class="header-content-img"><a target="_blank" href="https://www.linkedin.com/company/'+linkedInCompanyID+'\"><img src="/etc/designs/demo/images/social-media/slide-linkedin.png"/></a></div> <div class="header-content-text"><span class="dark-blue"><a target="_blank" href="https://www.linkedin.com/company/'+linkedInCompanyID+'">'+companyname+'</a></span> <span class="date">'+updateMonth+' '+updateDate+'</span> </div></div> <a target="_blank" href="https://www.linkedin.com/company/'+linkedInCompanyID+'"><div class="social-slide-main-content">'+contentToShow+'</div></a></div> </div>';
					
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
     									//	alert("Error::Unable to request Linkedin API");
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
   forUsername : USER_CHANNEL_NAME,
   key: YOUTUBE_API_KEY}, 
			 function(data){
                $.each(data.items, function(i,item){

                    console.log(item);
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
        key: YOUTUBE_API_KEY},
        function(data) {
            var results;
			$.each( data.items, function( i, item ) {
            console.log(item);
            videoTitle =  item.snippet.title;
            videoId =  item.snippet.resourceId.videoId;
				//Published Time
				publishedAt = item.snippet.publishedAt;
				var publishDate = new Date(publishedAt);
				
				var videoSubMonth = publishDate.getMonth();
				var videoMonth =  monthNames[videoSubMonth];
				//alert(videoMonth);
				var videoDate = publishDate.getDate();;
				//alert(videoDate);
               	youtuberesults = ' <div class="social-slide less-padding"> <div class="header-content"> <div class="header-content-img"><a target="_blank" href="https://www.youtube.com/watch?v='+videoId+'\"><img src="/etc/designs/demo/images/social-media/slide-youtube.png"/></a></div> <div class="header-content-text"><span class="dark-blue"><a target="_blank" href="https://www.youtube.com/user/'+USER_CHANNEL_NAME+'">'+USER_CHANNEL_NAME+'</a></span> <span class="date">'+videoMonth+' '+videoDate+'</span> </div></div> <iframe src="https://www.youtube.com/embed/'+videoId+'\"></iframe> </div>';
				$(youtuberesults).appendTo('#youtuberesults');
				setTimeout(function() {   //calls click event after a certain time
			$('.Subra-social-media-content-box').slick({
				dots: true,
				infinite: true,
				slidesToShow: 1,
				slidesToScroll: 1,
				prevArrow: false,
				nextArrow: false,
				autoplay:true,
				autoplaySpeed: 6000
});

}, 1400);
            });
        }
    );
}
});
