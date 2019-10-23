$(function () {
    hideErrorMsg();
    enterLogin();
});

// 显示错误信息
function showErrorMsg() {
    // $("#errorMsg").css('visibility', 'visible');
    $("#errorMsg").css('display', 'block');
}

// 隐藏错误信息
function hideErrorMsg() {
    // $("#errorMsg").css('visibility', 'hidden');
    $("#errorMsg").css('display', 'none');
}

// 执行登录
function doLogin() {
    hideErrorMsg();
    if (validateForm()) {
        $.post(
            commonObj.basePath + "/login/doLogin",
            $("#loginForm").serialize(),//表单所有数据
            function (result) {
                if (result.resCode === 200) {
                    location.href = "index";
                } else {
                    $("#errorMsg").html(result.resMsg);
                    showErrorMsg();
                }
            },
            "json"
        );
    }
}

// 校验表单
function validateForm() {
    let inputUsername = $.trim($("#inputUsername").val());
    if (inputUsername.length === 0) {
        $("#errorMsg").html("请输入账户!");
        showErrorMsg();
        return false;
    }
    let inputPassword = $.trim($("#inputPassword").val());
    if (inputPassword.length === 0) {
        $("#errorMsg").html("请输入密码!");
        showErrorMsg();
        return false;
    }
    return true;
}

// 回车登录
function enterLogin() {
    $("#inputUsername").bind('keyup', function (event) {
        if (event.keyCode === 13) {// 回车键的键值为13
            doLogin();
        }
    });
    $("#inputPassword").bind('keyup', function (event) {
        if (event.keyCode === 13) {// 回车键的键值为13
            doLogin();
        }
    });
}

// 跳转注册页
function toRegister() {
    location.href = "register";
}
