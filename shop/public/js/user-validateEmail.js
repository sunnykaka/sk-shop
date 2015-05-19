/**
 * Created by lein on 2015/5/18.
 */
$(function(){
    var timer = null,i = 60;
//短信倒计时
    function fun(obj,str){
        obj.attr('disabled','true');
        obj.text(i+'秒后重获取');
        timer = setInterval(function(){show(obj,str)},1000);
    }

    function show(obj,str){
        i--;
        if(i==0){
            obj.attr('disabled',null);
            obj.text(str);
            i=120;
            clearInterval(timer);
        }else{
            obj.text(i+'秒后重发送');
        }

    }
    //手机验证码
    function ValidatePhoneCode(obj){
        if(!/^[\d]{6}/.test(obj.val())){
            obj.siblings('.errormsg').text('手机验证码不正确');
            return false;
        }else{
            return true;
        }
    }

    //验证手机
    function ValidatePhone(obj){
        if(!/^0?(1[34578][0-9]{9})$/.test(obj.val())){
            obj.siblings('.errormsg').text('手机格式不正确');
            return false;
        }else{
            return true;
        }
    }

    //判断邮箱格式
    function ValidateEmail(obj){
        if(!/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.test(obj.val())){
            obj.siblings('.errormsg').text('邮箱格式不正确');
            return false;
        }else{
            return true;
        }
    }

    var getCodeBtn = $('#get-code-btn');

    getCodeBtn.on('click',function(evt){
        var phoneNum=$('#phoneNumber').val();
        evt.preventDefault();

        //根据手机号码获取验证码
       if(ValidatePhone($('#phoneNumber'))){
           $('.errormsg').text('');
           fun(getCodeBtn,'获取验证码');
           $.ajax({
               type: 'POST',
               async: false,
               url: '/user/phone/sendSMS?phone='+phoneNum,
               data: {
                   phone: phoneNum
               },
               dataType: "json",
               success: function (response) {

               }
           });
       }
    });





    //修改密码第二步
    $('.email').on('blur',function(){
        $(this).siblings('.errormsg').text('');
       if(!ValidateEmail($(this))){
           $(this).siblings('.errormsg').text('邮箱格式不正确');
           return false;
       }
    });
    $('.send-email').on('click',function(evt){
        evt.preventDefault(); var email = $('#email');
        if(ValidateEmail(email)){
            $('.errormsg').text('');
            fun($(this),'重发送email到邮箱');
            $.ajax({
                type: 'POST',
                async: false,
                url: '/my/security/email/end',
                data: {
                    email: email.val(),
                    token:$('#token').val()
                },
                dataType: "json",
                success: function (data) {
                    if(!data.result){

                        $('.validateEmail-first').find('#errormsg-email').text(data.message);
                    }
                }
            })
        }
    });

//修改密码第一步

    $('.validateEmail-first .next-btn').on('click',function(evt){
        evt.preventDefault();
        var validateCode = $('.phoneCode').val();
        //验证手机验证码
        if(ValidatePhoneCode($('.phoneCode'))){
            //根据手机号码获取验证码
            $.ajax({
                type: 'POST',
                async: false,
                url: '/my/security/email/new',
                data: {
                    verificationCode: validateCode
                },
                dataType: "json",
                success: function (data) {
                    if(data.result){
                        location.href = data.data;
                    }else{
                        var valObj = data.message,errMsg;
                        $.each(valObj,function(key,value) {
                            errMsg = value[0];
                        });

                        $('.validateEmail-first').find('#errormsg-phoneCode').text(errMsg);
                    }
                }
            });
        }
    });
});
