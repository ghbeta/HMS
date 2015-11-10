/**
 * Created by Hao on 2015/11/10.
 */
var section=2;
function getthreads(semesteruserid,lectureid,type){

}

function getposts(semesteruserid,lectureid){

}

function newthread(semester,semesteruserid,lectureid){
    section=2;
    console.log(semesteruserid,lectureid)
    var posturl="/"+semester+"/"+lectureid+"/forum/newthread";
    console.log(posturl);
    //var formData = form2js('testForm', '.',true);
    var formData = $("#createthreadform").serializeArray();
    //console.log(JSON.stringify(formData));
    //console.log(JSON.stringify(form));
    //document.getElementById('testArea').innerHTML = JSON.stringify(formData);
    var xhr= new XMLHttpRequest();
    xhr.open('POST',posturl);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.status == 200) {
            alert(xhr.responseText);
            console.log(xhr.responseText);
        }
    }
    xhr.send(JSON.stringify(formData));
}

var app=angular.module("forumapp",[]);
app.controller("forumcontroller",function($scope){

    $scope.threads=getthreads();
    $scope.posts=getposts();
    $scope.section=function(id){
        section=id;
    }
    $scope.showsection=function(id){
        return section==id;
    }
    $scope.newthread=newthread
})