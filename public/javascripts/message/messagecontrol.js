/**
 * Created by Hao on 2015/11/14.
 */

var convjson={};
var semester1="";
var username={};
var currentconvid="";

var app = angular.module('messages',[
    'luegg.directives'
]);
app.controller('messagecontroller',function($scope,$http){
    $scope.newmessage="";

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

    $scope.chatcontent=function showcontent(convid){
        currentconvid=convid;
      var posturl="/conversation/"+semester1+"/"+convid;
        console.log(posturl);

        $http({method: 'GET',
            url:posturl,
            headers:{'Content-Type':'text/plain'}}).success(function(response){
            $scope.allmessages=response;
            //return $scope.otheruser.lastname;
            console.log(response);
        })

    };

    $scope.sender=function getsender(senderemail){
        var posturl="/messages/"+semester1+"/"+senderemail;
        console.log(posturl);
        $http({method: 'GET',
            url:posturl,
            headers:{'Content-Type':'text/plain'}}).success(function(response){
            $scope.msgsender=response;
            //return $scope.otheruser.lastname;
            console.log(response);
        })
    }

    $scope.reply=function sendmessage(){
          var data= JSON.stringify({
                  content:$scope.newmessage
              })

        console.log("reply data "+data);
        var posturl="/conversation/"+semester1+"/"+currentconvid+"/newmessage";
        console.log("create new reply"+posturl);
        $http({method: 'POST',
            url:posturl,
            data:data,
            headers:{'Content-Type':'application/json'}}).success(function(response){
            $scope.allmessages=response;
            //return $scope.otheruser.lastname;
            console.log(response);
            $scope.newmessage="";
        })
    }

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