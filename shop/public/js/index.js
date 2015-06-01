/**
 * Created by lein on 2015/6/1.
 */
$(function(){
    $('.empty-box li').hover(function(){
        $(".title", this).stop().animate({top:"367px"},{queue:false,duration:100});
    },function(){
        $(".title", this).stop().animate({top:"437px"},{queue:false,duration:300});
    });

    //轮播
    $('.slider-box-inner').mobilyslider({
        content:'.slider-box-img',
        children:'div',
        transition:'fade',
        animationSpeed:1000,
        autoplay: true,
        autoplaySpeed:3000,
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
        $(".time-detail").each(function(){
            var obj = $(this);
            var endTime = new Date(obj.attr('value'));
            var nowTime = new Date();
            var nMS=endTime.getTime() - nowTime.getTime();
            var myD=Math.floor(nMS/(1000 * 60 * 60 * 24)); //天
            var myH=Math.floor(nMS/(1000*60*60)) % 24; //小时
            var myM=Math.floor(nMS/(1000*60)) % 60; //分钟
            var myS=Math.floor(nMS/1000) % 60; //秒
            var myMS=Math.floor(nMS/100) % 10; //拆分秒

            if(myD>= 0){
                var str = myD+"天"+myH+"小时"+myM+"分"+myS+"."+myMS+"秒";

            }else{
                var str = "已结束！";
            }
            obj.text(str);
        });
    }, 100); //每个0.1秒执行一次



});