$(function () {
    initUser();
    enterSend();
});

// 获取用户昵称和性别
function initUser() {
    $.ajaxSettings.async = false;
    $.get(
        commonObj.basePath + "/login/getNicknameAndGender",
        function (result) {
            $("#chat-nickname").html(result.nickname);
            $("#chat-gender").html(result.gender);
        }
    );
    $.ajaxSettings.async = true;
    checkSessionAndConnect();
}

// 退出聊天室
function outChat() {
    closeWebSocket();
    /*location.href = "index";*/
    window.history.go(-1);
}

// 回车发送消息
function enterSend() {
    $("#sendText").bind('keyup', function (event) {
        if (event.keyCode === 13) {// 回车键的键值为13
            send();
        }
    });
}

// 校验session并连接webSocket
function checkSessionAndConnect() {
    let nickname = $("#chat-nickname").html();
    let gender = $("#chat-gender").html();
    if (nickname != null && "" != nickname.trim() && gender != null && "" != gender.trim()) {
        connectWebSocket(nickname);
    } else {
        alert("当前网络繁忙, 请稍后再试!");
        $.ajaxSettings.async = false;
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
        $.ajaxSettings.async = true;
    }
}

// 获取在线用户
function getOnlineUsers() {
    $.get(
        commonObj.basePath + "/login/getOnlineUsers",
        function (result) {
            let chatArea = document.getElementById("online_users");
            for (let i = 0, len = result.length; i < len; i++) {
                let nickname = result[i];
                console.log(nickname);
                chatArea.innerHTML += nickname+ '<br />';
                chatArea.scrollTop = chatArea.scrollHeight;
            }
        }
    );
}

// 群发欢迎语句
function welcomeToAll() {
    $.ajaxSettings.async = false;
    $.get(commonObj.basePath + "/login/welcomeToAll");
    $.ajaxSettings.async = true;
}
// 群发欢送语句
function goodbyeToAll() {
    $.ajaxSettings.async = false;
    $.get(commonObj.basePath + "/login/goodbyeToAll");
    $.ajaxSettings.async = true;
}