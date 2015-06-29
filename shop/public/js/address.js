/**
 * Created by lein on 2015/5/14.
 */

$(function(){


    $(".address-list").delegate("li", "hover", function(){
        $(this).toggleClass("highLight");
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
            zipCode: form.find("input[name='zipCode']"),
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
            zipCode = formItems.zipCode,
            mobile = formItems.mobile,
            message = formItems.message,
            regexZipCode = /^([0-9]{6})$/,
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
                message.text("请输入收件人的详细地址，不能大于200个字");
                location.after(message.clone());
            } else {
                type=='add'?location.siblings("span").remove():location.siblings('p').remove();
            }
        });

        zipCode.blur(function () {
            if (!zipCode.val() || !regexZipCode.test(zipCode.val())) {
                type=='add'?zipCode.siblings("span").remove():zipCode.siblings('p').remove();
                message.text("请输入六位数字的邮政编码");
                zipCode.after(message.clone());
            } else {
                type=='add'?zipCode.siblings("span").remove():zipCode.siblings('p').remove();

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
                e.cancelBubble = true;
                updateForm(type, formId,closeBtn);
            });
        }

    }

//提交验证
    function validateForm(type,formId){

        var formItems = getFormItems(type,formId),
            name = formItems.name,
            location = formItems.location,
            zipCode = formItems.zipCode,
            mobile = formItems.mobile,
            province = formItems.province,
            city = formItems.city,
            districts = formItems.districts,
            message = formItems.message,
            regexZip = /^([0-9]{6})$/,
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
            message.text("请输入收件人的详细地址，不能大于200个字");
            location.after(message.clone());
            location.focus();
            return false;
        }
        type=='update'?location.siblings('p').remove():location.siblings("span").remove();

        if (!zipCode.val() || !regexZip.test(zipCode.val())) {
            type=='add'? zipCode.siblings("span").remove():zipCode.siblings("span").remove();
            message.text("请输入六位数字的邮政编码");
            zipCode.after(message.clone());
            zipCode.focus();
            return false;
        }
        type=='add'? zipCode.siblings("span").remove():zipCode.siblings("span").remove();

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
            return "<li  data-id='"+data.id+"' class='highLight'><div class='receiver'><strong><span class='user'>"+data.name+"</span><span class='space'></span>收</strong><span class='default' data-id="+data.id+">默认地址</span></div><div class='details-address'>"+
                "<p><span class='provice'>"+data.province+"</span><span class='space'></span><span class='city'>"+data.city+"</span><span class='space'></span><span class='area'>"+data.area+"</span></p><p class='location'  title="+data.location+">"+data.location+"</p>"+
                "<p class='phone'>"+data.mobile+"</p></div><div class='edit-address'><span class='edit btn' data-id="+data.id+">修改</span></div><span class='current-ico'></span></li>";
        }else{
            return "<li  data-id='"+data.id+"'><div class='receiver'><strong><span class='user'>"+data.name+"</span><span class='space'></span>收</strong><span class='default' data-id="+data.id+">设置默认地址</span></div><div class='details-address'>"+
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
                    if($('.address-list-inner li').size()==4){
                        $(".address-list-inner").find(".add").hide();
                    }
                    if(!!$.dialog.get.addForm){
                        $.dialog.get.addForm.hide();
                    }
                    if($(".address-list-inner").find(".address-form").size()==1){
                        $('.address-form').slideUp('normal',function(){
                            $('.address-list-inner ul').slideDown();
                            $(".address-list-inner").find(".add").before(addressDom(data.data,'默认地址'));
                            $(this).remove();
                        });
                    }else{
                        var form = $('.add-form');
                        form.each(function(){
                            $(this).get(0).reset();
                        });
                        $(".address-list-inner").find(".add").before(addressDom(data.data));
                    }

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




    //删除地址
    $('.address-list').delegate(' .delete','click',function(e){
        var addressId = $(this).attr('data-id'),item = $(this).parents('li');
        $.dialog({
            title:'提示',
            content:'<div class="warning-inner clearfix"><p class="warning"><span class="warning-ico"></span>确定要删除这个收货地址？</p></div>',
            width:540,
            height:248,
            lock:true,
            drag:false,
            btn: {
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
                                    item.remove();
                                    if($('.address-list li').size() == 4){
                                        $(".address-list-inner").find(".add").show();
                                    }
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

//默认地址
    $('.address-list').delegate('.default','click',function(e){

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

    //list页面添加
    $('.address-list').delegate(' .add','click',function(){

        var html = createFormHtml('add');

        $.dialog({
            title:'添加收货地址',
            id:'addForm',
            content:html,
            lock:true,
            width:650,
            height: 450
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
                    '           <label><b>*</b>邮政编码：</label>' +
                    '           <input type="text" name="zipCode" class="text" value="' + getData.zipCode + '">' +
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
                '           <div class="select_border"><div class="container"><select name="province" class="select"></select></div></div>' +
                '           <div class="select_border"><div class="container"><select name="city" class="select"></select></div></div>' +
                '           <div class="select_border"><div class="container"><select name="districts" class="select"></select></div></div>' +
                '       </div>' +
                '       <div class="form-item">' +
                '           <label><b>*</b>街道地址：</label>' +
                '           <input type="text" name="location" class="text location"  value="">' +
                '           </li>' +
                '       </div>' +
                '       <div class="form-item">' +
                '           <label><b>*</b>邮政编码：</label>' +
                '           <input type="text" name="zipCode" class="text" value="">' +
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
    $('.address-list').delegate(' .edit','click',function() {
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
                        height: 450
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


    //修改地址
    function updateForm(type,formId,closeBtn){

        var formData = formId.serialize(),item;
        //防止360游览器傻瓜
        formData = formData+'&ts='+Math.random();
        //alert(formData);
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
                   item = $(".address-list li[data-id="+data.data.id+"]");
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

});


