/**
 * Created by lein on 2015/6/1.
 */
$(function(){
    $('.empty-box li').hover(function(){
        $(".title", this).stop().animate({top:"377px"},{queue:false,duration:100});
    },function(){
        $(".title", this).stop().animate({top:"437px"},{queue:false,duration:300});
    });

    //获取购物车 商品数量
    $.ajax({
        url: '/cart/getUserCartItemNum',
        cache:false,
        success: function (data) {
            if (data.result) {
                $('#cart-quantity').text(data.data);
            }
        }
    });

    //固定购物车
    fixedcart($('#cart'), $('#container'));

    //点击购物车，进入购物车页面
    $('#cart-quantity-btn').click(function () {
        window.location.href = "/cart/showCart";
    });


    //轮播
    $('.slider-box-inner').mobilyslider({
        content:'.slider-box-img',
        children:'div',
        transition:'fade',
        animationSpeed:1000,
        autoplay: true,
        autoplaySpeed:6000,
        pauseOnHover:true,
        bullets:true,
        arrows:true,
        arrowsHide:true,
        prev:'prev',
        next:'next',
        animationStart: function(){},
        animationComplete: function(){}
    });


    setInterval(function(){
        $(".time-detail:not(:contains(已结束))").each(function(){
            var obj = $(this);
            //var endTime = new Date(obj.attr('value'));
            var nowTime = new Date(), utc = nowTime.getUTCFullYear()+"/"+(nowTime.getUTCMonth()+1)+"/"+nowTime.getUTCDate()+" "+nowTime.getUTCHours()+":"+nowTime.getUTCMinutes()+":"+nowTime.getUTCSeconds();
            var nMS = +new Date(obj.attr('value').replace(/-/g,'/')) - (+new Date(utc)+28800000);
            //var nMS = +new Date(obj.attr('value').replace(/-/g,'/')) - nowTime.getTime();
            var myD=Math.floor(nMS/(1000 * 60 * 60 * 24)); //天
            var myH=Math.floor(nMS/(1000*60*60)) % 24; //小时
            var myM=Math.floor(nMS/(1000*60)) % 60; //分钟
            var myS=Math.floor(nMS/1000) % 60; //秒
            var myMS=Math.floor(nMS/100) % 10; //拆分秒

            if(myD>= 0){
                var str = myD+"天"+myH+"小时"+myM+"分"+myS+"秒";

            }else{
                var str = "已结束！";
                //$(".time-detail:contains(结束)").siblings('.over-text').fadeOut();
                obj.parent('.time-text').html('').text(str);
            }
            obj.text(str);
        });
    }, 100); //每个0.1秒执行一次



});