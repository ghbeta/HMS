/**
 * Created by Hao on 2015/11/24.
 */

$(document).ready(
  connectws()
);

function connectws(){
    var wsuri = "ws://"+location.host+"/websocket";
    var websocket;
    websocket= new WebSocket(wsuri);
    websocket.onopen=function(evt){onOpen(evt)};
    websocket.onmessage=function(evt){onMessage(evt)};
}

function onOpen(evt){
    console.log("connected");
}

function onMessage(evt){
    console.log(evt.data);
    var msg=JSON.parse(evt.data);
    if(msg.event==="noti"){
        var namepart=msg.data.split(";")[0];
        //alert("new messages from" + namepart);
        $.notify("you have a new messages from " + namepart,"success");
    }
}