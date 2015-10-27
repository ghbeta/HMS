/**
 * Created by Hao on 2015/10/27.
 */
$(document).ready(function(){
    $('input[type="checkbox"]').click(function(){
        if($(this).attr("value")=="true"){
            $("#local").toggleClass("hide show");
            $("#remote").toggleClass("show hide");
        }


    });
});