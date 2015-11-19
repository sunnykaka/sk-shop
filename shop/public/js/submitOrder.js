/**
 * Created by lein on 2015/5/22.
 */
$(function(){

    //使用优惠劵
    $('.Coupon-Btn').on('click',function(){
        if($(this).text() == "已使用") return false;
        var curMoney = $(this).parent('td').siblings('.coupon-money').text(), conponVal = $('#couponVal').text(),currentType= $(this).parents('tr').attr('type'),voucherT = 0;

        //先找出同一类型的劵
       $(this).parents('table').find('tr[type="'+currentType+'"]').removeClass('used');
        $(this).parents('table').find('tr[type="'+currentType+'"] .Coupon-Btn').text('使用').removeClass('used-status');
        $(this).parents('tr').addClass('used');
        //代金劵值
       // $('#couponVal').text((Number(curMoney)+Number(conponVal)).toFixed(2));
        $(this).parents('table').find('.used').each(function(i,item){
           voucherT +=  Number($(item).find('.coupon-money').text());
        });
        $('#couponVal').text(voucherT.toFixed(2));
        //总价格
        var getTotalM = (Number($('#actualVal').text())- Number(voucherT)).toFixed(2);

        if(getTotalM>0){
            $('#actualVal').text(getTotalM);
        }else{
            $('#actualVal').text(0);
        }

       $(this).text('已使用').addClass('used-status');
    });

    //地址鼠标移动 事件委托
    $('.select-address').delegate('li','hover',function(evt){
       $(this).toggleClass('highLight');
    });

    //选择地址
    $('.select-address').delegate(' li:not(.add)','click',function(){
        $(this).siblings('.current').find('.current-ico').hide();
        $(this).find('.current-ico').fadeIn();
        $(this).addClass('current').siblings().removeClass('current');
    });

    if( $('.add-form').length>0){
        $('.add-form').find('.area').selectArea();
        // 表单item事件(blur,submit)
        formItemEvent('add',$('.add-form'));
    }

// 获取表单项值
    function getFormItems(type,formId) {
        "use strict";
        var form = $(formId),message;
        if(type == 'update'){
            message = $('<p class="errormsg"></p>');
        }else{
            message = $('<span class="errormsg"></span>');
        }
        return {
            element: form,
            name: form.find("input[name='name']"),
            location: form.find("input[name='location']"),
            mobile: form.find("input[name='mobile']"),
            province: form.find('select[name=province]'),
            city: form.find('select[name=city]'),
            districts: form.find('select[name=districts]'),
            message:message
        };
    }

// 表单item事件(blur,submit)
    function formItemEvent(type,formId,closeBtn){
        var formItems= getFormItems(type,formId),
            form = formItems.element,
            name = formItems.name,
            location = formItems.location,
            mobile = formItems.mobile,
            message = formItems.message,
            regexMobile = /(^0?(1[358][0-9]{9})$)|(^(\d{3,4}-)?\d{7,8}$)/;


        name.blur(function () {
            if (!name.val() || name.val().length < 2||name.val().length >20) {
                type=='add'?name.siblings("span").remove():name.siblings('p').remove();
                message.text("请输入收件人的姓名,不能少于2个字或者大于20个字");
                name.after(message.clone());
            } else {
                type=='add'?name.siblings("span").remove():name.siblings('p').remove();
            }
        });

        location.blur(function () {
            if (!location.val()||location.val().length >200) {
                type=='add'?location.siblings("span").remove():location.siblings('p').remove();
                message.text("请输入收件人的详细地址");
                location.after(message.clone());
            } else {
                type=='add'?location.siblings("span").remove():location.siblings('p').remove();
            }
        });
        mobile.blur(function () {
            if (!mobile.val() || !regexMobile.test(mobile.val())) {
                type=='add'?mobile.siblings("span").remove():mobile.siblings('p').remove();
                message.text("请输入正确的电话/手机号码");
                mobile.after(message.clone());
            } else {
                type=='add'?mobile.siblings("span").remove():mobile.siblings('p').remove();

            }
        });
        if(type == 'add'){
            form.on('click', 'input[type=submit]', function (e) {
                e.preventDefault();
                saveForm(type, formId)
            });
        }else if(type == 'update' && form.selector == '.add-form'){
            form.on('click', 'input[type=submit]', function (e) {
                e.preventDefault();
                saveForm(type, formId)
            });
        }else{
            form.on('click', 'input[type=submit]', function (e) {
                e.preventDefault();
                updateForm(type, formId,closeBtn);
            });
        }

    }

//提交验证
    function validateForm(type,formId){

        var formItems = getFormItems(type,formId),
            name = formItems.name,
            location = formItems.location,
            //zipCode = formItems.zipCode,
            mobile = formItems.mobile,
            province = formItems.province,
            city = formItems.city,
            districts = formItems.districts,
            message = formItems.message,
            //regexZip = /^([0-9]{3,6})$/,
            regexMobile = /(^0?(1[358][0-9]{9})$)|(^(\d{3,4}-)?\d{7,8}$)/;


        if (!name.val() || name.val().length < 2) {
            type=='add'?name.siblings("span").remove():name.siblings('p').remove();
            message.text("请输入收件人的姓名,不能少于2个字或者大于20个字");
            name.after(message.clone());
            name.focus();
            return false;
        }
        type=='add'?name.siblings("span").remove():name.siblings('p').remove();

        if (province.val() === '0') {
            type=='add'?districts.siblings("span").remove():districts.siblings('p').remove();
            message.text("请选择省");
            districts.after(message.clone());
            return false;
        }
        if (city.val() === '0') {
            type=='add'?districts.siblings("span").remove():districts.siblings('p').remove();
            message.text("请选择市");
            districts.after(message.clone());
            return false;
        }

        if (districts.find("option").length > 1) {
            if (districts.val() === '0') {
                type=='add'?districts.siblings("span").remove():districts.siblings('p').remove();
                message.text("请选择区县");
                districts.after(message.clone());
                return false;
            }
        } else {
            districts.find("option").eq(0).attr("value", "");
        }
        type=='add'?districts.siblings("span").remove():districts.siblings('p').remove();

        if (!location.val()) {
            type='add'? location.siblings("span").remove():location.siblings("span").remove();
            message.text("请输入收件人的详细地址");
            location.after(message.clone());
            location.focus();
            return false;
        }
        type=='update'?location.siblings('p').remove():location.siblings("span").remove();


        if (!mobile.val() || !regexMobile.test(mobile.val())) {
            type=='add'?mobile.siblings("span").remove():mobile.siblings("p").remove();
            message.text("请输入正确的电话/手机号码");
            mobile.after(message.clone());
            mobile.focus();
            return false;
        }
        type=='add'?mobile.siblings("span").remove():mobile.siblings("p").remove();

        return true;
    }

    //生成地址dom
    function  addressDom (data,value){
        if(value){
            return "<li  data-id='"+data.id+"' class='current highLight'><div class='receiver'><strong class='receiver-name'><span class='user'>"+data.name+"</span><span class='space'></span>收</strong><span class='default' data-id="+data.id+">默认地址</span></div><div class='details-address'>"+
                "<p><span class='provice'>"+data.province+"</span><span class='space'></span><span class='city'>"+data.city+"</span><span class='space'></span><span class='area'>"+data.area+"</span></p><p class='location'  title="+data.location+">"+data.location+"</p>"+
                "<p class='phone'>"+data.mobile+"</p></div><div class='edit-address'><span class='edit btn' data-id="+data.id+">修改</span></div><span class='current-ico'></span></li>";
        }else{
            return "<li  data-id='"+data.id+"'><div class='receiver'><strong class='receiver-name'><span class='user'>"+data.name+"</span><span class='space'></span>收</strong><span class='default' data-id="+data.id+">设置默认地址</span></div><div class='details-address'>"+
                "<p><span class='provice'>"+data.province+"</span><span class='space'></span><span class='city'>"+data.city+"</span><span class='space'></span><span class='area'>"+data.area+"</span></p><p class='location'  title="+data.location+">"+data.location+"</p>"+
                "<p class='phone'>"+data.mobile+"</p></div><div class='edit-address'><span class='edit btn' data-id="+data.id+">修改</span><span class='delete btn' data-id="+data.id+">删除</span></div><span class='current-ico' style='display: none'></span></li>";
        }
    }

    //添加地址
    function saveForm(type,formId){
        var formData = formId.serialize();

        //验证表单
        if (!validateForm(type,formId)) {
            return;
        }
        $.ajax({
            type: "POST",
            async: false,
            url: '/my/address/add',
            data: formData,
            dataType: 'json',
            success: function (data) {
                if (data.result){ //成功
                    if($('.select-address li').size()==4){
                        $(".select-address").find(".add").hide();
                    }
                    if(!!$.dialog.get.addForm){
                        $.dialog.get.addForm.hide();
                    }
                    if($(".select-address").find(".address-form").size()==1){
                        $('.address-form').slideUp('normal',function(){
                            $('.select-address ul').slideDown();
                            $('.select-address ul').before("<h3>选择收货地址</h3>");
                            $(".select-address").find(".add").before(addressDom(data.data,'默认地址'));
                            $(this).remove();
                        });
                    }else{
                        var form = $('.add-form');
                        form.each(function(){
                            $(this).get(0).reset();
                        });
                        $(".select-address").find(".add").before(addressDom(data.data));
                    }

                } else {
                    var errMsg = data.message;

                    $(formId).find('#addMsg').text(errMsg);
                }
            }
        });

    }

    //删除地址
    $('.select-address').delegate(' .delete','click',function(e){
        var addressId = $(this).attr('data-id'),item = $(this).parents('li'),that = $(this);
        $.dialog({
            title:'提示',
            content:'<div class="warning-inner clearfix"><p class="warning"><i class="icon iconfont">&#xe60c;</i>确定要删除这个收货地址？</p></div>',
            width:540,
            height:248,
            lock:true,
            drag:false,
            btn: {
                cancle: {
                    val: '取消'
                },
                ok: {
                    val: '确定',
                    type: 'red',
                    click: function(btn) {
                        $.ajax({
                            type: 'POST',
                            url:'/my/address/del?addressId='+addressId,
                            dataType: 'json',
                            success: function (data) {
                                if (data.result) {
                                    if(that.parents('li').hasClass('current')){
                                        that.parents('li').siblings("li:not(:contains('设置默认地址')):not('.add')").addClass('current').find(".current-ico").show();
                                    }
                                    item.remove();
                                    if($('.select-address li').size() == 4){
                                        $(".select-address").find(".add").show();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

    });

    //list页面添加
    $('.select-address').delegate(' .add','click',function(){

        var html = createFormHtml('add');

        $.dialog({
            title:'添加收货地址',
            id:'addForm',
            content:html,
            lock:true,
            width:650,
            height: 400
        });

        var form = $('.add-form');
        //打开时重置表单
        form.get(0).reset();
        form.find('.errormsg').remove();
        //绑定 blur focus事件
        formItemEvent('update', form);

        form.find('.area').selectArea();

    });

    function createFormHtml(type,getData){

        if(type == 'update'){
            var formId = 'modify_address_' + getData.id,
                html =
                    '<form class="update-form" id="' + formId + '">' +
                    '       <div class="form-item">' +
                    '           <label><b>*</b>收 货 人：</label>' +
                    '           <input type="text" name="name" class="text" value="' + getData.name + '">' +
                    '       </div>' +
                    '       <div class="area form-item">' +
                    '           <label><b>*</b>选择地区：</label>' +
                    '           <div class="select_border"><div class="container"><select name="province" class="select"></select></div></div>' +
                    '           <div class="select_border"><div class="container"><select name="city" class="select"></select></div></div>' +
                    '           <div class="select_border"><div class="container"><select name="districts" class="select"></select></div></div>' +
                    '       </div>' +
                    '       <div class="form-item">' +
                    '           <label><b>*</b>街道地址：</label>' +
                    '           <input type="text" name="location" class="text location"  value="' + getData.location + '">' +
                    '           </li>' +
                    '       </div>' +
                    '       <div class="form-item">' +
                    '           <label><b>*</b>联系电话：</label>' +
                    '           <input type="text" name="mobile" class="text" value="' + getData.mobile + '">' +
                    '       </div>' +
                    '       <div class="form-item submit">' +
                    '           <input type="submit" class="update-btn" value="修改地址">' +
                    '           <p class="errormsg" id="addMsg"></p>' +
                    '       </div>' +
                    '   <input type="hidden" name="id" value="' + getData.id + '" />' +
                    '</form">';
        }else{
            html =
                '<form class="add-form" id="">' +
                '       <div class="form-item">' +
                '           <label><b>*</b>收 货 人：</label>' +
                '           <input type="text" name="name" class="text" value="">' +
                '       </div>' +
                '       <div class="area form-item">' +
                '           <label><b>*</b>选择地区：</label>' +
                '          <div class="select_border"><div class="container"><select name="province" class="select"></select></div></div>' +
                '            <div class="select_border"><div class="container"><select name="city" class="select"></select></div></div>' +
                '           <div class="select_border"><div class="container"><select name="districts" class="select"></select></div></div>' +
                '       </div>' +
                '       <div class="form-item">' +
                '           <label><b>*</b>街道地址：</label>' +
                '           <input type="text" name="location" class="text location"  value="">' +
                '           </li>' +
                '       </div>' +
                '       <div class="form-item">' +
                '           <label><b>*</b>联系电话：</label>' +
                '           <input type="text" name="mobile" class="text" value="">' +
                '       </div>' +
                '       <div class="form-item submit">' +
                '           <input type="submit" class="add-address-btn" value="添加地址">' +
                '           <p class="errormsg" id="addMsg"></p>' +
                '       </div>' +
                '</form">';
        }

        return html;
    }

    //获取要修改的地址
    $('.select-address').delegate(' .edit','click',function() {
        var addressId = $(this).attr('data-id');
        $.ajax({
            url: '/my/address/query',
            type: 'GET',
            data: {
                addressId: addressId
            },
            dataType: 'json',
            success: function (data) {
                if (data.result) {

                    var getData = data.data,html = createFormHtml('update',getData);
                    var obj =  $.dialog({
                        title:'修改收货地址',
                        id:'#modify_address_' + getData.id,
                        content:html,
                        lock:true,
                        width:650,
                        height: 400
                    });

                    //设置省市区
                    var form = $('#modify_address_' + getData.id);
                    form.find('.area').selectArea({
                        province: getData.province || null,
                        city: getData.city || null,
                        districts: getData.area || null
                    });
                    //绑定 blur focus事件
                    formItemEvent('update', form ,obj.$close);

                }
            }
        });
    });

    //默认地址
    $('.select-address').delegate('.default','click',function(e){

        var that = $(this),addressId = $(this).attr('data-id'),item = $(this).parents('li');
        var defaultId = item.siblings('.current').attr('data-id');


        if(item.hasClass('current')){
            return;
        }else{
            $.ajax({
                type: 'POST',
                url:'/my/address/defaultAddress?addressId='+addressId,
                dataType: 'json',
                success: function (data) {
                    if (data.result) {
                        // location.href= '/my/address';
                        item.siblings('.current').find('.edit-address').append("<span class='delete btn' data-id="+defaultId+">删除</span>");
                        item.siblings('.current').find('.current-ico').hide();
                        item.find('.current-ico').show();
                        item.addClass('current').siblings('li').find('.default').text('设置默认地址');
                        item.find('.delete').remove();
                        item.addClass('current').siblings('li').removeClass('current');
                        that.text('默认地址');

                    }
                }
            });
        }
    });

    //修改地址
    function updateForm(type,formId,closeBtn){

        var formData = formId.serialize(),item;
        //验证表单
        if (!validateForm(type,formId)) {
            return;
        }

        $.ajax({
            type: "POST",
            async: false,
            url: '/my/address/update',
            data: formData,
            dataType: 'json',
            success: function (data) {
                if (data.result){ //成功
                    closeBtn.trigger('click');
                    //防止多次提交
                    $(formId).off('click');
                    //更新数据
                    item = $(".select-address li[data-id="+data.data.id+"]");
                    item.find('.phone').text(data.data.mobile);
                    item.find('.user').text(data.data.name);
                    item.find('.location').text(data.data.location);
                    item.find('.location').attr('title',data.data.location);
                    item.find('.provice').text(data.data.province);
                    item.find('.city').text(data.data.city);
                    item.find('.area').text(data.data.area);
                } else {
                    var valObj = data.message,errMsg;
                    $.each(valObj,function(key,value) {
                        errMsg = value[0];
                    });

                    $(formId).find('#addMsg').text(errMsg);
                }
            }
        });
    }

    //提交订单
    $('#order-submit-btn').click(function(){

        var addressId = $('.select-address li.current').attr('data-id');
        if(!addressId){
            $.dialog({
                title:'提示',
                lock:true,
                content:'<div class="warning-inner clearfix"><p class="warning"><span class="warning-ico"></span>请选择收货地址！</p></div>',
                width:540,
                height:248,
                btn: {
                    ok : {
                        val : '关闭',
                        type : 'red'
                    }
                }
            });
        }

        var selItems = $('#selItems').val(),isPromptlyPay = $('#isPromptlyPay').val();
        //获取代金劵编号
        var couponList = $('tr.used').find('.coupon-uniqueNo'),couponStr = '';
        if(couponList.size()>0){
            couponList.each(function(i,item){
                couponStr += "&vouchers="+$(item).text();
            });
        }
        $.ajax({
            type: "get",
            url: '/order/submitOrder?selItems='+selItems+"&addressId="+addressId+"&isPromptlyPay="+isPromptlyPay+couponStr,
            async: false,
            dataType: 'json',
            cache: false,
            success: function (data) {
                if(data.result){
                    if(/\//.test(data.data)){
                        window.location.href =  data.data;
                    }else{
                        window.location.href = '/order/toOrderPlay?orderIds='+data.data;
                    }
                }else{
                    $.dialog({
                        title:'提示',
                        lock:true,
                        content:'<div class="warning-inner clearfix"><p class="warning"><i class="icon iconfont">&#xe60c;</i>'+data.message+'</p></div>',
                        width:540,
                        height:248,
                        btn: {
                            ok : {
                                val : '关闭',
                                type : 'red'
                            }
                        }
                    });
                }
            }
        });
    });
});