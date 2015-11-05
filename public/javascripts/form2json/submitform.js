/**
 * Created by Hao on 2015/11/5.
 */
function test()
{
    var formData = form2js('exerciseForm', '.',true);

    document.getElementById('testArea').innerHTML = JSON.stringify(formData);
}