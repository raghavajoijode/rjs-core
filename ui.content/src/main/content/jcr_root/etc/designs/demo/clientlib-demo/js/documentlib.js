$(document)
		.ready(
				function() {

					var currentDamPath = "";
					var inc = 0;
					var tbid = [];

					$(".doclib")
							.each(
									function(inc) {

										tbid.push($(this).children('tbody')
												.attr('id'));
										var compid = $("#compid").val();

										var jsonString = $(
												'#thisField-' + tbid[inc])
												.val();
										var obj = JSON.parse(jsonString);

										var selectedPath = $(
												'#selectedPath-' + tbid[inc])
												.val();

										$('#back-' + tbid[inc]).hide();
										for (var i = 0; i < obj.length; i++) {

											var element = obj[i];

											if (element.type != "folder") {

												$('<tr>')
														.append(
																$('<td style =" width : 50% "><a href="'
																		+ element.path
																		+ '" target="_blank"><img src="'
																		+ element.imageType
																		+ '" class="contactListIcon"/>'
																		+ element.title
																		+ '</a></td><td style =" width : 18% ">'
																		+ element.modifiedBy
																		+ '</td><td style =" width : 32% ">'
																		+ element.modifiedDate
																		+ '</td>'))
														.appendTo(
																'#' + tbid[inc]);
											} else {
												$('<tr>')
														.append(
																$('<td style =" width : 50% "><span id="'
																		+ element.path
																		+ '"><img src="'
																		+ element.imageType
																		+ '" class="contactListIcon"/>'
																		+ element.name
																		+ '</span></td><td style =" width : 18% ">'
																		+ element.modifiedBy
																		+ '</td><td style =" width : 32% ">'
																		+ element.modifiedDate
																		+ '</td>'))
														.appendTo(
																'#' + tbid[inc]);

											}
										}

										var containerId = $(this).children(
												'tbody').attr('id');
										// var currentDamPath =
										// $("#"+tbid[inc]+" tr td
										// span").attr('id');

										$(document)
												.on(
														'click',
														'#' + tbid[inc]
																+ ' tr td span',
														function(e) {
															// $("#"+tbid[inc]+"
															// tr td
															// span").click(function(){
															currentDamPath = $(
																	this).attr(
																	'id');
															generateTable(
																	containerId,
																	currentDamPath,
																	selectedPath);

														});
										$('#back-' + tbid[inc])
												.click(
														function() {

															var str = $(this)
																	.attr('id');
															var id = str
																	.substring(
																			str
																					.indexOf("-") + 1,
																			str.length);
															var path = $(
																	"#"
																			+ id
																			+ " tr td span")
																	.attr('id');
															var emptyTest = $(
																	"#"
																			+ id
																			+ " tr td ")
																	.attr('id');

															if (emptyTest != 'empty') {

																if (typeof path == 'undefined') {
																	path = $(
																			"#"
																					+ id
																					+ " tr td a")
																			.attr(
																					'href');
																}
																path = path
																		.substr(
																				0,
																				path
																						.lastIndexOf('/'));
																// alert(test);
																currentDamPath = path
																		.substr(
																				0,
																				path
																						.lastIndexOf('/'));

																// alert(currentDamPath);
																generateTable(
																		containerId,
																		currentDamPath,
																		selectedPath);
															} else {

																currentDamPath = selectedPath;

																generateTable(
																		containerId,
																		currentDamPath,
																		selectedPath);
															}

														});

										inc++;

									});

					function generateTable(containerId, damPath, selectedPath) {

						$("#" + containerId).empty();

						currentDamPath = damPath;
						// alert(currentDamPath);

						$
								.ajax({
									type : "GET",
									url : "/bin/documentLibrary",
									dataType : "json",
									data : {
										path : damPath
									},
									success : function(result) {

										if (result != null && result.length > 0) {

											if (currentDamPath == selectedPath) {

												$('#back-' + containerId)
														.hide();
											} else {
												$('#back-' + containerId)
														.show();
											}
											for (var i = 0; i < result.length; i++) {
												var element = result[i];
												if (element.type != "folder") {

													$('<tr>')
															.append(
																	$('<td style =" width : 50% "><a href="'
																			+ element.path
																			+ '" target="_blank"><img src="'
																			+ element.imageType
																			+ '" class="contactListIcon"/>'
																			+ element.title
																			+ '</a></td><td style =" width : 18% ">'
																			+ element.modifiedBy
																			+ '</td><td style =" width : 32% ">'
																			+ element.modifiedDate
																			+ '</td>'))
															.appendTo(
																	'#'
																			+ containerId);
												} else {
													$('<tr>')
															.append(
																	$('<td style =" width : 50% "><span id="'
																			+ element.path
																			+ '"><img src="'
																			+ element.imageType
																			+ '" class="contactListIcon"/>'
																			+ element.name
																			+ '</span></td><td style =" width : 18% ">'
																			+ element.modifiedBy
																			+ '</td><td style =" width : 32% ">'
																			+ element.modifiedDate
																			+ '</td>'))
															.appendTo(
																	'#'
																			+ containerId);

												}
											}
										} else {
											$('#back-' + containerId).show();
											$('<tr>')
													.append(
															$('<td id="empty" style="width: 100%; padding-left: 19%;">There are no Folders or Assets in the selected Folder</td>'))
													.appendTo('#' + containerId);

										}
									}
								});

						e.preventDefault();

					}

				});