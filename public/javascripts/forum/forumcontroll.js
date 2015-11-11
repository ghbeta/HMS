/**
 * Created by Hao on 2015/11/10.
 */
var section=2;


function getposts(semesteruserid,lectureid){

}
//var threads;
//function newthread(username,semester,semesteruserid,lectureid){
//    section=2;
//    console.log(semesteruserid,lectureid)
//    var posturl="/admin/"+username+"/"+semester+"/"+lectureid+"#forum";
//    console.log(posturl);
//    //var formData = form2js('testForm', '.',true);
//    var formData = $("#createthreadform").serializeArray();
//    //console.log(JSON.stringify(formData));
//    //console.log(JSON.stringify(form));
//    //document.getElementById('testArea').innerHTML = JSON.stringify(formData);
//    var xhr= new XMLHttpRequest();
//    xhr.open('POST',posturl);
//    xhr.setRequestHeader('Content-Type', 'application/json');
//    xhr.onreadystatechange = function () {
//        if (xhr.readyState == 4 && xhr.status == 200) {
//            //alert(xhr.responseText);
//            //console.log(xhr.responseText);
//            //threads=JSON.parse(xhr.responseText);
//            //console.log(threads)
//            //forum.scope.$apply()
//            //$route.reload();
//        }
//    }
//    xhr.send(JSON.stringify(formData));
//}

var app=angular.module("forumapp",[]);
app.controller("forumcontroller",function($scope){

    //$scope.threads=getthreads();
    $scope.posts=getposts();
    $scope.section=function(id){
        section=id;
    }
    $scope.showsection=function(id){
        return section==id;
    }
    //$scope.newthread=newthread

})