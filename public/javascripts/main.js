$(document).ready(function() {
   $(".fileNameInput #fileName").keyup(function() {
       var pattern = "^[a-zA-Z0-9_.-]{1,20}$";
       if (!$(this).val().match(pattern)) {
           $(".fileNameInput .regex-error").css("visibility", "visible");
       } else {
           $(".fileNameInput .regex-error").css("visibility", "hidden")
       }
   })
});