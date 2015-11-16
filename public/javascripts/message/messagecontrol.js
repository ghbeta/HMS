/**
 * Created by Hao on 2015/11/14.
 */

var convjson={};
var semester1="";
var username={};
//function filter(semester){
//    var posturl="/messages/"+semester;
//    semester1=semester;
//    var xhr= new XMLHttpRequest();
//    xhr.open('POST',posturl);
//    xhr.setRequestHeader('Content-Type', 'text/plain');
//    xhr.onreadystatechange = function () {
//        if (xhr.readyState == 4 && xhr.status == 200) {
//            //alert(xhr.responseText);
//            //convjson=angular.fromJson(xhr.responseText);
//            setconvjson(xhr.responseText);
//
//            //threads=JSON.parse(xhr.responseText);
//            //console.log(threads)
//            //forum.scope.$apply()
//            //$route.reload();
//        }
//    }
//
//    xhr.send();
//
//
//}

function setconvjson(json){
    convjson.init=JSON.parse(json);
    convjson.scope.$apply();


}

//function participant(selfemail,user1email,user2email){
//    var resultemail="";
//    var name="";
//    if(selfemail===user1email){
//        resultemail=user2email;
//        //return user2email;
//    }
//    if(selfemail===user2email){
//        resultemail=user1email;
//        //return user1email
//    }
//    var posturl="/messages/"+semester1+"/"+resultemail;
//    console.log(posturl);
//
//    //var xhr= new XMLHttpRequest();
//    //xhr.open('POST',posturl);
//    //xhr.setRequestHeader('Content-Type', 'text/plain');
//    //xhr.onreadystatechange = function () {
//    //    if (xhr.readyState == 4 && xhr.status == 200) {
//    //        //alert(xhr.responseText);
//    //        //convjson=angular.fromJson(xhr.responseText);
//    //        var userobject= JSON.parse(xhr.responseText);
//    //        name= userobject.firstname+" "+userobject.lastname;
//    //        //convjson.scope.$apply();
//    //        //threads=JSON.parse(xhr.responseText);
//    //        //console.log(xhr.responseText)
//    //        //forum.scope.$apply()
//    //        //$route.reload();
//    //    }
//    //}
//
//    //xhr.send();
//    console.log(name);
//    return name;
//}

var app = angular.module('messages',[]);
app.controller('messagecontroller',function($scope,$http){
    $scope.filter=function filter(semester){
        semester1=semester;
        var posturl="/messages/"+semester;
        $http({method: 'GET',
               url:posturl,
               headers:{'Content-Type':'text/plain'}}).success(function(response){

            $scope.talks=response;
            console.log(response);
        })
    };
    $scope.username=username.fullname;
    //$scope.talks=convjson;
    //convjson.scope=$scope;
    //console.log(convjson);
    $scope.participant=function participant(selfemail,user1email,user2email){
        var resultemail="";
        var name="";
        if(selfemail===user1email){
            resultemail=user2email;
            //return user2email;
        }
        if(selfemail===user2email){
            resultemail=user1email;
            //return user1email
        }
        //console.log("WS2016");
        var posturl="/messages/"+semester1+"/"+resultemail;
        console.log(posturl);
        $http({method: 'GET',
            url:posturl,
            headers:{'Content-Type':'text/plain'}}).success(function(response){
            $scope.otheruser=response;
            //return $scope.otheruser.lastname;
            console.log(response);
        })
    };
})