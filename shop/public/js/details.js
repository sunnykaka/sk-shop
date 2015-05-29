/**
 * Created by lein on 2015/5/22.
 */
$(function(){

    //获取购物车 商品数量
    $.ajax({
        url:'/cart/getUserCartItemNum',
        success:function(data) {
            if(data.result){
                $('#cart-quantity').text(data.data);
            }
        }
    });

    //点击购物车，进入购物车页面
    $('#cart-quantity-btn').click(function(){
       window.location.href = "/cart/showCart";
    });



    //页面初始化滚动
    $.scrollTo('#detail',500);

    $('.comment-header li').click(function(){
        $(this).addClass('current').siblings('li').removeClass('current');
    });
    //查看详情
    $('#openDetail').click(function(){
        $('.container').slideDown(300);
        $('.hide-detail').show();
    });
    //隐藏详情
    $('#hideDetail').click(function(){
        $(this).hide();
        $('.container').slideUp(500);
    });


    //我喜欢按钮
    $('.detail-like .like').click(function(){
        //$(this).toggleClass('current');
        if($(this).is('.current')){
            $(this).removeClass('current');
            $('.like-text').text('我喜欢');
        }else{
            $(this).addClass('current');
            $('.like-text').text('50000000');
        }
    });

    //市场价格tips
    $('.market-price').hover(function(){
        $(this).addClass('current');
        $('.info-tip').fadeIn(500);
    },function(){
        $(this).removeClass('current');
        $('.info-tip').hide();
    })

    //转换skuMap数据
    function skuModel(skuMap) {

        //保存最后的组合结果信息
        var SKUResult = {};

        //获得对象的key
        function getObjKeys(obj) {
            if (obj !== Object(obj)) throw new TypeError('Invalid object');
            var keys = [];
            for (var key in obj)
                if (Object.prototype.hasOwnProperty.call(obj, key))
                    keys[keys.length] = key;
            return keys;
        }

        //把组合的key放入结果集SKUResult
        function add2SKUResult(key, sku) {
            if (SKUResult[key]) {//SKU信息key属性·
                SKUResult[key].stock += sku.stockQuantity;
                SKUResult[key].price.push(sku.price);
            } else {
                SKUResult[key] = {
                    stock: sku.stockQuantity,
                    price: [sku.price]
                };
            }
        }

        //初始化得到结果集
        function initSKU() {
            var skuKeys = getObjKeys(skuMap);
            for (i = 0; i < skuKeys.length; i++) {
                var skuKey = skuKeys[i];//一条SKU信息key
                var sku = skuMap[skuKey];    //一条SKU信息value
                var skuKeyAttrs = skuKey.split(","); //SKU信息key属性值数组
                var len = skuKeyAttrs.length;

                combineSKU(skuKeyAttrs, skuKeyAttrs, len, sku, 1);
            }
        }

        function combineSKU(sourceArr, keysArr, len, sku, cicleIndex) {
            var arr = copyArr(sourceArr);
            if (arr.length < 1) return;
            if (cicleIndex > 6) return;
            var tempArr = [];

            for (var i = 0; i < arr.length; i++) {
                var subArr = arr[i].split(",");
                var index = indexArr(keysArr, subArr[subArr.length - 1]);

                if (cicleIndex == 1) add2SKUResult(arr[i], sku);

                for (var j = index + 1; j < len; j++) {
                    var newEle = arr[i] + "," + keysArr[j];
                    tempArr.push(newEle);

                    add2SKUResult(newEle, sku);
                }
            }
            cicleIndex++;
            combineSKU(tempArr, keysArr, len, sku, cicleIndex);
        }

        function copyArr(arr) {
            var temp = [];
            temp = arr.join("-").split("-");
            return temp;
        }

        function indexArr(arr, str) {
            for (var i = 0; i < arr.length; i++) {
                if (arr[i] == str) return i;
            }
        }

        initSKU();
        //返回数据
        return SKUResult;
    }

    //判断是否有 skuMap
    if( typeof skuMap!= 'undefined'){
        skuView({
            priceEle: '#start-price',
            marketPrice:'#market-price',
            amountEle: '#amount',
            //integralEle: '#integral',
            //descEle: '#skuDesc',//2014-07-29新增描述
            amountInputEle: '#buy-number',
            addToCartBtn: '#addToCart',
            addToOrderBtn: '#addToOrder',
            hasSkuMap: {
                skuMapEle: '#choose-sku',
                model: skuModel(skuMap),
                skuMapData: skuMap
            }
        });
    }

    function skuView(options){
        var priceEle = $(options.priceEle);
        var skuTextEle = (!options.skuTextEle ? null : $(options.skuTextEle));
        var amountEle = $(options.amountEle);
        //var integralEle = $(options.integralEle);
        //var descEle = $(options.descEle); //2014-07-29新增描述
        var amountInputEle = $(options.amountInputEle);
        var addToCartBtn = $(options.addToCartBtn);
        var addToOrderBtn = $(options.addToOrderBtn);
        //var addToCartUrl = EJS.AddSkuToCart;
        var _isSKU = !!(options.hasSkuMap);

        var _selectedIds = [];
        var _stock = 0;

        //防止数据没有提前获取
        if(_isSKU){
            var _limit =1;
        }else{
            var _limit = defaultSku['limit'];
        }

        var _price = 0;
        var _marketing = false;
        var _marketingPrice = 0;
        var _marketPrice = 0;
        var _num = 1;
        var _skuImgList = null;


        //有多个sku的情况
        if (!!options.hasSkuMap) {
            var isDirectBuy = options.isDirectBuy;
            var buyCallback = options.buyCallback;
            var SKUResult = options.hasSkuMap.model;
            var skuMap = options.hasSkuMap.skuMapData;
            var skuMapEle = $(options.hasSkuMap.skuMapEle);
            var _skuAttrNum = $(" dl", skuMapEle).length; //属性个数
            var _skuEles = $('.sku', skuMapEle);//获取总的属性
            attachSkuEvent();
            //_skuImgList = createSKUImage();
        }

        //当有sku组合时，绑定事件
        function attachSkuEvent() {

            //skumap初始化及事件绑定
            _skuEles.each(function () {
                var self = $(this);
                var attr_id = self.attr('data');
                if (self.siblings().length == 0) {
                    self.addClass("selected");
                    veritySkuBtn(self);
                }

                if (!SKUResult[attr_id] || SKUResult[attr_id]['stock'] == 0) {
                    self.addClass("disabled");

                }
            }).click(function () {
                if ($(this).hasClass("disabled") || $(this).hasClass("selected")) return false;
                var self = $(this);
                self.toggleClass('selected').siblings().removeClass('selected');
                veritySkuBtn(self);
                return false;
            });

            //判断其它按钮否则可选
            function veritySkuBtn(self) {
                self = self || null;
                var selectedObjs = $('.selected', skuMapEle);

                if (selectedObjs.length) {
                    //获得组合key价格
                    _selectedIds = [];
                    selectedObjs.each(function () {
                        _selectedIds.push($(this).attr('data'));
                    });
                    _selectedIds.sort(function (value1, value2) {
                        return parseInt(value1) - parseInt(value2);
                    });
                    var len = _selectedIds.length;
                    var prices = SKUResult[_selectedIds.join(',')].price;
                    var maxPrice = Math.max.apply(Math, prices);
                    var minPrice = Math.min.apply(Math, prices);
                    var currentSku = skuMap[_selectedIds.join(',')];

                    if (len == _skuAttrNum) {
                        _price = currentSku.marketPrice;
                        if ($("#start-price").length) {
                            $("#start-price").html(currentSku.price);
                        }

                        if (currentSku.marketPrice) {
                            if ($("#market-price").length) {
                                $("#market-price").html(currentSku.marketPrice);
                            }
                        }
                    }

                    //用已选中的节点验证待测试节点 underTestObjs

                    _skuEles.not(selectedObjs).not(self).each(function () {
                        var siblingsSelectedObj = $(this).siblings('.selected');
                        var testAttrIds = [];//从选中节点中去掉选中的兄弟节点
                        if (siblingsSelectedObj.length) {
                            var siblingsSelectedObjId = siblingsSelectedObj.attr('data');
                            for (var i = 0; i < len; i++) {
                                (_selectedIds[i] != siblingsSelectedObjId) && testAttrIds.push(_selectedIds[i]);
                            }
                        } else {
                            testAttrIds = _selectedIds.concat();

                        }
                        testAttrIds = testAttrIds.concat($(this).attr('data'));
                        testAttrIds.sort(function (value1, value2) {
                            return parseInt(value1) - parseInt(value2);
                        });
                        if (!SKUResult[testAttrIds.join(',')] || SKUResult[testAttrIds.join(',')]['stock'] == 0) {
                            $(this).removeClass('selected').addClass("disabled");
                        } else {
                            $(this).removeClass("disabled");
                        }
                    });

                } else {

                    _selectedIds = [];
                    _skuEles.each(function () {
                        SKUResult[$(this).attr('data')] ? $(this).removeClass('disabled') : $(this).removeClass('selected');
                    });
                }
            }

            function initSelect() {
                if (selectSku.stockQuantity > 0) {
                    _selectedIds = selectSku.skuPropertiesInDb;
                    readSkuDom(selectSku);
                } else {
                    for (var sku in skuMap) {
                        if (skuMap[sku].skuPropertiesInDb > 0) {
                            _selectedIds = skuMap[sku].pvList;
                            (skuMap[sku]);
                            break;
                        }
                    }
                }

                function readSkuDom(sku) {
                    priceEle.html(sku.price);
                    sku.skuPropertiesInDb = sku.skuPropertiesInDb.split(',');
                    for (var i = 0; i < _skuAttrNum; i++) {
                        skuMapEle.find(".sku[data=" + sku.skuPropertiesInDb[i] + "]").addClass("selected");
                    }
                    veritySkuBtn();
                }

            }

            if (!(typeof defaultSku == 'undefined') && !!skuMap) {
                var selectSku = skuMap[defaultSku];
                initSelect();
            }

        }

        //立即购买
        addToOrderBtn.click(function(){
            var skuId = skuMap[_selectedIds.join(',')]["skuId"],number = amountInputEle.val();

            $.ajax({
                type: "get",
                url: '/cart/verifyPromptlyPayData?skuId='+skuId+"&number="+number,
                async: false,
                dataType: 'json',
                data:{skuId:skuId,number:number},
                cache: false,
                success: function (data) {
                   if(data.result){
                       window.location.href = '/cart/promptlyPayChooseAddress?skuId='+skuId+"&number="+number;
                   }
                }
            });


        });

        //添加购物车
        addToCartBtn.click(function(){
            var skuId = skuMap[_selectedIds.join(',')]["skuId"],number = amountInputEle.val();
            $.ajax({
                type: "get",
                url: ' /cart/addSkuToCartAddNum?skuId='+skuId+"&number="+number,
                async: false,
                dataType: 'json',
                data:{skuId:skuId,number:number},
                cache: false,
                success: function (data) {
                    if(data.result){
                        $('#cart-quantity').text(data.data.itemTotalNum);
                    }else{
                        $.dialog({
                            title:'提示',
                            lock:true,
                            content:'<div class="warning-inner clearfix"><p class="warning"><span class="warning-ico"></span>超过最大能购买商品数量</p></div>',
                            width:500,
                            height:200
                        });
                    }

                }
            });
        });


        //数量组件绑定事件
        amountEle.click(function (event) {
            if (_isSKU) {
                if (!verifySku()) return false;
                _limit = skuMap[_selectedIds.join(',')]["tradeMaxNumber"];
                _stock = skuMap[_selectedIds.join(',')]["stock"];
            } else {
                _limit = defaultSku["tradeMaxNumber"];
                _stock = defaultSku["stock"];
            }

            var _targetParentClassName = event.target.parentNode.className;


            if (_targetParentClassName == "btn-add") {
                _limit = _limit > _stock ? _stock : _limit;
                _num = parseInt(amountInputEle.val()) + 1;
                if (_num > _limit) {
                    _num = _limit;
                    FG.tip(amountInputEle, "limit_tip", "超出此商品能购买的最大数量", 30);
                }
                amountInputEle.val(_num);

            } else if (_targetParentClassName == "btn-sub") {

                _limit = _limit > _stock ? _stock : _limit;
                _num = parseInt(amountInputEle.val()) - 1;
                _num = _num < 1 ? 1 : _num;
                amountInputEle.val(_num);

            }

        });


        amountInputEle.blur(function () {
            var reg = /^[0-9]+$/;
            if (!reg.test(amountInputEle.val())) {
                amountInputEle.val(1);
            }
            if (amountInputEle.val() > _limit) {
                amountInputEle.val(_limit);
                FG.tip(amountInputEle, "limit_tip", "超出此商品能购买的最大数量", 30);
            }
            if (amountInputEle.val() < 1 && _limit > 0) {
                amountInputEle.val(1);
                FG.tip(amountInputEle, "limit_tip", "商品购买数量不能小于1", 30);
            }
            countPrice(+amountInputEle.val());
        });


        //验证商品属性选择数量是否正确
        function verifySku() {
            var selectAttrNum = _selectedIds.length || 0,
                information = selectAttrNum === 0 ? "未选择商品属性" : "您还有" + (_skuAttrNum - selectAttrNum) + "个商品属性未选择";

            if (selectAttrNum < _skuAttrNum) {

                skuMapEle.addClass('no_finished');
                if (!skuMapEle.find('a.close_btn').length) {
                    var closeBtn = $('<a class="close_btn" href="javascript:;">关闭</a>').appendTo(skuMapEle);
                    closeBtn.bind('click', function () {
                        skuMapEle.removeClass('no_finished');
                    })
                }
                return false;
            }
            return true;
        }




    }






});