/**
 * setInterval(表达式，时间): 页面载入后, 每经过指定毫秒值后执行指定表达式, 是间隔多次执行的
 * setTimeout(表达式，时间): 页面载入后, 经过指定毫秒值后执行指定表达式, 只执行一次
 */
let nickName = "",
    sendText = $("#sendText"),
    chatArea = document.getElementById("messages");
$(function(){
    nickName = initUser();
    magicIceIntroduction();
    enterSend();
    /*noSpeak();*/
});

// 问好
function magicIceIntroduction() {
    /**
     * 生成一个从 m - n 之间的随机整数
     * parseInt(Math.random() * (n - m + 1) + m);
     */
    let num = parseInt(Math.random() * (3 - 1 + 1) + 1),
        helloWord = "";
    if (num === 1) {
        helloWord = "你好鸭~";
    } else if (num === 2) {
        helloWord = "哈喽哈喽~";
    } else if (num === 3) {
        helloWord = "终于有人来了!!! (#`O′)";
    } else {
        helloWord = "嗯???";
    }
    setTimeout(function () {
        chatArea.innerHTML += "小冰: " + helloWord + '<br />';
        chatArea.scrollTop = chatArea.scrollHeight;
    }, 2000);
    let num2 = parseInt(Math.random() * (3 - 1 + 1) + 1),
        helloWord2 = "";
    if (num2 === 1) {
        helloWord2 = "我是小冰~你是" + nickName + "吗? Σ( ° △ °|||)";
    } else if (num2 === 2) {
        helloWord2 = "我认识你喔~你是" + nickName + "吧! ︿(￣︶￣)︿";
    } else if (num2 === 3) {
        helloWord2 = "猜猜我是谁 (*/ω＼*)";
    } else {
        helloWord2 = "哎鸭";
    }
    setTimeout(function () {
        chatArea.innerHTML += "小冰: " + helloWord2 + '<br />';
        chatArea.scrollTop = chatArea.scrollHeight;
    }, 3500);
}

// 获取用户昵称
function initUser() {
    let returnName = "";
    $.ajaxSettings.async = false;
    $.get(
        commonObj.basePath + "/login/getNicknameAndGender",
        function (result) {
            returnName = result.nickname;
        }
    );
    $.ajaxSettings.async = true;
    return returnName;
}

// 不说话监控
function noSpeak() {
    $('#username').bind('input propertychange', function() {
        setInterval(function () {
            if ($('#username').val().length === 0) {
                $('#result').html('说话啊');
            } else {
                $('#result').html($(this).val().length + ' characters');
            }
        }, 3000);
    });
}

// 小冰再见
function outIce() {
    /*location.href = "index";*/
    window.history.go(-1);
}

// 发送消息
function sendIce() {
    let message = document.getElementById('sendText').value;
    if ("" == message || message == null) {
        alert("跟小冰说点啥吧");
        return;
    }
    chatArea.innerHTML += nickName + ": " + message + '<br />';
    chatArea.scrollTop = chatArea.scrollHeight;
    sendText.val("");
    sendText.focus();
    $.post(
        commonObj.basePath + "/ice/loveIce",
        {
            "message": message
        },
        function (result) {
            setTimeout(function () {
                if (result.resCode === 200) {
                    chatArea.innerHTML += "小冰: " + result.resMsg + '<br />';
                    chatArea.scrollTop = chatArea.scrollHeight;
                } else {
                    chatArea.innerHTML += "小冰: " + result.resMsg + '<br />';
                    chatArea.scrollTop = chatArea.scrollHeight;
                }
            }, 2500);
        },
        "json"
    );
}

// 回车发送消息
function enterSend() {
    sendText.bind('keyup', function (event) {
        if (event.keyCode === 13) {// 回车键的键值为13
            sendIce();
        }
    });
}