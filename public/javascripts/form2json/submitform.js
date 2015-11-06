/**
 * Created by Hao on 2015/11/5.
 */
function submit()
{
    //var idofform="'"+formid+"'";
    //console.log(idofform);
    var formData = form2js('testForm', '.',true);

    console.log("click");
    console.log(JSON.stringify(formData));
    //document.getElementById('testArea').innerHTML = JSON.stringify(formData);
}