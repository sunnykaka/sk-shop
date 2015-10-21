/**
 * Created by lein on 2015/4/28.
 */
(function($){
    //隐藏label
    function hideLabel(items) {
        var i, itemsCount = items.length;
        for (i = 0; i < itemsCount; i++) {
            if ($('#' + items[i].id).val() != '') {
                $('label[for=' + items[i].id + ']').hide();
            }
        }

    }
    //延迟隐藏label
    function delayHideLabel(items) {
        $(window).on('load', function () {
            setTimeout(function () {
                hideLabel(items);
            }, 400);
        });
    }

    //给手机号码添加星星隐藏
    function hideTel(tel){
        return tel.replace(/(1[358]{1}[0-9])[0-9]{4}([0-9]{4})/i,"$1****$2");
    }

    var phoneNumber = $('#phoneNumber').val();
    if(phoneNumber){
        $('.userPhone').text(hideTel(phoneNumber));
    }

    // 重载图形验证码
    function reloadImageCode() {
        $('.code').attr('src', '/image/code?' + new Date().getTime());
    }

    $('.code').on('click', function () {
        reloadImageCode();
    });

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


    //找回密码第三步
    function forgetThrPw(){
        var options = {
            form: $('#forget-pw-thr-form'),
            label: true,
            defaultTipsPosition:'bottom',
            items: [
                {
                    id: 'pw',
                    itemType: 'password',
                    vtype: 'PassWord',
                    allowBlank: false,
                    allowBlankText: '密码不能为空',
                    tipsText: '请输入6-16位英文、数字或符号',
                    minLength: 6,
                    maxLength: 16
                },
                {
                    id: 'confirm-pw',
                    itemType: 'password',
                    vtype: 'PassWord',
                    allowBlank: false,
                    allowBlankText: '确认密码不能为空',
                    //tipsText: '请再次输入密码',
                    validator: function (v) {
                        var passwordValue = $('#pw').val();
                        if (passwordValue != v) {
                            return '两次输入的密码不一致';
                        }
                        return true;
                    }
                }
            ]
        }, forgetPwThrFormValidate = new FG.user.FormValidate(options, function () {
            var form = options.form,
                msgEle = $('#errormsg-vcode');

            $.ajax({
                type: 'POST',
                url: '/recover/pswDo',
                async: false,
                data: form.serialize(),
                dataType: 'JSON',
                success: function (response) {
                    if (response.result) {
                        window.location.href= response.data;
                    }else{
                        msgEle.html(response.message);
                    }
                },
                error: function () {
                    msgEle.html('服务器错误');
                }
            });
        });
        forgetPwThrFormValidate.init();
        delayHideLabel(options.items);
    }


    //找回密码第二步
    function forgetSecPw(){

        var getCodeBtn = $('#get-code-btn'),that = $(this);

        getCodeBtn.on('click',function(evt){
            var phoneNum=$('#phoneNum').val();
            evt.preventDefault();
            fun(getCodeBtn);
            //根据手机号码获取验证码
            var randomCodeStr = randomCode();
            $.ajax({
                type: 'POST',
                async: false,
                url: '/user/phone/get_code?phone='+phoneNumber+"&code="+randomCodeStr,
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
                    }
                }
            });
        }).trigger('click');


        var options = {
            form: $('#forget-pw-sec-form'),
            label: true,
            defaultTipsPosition:'bottom',
            items: [
                {
                    id: 'phoneCode',
                    allowBlank: false,
                    vtype:'PhoneCode',
                    maxLength: 6,
                    tipsText: '输入6位手机短信验证码',
                    allowBlankText: '手机验证码不能为空'
                }
            ]
        }, forgetPwSecFormValidate = new FG.user.FormValidate(options, function () {
            var form = options.form,
                msgEle = $('#errormsg-phoneCode');

            $.ajax({
                type: 'POST',
                url: '/recover/checkSms',
                async: false,
                data: {phone:phoneNumber,verificationCode:$('#phoneCode').val()},
                dataType: 'JSON',
                success: function (response) {
                    if (response.result) {
                        window.location.href= response.data;
                    }else{
                        msgEle.html(response.message);
                    }
                },
                error: function () {
                    msgEle.html('服务器错误');
                }
            });
        });
        forgetPwSecFormValidate.init();
        delayHideLabel(options.items);
    }


    //找回密码第一步
    function fontgetPw(){
        var options = {
                form: $('#forget-pw-fir-form'),
                label: true,
                defaultTipsPosition:'bottom',
                items: [
                    {
                        id: 'username',
                        allowBlank: false,
                        minLength: 4,
                        maxLength: 20,
                        vtype: 'Mobile',
                        tipsText: '请输入正确的手机号码',
                        allowBlankText: '手机号码不能为空',
                        validatorSynchro: function (value, callback) {
                            //检查手机号码是否存在
                            $.ajax({
                                type: 'GET',
                                async: false,
                                url: '/user/phone/exist',
                                data: {
                                    phone: value
                                },
                                dataType: "json",
                                success: function (response) {
                                    if (response.result) {
                                       return true;
                                    }else{
                                        callback(false,'该手机号不存在!');
                                    }
                                }
                            });
                        }
                    },
                    {
                        id: 'vcode',
                        allowBlank: false,
                        vtype:'Vcode',
                        maxLength: 4,
                        tipsText: '请输入4位验证码',
                        allowBlankText: '验证码不能为空'
                    }
                ]
            }, forgetPwFormValidate = new FG.user.FormValidate(options, function () {
            var form = options.form,
                msgEle = $('#errormsg-vcode');

            $.ajax({
                type: 'POST',
                url: '/recover/checkPhone',
                async: false,
                data: form.serialize(),
                dataType: 'JSON',
                success: function (response) {
                    if (response.result) {
                        window.location.href= response.data;
                    }else{
                        msgEle.html(response.message);
                    }
                },
                error: function () {
                    msgEle.html('服务器错误');
                }
            });
        });
        forgetPwFormValidate.init();
        delayHideLabel(options.items);
    }


    FG.user.forgetSecPw = forgetSecPw;
    FG.user.forgetThrPw = forgetThrPw;
    FG.user.fontgetPw = fontgetPw;

})(jQuery);