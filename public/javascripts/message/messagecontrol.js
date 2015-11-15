/**
 * Created by Hao on 2015/11/14.
 */
function filter(semester){
    var posturl="/messages/"+semester;
    var xhr= new XMLHttpRequest();
    xhr.open('POST',posturl);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.status == 200) {
            alert(xhr.responseText);
            //console.log(xhr.responseText);
            //threads=JSON.parse(xhr.responseText);
            //console.log(threads)
            //forum.scope.$apply()
            //$route.reload();
        }
    }
    xhr.send(JSON.stringify(semester));
}