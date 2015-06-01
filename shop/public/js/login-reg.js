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
    var timer = null,i = 120;
    //短信倒计时
    function fun(obj){
        obj.attr('disabled','true');
        obj.text(i+'秒后重新获取');
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
            obj.text(i+'秒后可重新获取');
        }
    }

    var winsns=(function(){
        var o={};

        function clearPrev(){//dereference
            for(var key in o){
                if(key.indexOf("/login")>-1){
                    o[key].close&&o[key].close();
                    o[key]=null;
                    delete o[key];
                }
            }
        }

        return {
            open:function(url){
                var l,t;
                if(o[url]&&o[url].closed===false){
                    o[url].focus&&o[url].focus();
                    return ;
                }
                clearPrev();
                l=(screen.width-600)/2,
                    t=(screen.height-400)/2;
                (o[url]=window.open(url, '_blank', 'toolbar=no, directories=no, status=no, menubar=no, width=600, height=500, top='+t+', left='+l)).focus();
            },
            clear:clearPrev
        }
    })();




    function register(){
        var regBtn = $('#reg-btn'),msgEle=$('#errormsg-confirm-pw'),getCodeBtn = $('#get-code-btn');

        getCodeBtn.on('click',function(evt){
            var phoneNum=$('#phoneNum').val();
            evt.preventDefault();
            fun(getCodeBtn);
            //根据手机号码获取验证码
            $.ajax({
                type: 'POST',
                async: false,
                url: '/user/phone/get_code?phone='+phoneNum,
                data: {
                    phone: phoneNum
                },
                dataType: "json",
                success: function (response) {

                }
            });
        });

        var options = {
            form:$('#reg-form'),
            defaultTipsPosition:'bottom',
            defaultTipsWidth:120,
            label: true,
            items:[
                {
                    id: 'username',
                    allowBlank: false,
                    minLength: 4,
                    maxLength: 20,
                    vtype: 'Account',
                    tipsText: '请输入4-20字符，可由中文、英文或者数字组成',
                    allowBlankText: '用户名不能为空',
                    validatorSynchro: function (value, callback) {
                        //检查用户名是否存在
                        $.ajax({
                            type: 'GET',
                            async: false,
                            url: '/user/username/exist',
                            data: {
                                username: value
                            },
                            dataType: "json",
                            success: function (response) {
                                if (response.result) {
                                    callback(false, response.message);
                                }
                            }
                        });
                    }
                },
                {
                    id:'phoneNum',
                    allowBlank: false,
                    vtype: 'Mobile',
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
                                    callback(false, response.message);
                                }
                            }
                        });
                    }
                },
                {
                    id:'phone-code',
                    allowBlank: false,
                    vtype:'PhoneCode',
                    tipsText: '输入6位手机短信验证码',
                    allowBlankText: '手机验证码不能为空'
                },
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
        },RegisterFormValidate;
        RegisterFormValidate = new FG.user.FormValidate(options,function(){
            var form = options.form;
            regBtn.attr('disabled', true);
            regBtn.text('正在提交...');
            $.ajax({
                type:'POST',
                url:'/register',
                data:form.serialize(),
                dataType: 'JSON',
                success:function(response){
                    if (response.result) {
                        window.location.href= response.data;
                    }else{
                        regBtn.attr('disabled', false);
                        regBtn.text('立即注册');
                        msgEle.html(response.message);
                    }
                },
                error:function(){
                    msgEle.html('服务器错误');
                }
            })
        });

        RegisterFormValidate.init();
        delayHideLabel(options.items);
    }

    //登陆
    function login(){
        var options = {
                form: $('#login-form'),
                label: true,
                items: [
                    {
                        id: 'username',
                        //blankText: '手机号/邮箱/用户名',
                        allowBlank: false,
                        allowBlankText: '账号不能为空'
                    },
                    {
                        id: 'password',
                        allowBlank: false,
                        allowBlankText: '密码不能为空'
                    }
                ]
            }, loginFormValidate = new FG.user.FormValidate(options, function () {
            var form = options.form,
                msgEle = $('#errormsg-password');

            $.ajax({
                type: 'POST',
                url: '/login',
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
        loginFormValidate.init();
        delayHideLabel(options.items);

        $(document).on("click","[data-login-sns]",function(){
            winsns.open($(this).attr("data-login-sns"));
        });
    }

    //注册对外接口
    FG.user.register = register;

    FG.user.login = login;

})(jQuery);