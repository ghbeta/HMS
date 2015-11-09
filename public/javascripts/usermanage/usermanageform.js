/**
 * Created by Hao on 2015/11/9.
 */
$(function() {
    $('#searchForm2').hide();
    $('#searchForm0').hide();
    $('#searchtype').change(function(){
        if($('#searchtype').val() == '0') {
            $('#searchForm0').show();
            $('#searchForm1').hide();
            $('#searchForm2').hide();
        }
        if($('#searchtype').val() == '1') {
            $('#searchForm1').show();
            $('#searchForm2').hide();
            $('#searchForm0').hide();
        }
        if($('#searchtype').val() == '2') {
            $('#searchForm2').show();
            $('#searchForm1').hide();
            $('#searchForm0').hide();
        }
    });
});
function UserRoll(){
    this.email="";
    this.role="";
}

function changerole(email,selectid){
  var sendrole=new UserRoll();
    sendrole.email=email;
    sendrole.role=$("#"+selectid).val();
    var senddata=(JSON.stringify(sendrole));
    console.log(senddata);
    var posturl="/usermanage/changerole/admin";
    var xhr= new XMLHttpRequest();
    xhr.open('POST',posturl);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.status == 200) {
            alert(xhr.responseText);
        }
    }
    xhr.send(senddata);
}

