/**
 * Created by lein on 2015/5/5.
 */
var FG = window.FG || {};
// 提示信息
FG.tip = function (ele, id, msg, x, y, timer) {
    var _timer = 1200;
    var _x = 0;
    var _y = 0;
    if (typeof x === "number") {
        _x = x;
    }
    if (typeof y === "number") {
        _y = y;
    }
    if (typeof timer === "number") {
        _timer = timer;
    }
    var getId = $("#" + id),
        Left = ele.offset().left + parseInt(_y),
        Top = ele.offset().top + parseInt(_x),
        Css = {
            position: "absolute",
            left: Left,
            top: Top,
            display: "none"
        };

    if (getId.length > 0) {
        getId.hide();
    } else {
        getId = $("<div>" + msg + "</div>")
            .attr("id", id)
            .addClass("com_tip");
        $("body").append(getId);
    }
    getId.css(Css).stop().fadeIn(600);

    window.setTimeout(function () {
        getId.fadeOut(400, function () {
            getId.remove();
        });
    }, _timer);
};
