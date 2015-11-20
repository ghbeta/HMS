/**
 * Created by Hao on 2015/11/20.
 */
function showcanvas(useremail,semester,lecture,user){
    var posturl="/"+semester+"/"+lecture+"/"+useremail+"/repostatus#home";
    console.log(posturl);
    var xhr= new XMLHttpRequest();
    xhr.open('GET',posturl);
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.status == 200) {
            //alert(xhr.responseText);
            var gitgraph = new GitGraph({
                elementId:user,
                template: "metro",
                orientation: "horizontal",
                mode: "compact"
            });
            var master=gitgraph.branch("master");
            var commithistory=JSON.parse(xhr.responseText);
            for(textmessage in commithistory){
                  master.commit();
            }
        }
    };
    xhr.send();

}