/**
 * Created by lein on 2015/5/14.
 */

$(function(){



    $('.address-list li').hover(function(){
        $(this).addClass('highLight');
    },function(){
        $(this).removeClass('highLight');
    })


    $('.add-form').find('.area').selectArea();


// 表单item事件(blur,submit)
    formItemEvent('add',$('.add-form'));


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
    function formItemEvent(type,formId){
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
                type=='updata'?name.siblings('p').remove():name.siblings("span").remove();
            }
        });

        location.blur(function () {
            if (!location.val()||location.val().length >200) {
                type=='add'?location.siblings("span").remove():location.siblings('p').remove();
                message.text("请输入收件人的详细地址，不能大于200个字");
                location.after(message.clone());
            } else {
                type=='updata'?location.siblings('p').remove():location.siblings("span").remove();
            }
        });

        zipCode.blur(function () {
            if (!zipCode.val() || !regexZipCode.test(zipCode.val())) {
                type=='add'?zipCode.siblings("span").remove():zipCode.siblings('p').remove();
                message.text("请输入六位数字的邮政编码");
                zipCode.after(message.clone());
            } else {
                type=='updata'?zipCode.siblings('p').remove():zipCode.siblings("span").remove();

            }
        });

        mobile.blur(function () {
            if (!mobile.val() || !regexMobile.test(mobile.val())) {
                type=='add'?mobile.siblings("span").remove():mobile.siblings('p').remove();
                message.text("请输入正确的电话/手机号码");
                mobile.after(message.clone());
            } else {
                type=='updata'?mobile.siblings('p').remove():mobile.siblings("span").remove();

            }
        });
        if(type == 'add'){
            form.on('click', 'input[type=submit]', function (e) {
                e.preventDefault();
                saveForm(type, formId)
            });
        }else{
            form.on('click', 'input[type=submit]', function (e) {
                e.preventDefault();
                updateForm(type, formId)
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
            message.text("请输入收件人的姓名,不能少于2个字");
            name.after(message.clone());
            name.focus();
            return false;
        }
       type=='update'?name.siblings('p').remove():name.siblings("span").remove();

        if (province.val() === '0') {
            type=='add'?province.siblings("span").remove():province.siblings('p').remove();
            message.text("请选择省");
            districts.after(message.clone());
            return false;
        }
        if (city.val() === '0') {
            type=='add'? city.siblings("span").remove():city.siblings('p').remove();
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
        type=='update'?districts.siblings('p').remove():districts.siblings("span").remove();

        if (!location.val()) {
            type='add'? location.siblings("span").remove():location.siblings("span").remove();
            message.text("请输入收件人的详细地址");
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
        type=='update'?zipCode.siblings('p').remove():zipCode.siblings("span").remove();

        if (!mobile.val() || !regexMobile.test(mobile.val())) {
            type=='add'?mobile.siblings("span").remove():mobile.siblings("span").remove();
            message.text("请输入正确的电话/手机号码");
            mobile.after(message.clone());
            mobile.focus();
            return false;
        }
        type=='update'?mobile.siblings('p').remove(): mobile.siblings("span").remove();

        return true;
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
                    location.href= '/my/address'
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
    $('.address-list .delete').click(function(e){
        var addressId = $(this).attr('data-id'),item = $(this).parents('li');

        FG.dialogConfirm(e, '您确定要删除这条收货地址吗？', function(){
            $.ajax({
                type: 'POST',
                url:'/my/address/del',
                data: {
                    addressId: addressId
                },
                dataType: 'json',
                success: function (data) {
                    if (data.result) {
                        item.hide(400, function () {
                            item.remove();
                        });
                    }
                }
            });
        });
    });

//删除地址
    $('.address-list .default').click(function(e){
        var addressId = $(this).attr('data-id'),item = $(this).parents('li');
            $.ajax({
                type: 'POST',
                url:'/my/address/defaultAddress',
                data: {
                    addressId: addressId
                },
                dataType: 'json',
                success: function (data) {
                    if (data.result) {
                        item.addClass('current').siblings('li').removeClass('current');
                    }
                }
            });

    });

    //获取要修改的地址
    $('.address-list .edit').click(function() {
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
                    var getData = data.data;
                    var formId = 'modify_address_' + getData.id,
                        html =
                            '<form class="update-form" id="' + formId + '">' +
                            '       <div class="form-item">' +
                            '           <label><b>*</b>收 货 人：</label>' +
                            '           <input type="text" name="name" class="text" value="' + getData.name + '">' +
                            '       </div>' +
                            '       <div class="area form-item">' +
                            '           <label><b>*</b>选择地区：</label>' +
                            '           <select name="province" class="select"></select>' +
                            '           <select name="city" class="select"></select>' +
                            '           <select name="districts" class="select"></select>' +
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
                            '           <input type="submit" class="update-btn" value="确认收货地址">' +
                            '           <p class="errormsg" id="addMsg"></p>' +
                            '       </div>' +
                            '   <input type="hidden" name="id" value="' + getData.id + '" />' +
                            '</form">';

                    new FG.Dialog({
                        title: '提示',
                        outClass: 'dialog-2014',
                        border: '1px solid #ddd;',
                        backgroundColor: '#ffffff',
                        type: null || FG.Dialog.type.ERROR,
                        height: '450px',
                        width: '550px',
                        info: html,
                        infoStyle: {
                            height: '100px'
                        },
                        buttons: null
                    });
                    //设置省市区
                    var form = $('#' + formId);
                    form.find('.area').selectArea({
                        province: getData.province || null,
                        city: getData.city || null,
                        districts: getData.area || null
                    });
                    //绑定 blur focus事件
                    formItemEvent('update', form);

                }
            }
        });
    });
        //var getData = {
        //    "id":1,
        //    "userId":14329,
        //    "name":"123123",
        //    "province":"广东省",
        //    "city":"深圳市",
        //    "area":"南山区",
        //    "location":"12321332df",
        //    "mobile":"18927429864",
        //    "telephone":null,
        //    "email":null,
        //    "zipCode":"515162",
        //    "defaultAddress":true,
        //    "frequency":0,
        //    "deleted":false,
        //    "createDate":"2015-05-05 10:51:25","updateDate":"2015-05-14 16:20:33"};



    //修改地址
    function updateForm(type,formId){

        var formData = formId.serialize();
        console.log(formData);
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
                    location.href= '/my/address'
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


