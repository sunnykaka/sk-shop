/**
 * Created by lein on 2015/5/22.
 */
$(function(){
    $('.select-address li').hover(function(){
        $(this).addClass('highLight');
    },function(){
        $(this).removeClass('highLight');
    });

    //提交订单
    $('#order-submit-btn').click(function(){
        var addressId = $('.select-address li.current').attr('data-id');
        var selItems = $('#selItems').val(),isPromptlyPay = $('#isPromptlyPay').val();
        $.ajax({
            type: "get",
            url: '/order/submitOrder?selItems='+selItems+"&addressId="+addressId+"&isPromptlyPay="+isPromptlyPay,
            async: false,
            dataType: 'json',
            cache: false,
            success: function (data) {
                if(data.result){
                    window.location.href = '/order/toOrderPlay?orderId='+data.data;
                }
            }
        });
    });
});