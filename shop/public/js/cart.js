$(function(){

    $('#all').click(function(){
        if(this.checked){
            $('.product-list :checkbox').attr('checked',true);
        }else{
            $('.product-list :checkbox').attr('checked',false);
        }
    });

    $('.product-list :checkbox').click(function(){
        var chknum = $('.product-list :checkbox').size();
        var chk = 0;

        $('.product-list :checkbox').each(function(){
            if($(this).is(':checked')){
                chk++;
            }
        });
        if(chknum == chk){
            $('#all').attr('checked',true);
        }else{
            $('#all').attr('checked',false);
        }

    });

    var cartPage = $('.mycart');
    function setTotalPrice() {
        var totalPrice = 0,proTotalMoney =$('.pro-total-money') ;
        if(proTotalMoney.size()==0){
            $('.total-price').text('¥' + 0.00);
            return;
        }
        proTotalMoney.each(function(){
            totalPrice += parseInt($(this).text(),0);
        });

        var  val = parseInt(totalPrice, 0).toFixed(2);
        $('.total-price').text('¥' + val);
    }

    // 更新数量
    function updateNumber(ele) {
        var numberInput = ele.parent('.amount').find('.text-number'),
            val = parseInt(numberInput.val(), 0),
            limit = parseInt(numberInput.attr('limit'), 0),
            price = parseInt(numberInput.attr('data-price'), 0);
        // 同步更改到数据库
        function sync() {
            var skuId = ele.parent('.amount').attr('data-skuId');
            $.ajax({
                type: 'get',
                url: ' /cart/addSkuToCartReplaceNum?skuId='+skuId+"&number="+val,
                async: false,
                dataType: 'JSON',
                success: function (result) {
                    if (result.result) {
                        //产品单品总价
                        updatePrice(ele,val,price);
                        //总价
                        setTotalPrice();

                    }
                }
            });
        }

        function updatePrice(ele,val,price){
            ele.parents('tr').find('.pro-total-money').text((val*price).toFixed(2));
        }



        return {
            add: function () {
                val += 1;

                if (val > limit) {
                    val = limit;
                    FG.tip(numberInput, 'limit-tip', '超出此商品能购买的最大数量', 30);
                    return;
                }

                numberInput.val(val);


                sync();

            },
            remove: function () {
                val -= 1;

                if (val < 1) {
                    val = 1;
                    return;
                }

                numberInput.val(val);
                setTotalPrice(val * price);
                sync();
            },
            checkValue: function () {

                if (!/^[1-9]*\d*$/g.test(val)) {
                    val = 1;
                }

                if (val < 1) {
                    val = 1;
                }

                if (val > limit) {
                    val = limit;
                    FG.tip(numberInput, 'limit-tip', '超出此商品能购买的最大数量', 30);
                    return;
                }

                numberInput.val(val);
                setTotalPrice(val * price);
                sync();
            }
        };

    }

    // 增加数量事件
    cartPage.on('click', '.btn-add', function () {
        updateNumber($(this)).add();
    });

    // 减小数量事件
    cartPage.on('click', '.btn-sub', function () {
        updateNumber($(this)).remove();
    });

    // 手动输入数量验证
    cartPage.on('blur', '.text-number', function () {
        updateNumber($(this)).checkValue();
    });

    //提交购物车
    $('#toOrder').click(function(){
        var checkboxs = $('.product-list :checkbox'),cartItem=[];
        checkboxs.each(function(){
            if($(this).is(':checked')){
                cartItem.push($(this).attr('value'));
            }
        });

        if(cartItem.length<1){
            $.dialog({
                title:'提示',
                lock:true,
                content:'<div class="warning-inner clearfix"><p class="warning"><span class="warning-ico"></span>请选择您需要购买的商品</p></div>',
                width:500,
                height:200
            });
            return;
        }

        $.ajax({
            type: "get",
            url: '/cart/selCartItemProcess?selCartItems='+cartItem.join(','),
            async: false,
            dataType: 'json',
            cache: false,
            success: function (data) {
                if(data.result){
                    window.location.href = '/cart/chooseAddress?selCartItems='+cartItem.join(',');
                }
            }
        });

    });

    //删除购物车
    $('.product-list .del').click(function(){
        var that = $(this),globalcartId = that.attr('data-id');
        $.dialog({
            title:'提示',
            lock:true,
            content:'<div class="warning-inner clearfix"><p class="warning"><span class="warning-ico"></span>确定要删除该商品吗？</p></div>',
            width:540,
            height:248,
            drag:false,
            show:function(){
                this.content(that);
                that.parents('tr').addClass('toDelete');
            },
            hide:function(){
                this.content.parents('tr').hasClass('toDelete')? this.content.parents('tr').removeClass('toDelete'):null;
            },
            btn: {
                ok: {
                    val: '确定',
                    type: 'red',
                    click: function(btn) {
                        $.ajax({
                            type: 'get',
                            url:'/cart/deleteCartItem?cartItemId='+globalcartId,
                            dataType: 'json',
                            success: function (data) {
                                if(data.result){
                                    that.parents('tr').remove();
                                    if($('.product-list').find('tr').size() == 0){
                                        $('.mycart-list').remove();
                                        $('.mycart-inner').append("<div class='mycart-empty'><p>购物车里空空如也，赶紧去 <a href='/'>逛逛吧&gt;</a></p></div>");
                                    }
                                    setTotalPrice();
                                }
                            }
                        });
                    }
                },
                cancle: {
                    val: '取消'
                }
            }
        });
    });

});
