/**
 * Created by lein on 2015/5/18.
 */
$(function(){
    var timer = null,i = 60;
//短信倒计时
    function fun(obj){
        obj.attr('disabled','true');
        obj.text(i+'秒后重获取');
        timer = setInterval(function(){show(obj)},1000);
    }

    function show(obj){
        i--;
        if(i==0){
            obj.attr('disabled',null);
            obj.text('获取验证码');
            i=120;
            clearInterval(timer);
        }else{
            obj.text(i+'秒后重获取');
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

    //判断密码格式
    function ValidatePw(obj){
        if(!/^.{6,20}$/.test(obj.val())){
            obj.siblings('.errormsg').text('密码格式不正确');
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
           fun(getCodeBtn);
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
//获取旧密码值
    var oldPw = $('.oldPw'),newPw = $('.newPw'),confirmPw = $('.confirmPw');
    oldPw.on('blur',function(){
       $(this).siblings('.errormsg').text('');
        ValidatePw($(this));
    });
    newPw.on('blur',function(){
        $(this).siblings('.errormsg').text('');
        if(ValidatePw($(this))){
            if(oldPw.val() === newPw.val()){
                $(this).siblings('.errormsg').text('新密码与旧密码不能相等');
                return false;
            }

        }
    });

    confirmPw.on('blur',function(){
        $(this).siblings('.errormsg').text('');
        if(ValidatePw($(this))){
            if(newPw.val() != confirmPw.val()){
                $(this).siblings('.errormsg').text('确认密码必须与新密码一致');
                return false;
            }
        }

    });

    $('.updatePw-sec .next-btn').on('click',function(evt) {
        evt.preventDefault();
        ValidatePw(oldPw);
        ValidatePw(newPw);
        ValidatePw(confirmPw);
        if(ValidatePw(newPw)){
            if(oldPw.val() === newPw.val()){
                newPw.siblings('.errormsg').text('新密码与旧密码不能相等');
                return false;
            }
        }
        if( ValidatePw(confirmPw)){
            if(newPw.val() != confirmPw.val()){
                confirmPw.siblings('.errormsg').text('确认密码必须与新密码一致');
                return false;
            }
        }
        $.ajax({
            type: 'POST',
            async: false,
            url: '/my/security/psw/end',
            data: {
                password:oldPw.val(),
                newPassword:newPw.val(),
                rePassword:confirmPw.val(),
                token:$('#token').val()
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

                    $('.updatePw-sec').find('#errormsg-confirmPw').text(errMsg);
                }
            }
        });

    });

//修改密码第一步

    $('.phoneValidate-first .next-btn').on('click',function(evt){
        evt.preventDefault();
        var validateCode = $('.phoneCode').val();
        //验证手机验证码
        if(ValidatePhoneCode($('.phoneCode'))){
            //根据手机号码获取验证码
            $.ajax({
                type: 'POST',
                async: false,
                url: '/my/security/psw/new',
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

                        $('.updatePw-first').find('#errormsg-phoneCode').text(errMsg);
                    }
                }
            });
        }
    });
});

