$(function () {
    welcomeObj.init();
});

var welcomeObj = {
    init: function () {
        this.hideErrorMsg();
    },
    hideErrorMsg: function () {// 加载时隐藏错误信息提示栏
        $("#errorMsg").css('visibility', 'hidden');
    }
};

$("#login").click(function () {
    $("#errorMsg").css('visibility', 'visible');
});