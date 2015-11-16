/**
 * Created by Hao on 2015/11/14.
 */

var convjson={};
var semester1="";
var username={};
var currentconvid="";
var otheruseremail="";
var app = angular.module('messages',[
    'ngWebsocket',
    'luegg.directives'
]);
app.controller('messagecontroller',function($scope,$http,$websocket){
    $scope.newmessage="";
    var ws;
    $scope.socketfilter=function socketfilter(semester,userid){
        semester1=semester;
        var url="ws://"+location.host+"/websocket";
        console.log(url);
        ws = $websocket.$new(url);
        ws.$on('$open', function () {
            console.log('Oh my gosh, websocket is really open! Fukken awesome!');

            var data={
                semester:semester,
                email:userid
            }

            ws.$emit('allconversations', data);
        })
        .$on('allconversations',function(data){
            //console.log("allconversations");
                $scope.talks=JSON.parse(data);
                $scope.$digest();
            //console.log(JSON.parse(data));
        })
    };

    $scope.socketchatcontent=function showcontent(convid,otheremail){
        otheruseremail=otheremail;
        currentconvid=convid;
        var request={
            convid:convid,
            semester:semester1
        }
      ws.$emit("chatcontent",request);
        ws.$on('chatcontent',function(data){
            $scope.allmessages=JSON.parse(data);
            $scope.$digest();
            console.log(JSON.parse(data));
        })
    };


   $scope.socketreply=function addreply(){
       var message={
           semester:semester1,
           convid:currentconvid,
           content:$scope.newmessage,
           other:otheruseremail
       };

      ws.$emit("newmessage",message);
       ws.$on("newmessage",function(data){
           $scope.allmessages=JSON.parse(data);
           $scope.newmessage="";
           $scope.$digest();
           console.log(JSON.parse(data));
       });

   };



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

    //$scope.msgsender={};
    //$scope.sender=function getsender(senderemail){
    //
    //    var posturl="/messages/"+semester1+"/"+senderemail;
    //    console.log(posturl);
    //    $http({method: 'GET',
    //        url:posturl,
    //        headers:{'Content-Type':'text/plain'}}).success(function(response){
    //
    //        $scope.msgsender[senderemail]=response;
    //        //return $scope.otheruser.lastname;
    //
    //    });
    //
    //};


    $scope.reply=function sendmessage(){
          var data= JSON.stringify({
                  content:$scope.newmessage
              });

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

    $scope.participant=function participant(selfemail,user1,user2){
        var resultemail="";
        var name="";
        if(selfemail===user1.email){
            //resultemail=user2email;
            //return user2email;
            return user2;
        }
        if(selfemail===user2.email){
            //resultemail=user1email;
            //return user1email
            return user1;
        }
        //console.log("WS2016");
        //var posturl="/messages/"+semester1+"/"+resultemail;
        //console.log(posturl);
        //$http({method: 'GET',
        //    url:posturl,
        //    headers:{'Content-Type':'text/plain'}}).success(function(response){
        //    $scope.otheruser=response;
        //    //return $scope.otheruser.lastname;
        //    console.log(response);
        //})
    };
})