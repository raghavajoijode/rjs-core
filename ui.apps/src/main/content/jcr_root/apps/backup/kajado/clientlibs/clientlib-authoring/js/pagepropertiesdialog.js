
( function(document, $,ns) {
    "use strict";
  var value;
    var ddlOrderValue;
    var count =0;
  var CONTENTOWNER =  "./content-owner";
    var OFFERINGS =  "./offerings";
    var SEGMENTS =  "./segments";
    var coOfferings = {};
    var coSegments = {};
    var contentowner;
    var offerings;
  var segments;
  var selectedFlag;
    var primaryBVTag;
    var primarySLTag;
    var contentTypeTag;
    var passContentOwner ;

// -----------------------------------------------------------------------------------------------------------------------     
  $(document).on("focusout", "#createtagid", function(e) {
      
    var tagsValue = $("#createtagid").val(); 

        if(tagsValue !=''){
         $.ajax({
      type:'POST',  
            url :'/bin/createtagonthefly',
      dataType: 'text',
      data:'path='+encodeURIComponent(tagsValue) ,

             success:function(msg){alert("Tags succesfully created & Ready to use");},
            error:function(jqXHR, textStatus, errorThrown){
                                           alert("inside error page properties servlet not configured");

              }
        });

    }

  });   



// -----------------------------------------------------------------------------------------------------------------------     
  $(document).on("selected", ".aman-new", function(e) {
      var x=    $("select[name='./offerings']").val('');
        var x=    $("select[name='./segments']").val('');
       if(value =='case-study' || value =='latest-thinking' || value =='solution'){
      $('.offerings-class').removeClass("hide");
           $('.segments-class').removeClass("hide");
      ddlOrderValue = $("#ddlChar option:selected").val();
      passContentOwner =  ddlOrderValue;
      ajaxOffering(ddlOrderValue);

        }



  });

// ----------------------------------------------------------------------------------------------------------------------- 
    function ajaxOffering(path){
     $.ajax({
      type:'POST',  
            url :'/bin/pagepropertiesofferingdropdown',
      dataType: 'json',
      data:'path='+path ,

            success:populateOfferings,
            error:function(jqXHR, textStatus, errorThrown){
                                           alert("inside error page properties servlet not configured");

              }
        }); 
    }

// -----------------------------------------------------------------------------------------------------------------------     
    function populateOfferings(data) {
  //  alert("populateOfferings");
  coOfferings = data;
    contentowner = $("[name='" + CONTENTOWNER +"']").closest(".coral-Select");
   //    for(var keys in coOfferings){alert("fff"+keys+"   "+coOfferings[keys].text+  "     "+coOfferings[keys].value);}
  //   alert("contentowner.val"+contentowner.val());
    offerings = new CUI.Select({
                        element: $("[name='" + OFFERINGS +"']").closest(".coral-Select")
                     });
   // alert("offerings");
    if(_.isEmpty(offerings) || _.isEmpty(contentowner)){
            return;
      }

  offerings._selectList.children().not("[role='option']").remove();

   var $form = offerings.$element.closest("form");

        $.getJSON($form.attr("action") + ".json").done(function(data){
                if(_.isEmpty(data)){
                    return;
                }
    fillOfferings(contentowner.val(), data.offerings);
        })
      };    


// -----------------------------------------------------------------------------------------------------------------------

function fillOfferings(selectedContentOwner, selectedOfferings){
    //alert("fillOfferings");
  var x = $("[name='./offerings']").closest(".coral-Select").find('option').remove().end();
  var len =  $('#selectOfferingsID ul li').size();
    for(var i=0; i<len; i++){
     $("#selectOfferingsID ul li").eq(0).remove();
    }
  var count =0;

    _.each(coOfferings, function(text, value) {

        var selectField = $("[name='./offerings']")[0];
                if(count == 0){
                    $("<option >").appendTo(selectField).val("").html("Select Option");
                    count++;
                }
      //  alert(text.value +"   offerings fill  "+text.text);
                $("<option>").appendTo(selectField).val(text.value).html(text.text); 
       });


    offerings = new CUI.Select({
                element: $("[name='" + OFFERINGS +"']").closest(".coral-Select")
            });
            
            
            if(!_.isEmpty(selectedOfferings)){
                
                offerings.setValue(selectedOfferings);
                
            }
            
      ajaxSegments(passContentOwner);
        }    
// ----------------------------------------------------------------------------------------------------------------------- 
    function ajaxSegments(path){
      //  alert("ajaxSegments");
     $.ajax({
      type:'POST',  
            url :'/bin/pagepropertiessegmentdropdown',
      dataType: 'json',
      data:'path='+path ,

            success:populateSegments,
            error:function(jqXHR, textStatus, errorThrown){
                                           alert("inside error page properties  populateSegments servlet not configured");

              }
        }); 
    }   

// -----------------------------------------------------------------------------------------------------------------------     
    function populateSegments(data) {
     //  alert("populateSegments");
  coSegments = data;
       //  alert("populateSegments coSegments");
    contentowner = $("[name='" + CONTENTOWNER +"']").closest(".coral-Select");
    //     alert("populateSegments coSegments contentowner");
       // for(var keys in coSegments){
            //alert("fff"+keys+"   "+coSegments[keys].text);
          //  alert("wds");
      //  }
    //   alert("contentowner.val"+contentowner.val());
    segments = new CUI.Select({
                        element: $("[name='" + SEGMENTS +"']").closest(".coral-Select")
                     });
      //   alert("segments CUI.Select");
    if(_.isEmpty(segments) || _.isEmpty(contentowner)){
       // alert("Inside If ");
            return;
      }

  segments._selectList.children().not("[role='option']").remove();
// alert("segments._selectList");
   var $form = segments.$element.closest("form");
//alert(" $form   "+ $form);
        $.getJSON($form.attr("action") + ".json").done(function(data){
         //   alert("getJSon action");
                if(_.isEmpty(data)){
                    return;
                }
    fillSegments(contentowner.val(), data.segments);
        })
      };


// -----------------------------------------------------------------------------------------------------------------------

function fillSegments(selectedContentOwner, selectedSegments){
//alert("fillSegments  "+ selectedSegments+"   "+selectedContentOwner);
  var x = $("[name='./segments']").closest(".coral-Select").find('option').remove().end();
  var len =  $('#selectSegmentsID ul li').size();
    for(var i=0; i<len; i++){
     $("#selectSegmentsID ul li").eq(0).remove();
    }
  var count =0;

    _.each(coSegments, function(text, value) {

        var selectField = $("[name='./segments']")[0];
                if(count == 0){
                    $("<option >").appendTo(selectField).val("").html("Select Option");
                    count++;
                }

                $("<option>").appendTo(selectField).val(text.value).html(text.text); 
       });


    segments = new CUI.Select({
                element: $("[name='" + SEGMENTS +"']").closest(".coral-Select")
            });
            
            
            if(!_.isEmpty(selectedSegments)){
                
                segments.setValue(selectedSegments);
                
            }
            

        }    

// ----------------------------------------------------------------------------------------------------------------------- 
    function ajaxContentTypeTag(corpType,pageType){

     $.ajax({
      type:'POST',  
            url :'/bin/contenttypetagservlet',
      data:'pageType='+pageType+'&corpType='+ corpType ,

            success:function(msg){
                                          // alert("msg    "+msg);
                           $("#contenttypetagid").val(msg);

              },
            error:function(jqXHR, textStatus, errorThrown){
                                           alert("inside error page properties content type tag servlet not configured");

              }
        }); 
    }    






// -----------------------------------------------------------------------------------------------------------------------    
    $(document).on("foundation-contentloaded", function(e) {

        selectedFlag = 1; 
      //  ajaxContentTypeTag("page-type");
        $('.setPrimaryBV-class').addClass("hide");

        $('#corporate-id').addClass("hide");
        $("#pdfpathid").addClass("hide");
        $("#contenttypetagid").prop("readonly", true);
        showHide($(".cq-dialog-dropdown-showhide", e.target));
        $("#primarytagid").prop("readonly", true);
        var b = $("[name='./corporate-page-type']").val();

        if(b=="blogs-detail"){
            $("#selectBlogCatID").children().removeAttr('disabled');
            $("#blog-author").removeAttr('disabled');
        }

        getBVTag($(".primarybvclass", e.target));
        getSLTag($(".primaryslclass", e.target));

    /* --------------------------For carousel component radio button------------------------------------------*/
     var carouselFirstradio = $("input[name='./indicator']").attr("aria-selected");
         var carouselSecondradio = $("input[name='./indicator']").last().attr("aria-selected");
       if(carouselFirstradio == "false" && carouselSecondradio == "false")
        {
      $("input[name='./indicator']").first().attr("aria-selected","true");
            $("input[name='./indicator']").first().attr("checked", "checked");

        }
    /* ---------------------------------End carousel component ------------------------------------------------*/

    /* --------------------------For demo image component radio button------------------------------------------*/
     var carouselFirstradio = $("input[name='./selectoption']").attr("aria-selected");
         var carouselSecondradio = $("input[name='./selectoption']").last().attr("aria-selected");
       if(carouselFirstradio == "false" && carouselSecondradio == "false")
        {
      $("input[name='./selectoption']").first().attr("aria-selected","true");
            $("input[name='./selectoption']").first().attr("checked", "checked");

        }
    /* ---------------------------------End demo image component ------------------------------------------------*/

    });

// -----------------------------------------------------------------------------------------------------------------------   
    $(document).on("selected", ".cq-dialog-dropdown-showhide", function(e) {
        selectedFlag = 0;
       // alert("selectedFlag =0");
        $("#pdfpathid").addClass("hide");
    showHide($(this));
        getCorporates($(this));
    $(".pdfpathattribute").children().children().val('');
        $("#primarytagid").val("");

    });

// -----------------------------------------------------------------------------------------------------------------------   

     $(document).on("selected", ".primarybvclass", function(e) {
  //  alert("selectedFlag.primarybvclass =0");
    getBVTag($(this));
    });

// -----------------------------------------------------------------------------------------------------------------------_

     $(document).on("selected", ".primaryslclass", function(e) {
      //  alert("selectedFlag.primarybvclass =0");

      getSLTag($(this));
    });
// -----------------------------------------------------------------------------------------------------------------------   
    $(document).on("selected", ".corporate-class-new", function(e) {
       // alert("corporate-class-new");
          var b = $("[name='./corporate-page-type']").val();

        if(b=="blogs-detail"){
      $("#selectBlogCatID").children().removeAttr('disabled');
            $("#blog-author").removeAttr('disabled');

        }else{
          $("#selectBlogCatID").children().attr('disabled', true);
            $("#blog-author").attr('disabled', true);

        }
    getCorporate($(this));
    });
// -----------------------------------------------------------------------------------------------------------------------   
var corps;
function getCorporate(el){ 

       el.each(function(i, element) {

           var widget = $(element).data("select");


           if (widget) {

        corps = widget.getValue();
                }


           ajaxContentTypeTag(corps,pages);
       })

   }
// -----------------------------------------------------------------------------------------------------------------------   
var pages;
function getCorporates(el){ 

       el.each(function(i, element) {

           var widget = $(element).data("select");


           if (widget) {

        pages = widget.getValue();
                }

           ajaxContentTypeTag(corps,pages);
       })

   }
// -----------------------------------------------------------------------------------------------------------------------   
   function getBVTag(el){ 

       el.each(function(i, element) {

           var widget = $(element).data("select");


           if (widget) {
      //  alert("widget" + widget);
        primaryBVTag = widget.getValue();
                }
  var primarytagvalue;
           if(primaryBVTag != '')
           {
       if(primarySLTag != '')
             {
        primarytagvalue = primaryBVTag+","+primarySLTag;
              }else { primarytagvalue = primaryBVTag; }

           }else{
      if(primarySLTag != '')
            {
        primarytagvalue = primarySLTag;
            }else { primarytagvalue = ''; }
           }
    var primarytagid =  $("#primarytagid").val(primarytagvalue); 

       })

   }  

// -----------------------------------------------------------------------------------------------------------------------
     function getSLTag(el){ 

       el.each(function(i, element) {

           var widget = $(element).data("select");

           if (widget) {
        //alert("widget" + widget);
        primarySLTag = widget.getValue();


      }

    var primarytagvalue;

           if(primaryBVTag != '')
           {
       if(primarySLTag != '')
             {
        primarytagvalue = primaryBVTag+","+primarySLTag;
              }else { primarytagvalue = primaryBVTag; }

           }else{
      if(primarySLTag != '')
            {
        primarytagvalue = primarySLTag;
            }else { primarytagvalue = ''; }
           }

           var primarytagid =  $("#primarytagid").val(primarytagvalue); 

       })
  
   }  
// -----------------------------------------------------------------------------------------------------------------------   
   function showHide(el){ 
    //  alert("hellooo"+$('#setcontentownerid').hasClass("hide"));
       $('#setPrimaryBV-id').addClass("hide");
       $('#setPrimarySL-id').addClass("hide");

       if(($('#setcontentownerid').hasClass("hide")) == true)
       {  
           $('.offerings-class').addClass("hide");
       $('.segments-class').addClass("hide");
       }

       if(selectedFlag == 0){
      var x=    $("select[name='./offerings']").val('');
      var xx=    $("select[name='./segments']").val('');
           var y=    $("select[name='./content-owner']").val('');
           var z=    $("select[name='./corporate-page-type']").val('');

      
       }
     

       el.each(function(i, element) {
         
           var widget = $(element).data("select");

           if (widget) {
      var target = $(element).data("cqDialogDropdownShowhideTarget");
        value = widget.getValue();
       $(target).not(".hide").addClass("hide");

               $(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");

                if( ($.trim(value) == 'case-study')){
        $('#setPrimaryBV-id').removeClass("hide");
                $('#setPrimarySL-id').removeClass("hide");
                $("#pdfpathid").removeClass("hide");
        }
        if( ($.trim(value) == 'corporate')){

                    $("#corporate-id").removeClass("hide");
          $("#pdfpathid").removeClass("hide");
                }else{
           $("#corporate-id").addClass("hide");
                }
        if( ($.trim(value) == 'solution')){
          value = 'case-study';
          $(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");
          value = 'solution';
                     $('#setPrimaryBV-id').removeClass("hide");
                     $('#setPrimarySL-id').removeClass("hide");
                    $("#pdfpathid").removeClass("hide");
        }else if(($.trim(value) == 'offering')){ 
          value = 'case-study';
          $(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");
          value = 'offering';
                   $('#setPrimaryBV-id').removeClass("hide");
                   $('#setPrimarySL-id').removeClass("hide");
          }else if(($.trim(value) == 'latest-thinking')){ 
          value = 'case-study';
          $(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");
          value = 'latest-thinking';
                    $('#setPrimaryBV-id').removeClass("hide");
                    $('#setPrimarySL-id').removeClass("hide");
                    $("#pdfpathid").removeClass("hide");
       }else if(($.trim(value) == 'segment')){ 
          value = 'case-study';
          $(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");
          value = 'segment';
                  $('#setPrimaryBV-id').removeClass("hide");
                    $('#setPrimarySL-id').removeClass("hide");
    }

           }
       })
     
    var contentOwnerValue = $("#ddlChar option:selected").val() ;
    passContentOwner =  contentOwnerValue;

      if(  (($('#setcontentownerid').hasClass("hide")) == false ) && (!!contentOwnerValue) )
       {  
         ajaxOffering(contentOwnerValue);
    // ajaxSegments(contentOwnerValue);
     }


   }
// -------------------------------------When dialog get submitted----------------------------------------------------------------------------------   

  $(document).on("click", ".cq-dialog-submit", function (e) {
    e.stopPropagation();
        e.preventDefault();
      ddlOrderValue = $("#ddlChar option:selected").text();
      var chcek = $(".primaryslclass option:selected").text();
     // alert("ddlOrderValue"+ddlOrderValue+" "+chcek +" "+$(".primarybvclass option:selected").text());
     // ddlOrderValue = $("#ddlChar option:selected").val();
  if(typeof ddlOrderValue == "string" && typeof primaryBVTag == "string" && typeof primarySLTag == "string"){
          var ddlOrderValueCheck  = ddlOrderValue;
        var primaryBVTagCheck  = $(".primarybvclass option:selected").text();
          var primarySLTagCheck  = $(".primaryslclass option:selected").text();
      }

        var $form = $(this).closest("form.foundation-form"), message, clazz = "coral-Button " ;

        if( (($('#setcontentownerid').hasClass("hide")) == false ) && ( ( primaryBVTagCheck == 'Select Options' &&  primarySLTagCheck == 'Select Options') || ((ddlOrderValueCheck != primaryBVTagCheck) && (ddlOrderValueCheck != primarySLTagCheck))) ) {

        ns.ui.helpers.prompt({
              title: Granite.I18n.get("Invalid Input"),
              message: "Owner Tag should be either a Primary BV or Primary SL",
                actions: [{
                    id: "CANCEL",
                    text: "CANCEL",
                    className: "coral-Button"
                }],
            callback: function (actionId) {
                if (actionId === "CANCEL") {
                }
            }
        });

        

        }else{

        $form.submit();
          }

    }); 


// -----------------------------------------------------------------------------------------------------------------------    
  /*  var carouselFirstradio = $("input[name='./indicator']").attr("aria-selected");
    var carouselSecondradio = $("input[name='./indicator']").last().attr("aria-selected");
    alert("carouselFirstradio   "+carouselFirstradio+" carouselSecondradio      "+carouselSecondradio);*/
// -----------------------------------------------------------------------------------------------------------------------     

})(document,Granite.$,Granite.author);



