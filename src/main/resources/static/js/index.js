$(function () {
    getNicknameAndGender();
});

// 获取用户昵称和性别
function getNicknameAndGender() {
    $.get(
        commonObj.basePath + "/login/getNicknameAndGender",
        function (result) {
            $("#user-nickname").html(result.nickname);
            $("#user-gender").html(result.gender);
        }
    );
}

// 用户注销
function doLogout() {
    $.get(
        commonObj.basePath + "/login/doLogout",
        function (result) {
            if (result.resCode === 200) {
                location.href = "login";
            } else {
                alert(result.resMsg);
            }
        }
    );
}

// 去聊天室
function toChat() {
    location.href = "chat";
    /**
     * 不要简单的跳转, 走modelAndView初始化一些数据, 同步消息显示(如欢迎语句)
     */
}

// 神奇小冰
function toMagicIce() {
    location.href = "magic-ice";
}