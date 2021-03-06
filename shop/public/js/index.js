/**
 * Created by lein on 2015/6/1.
 */
$(function(){
    //固定购物车
    fixedcart($('#cart'), $('#container'));
    //点击购物车，进入购物车页面
    $('#cart-quantity-btn').click(function () {
        window.location.href = "/cart/showCart";
    });

    $(function() {
        $(".flexslider").flexslider({
            animation:"fade",
            pauseOnHover:true,
            touch: false
        });
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
                if(obj.parents('.preview').size()>0){
                    obj.parent('.time-text').html('').text("已开始！");
                }else{
                    obj.parent('.time-text').html('').text(str);
                }
            }
            obj.text(str);
        });
    }, 100); //每个0.1秒执行一次

});