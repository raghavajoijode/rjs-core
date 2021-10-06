/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2013 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 *
 */

(function (document, $) {
	
	"use strict";

	// For cross browser support - CQ-37914
	if (!String.prototype.endsWith) {
		String.prototype.endsWith = function(suffix) {
			return this.indexOf(suffix, this.length - suffix.length) !== -1;
		}
	}
	var primaryBVTag ;
	var primarySLTag;
	var collectionSel = '.assets-properties-articles .aem-assets-admin-properties-childpages';
	var articles = collectionSel + ' article';
	var selArticles = articles + '.selected';
	
	$(document).on("keypress", ".data-fields input[type=text]", function (e) {

        if (e.keyCode === 13) {
            return false;
        }
       });
// -----------------------------------------------------------------------------------------------------------------------   

     $(document).on("selected", ".primarybvclass", function(e) {

		getBVTag($(this));
        var selectionCOwner = $("[name='./jcr:content/metadata/contentownerselection'] option:selected").val();
    if(selectionCOwner!='' ){
	//alert("selected content owner --->    "+selectionCOwner );
    var valueCowner = $(" " + selectionCOwner +" option:selected").text();
    $('#ddlChar').val(valueCowner);
    }
   // alert("selected content owner      "+selectionCOwner +"  <---  ddlChar --->  "+valueCowner+"   ------> "+$('#ddlChar').val());
    });


// -----------------------------------------------------------------------------------------------------------------------   

     $(document).on("selected", ".primaryslclass", function(e) {

			getSLTag($(this));
    var selectionCOwner = $("[name='./jcr:content/metadata/contentownerselection'] option:selected").val();
    if(selectionCOwner!='' ){
	//alert("selected content owner --->    "+selectionCOwner );
    var valueCowner = $(" " + selectionCOwner +" option:selected").text();
    $('#ddlChar').val(valueCowner);
    }
   // alert("selected content owner      "+selectionCOwner +"  <---  ddlChar --->  "+valueCowner+"   ------> "+$('#ddlChar').val());
    });
// -----------------------------------------------------------------------------------------------------------------------   

     $(document).on("selected", ".selectionCOwner", function(e) {


    var selectionCOwner = $("[name='./jcr:content/metadata/contentownerselection'] option:selected").val();
    if(selectionCOwner!=''){
	//alert("selected content owner --->    "+selectionCOwner );
    var valueCowner = $(" " + selectionCOwner +" option:selected").text();
    $('#ddlChar').val(valueCowner);
    }
   // alert("selected content owner      "+selectionCOwner +"  <---  ddlChar --->  "+valueCowner+"   ------> "+$('#ddlChar').val());
    });
// -----------------------------------------------------------------------------------------------------------------------  	
	function validateRequiredFields () {

		var ariaRequired = $('.data-fields.active [aria-required="true"]');
		var dataRequired = $('.data-fields.active [data-required="true"]');
		var isValid = true;
		ariaRequired.each(function(index, item){
			if (!$(item).val()) {
				isValid = false;
			}
		});
		dataRequired.each(function(index, item) {
            if (!$('input.coral-Textfield', $(item)).val()) {
                isValid = false;
                if ($(item).data('metatype') == 'number')
                    $(item).addClass('is-invalid');
            }
            else {
                if ($(item).data('metatype') == 'number')
                    $(item).removeClass('is-invalid');
            }

		});
		
		return isValid;
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
		 $("#contenttypetagid").prop("readonly", true);
        showHide($(".cq-dialog-dropdown-showhide", e.target));
    	getCorporates($(".cq-dialog-dropdown-showhide", e.target));
        getBVTag($(".primarybvclass", e.target));
        getSLTag($(".primaryslclass", e.target));
	var selectionCOwner = $("[name='./jcr:content/metadata/contentownerselection'] option:selected").val();
    if(selectionCOwner!='' && selectionCOwner !=null && !selectionCOwner){
//alert("selected content owner --->    "+selectionCOwner );
    var valueCowner = $(" " + selectionCOwner +" option:selected").text();
    $('#ddlChar').val(valueCowner);
    }
  //  alert("selected content owner      "+selectionCOwner +"  <---  ddlChar --->  "+valueCowner+"   ------> "+$('#ddlChar').val());

    });

// -----------------------------------------------------------------------------------------------------------------------  

	 $(document).on("selected", ".cq-dialog-dropdown-showhide", function(e) {
          $('#ddlChar').val('');
         $("#primarytagid").val("");
         $("select[name='./primary-bv']").val('');
         $("select[name='./primary-sl']").val('');
       showHide($(this));
		getCorporates($(this));
        var selectionCOwner = $("[name='./jcr:content/metadata/contentownerselection'] option:selected").val();
    if(selectionCOwner!=''){
	//alert("selected content owner --->    "+selectionCOwner );
    var valueCowner = $(" " + selectionCOwner +" option:selected").text();
    $('#ddlChar').val(valueCowner);
    }
  //  alert("selected content owner      "+selectionCOwner +"  <---  ddlChar --->  "+valueCowner+"   ------> "+$('#ddlChar').val());
		//$(".tileimageattribute").children().children().val('');
	 });
// -----------------------------------------------------------------------------------------------------------------------  
 
	  function showHide(el){

       el.each(function(i, element) {

           var widget = $(element).data("select");

           if (widget) {

               // get the selector to find the target elements. its stored as data-.. attribute
               var target = $(element).data("cqDialogDropdownShowhideTarget");

               // get the selected value
               var value = widget.getValue();
               // make sure all unselected target elements are hidden.
               
               $(target).not(".hide").addClass("hide");
				//alert($.trim(value) + "   "+($.trim(value) == 'article-pdf'));
               // unhide the target element that contains the selected value as data-showhidetargetvalue attribute
               $(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");


				if( ($.trim(value) == 'solution')){
					value = 'case-study';
					$(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");

				}else if(($.trim(value) == 'offering')){ 
					value = 'case-study';
					$(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");

	  		  }else if(($.trim(value) == 'latest-thinking')){ 
					value = 'case-study';
					$(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");

			 }else if(($.trim(value) == 'segment')){ 
					value = 'case-study';
					$(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");

				}


           }
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

           ajaxContentTypeTag('undefined',pages);
       })

   }
// -----------------------------------------------------------------------------------------------------------------------   
    
	$(document).on("click", ".aem-assets-admin-properties-submit", function(e) {

		var ddlOrderValue = $("#ddlChar").val();

		var totalArticles = $(articles).length;
		var numArticles = $(selArticles).length;
		
		if(typeof ddlOrderValue == "string" && typeof primaryBVTag == "string" && typeof primarySLTag == "string"){
          var ddlOrderValueCheck  = ddlOrderValue;
    	  var primaryBVTagCheck  = $(".primarybvclass option:selected").text();
          var primarySLTagCheck  = $(".primaryslclass option:selected").text();
      }

		// @see CQ-29669 Don't validate for bulkeditor
		if ((totalArticles == 1 || numArticles == 1) && !validateRequiredFields()) {
			// Invalid class sometimes doesn't get added to input element
			$('.data-fields.active .coral-DatePicker.is-invalid').each(function(index, field){
	    		$('input[type="text"]', field).addClass('is-invalid');
	    	});
			$('#aem-assets-metadataedit-validationerror').modal('show');
			return;
		}

        // if this is bulk editor page we need to show popover for soft/hard mode edit
        if ($(articles).length > 1) {
            var popover = $('#softSubmit').data('popover');
            if (!popover) {
                popover = new CUI.Popover({
                  element:"#softSubmit",
                  pointAt:".aem-assets-admin-properties-submit",
                    content: '<div class="coral-Popover-content u-coral-padding" style="border:none">'+
                              "<label class='coral-Checkbox'>"+
                                  "<input class='coral-Checkbox-input' type='checkbox' name='c2' value='2'>"+
                                  "<span class='coral-Checkbox-checkmark'></span>" +
                                  "<span class='coral-Checkbox-description'>" + Granite.I18n.get("Append mode") + "</span>" +
                              "</label>"+
                          '</div>'+
                          '<div class="coral-Popover-content u-coral-padding" style="margin-top: 0; border: none;">'+
                              '<button class="coral-Button coral-Button--primary">'+ Granite.I18n.get("Submit") +'</button>'+
                          '</div>'


                });
            }
            popover.toggleVisibility();
            return;
        } else if( (($('#selectionCOwner').hasClass("hide")) == false ) && ( ($('#ddlChar').val() =='Select Options') || ($('#ddlChar').val() =='') ) ){
			//	alert("true---"+ $('#setcontentownerid').hasClass("hide") +"   "+primaryBVTagCheck+"    "+primarySLTagCheck+"    "+((ddlOrderValueCheck != primaryBVTagCheck) && (ddlOrderValueCheck != primarySLTagCheck))+"       "+(ddlOrderValueCheck != primaryBVTagCheck)+"     "+(ddlOrderValueCheck != primarySLTagCheck)+"      "+( primaryBVTagCheck == '' &&  primarySLTagCheck == '')) ;
			    $('#aem-assets-metadataedit-demo-validationerror').modal('show');
				return;
		} 
		 else {
            var cur = $(e.currentTarget),
                beforesubmit = $.Event('beforesubmit', {
                    originalEvent: e
            });
            cur.trigger(beforesubmit);
            if(beforesubmit.isDefaultPrevented()){
                return false;
            }
            if ($('#collection-modifieddate').length) {
                $('#collection-modifieddate').attr('value' , (new Date()).toISOString())
            }
            createNewTags($(".data-fields.active form")).done(function(){
                var form = $('.data-fields.active form');
                handleResponse(form, submitForm(form));
            }).fail(function(response){
                var modal = $('#aem-assets-metadataedit-error');
                var body = Granite.I18n.get('Unable to create new tags. Check for access privileges to create tags.');
                modal.find(".coral-Modal-body").html('<p>' + body + '</p>');
                modal.modal("show");
            });
        }
    });

	$(document).on("click", "#softSubmit button", function(e) {
	var cur = $(e.currentTarget),
            beforesubmit = $.Event('beforesubmit', {
                originalEvent: e
        });
        cur.trigger(beforesubmit);
        if(beforesubmit.isDefaultPrevented()){
            return false;
        }
        if ($('#collection-modifieddate').length) {
            $('#collection-modifieddate').attr('value' , (new Date()).toISOString())
        }
        createNewTags($(".data-fields.active form")).done(function(){
			var form = $('.data-fields.active form');
			handleResponse(form, submitForm(form));
		}).fail(function(response){
			var modal = $('#aem-assets-metadataedit-error');
			var body = Granite.I18n.get('Unable to create new tags. Check for access privileges to create tags.');
	    	modal.find(".coral-Modal-body").html('<p>' + body + '</p>');
	    	modal.modal("show");
		});
	});
	
	function handleResponse(form, xhr) {
	    xhr.done(function(){
        	addRating();
        	createSuccessHandler(form);
        	
        })
            .fail(function(){
            	var modal = $('#aem-assets-metadataedit-error');
    			var body = Granite.I18n.get('Unable to edit properties.');
    	    	modal.find(".coral-Modal-body").html('<p>' + body + '</p>');
    	    	modal.modal("show");
            });
    }
	
	function createSuccessHandler(form) {
		var $articles = $(articles);
    	var length = $articles.length;
        if (length > 1) {
        	successModalForBulkEdit();
        }
        else {
        	var url = $('[data-selfurl]').data('selfurl');
            if (!url) {
            	// Fallback
            	url = '/mnt/overlay/dam/gui/content/assets/metadataeditor.html';
            }
            var assetPath = $articles.data('path');
            url += assetPath;
            var resp = "";
            $.ajax({
                type: "GET",
                cache: false,
                url: url
            }).success(function(response){
            	$('.data-fields.active').replaceWith(Granite.UI.Foundation.Utils.processHtml(response, '.data-fields.active'));
            	$('.data-fields.active').initializeComponents();
            	$('.data-fields.active').rearrangeFormLayout();
            	$(document).trigger('foundation-mode-change', ['default', 'aem-assets-admin-properties']);
            	$(document).trigger('foundation-contentloaded');
            });
        	
        }
    }
	
	function addRating () {
		var rating = $(".rating.edit-mode .coral-Icon--star.current-rating").data("rate");
		if (rating) {
			var contentPath = $(".foundation-content-path").data("foundation-content-path");
			if (!contentPath) {
				return;
			}
			var url = contentPath + '/ratings.social.json';
			$.ajax({
				type: 'POST',
				url: url,
				async: false,
				data: {
					tallyGenerator: "assets",
					response: rating,
					tallyType: 'Rating',
					':operation': 'social:postTallyResponse'
				},
				error: function(e) {
					$("#aem-assets-rating-error").modal("show");
				}
			});
		}
	}
	
	function successModalForBulkEdit () {
		var selectedArticles = $(selArticles);
    	var assets = new Array();
    	var limit = 10;
    	selectedArticles.each(function(item, value){
    		assets[item] = $(value).data('title');
    	});
    	var resp = "";
    	if (assets.length > 1) {
    		if (selectedArticles.hasClass('card-collection')) {
    			resp = "<p>" + Granite.I18n.get("The following {0} collections have been modified:", assets.length) +"</p>";
    		} else {
    			resp = "<p>" + Granite.I18n.get("The following {0} assets have been modified:", assets.length) +"</p>";
    		}
    	} else if (assets.length == 1){
    		if (selectedArticles.hasClass('card-collection')) {
    			resp = "<p>" + Granite.I18n.get("The following collection have been modified:") +"</p>";
    		} else {
    			resp = "<p>" + Granite.I18n.get("The following asset have been modified:") +"</p>";
    		}
    	}
    	
    	resp += "<p class=\"item-list\">";
    	var iterLim = assets.length;
    	if (assets.length > limit) {
    		iterLim = limit - 1;
    	}
    	for (var i=0 ; i < iterLim ; i++) {
    		resp += _g.XSS.getXSSValue(assets[i]) + "<br>";
    	}
    	if (assets.length > limit) {
    		resp += "...<br>";
    	}
    	resp += "</p>";
    	
    	var modal = $('#aem-assets-metadataedit-success');
    	modal.find(".coral-Modal-body").html(resp);
    	modal.modal("show");
    }
	
	function submitForm(form) {
	     var data = formDataForMultiEdit(form);
        return $.ajax({
            type: 'post',
            url: '/content/dam.html',
            data: data,
            cache: false,
            async: false
        });
    }
	
	function formDataForMultiEdit(form) {
		var assets = new Array();
    	var assetTags = new Array();
    	var articleMarkup = new Array();
    	if ($(articles).length === 1) {
    		$(articles).each(function(index, value){
    		    articleMarkup[index] = $(value);
	    		assets[index] = articleMarkup[index].data('path');
	    	});
    	} else {
    		$(selArticles).each(function(index, value){
    		    articleMarkup[index] = $(value);
	    		assets[index] = articleMarkup[index].data('path');
	    		assetTags[index] = articleMarkup[index].data('tags');
	    	});
    	}
    	var multiAssets = false;
    	if (assets.length > 1) {
    	    multiAssets = true;
    	}

    	var basePath = '/content/dam';
    	var charset = form.data('charset');
    	if (!charset) {
    		charset = "utf-8";
    	}
    	
    	var eligibleFormData = $('[name]', $($('.data-fields.active form').not('.hide'))).not('[disabled="disabled"]');
    	var hintFields = createHintFields(multiAssets, $(articles).data('type') === 'collection');
    	
    	var k = 0;
    	var data = new Array();
        data[k++] = {'name' : '_charset_', 'value' : charset};

        var checked = $("#softSubmit input:checkbox").prop("checked");

        data[k++] = {'name': 'dam:bulkUpdate', 'value': 'true'};
        data[k++] = checked ? {'name': 'mode', 'value': 'soft'} : {'name': 'mode', 'value': 'hard'};         // check if soft mode is checked
        $(selArticles).each(function(index, value){
            var cvm = $('span.image', value).data('contentvm');
            var mdvm = $('span.image', value).data('metadatavm');
            var p = {};
            p['path'] = $(value).data('path');
            p['cvm'] = cvm;
            p['mdvm'] = mdvm;

            data[k++] = {'name': 'asset', 'value': JSON.stringify(p)};
        });

        $("#myPopover input:checkbox").prop("checked", false);

        for (var i = 0 ; i < assets.length ; i++) {
    		for (var j = 0 ; j < eligibleFormData.length ; j++) {
                if ($(eligibleFormData[j]).data('skip') || $(eligibleFormData[j]).parent().attr("disabled") == "disabled" || $(eligibleFormData[j]).attr('name') === './jcr:content/cq:lastReplicationAction') {
                    // Skip from post
                    //Skipping even if the parent is disabled, required for complex widgets such as filters
                    continue;
                }
                var dataName = $(eligibleFormData[j]).attr('name');
                var name;
                var formDataName;
                if (dataName.indexOf('./') !== 0) {
                	if (i !== 0) {
                		// Add to form data only once
                		continue;
                	}
                	name = dataName;
                } else {
                	formDataName = $(eligibleFormData[j]).attr('name').substring(1);
        			name = '.' + assets[i].substring(basePath.length) + formDataName;
                }
    			var value = $(eligibleFormData[j]).val();
    			
    			var isTag = false;
    			var field = $(eligibleFormData[j]).closest('.coral-Form-fieldwrapper');
    			var metaType = $('[data-metatype]', $(field));
    			
                if (metaType && metaType.data('metatype') === 'tags' && !name.endsWith("@Delete")) {
                    if(assetTags[i] != null && assetTags[i] != ""){
                        assetTags[i].split(", ").forEach(function (val){
                        	data[k++] = {
                                'name': name,
                                'value': val
                            };
                        });
                    }
                }
                if($(eligibleFormData[j]).attr('name') === "./jcr:content/onTime" && articleMarkup[i].data("is-s7set") === true){
                    articleMarkup[i].find(".s7set-subassets .s7set-subasset").each(function(index, val){
                        data[k++] = {
                                'name': '.' + $(val).data("path").substring(basePath.length) + formDataName,
                                'value': value
                            };
                    });
                }
                if (value || assets.length == 1) {
                	if ($(eligibleFormData[j]).attr('multiple') === 'multiple' && value && Array.isArray(value)) {
                        value.forEach(function (val) {
                            data[k++] = {
                                'name': name,
                                'value': val
                            };
                        });
                    } else {
                        data[k++] = {'name':name, 'value':value};
                    }
                }
            }
    		
    		for (var j = 0; j < hintFields.length; j++) {
                var name = '.' + assets[i].substring(basePath.length) + hintFields[j].name.substring(1);
                data[k++] = {
                    'name': name,
                    'value': hintFields[j].value
                };
            }
    	}
    	
    	return data;
    }
	
	function createHintFields(multiAssets, isCollection) {
	     var hintFields = [];
        var $form = $('.data-fields.active form');
        var allTags = $('[data-metatype=tags]', $form);
        allTags.each(function (index, tag) {
            var $tag = $(tag);
            var typeHint = $tag.parent().data('typehint');
            if (!typeHint) {
                typeHint = 'String[]';
            }
            var name = $('.coral-TagList', $tag).data('fieldname');
            if (!name) {
            	name = "./jcr:content/metadata/cq:tags";
            }
            hintFields.push({
                'name': name + '@TypeHint',
                'value': typeHint
            });
            if (!multiAssets) {
                hintFields.push({
                    'name': name + '@Delete',
                    'value': 'delete-empty'
                });
            }
        });

        var allDates = $('[data-metatype=datepicker]', $form);
        allDates.each(function (index, date) {
            var $date = $(date);
            var typeHint = $date.data('typehint');
            if (!typeHint) {
                typeHint = 'Date';
            }
            var name = $('input[type="hidden"]', $date).attr('name');
            hintFields.push({
                'name': name + '@TypeHint',
                'value': typeHint
            });
        });

        var allNumbers = $('[data-metatype=number]', $form);
        allNumbers.each(function (index, number) {
            var $number = $(number);
            var typeHint = $number.data('typehint');
            if (!typeHint) {
                typeHint = 'Long';
            }
            var name = $number.attr('name');
            if(! name)// fallback to textfield wrapped in form field
                name = $('.coral-Textfield', $number).attr('name');
            hintFields.push({
                'name': name + '@TypeHint',
                'value': typeHint
            });
        });

        var allMVText = $('[data-metatype=mvtext]', $form);
        allMVText.each(function (index, mvtext) {
            var $mvtext = $(mvtext);
            var typeHint = $mvtext.data('typehint');
            if (!typeHint) {
                typeHint = 'String[]';
            }
            var name = $mvtext.attr('name');
            hintFields.push({
                'name': name + '@TypeHint',
                'value': typeHint
            });
        });
        
        
        var allCheckbox = $('[data-metatype=checkbox]', $form);
        allCheckbox.each(function (index, checkbox) {
            var $checkbox = $(checkbox);
            if($checkbox.is(":checked")){
                $checkbox.attr('value','true');
            }
            else{
                $checkbox.attr('value','false');
            }
            var typeHint = $checkbox.data('typehint');
            if (!typeHint) {
                typeHint = 'Boolean';
            }
            var name = $checkbox.attr('name');	
            hintFields.push({
                'name': name + '@TypeHint',
                'value': typeHint
            });
        });

        return hintFields;
    }

	function createNewTags (form) {
		return $.when.apply(null, form.find('[data-metatype="tags"].coral-PathBrowser+ul.coral-TagList input[type="hidden"][name]').map(function() {
            var el = this;

            if (el.value.indexOf(":") >= 0) return;

            var tenantId = $(".foundation-form.mode-edit").attr("tenant-id");
            el.value = tenantId ? ("mac:" + tenantId + "/default/" + el.previousElementSibling.textContent ) : el.previousElementSibling.textContent;
            return createSingleTag(el.value).then(function(tag) {
                // Fix tag name in select element
                var tenantId = $(".foundation-form.mode-edit").attr("tenant-id");
                if (!tenantId) {
                    // Fix tag name in select element
                    el.value = tag;
                }
            });
        }));
    }
	
	function createSingleTag (name) {
		return $.post(Granite.HTTP.externalize("/bin/tagcommand"), {
            cmd: "createTagByTitle",
            tag: name,
            locale: "en", // This is fixed to "en" in old siteadmin also
            "_charset_": "utf-8"
        }).then(function(html) {
            return $(html).find("#Path").text();
        });
	}

// -----------------------------------------------------------------------------------------------------------------------   
   function getBVTag(el){ 

       el.each(function(i, element) {

           var widget = $(element).data("select");

           if (widget) {

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
			//	alert("primarySLTag" + primarySLTag);
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
	
})(document, Granite.$);
