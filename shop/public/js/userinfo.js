/**
 * Created by lein on 2015/5/15.
 */
$(function(){

    $('.user-form').find('input[name=name]').blur(function(){
        if($(this).val() && $(this).val().length>20){
            $(this).siblings('.errormsg').remove();
            $(this).parents('.form-item').append('<span class="errormsg">姓名长度不得大于20个字</span>');
            return false;
        }else if(!/^[A-Za-z\u4e00-\u9fa5]{2,20}$/.test($(this).val())){
            $(this).siblings('.errormsg').remove();
            $(this).parents('.form-item').append('<span class="errormsg">请输入2~20个汉字或字母</span>');
            return false;

        }else{
            $(this).siblings('.errormsg').remove();
        }
    });


    $('.user-form input[type=submit]').click(function(e){

        e.preventDefault();
        var form = $(this).parents('form');
        var name = form.find('input[name=name]');
        if(name.val() && name.val().length>20){
            name.siblings('.errormsg').remove();
            name.parents('.form-item').append('<span class="errormsg">用户名长度不得大于20个字</span>');
            return false;
        }else if(/^[A-Za-z\\u4e00-\\u9fa5]+$/.test(name.val())){
            name.siblings('.errormsg').remove();
            name.parents('.form-item').append('<span class="errormsg">用户名长度不得大于20个字</span>');
            return false;
        }




        $.ajax({
            type: "POST",
            async: false,
            url: '/my/data/update',
            data: form.serialize(),
            dataType: 'json',
            success: function (data) {
                if (data.result){ //成功
                    form.find('#addMsg').text('数据保存成功！');
                } else {
                    var valObj = data.message,errMsg;
                    $.each(valObj,function(key,value) {
                        errMsg = value[0];
                    });

                    form.find('#addMsg').text(errMsg);
                }
            }
        });
    });
});
