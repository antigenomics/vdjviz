$(document).ready(function () {

    $('.bxslider').bxSlider({
        mode: 'horizontal',
        adaptiveHeight: true,
        speed: 1500,
        captions: true
    });

    $('.bx-sign-in-button').click(function() {
        window.location.replace("/signin");
    })

});


