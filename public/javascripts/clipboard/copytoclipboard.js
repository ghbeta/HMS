/**
 * Created by Hao on 2016/1/19.
 */
var clipboard = new Clipboard('.ct');
clipboard.on("success",function(e){
    window.alert(e["text"]+" has been copied!")
});