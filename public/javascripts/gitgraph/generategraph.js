/**
 * Created by Hao on 2015/11/20.
 */
function showstatus(useremail,semester,lecture,user){
    var elementid="#con"+user;
    if($(elementid).is(":visible")){
        $(elementid).hide();
    }
    else{
    $(elementid).show();
        showcanvas(useremail,semester,lecture,user)}
}

function showcanvas(useremail,semester,lecture,user){
    var posturl="/"+semester+"/"+lecture+"/"+useremail+"/repostatus";
    console.log(posturl);
    var xhr= new XMLHttpRequest();
    xhr.open('GET',posturl);
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.status == 200) {
            //alert(xhr.responseText);
            var gitgraph = new GitGraph({
                elementId:user,
                template: "metro",
                orientation: "vertical",
                mode: "extended"

            });

            var master=gitgraph.branch("master");
            var commithistory=JSON.parse(xhr.responseText);
            //console.log(commithistory);
            for(index in commithistory){
                  master.commit({
                      message:commithistory[index][0],
                      author:commithistory[index][1]

                  });
            }

        }else{
            var gitgraph = new GitGraph({
                elementId:user,
                template: "metro",
                orientation: "vertical",
                mode: "extended"
            });
            var master=gitgraph.branch("master");
            master.commit({message:"init",author:"system"});
        }
    };
    xhr.send();

}