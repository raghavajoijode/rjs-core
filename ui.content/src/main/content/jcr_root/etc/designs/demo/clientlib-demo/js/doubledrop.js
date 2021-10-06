$(document).ready(function () {
    getData();
	staticData();
});

$('.selectpicker').change(function () {
  //  alert("aaaaaaaaaaaaa");
    getData();
});
function numberDate(date){
    var numDate;
    switch(date){

        case 0:
            numDate='00';
        case 1:
            numDate='01';
			break;
		case 2:
            numDate='02';
           	break;
        case 3:
            numDate='03';
            break;
        case 4:
          	numDate='04';
            break;
        case 5:
            numDate='05';
            break;
        case 6:
            numDate='06';
            break;
        case 7:
            numDate='07';
            break;
        case 8:
            numDate='08';
            break;
        case 9:
            numDate='09';
            break;
        default:
            numDate=date;

    }
    return numDate;
}
function wordMonth(month){

    var wordDate;
    switch(month){

        case 0:
            wordDate='Null';
        case 1:
            wordDate='January';
			break;
		case 2:
            wordDate='February';
           	break;
        case 3:
            wordDate='March';
            break;
        case 4:
          	wordDate='April';
            break;
        case 5:
            wordDate='May';
            break;
        case 6:
            wordDate='June';
            break;
        case 7:
            wordDate='July';
            break;
        case 8:
            wordDate='August';
            break;
        case 9:
            wordDate='September';
            break;
		case 10:
            wordDate='October';
            break;
        case 11:
            wordDate='November';
            break;
        case 12:
            wordDate='December';
            break;
        default:
            wordDate=month;

    }
    return wordDate;
}

function getData() {
    var year = $('#dropdown-year').val();
    var month = $('#second-dropdown').val();
	//alert("year "+year);
    //alert("month "+month);
   // document.getElementById("myYear").innerHTML = year;
    var pgPath=$('.dropMenu').attr('data-pgPath');
	 //alert("pgPath "+pgPath);
	var ddCount=$('.dropMenu').attr('data-ddCount');
	 //alert("ddCount "+ddCount);
    var secondDdType=$('.dropMenu').attr('data-secondDdType');
     //alert("secondDdType "+secondDdType);
    var yFormat=$('.dropMenu').attr('data-yFormat');
    var listType=$('.dropMenu').attr('data-listType');
     //alert("yFormat "+yFormat);
	 //alert("before new ajax");
    $.ajax({

        type: 'GET',
        url: '/bin/dropdownyearservlet',
        data: 'year=' + year + '&month=' + month + '&pgPath=' + pgPath +'&ddCount=' + ddCount +'&secondDdType=' + secondDdType+ '&yFormat=' + yFormat,


        success: function (result) {

       // alert("inside success "+typeof result);
		result = 	result.replace(/[]/g, "");
       // alert("resultresultresultresultresultresult "+result);
       var data = $.parseJSON(result);
     //  alert(data);

	var myObject = eval('(' + result + ')');
        var newsResult ='';
for (i in myObject)
        {	if(i<myObject.length){
  // alert("typetypwwtype "+typeof myObject[i].fromMonth);


        if(listType=="news"){
		news='<div class="dropdown-button-content-news"><h4 class="content-h4-neuzeit-grotesk">'+myObject[i].title+'</h4><a href="'+myObject[i].linkPath+'" target="_blank"><div class="dropdown-button-content-read-more-button content-h6-neuzeit-grotesk">Read more</div></a></div>'
        newsResult = newsResult + news;
    }
           else if(listType=="event"){
        numberD=numberDate(myObject[i].fromDate);
		numberM=numberDate(myObject[i].fromMonth);
           events='<div class="dropdown-button-content-news"><h4 class="content-h4-neuzeit-grotesk">'+myObject[i].title+'</h4><div class="events-p-container"><p class="content-h4-neuzeit-grotesk normal-font">DATE: '+numberD+'/'+numberM+'/'+myObject[i].fromYear+'</p><p class="content-h4-neuzeit-grotesk normal-font">'+myObject[i].desc+'</p></div><a href="'+myObject[i].linkPath+'" target="_blank"><div class="dropdown-button-content-read-more-button content-h6-neuzeit-grotesk">Read more</div></a></div>'
        newsResult = newsResult + events;
           }
    else if(listType=="pressrelease"){
         numberD=numberDate(myObject[i].fromDate);
		wordM=wordMonth(myObject[i].fromMonth);
	press= '<div class="dropdown-button-content-news"><h4 class="content-h4-neuzeit-grotesk">'+myObject[i].title+'</h4><div class="events-p-container"><p class="content-h4-neuzeit-grotesk normal-font">'+wordM+' '+numberD+','+myObject[i].fromYear+'</p><p class="content-h4-neuzeit-grotesk normal-font">'+myObject[i].desc+'</p></div><a href="'+myObject[i].linkPath+'" target="_blank"><div class="dropdown-button-content-read-more-button content-h6-neuzeit-grotesk">Read more</div></a></div>'
		newsResult = newsResult + press;
    }
    else if(listType=="newsroomevents"){
        numberfromD=numberDate(myObject[i].fromDate);
		numberfromM=numberDate(myObject[i].fromMonth);
		numbertoD=numberDate(myObject[i].toDate);
		numbertoM=numberDate(myObject[i].toMonth);
		newsroomevent='<div class="dropdown-button-content-news"><h4 class="content-h4-neuzeit-grotesk">'+myObject[i].title+'</h4><div class="events-p-container"><p class="content-h4-neuzeit-grotesk normal-font">Date: '+numberfromD+'/'+numberfromM+'/'+myObject[i].fromYear+'-'+numbertoD+'/'+numbertoM+'/'+myObject[i].toYear+'</p><p class="content-h4-neuzeit-grotesk normal-font">Time: '+myObject[i].fromTimeFinal+' to '+myObject[i].toTimeFinal+'</p><p class="content-h4-neuzeit-grotesk normal-font">Venue: '+myObject[i].venue+'</p><p class="content-h4-neuzeit-grotesk normal-font">'+myObject[i].desc+'</p></div><a href="'+myObject[i].linkPath+'" target="_blank"><div class="dropdown-button-content-read-more-button content-h6-neuzeit-grotesk">Read more</div></a></div>'
		newsResult = newsResult + newsroomevent;
    }

    }
//alert("newsresult1 "+newsResult);

} 



$(".dropdown-content-wrapper p").html(newsResult);

        },
        error: function (e) {
            alert("inside drop-down error");
        }
    });
//	alert("after ajax");
}
