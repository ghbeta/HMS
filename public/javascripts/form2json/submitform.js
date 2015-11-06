/**
 * Created by Hao on 2015/11/5.
 */
function submit(formid,semester,lecture,assignment,student)
{
    var posturl="/admin/"+semester+"/"+lecture+"/"+assignment+"/"+student+"/evaluation";
    console.log(posturl);
    //var formData = form2js('testForm', '.',true);
    var formData = $("#"+formid).serializeArray();
    console.log(JSON.stringify(formData));
    //console.log(JSON.stringify(form));
    //document.getElementById('testArea').innerHTML = JSON.stringify(formData);
    var xhr= new XMLHttpRequest();
    xhr.open('POST',posturl);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.status == 200) {
            alert(xhr.responseText);
        }
    }
    xhr.send(JSON.stringify(formData));
}