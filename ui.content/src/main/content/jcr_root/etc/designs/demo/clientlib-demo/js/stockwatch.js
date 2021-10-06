 $(document).ready(function(e) {
//alert("Before AJAX");
  $.ajax({
          type:'GET', 
         // dataType: "jsonp",
          url: 'https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22WIT%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=',

          success: function(data){    
              //  alert("In success");
              console.log("data::", data);
              var image ="";
              var change = data.query.results.quote.PercentChange;
          //    alert("Change  --"+change);
              if(change.includes('+')){
                 // alert("Greater than zero");
        image = "/etc/designs/demo/images/stockwatch/stock-green.png";
              }
              if(change.includes('-')){
                //  alert("Lesser than zero");
        image = "/etc/designs/demo/images/stockwatch/stock-red.png";
              }
                var nysedate = new Date(data.query.created);
              var year = nysedate.getFullYear();
        var month= nysedate.getMonth() + 1;
              var date = nysedate.getDate()
              var hour=nysedate.getHours() ;
              var min =nysedate.getMinutes() ;
              var sec = nysedate.getSeconds();
              if(date < 10){
        date = '0'+date
              }
              if(month < 10){
        month = '0'+month
              }
              var result = '<div class="stock-table stock-table-right"><h4 class="nse-nyse">NYSE</h4><h4><span class="stock-value-left">$'+data.query.results.quote.LastTradePriceOnly+' </span><span data-sly-test="${info.outNYSE.getpChange > 0 }" class="stock-img"><img src="'+image+'" alt=""></span> <span class="stock-value-right">'+data.query.results.quote.Change+' ('+data.query.results.quote.PercentChange+')</span></h4><table><tr><td>Volume (no. of ADRs) </td><td>'+data.query.results.quote.Volume+'</td></tr><tr><td>52 Week High</td><td>'+data.query.results.quote.YearHigh+'</td></tr><tr><td>52 week Low</td><td>'+data.query.results.quote.YearLow+'</td></tr><tr><td>Last Updated:</td><td>'+date+'-'+month+'-'+year+' '+hour+':'+min+':'+sec+'</td></tr>  </table></div>';
  //alert(result);
              $(result).appendTo('#stock-api-nyse');
             // alert("Done");
          },
          error:function(jqXHR, textStatus, errorThrown){
         // alert("Error::Unable to request  API");
                                        /* 
                                           alert("jqXHR: for linked in"+jqXHR.responseText);
                                           alert("textStatus:for linked in "+textStatus);
                                           alert("errorThrown: for linked in "+errorThrown);
                                           */
              }
            });
});