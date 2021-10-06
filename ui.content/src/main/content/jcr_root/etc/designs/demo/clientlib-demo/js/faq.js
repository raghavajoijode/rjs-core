
$(document).ready(function () {
	
	// FAQ-Section
	$(".Subra-FAQ-container-categories-questions .content-h4-neuzeit-grotesk").click(function(){
	$(this).next('.Subra-FAQ-container-categories-questions .Subra-FAQ-container-categories-answers').slideToggle(300);
	$(this).toggleClass("content-visible");
	return false;
	});
});