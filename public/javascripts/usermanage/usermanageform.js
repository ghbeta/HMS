/**
 * Created by Hao on 2015/11/9.
 */
$(function() {
    $('#searchForm2').hide();
    $('#searchtype').change(function(){
        if($('#searchtype').val() == '0') {
            $('#searchForm1').hide();
            $('#searchForm2').hide();
        }
        if($('#searchtype').val() == '1') {
            $('#searchForm1').show();
            $('#searchForm2').hide();
        }
        if($('#searchtype').val() == '2') {
            $('#searchForm2').show();
            $('#searchForm1').hide();
        }
    });
});