/**
 * Created by Hao on 2015/11/5.
 */
//;(function($){

    ///**
    // * Store scroll position for and set it after reload
    // *
    // * @return {boolean} [loacalStorage is available]
    // */
    ////$.fn.scrollPosReaload = function(){
    //function scrollPosReaload(){
    //    if (localStorage) {
    //        var posReader = localStorage["posStorage"];
    //
    //        if (posReader) {
    //            $(window).scrollTop(posReader);
    //            localStorage.removeItem("posStorage");
    //        }
    //
    //        $(this).click(function(e) {
    //            localStorage["posStorage"] = $(window).scrollTop();
    //        });
    //
    //        return true;
    //    }
    //
    //    return false;
    //}

$(document).ready(function () {
    $('.markstatus').click(function() {
        //store the id of the collapsible element
        localStorage.setItem('collapseItem', $(this).attr('href'));
    });

    var collapseItem = localStorage.getItem('collapseItem');
    if (collapseItem) {
        $(collapseItem).collapse('show')
    }
});
    /* ================================================== */

    //$(document).ready(function() {
    //    // Feel free to set it for any element who trigger the reload
    //
    //    var collapseItem = localStorage.getItem('collapseItem');
    //    if (collapseItem) {
    //        $(collapseItem).collapse('show')
    //        }
    //   // $('select').scrollPosReaload();
    //    $('a').click(function() {
    //        //store the id of the collapsible element
    //        //if($(this).attr('href').indexOf("Assignment")){
    //        localStorage.setItem('collapseItem', $(this).attr('href'));
    //            console.log("click sth"+$(this).attr('href'));//}
    //
    //    });
    //    //var last=$.cookie('activeAccordionGroup');
    //    //if (last!=null) {
    //    //    //remove default collapse settings
    //    //    $(".collapse").removeClass('shown');
    //    //    //show the account_last visible group
    //    //    $("#" + last).addClass("shown");
    //    //}
    //});

//$("#homeworkcorrectionlist").bind('shown', function() {
//    var active=$(".shown").attr('id');
//    $.cookie('activeAccordionGroup', active)
//});

//}(jQuery));


//function submit(formid,semester,lecture,assignment,student)
//{
//    var posturl="/admin/"+semester+"/"+lecture+"/"+assignment+"/"+student+"/evaluation#editor";
//    console.log(posturl);
//    //var formData = form2js('testForm', '.',true);
//    var formData = $("#"+formid).serializeArray();
//    console.log(JSON.stringify(formData));
//    //console.log(JSON.stringify(form));
//    //document.getElementById('testArea').innerHTML = JSON.stringify(formData);
//    var xhr= new XMLHttpRequest();
//    xhr.open('POST',posturl);
//    xhr.setRequestHeader('Content-Type', 'application/json');
//    xhr.onreadystatechange = function () {
//        if (xhr.readyState == 4 && xhr.status == 200) {
//            //alert(xhr.responseText);
//            location.reload();
//
//
//        }
//    }
//    xhr.send(JSON.stringify(formData));
//}

//function copytoclip(text){
//
//    var gitadress = document.querySelector('.copytext');
//    var range = document.createRange();
//    range.selectNode(gitadress);
//    window.getSelection().addRange(range);
//    document.execCommand("copy");
//
//}

//var c = document.cookie;
//
//$('.collapse').each(function () {
//    if (this.id) {
//        var pos = c.indexOf(this.id + "_collapse_in=");
//        if (pos > -1) {
//            c.substr(pos).split('=')[1].indexOf('false') ? $(this).addClass('in') : $(this).removeClass('in');
//        }
//    }
//}).on('hidden shown', function () {
//    if (this.id) {
//        document.cookie = this.id + "_collapse_in=" + $(this).hasClass('in');
//    }
//});
