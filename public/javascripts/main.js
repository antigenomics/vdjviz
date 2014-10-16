$(document).ready(function() {
   $(".fileNameInput #fileName").keyup(function() {
       var pattern = "^[a-zA-Z0-9_.-]{0,20}$";
       if (!$(this).val().match(pattern)) {
           $(".fileNameInput .regex-error").css("visibility", "visible");
           $(".buttonsInput input").addClass("disabled");
       } else {
           $(".fileNameInput .regex-error").css("visibility", "hidden");
           $(".buttonsInput input").removeClass("disabled");
       }
   })
});