$(document).ready(function() {
   $(".fileNameInput #fileName").keyup(function() {
       var pattern = "[a-zA-z0-9]$";
       if (!$(this).val().match(pattern)) {
           $(".fileNameInput .regex-error").css("visibility", "visible");
       } else {
           $(".fileNameInput .regex-error").css("visibility", "hidden")
       }
   })
});