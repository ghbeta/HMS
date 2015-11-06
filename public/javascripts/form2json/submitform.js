/**
 * Created by Hao on 2015/11/5.
 */
function submit(formid)
{
    var idofform="'"+"form"+"."+formid+"'";
    console.log(idofform);
    //var formData = form2js('testForm', '.',true);
    var formData = $("#"+formid).serializeArray();
    console.log(JSON.stringify(formData));
    //console.log(JSON.stringify(form));
    //document.getElementById('testArea').innerHTML = JSON.stringify(formData);
}