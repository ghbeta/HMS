/**
 * Created by Hao on 2015/10/1.
 */

    function isValid(password1,password2){


    if(password1===password2&&password2&&password1){
        return true;
    }
    else
    {
        return false;
    }

}


var app = angular.module('password',[]);
app.controller('passwordformcheck',function($scope){


   $scope.isValid=isValid;




});