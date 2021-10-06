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
    var collectionSel = '.assets-properties-articles .aem-assets-admin-properties-childpages';
    var articles = collectionSel + ' article';
    var selArticles = articles + '.selected';
    
    // @see CQ-29669 Overrides the coral function to allow custom behavior bulkeditor
    $.fn.superUpdateErrorUI = $.fn.updateErrorUI;
    $.fn.updateErrorUI = function () {
    	// Ignore errors for bulk editor
    	var totalArticles = $(articles).length;
		var numArticles = $(selArticles).length;
    	if (totalArticles == 1 || numArticles == 1) {
    		this.superUpdateErrorUI();
    	} else {
            //CQ-32788:Override default CUI behavior of adding 'is-invalid' to a date picker which is empty for bulk editing
            $('.coral-DatePicker.is-invalid input').removeClass('is-invalid');
            $('.coral-DatePicker.is-invalid').removeClass('is-invalid');
        }
	};

    $(document).on('cui-gridlayout-initialized', function () {
    	
    	if ($(collectionSel + ' article').length > 1) {
    		// Change to list layout.
        	// Need to trigger layout switch on cui-gridlayout-initialized because there is an intentional delay in setting layout in coralui2.
        	// cui-gridlayout-initialized event is fired after the layout modification. We need to change layout on this event.
    		Granite.UI.Foundation.Layouts.switchLayout($(collectionSel), {
                'name': 'foundation-layout-list'
            });
    		
    		// By default all the assets should be selected
    		var cv = CUI.CardView.get($(collectionSel))
    		cv.selectAll();
    	} else {
            // Remove the selection mode in the grid view in case of single asset.
            $(".foundation-collection").trigger("foundation-mode-change", [ "default", $(".foundation-collection").data("foundationModeGroup")]);
    		// Should be grid-1 layout
    		$('.aem-assets-admin-properties-childpages div.grid-2').removeClass('grid-2').addClass('grid-1');
    	}
    });

    function validateRequiredField () {

    	var fields = $('.data-fields.active [aria-required="true"]');
    	fields.each(function(index, field){
    		$(field).checkValidity();
    	    $(field).updateErrorUI();
    	});
    }
    
    function refUpdate () {
        var items = $(".assets-properties-articles article");
        var length = items.length;
        var grRef = Granite.References;
        if (!grRef) {
        	return;
        }
        if (items.length > 0) {
        	var paths = [];
            items.each(function() {
                paths.push($(this).data("path"));
            });

            var formerPaths;
            if (grRef.$root.data("paths")) {
                formerPaths = grRef.getReferencePaths();
            } else {
                formerPaths = [];
            }

            grRef.triggerChange({
                paths: paths,
                // avoid refresh if mutli selection now and before
                avoidRefresh: paths.length > 1 && formerPaths.length > 1
            });
        }
    }
    
    $(document).on('foundation-contentloaded', function () {
       	 $("#primarytagid").prop("readonly", true);
		$("#ddlChar").prop("readonly", true);
        if ($(".foundation-content-path").attr("data-foundation-subasset") == "false") {

			$("#aem-asset-details-views .subassets-popover").addClass('hide');
        }
        
    	$("#aem-asset-details-views .image, #aem-asset-details-views .viewerpreset").removeClass("bold");
    	$("#aem-asset-details-views .properties").addClass("bold");

        
        // TODO. SHould be removed latter. Popover should not be initialized. It should happen in foundation.js
    	$('#viewonpopover').popover();

    	if ($(collectionSel + ' article').length > 1) {

    		// Change to edit mode
    		$(document).trigger('foundation-mode-change', ['edit', 'aem-assets-admin-properties']);
    		
    		// Cancel should redirect to previous page in case of bulk edit.
    		// This behaviour is handled by dam/gui/components/admin/commons/redirecttopreviouspage
    		$('.cancel-metadata-edit').addClass('redirect-to-previous-page');
    	}
    	
    	// To arrange the layout of the form
    	$('.data-fields.active').rearrangeFormLayout();

    	// Validate required fields on foundation content loaded.
    	validateRequiredField();

        refUpdate();

    });
    $(document).on("input change", ".js-coral-NumberInput-input", function (e) {
        var totalArticles = $(articles).length;
        var numArticles = $(selArticles).length;
        if (totalArticles == 1 || numArticles == 1) {
            var el = $(this);
            var fieldErrorEl = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS' data-init='quicktip' data-quicktip-type='error' />");
            var field = el.closest('.coral-NumberInput');

            if(el.val().trim() ==='' ){

                if ($(field).data('metatype') === 'number' && $(field).data('required')){
                    $(field).addClass('is-invalid');
                    field.nextAll(".coral-Form-fieldinfo").addClass("u-coral-screenReaderOnly");

                    var error = field.nextAll(".coral-Form-fielderror");

                    if (error.length === 0) {
                        var arrow = field.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";
                        fieldErrorEl.clone()
                            .attr("data-quicktip-arrow", arrow)
                            .attr("data-quicktip-content", "Please fill out this field")
                            .insertAfter(field);
                    } else {
                        error.data("quicktipContent", "Please fill out this field");
                    }
                }

            }
            else if ($(field).data('metatype') === 'number')
                $(field).removeClass('is-invalid');
        }
    });
    $(document).on("change:selection", '.foundation-collection', function (e) {
        if (!e.moreSelectionChanges && $('article', this).length > 1) {
            selectionChange();
        }
    });

    var selectionChange = function () {
        var selectedArticles = $(selArticles);
        var size = selectedArticles.length;
        var submitButton = $('.aem-assets-admin-properties-submit');
        var formId = '';
        if (size >= 1) {
            submitButton.removeClass('hide');
            if (size > 1) {
                formId = 'bulkview';
            } else {
                formId = $(selectedArticles[0]).data('path');
                if (!formId) {
                    formId = 'default';
                }
            }

            var datafield = $('.data-fields[data-formId="' + formId + '"]');
            if (!datafield.length) {
                var datafield = getDataField(selectedArticles);
                $('.data-fields').parent().append(datafield);
                
                // Rearrange the layout and initialize components
                $(datafield).rearrangeFormLayout();
                $(datafield).initializeComponents();
            }
        } else {
            submitButton.addClass('hide');
        }
        showAppropriateDatafields(formId);
        
        // Change to edit mode
		$(document).trigger('foundation-mode-change', ['edit', 'aem-assets-admin-properties']);
		
    };

    /**
     * Shows the appropriate data field according to the mime type.
     * */
    var showAppropriateDatafields = function (formId) {
        $('.data-fields').removeClass('active');
        if (formId) {
            $('.data-fields[data-formId="' + formId + '"]').addClass('active');
        }
    };

    /**
     * Fetches the appropriate form
     * */
    var getDataField = function (selectedArticles) {

        var mode = 'mode-edit';
        if ($('.content-header .mode-edit').hasClass('hide')) {
            mode = 'mode-default';
        }

        var url = $('[data-selfurl]').data('selfurl');
        if (!url) {
        	// Fallback
        	url = '/mnt/overlay/dam/gui/content/assets/metadataeditor.html';
        }
        url += "?_charset_=utf-8";
        selectedArticles.each(function (index, value) {
            url += "&item=" + encodeURIComponent($(value).data("path"));
        });
        var resp = "";
        $.ajax({
            type: "GET",
            url: url,
            async: false,
            success: function (response) {
                resp = $(response);
            }
        });
        
        return $(Granite.UI.Foundation.Utils.processHtml(resp, '.data-fields'));
    };


})(document, Granite.$);