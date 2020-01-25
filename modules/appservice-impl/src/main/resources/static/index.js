function onClickLogin() {
   window.location.href = window.document.loginUrl;
}

function onHello() {
   url = "/hello";
   console.log(url);
   $.ajax({
       'url': url,
       type: 'GET',
       dataType: 'json'
   })
   .done(function(json) {
       console.log(json);
       var elm = $('#hello');
       console.dir(elm);
       $('#hello').text(json.message);
   })
   .fail(function(xhr, status, errorThrown) {
        console.log("Error: " + errorThrown);
        console.log("Status: " + status);
        console.dir(xhr);
   });
}
