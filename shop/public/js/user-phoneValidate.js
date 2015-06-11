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

    var getCodeBtn = $('#get-code-btn');

    getCodeBtn.on('click',function(evt){
      if($("#errormsg-phoneNum").size()>0){
          if($("#errormsg-phoneNum").text() != "") return false;
        }
        $(this).attr('disabled',true);
        var phoneNum=$('#phoneNumber').val(),that = $(this);
        evt.preventDefault();
        //根据手机号码获取验证码
       if(ValidatePhone($('#phoneNumber'))){
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
                    if(!response.result){
                        $('#errormsg-phoneCode').text(response.message);
                        that.attr('disabled',null);
                        that.text('获取验证码');
                        clearInterval(timer);
                        //验证码发送错误
                        if($('.phoneValidate-first').size()>0){
                          $('.phoneValidate-first').find('#errormsg-phoneCode').text(response.message);
                        }else{
                             $('.phoneValidate-sec').find('#errormsg-phoneNum').text(response.message);
                        }
                    }
               }
           });
       }
    });


    //验证手机第二步
    $('.phoneValidate-sec .phoneNum').on('blur',function(){
        var phoneNum = $(this).val();
       if(ValidatePhone($(this))){
           $.ajax({
               type: 'POST',
               async: false,
               url: '/my/security/phone/check?phone='+phoneNum,
               data: {
                   phone: phoneNum
               },
               dataType: "json",
               success: function (data) {
                    if(data.result){
                         $('#errormsg-phoneNum').text("");                
                    }else{
                        $('#errormsg-phoneNum').text(data.message);
                    }
               }
           });
       }
    });

    $('.phoneValidate-sec .next-btn').on('click',function(evt){
        $('.errormsg').text('');
        evt.preventDefault();
        var validateCode = $('.phoneCode').val();
        var phone = $('.phoneNum').val();
        //验证手机
        if(ValidatePhone($('.phoneNum'))){
            if(ValidatePhoneCode($('.phoneCode'))){

                $.ajax({
                    type: 'POST',
                    async: false,
                    url: '/my/security/phone/end',
                    data: {
                        verificationCode: validateCode,
                        token:$('#token').val(),
                        phone:phone
                    },
                    dataType: "json",
                    success: function (data) {
                        if(data.result){
                            location.href = data.data;
                        }else{
                            $('.phoneValidate-sec').find('#errormsg-phoneCode').text(data.message);
                        }
                    }
                });
            }
        }
    });


//验证手机第一步


    $('.phoneValidate-first .next-btn').on('click',function(evt){
        evt.preventDefault();
        var validateCode = $('.phoneCode').val();
        //验证手机验证码
        if(ValidatePhoneCode($('.phoneCode'))){
            //根据手机号码获取验证码
            $.ajax({
                type: 'POST',
                async: false,
                url: '/my/security/phone/new',
                data: {
                    verificationCode: validateCode
                },
                dataType: "json",
                success: function (data) {
                    if(data.result){
                        location.href = data.data;
                    }else{
                        $('.phoneValidate-first').find('#errormsg-phoneCode').text(data.message);
                    }
                }
            });
        }
    });
});

