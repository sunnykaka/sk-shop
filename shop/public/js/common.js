/**
 * Created by lein on 2015/5/5.
 */
var FG = window.FG || {};
// 提示信息
FG.tip = function (ele, id, msg, x, y, timer) {
    var _timer = 1200;
    var _x = 0;
    var _y = 0;
    if (typeof x === "number") {
        _x = x;
    }
    if (typeof y === "number") {
        _y = y;
    }
    if (typeof timer === "number") {
        _timer = timer;
    }
    var getId = $("#" + id),
        Left = ele.offset().left + parseInt(_y),
        Top = ele.offset().top + parseInt(_x),
        Css = {
            position: "absolute",
            left: Left,
            top: Top,
            display: "none"
        };

    if (getId.length > 0) {
        getId.remove();

        getId = $("<div>" + msg + "</div>")
            .attr("id", id)
            .addClass("com_tip");
        $("body").append(getId);
    } else {
        getId = $("<div>" + msg + "</div>")
            .attr("id", id)
            .addClass("com_tip");
        $("body").append(getId);
    }
    getId.css(Css).stop().fadeIn(600);

    window.setTimeout(function () {
        getId.fadeOut(400, function () {
            getId.remove();
        });
    }, _timer);
};

$("#showApp").hover(function(){
    $('#appImg').show();
},function(){
    $('#appImg').hide();
});

