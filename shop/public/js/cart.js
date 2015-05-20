/**
 * Created by lein on 2015/5/5.
 */
(function($,FG){
    //全选
    $('#all').click(function(){
        if(this.checked){
            $('.product-list :checkbox').attr('checked',true);
        }else{
            $('.product-list :checkbox').attr('checked',false);
        }
    });

    //单选
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

    // 更新价格
    function updatePrice(data) {
        cartPage.find('.total-price').text('¥' + data.totalPrice);
        //cartPage.find('.total-number').text(data.totalNumber);
        //cartPage.find('.preferential').text('-¥' + data.discountPrice);
    }

    // 更新数量
    function updateNumber(ele) {
        var numberInput = ele.parent('.amount').find('.text-number'),
            val = parseInt(numberInput.val(), 0),
            limit = parseInt(numberInput.attr('limit'), 0),
            price = parseInt(numberInput.attr('data-price'), 0);

        //同步更改到数据库
        function sync() {
            var url = $('#updateItemUrl').val();
            $.ajax({
                type: 'POST',
                url: url,
                data: {
                    cartId: $('#cartId').val(),
                    skuId: ele.parent('.amount').attr('data-skuId'),
                    number: val
                },
                async: false,
                dataType: 'JSON',
                success: function (result) {
                    if (result.success) {
                        updatePrice(result.data);
                    }
                }
            });
        }

        function setTotalPrice(val) {
            val = val.toFixed(2);
            ele.parents('tr').find('.col7').text('￥' + val);
        }

        return {
            add: function () {
                val += 1;

                if (val > limit) {
                    val = limit;
                    FG.tip(numberInput, 'limit-tip', '超出此商品能购买的最大数量', 30);
                }

                numberInput.val(val);

                setTotalPrice(val * price);
                sync();

            },
            remove: function () {
                val -= 1;

                if (val < 1) {
                    val = 1;
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



})(jQuery,FG);