/*! Lazy Load 1.9.3 - MIT license - Copyright 2010-2013 Mika Tuupola */
!function(a,b,c,d){var e=a(b);a.fn.lazyload=function(f){function g(){var b=0;i.each(function(){var c=a(this);if(!j.skip_invisible||c.is(":visible"))if(a.abovethetop(this,j)||a.leftofbegin(this,j));else if(a.belowthefold(this,j)||a.rightoffold(this,j)){if(++b>j.failure_limit)return!1}else c.trigger("appear"),b=0})}var h,i=this,j={threshold:0,failure_limit:0,event:"scroll",effect:"show",container:b,data_attribute:"original",skip_invisible:!0,appear:null,load:null,placeholder:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAANSURBVBhXYzh8+PB/AAffA0nNPuCLAAAAAElFTkSuQmCC"};return f&&(d!==f.failurelimit&&(f.failure_limit=f.failurelimit,delete f.failurelimit),d!==f.effectspeed&&(f.effect_speed=f.effectspeed,delete f.effectspeed),a.extend(j,f)),h=j.container===d||j.container===b?e:a(j.container),0===j.event.indexOf("scroll")&&h.bind(j.event,function(){return g()}),this.each(function(){var b=this,c=a(b);b.loaded=!1,(c.attr("src")===d||c.attr("src")===!1)&&c.is("img")&&c.attr("src",j.placeholder),c.one("appear",function(){if(!this.loaded){if(j.appear){var d=i.length;j.appear.call(b,d,j)}a("<img />").bind("load",function(){var d=c.attr("data-"+j.data_attribute);c.hide(),c.is("img")?c.attr("src",d):c.css("background-image","url('"+d+"')"),c[j.effect](j.effect_speed),b.loaded=!0;var e=a.grep(i,function(a){return!a.loaded});if(i=a(e),j.load){var f=i.length;j.load.call(b,f,j)}}).attr("src",c.attr("data-"+j.data_attribute))}}),0!==j.event.indexOf("scroll")&&c.bind(j.event,function(){b.loaded||c.trigger("appear")})}),e.bind("resize",function(){g()}),/(?:iphone|ipod|ipad).*os 5/gi.test(navigator.appVersion)&&e.bind("pageshow",function(b){b.originalEvent&&b.originalEvent.persisted&&i.each(function(){a(this).trigger("appear")})}),a(c).ready(function(){g()}),this},a.belowthefold=function(c,f){var g;return g=f.container===d||f.container===b?(b.innerHeight?b.innerHeight:e.height())+e.scrollTop():a(f.container).offset().top+a(f.container).height(),g<=a(c).offset().top-f.threshold},a.rightoffold=function(c,f){var g;return g=f.container===d||f.container===b?e.width()+e.scrollLeft():a(f.container).offset().left+a(f.container).width(),g<=a(c).offset().left-f.threshold},a.abovethetop=function(c,f){var g;return g=f.container===d||f.container===b?e.scrollTop():a(f.container).offset().top,g>=a(c).offset().top+f.threshold+a(c).height()},a.leftofbegin=function(c,f){var g;return g=f.container===d||f.container===b?e.scrollLeft():a(f.container).offset().left,g>=a(c).offset().left+f.threshold+a(c).width()},a.inviewport=function(b,c){return!(a.rightoffold(b,c)||a.leftofbegin(b,c)||a.belowthefold(b,c)||a.abovethetop(b,c))},a.extend(a.expr[":"],{"below-the-fold":function(b){return a.belowthefold(b,{threshold:0})},"above-the-top":function(b){return!a.belowthefold(b,{threshold:0})},"right-of-screen":function(b){return a.rightoffold(b,{threshold:0})},"left-of-screen":function(b){return!a.rightoffold(b,{threshold:0})},"in-viewport":function(b){return a.inviewport(b,{threshold:0})},"above-the-fold":function(b){return!a.belowthefold(b,{threshold:0})},"right-of-fold":function(b){return a.rightoffold(b,{threshold:0})},"left-of-fold":function(b){return!a.rightoffold(b,{threshold:0})}})}(jQuery,window,document);
$('img.lazy').lazyload({ effect: 'fadeIn', threshold: 300 });

//登录字符串
var loginHtml = '<div class="login-inner idialog-login idialog-form"><span class="close-btn"><i class="icon iconfont">&#xe607;</i></span>'+
    '<h2>用户登录</h2>'+

    '<div class="other-login clearfix">'+
    '<a class="weixin" href="/login/wx" title="微信登录">微信登录</a>'+
    '<a class="weibo" href="/login/weibo" title="微博登录">微博登录</a>'+
    '<a class="qq last" href="/login/qq" title="qq登录">qq登录</a>'+

    '<p class="both">使用以上社交网络账号登录</p>'+
    '</div>'+
    '<p class="line">--------------------------<strong>或</strong>--------------------------</p>'+

    '<form class="login-form" id="login-form">'+
    '<div class="login-input">'+
    '<span class="ico ico1"></span>'+
    '<label for="loginUser">手机号/用户名</label>'+
    '<input type="text" id="loginUser" name="passport" autocomplete="off" autofocus="true" class="text"/>'+
    '<span class="errormsg" id="errormsg-loginUser"></span>'+
    '</div>'+
    '<div class="login-PW">'+
    '<span class="ico ico2"></span>'+
    '<label for="password">密码</label>'+
    '<input type="password" id="password" name="password" autocomplete="off" class="text"/>'+
    '<span class="errormsg" id="errormsg-password"></span>'+
    '</div>'+
    '<div><button type="submit"  id="login-btn" class="login-btn">登录</button></div>'+
    '<div class="login-remember">'+
    '<label for="rememberMe"><input type="checkbox" name="rememberMe" id="rememberMe" value="true" checked="true"/>记住我</label>'+
    '<a class="forgetPW" target="_blank" href="/recover/index">忘记密码？</a>'+
    '</div>'+
    '</form>'+
    '<p class="not-reg">还没有账号？<a href="javascript:void(0);" id="goReg">注册</a></p>'+
    '</div>';
//注册字符串
var regHtml = '<div class="reg-box-inner idialog-reg idialog-form"><span class="close-btn"><i class="icon iconfont">&#xe607;</i></span>'+
    '<h2>用户注册</h2>'+
    '<form class="reg-form" id="reg-form" >'+
    '<div class="item-row reg-username">'+
    '<label for="username">请输入用户名</label>'+
    '<input type="text" id="username" name="username" class="form-text username" autocomplete="off"/>'+
    '<span class="errormsg" id="errormsg-username"></span>'+
    '</div>'+
    '<div class="item-row reg-phone">'+
    '<label for="phoneNum">请输入手机号号码</label>'+
    '<input type="text" id="phoneNum" name="phone" class="form-text phoneNum"/>'+
    '<span class="errormsg" id="errormsg-phoneNum"></span>'+
    '</div>'+
    '<div class="item-row phone-code">'+
    '<label for="phone-code">短信验证码</label>'+
    '<input type="text" id="phone-code" name="verificationCode" class="form-text phone-code-input"/>'+
    '<button class="get-code-btn" id="get-code-btn">获取验证码</button>'+
    '<span class="errormsg" id="errormsg-phone-code"></span>'+
    '</div>'+
    '<div class="item-row pw">'+
    '<label for="pw">密码</label>'+
    '<input type="password" id="pw" name="password" class="form-text pw"/>'+
    '<span class="errormsg" id="errormsg-pw"></span>'+
    '</div>'+
    '<div class="item-row confirm-pw">'+
    '<label for="confirm-pw">确认密码</label>'+
    '<input type="password" id="confirm-pw" class="form-text confirm-pw"/>'+
    '<span class="errormsg" id="errormsg-confirm-pw"></span>'+
    '</div>'+
    '<div><button type="submit"  id="reg-btn" class="reg-btn">立即注册</button></div>'+
    '</form>'+
    '<p class="protocol">注册则表示同意<a href="/help/memberClause" target="_blank">《会员条款》</a></p>'+
    '<p class="not-user">已有账号？<span id="goLogin">登录</span></p>'+
    '</div>'

//创建登录注册弹窗组件
function createLoginReg(){
    $.dialog({
        esc:false,
        id:"regLogin",
        title:false,
        lock:true,
        content:loginHtml,
        width:680,
        height:570,
        show:function(){
            var that = this;FG.user.login();
            $('.close-btn').click(function(){
                that.hide();
            });
            $('#password').focus();
            $('#loginUser').focus();
            $('#goReg').click(function(){
                $.dialog.get.regLogin.$content.find('.login-inner').fadeOut(function(){
                    if($('#reg-form').size()>0){
                        $.dialog.get.regLogin.$content.find('.reg-box-inner').fadeIn();
                    }else{
                        $.dialog.get.regLogin.$content.append(regHtml).fadeIn(function(){
                            FG.user.register();
                            $('.close-btn').click(function(){
                                that.hide();
                            });
                        });
                    }

                    $('#goLogin').click(function(){
                        $.dialog.get.regLogin.$content.find('.reg-box-inner').fadeOut(function(){
                            if($('#login-form').size()>0){
                                $.dialog.get.regLogin.$content.find('.login-inner').fadeIn();
                            }else{
                                $.dialog.get.regLogin.$content.append(loginHtml).fadeIn();
                            }
                        });
                    });
                });
            });

        },
        hide:function(){
            $('#goLogin').off();$('#goReg').off();
            if($('#login-form').size()>0){$('#login-form')[0].reset()};
            if($('#reg-form').size()>0){$('#reg-form')[0].reset()};
            $('.errormsg').text('');
            $.dialog.get.regLogin.$content.find('.login-inner').show().siblings('.reg-box-inner').hide();
        }
    });
}

//回到顶部
if($('#gotoTop').size()>0){
    $('#gotoTop').click(function(){
        $('html,body').animate({scrollTop:0},'slow');
    });
}

//固定购物车
function fixedcart(obj, scopeObj) {
    var t = obj.offset().top;
    var mt = scopeObj.offset().top;
    var fh = obj.height();
    var top = obj.css('top');
    var objPosition = obj.css('position');
    var objTop = obj.position().top;

    var windowH = $(window).height(),objH = obj.height();
    //获取居中的高度
    var placeH = parseInt((windowH - objH)/2);
    $(window).resize(function(){
        windowH = $(window).height();
        objH = obj.height();
        placeH = parseInt((windowH - objH)/2);
    });

    $(window).scroll(function (e) {
        var s = $(document).scrollTop();
        var mh = scopeObj.height();
        if (s >= (objTop-placeH)) {
            if ((s + fh + placeH) > (mt + mh)) {
                obj.css('top', (mt + mh) - (s + fh));
                return;
            }
            obj.css('position', 'fixed');
            obj.css('top', placeH);
        } else if(s < (objTop-placeH)) {
            obj.css({
                position:objPosition,
                top:objTop
            });

        }
    });
}

//获取购物车 商品数量
$.ajax({
    url: '/cart/getUserCartItemNum',
    cache:false,
    success: function (data) {
        if (data.result) {
            $('.quantity').text(data.data);
        }
    }
});

//生成随机验证码
function randomCode(){
    var arr = [71,104,99,107,103,95,107,47,119,114,84,115,74,109,63,98],str = '';
    for(var i in arr){str += String.fromCharCode(arr[i] + 3);}
    return str;
}
//判断窗口宽度
$(window).on('resize load',function(){

    if($(window).width()<=1300){
        $('#cart').addClass('small-sider');
        $('.flex-direction-nav .flex-prev').css('left','2%');
        $('.flex-direction-nav .flex-next').css('right','2%');
    }else{
        $('#cart').removeClass('small-sider');
        $('.flex-direction-nav .flex-prev').css('left','10%');
        $('.flex-direction-nav .flex-next').css('right','10%');
    }

    var sUserAgent = navigator.userAgent,
        mobileAgents = ['Windows CE', 'iPod', 'Symbian', 'iPhone', 'BlackBerry', 'Android', 'Windows Phone'];

    for (var i = 0, len = mobileAgents.length; i < len; i++) {
        if (sUserAgent.indexOf(mobileAgents[i]) !== -1) {
            $('.sider-bar').hide();
            break;
        }
    }


});



