$(function () {
    hideAllErrorMsg();
});

/**
 * 隐藏标签的方法
 * visibility: visible / hidden => 占用布局
 * display: block / none => 不占用布局
 */

// 显示错误信息
function showErrorMsg(showId) {
    /*$(showId).css('visibility', 'visible');*/
    $(showId).css('display', 'block');
}

// 隐藏错误消息
function hideErrorMsg(hideId) {
    $(hideId).css('display', 'none');
}

// 隐藏所有错误信息
function hideAllErrorMsg() {
    /*$(".register-err").css('visibility', 'hidden');*/
    $(".tr-err").css('display', 'none');
}

// 获取验证码
function getCheckCode(flag) {
    // 获取验证码
    let inputPhone = $.trim($("#inputPhone").val()),
        regPhone = /^1[34578]\d{9}$/;
    console.log(inputPhone);
    if (inputPhone.length === 0) {
        alert("请先输入手机号!");
        return;
    } else if (!regPhone.exec(inputPhone)) {
        alert("请输入正确的手机号!");
        return;
    }
    $.post(
        commonObj.basePath + "/register/getCheckCode",
        {
            "inputPhone": inputPhone,
            "flag": flag
        },
        function (result) {
            if (result.resCode !== 200) {
                alert(result.resMsg);
            }
        },
        "json"
    );
    time(document.getElementById("btn-code"));
}

// 60秒内不可再次获取验证码
function time(o) {
    if (wait === 0) {
        o.removeAttribute("disabled");
        o.value = "获取验证码";
        wait = 60;
    } else {
        o.setAttribute("disabled", true);
        o.value = "重新发送(" + wait + ")";
        wait--;
        setTimeout(function () {
            time(o)
        }, 1000)
    }
}

// 执行注册
function doRegister() {
    hideErrorMsg();
    if (validateForm()) {
        $.post(
            commonObj.basePath + "/register/doRegister",
            $("#registerForm").serialize(),//表单所有数据
            function (result) {
                if (result.resCode === 200) {
                    location.href = "register-ok";
                } else if (result.resCode === 300) {
                    $("#err-checkCode").html(result.resMsg);
                    showErrorMsg("#err-checkCode");
                } else {
                    alert(result.resMsg);
                }
            },
            "json"
        );
    }
}

// 校验表单
function validateForm() {
    // 校验账户
    let inputUsername = $.trim($("#inputUsername").val()),
        regUsername = /^[a-zA-Z]\w{5,17}$/;
    if (inputUsername.length === 0) {
        $("#err-username").html("看不见吗");
        showErrorMsg("#tr-err-username");
        return false;
    } else if (!regUsername.exec(inputUsername)) {
        $("#err-username").html("首位英文, 长度6~18");
        showErrorMsg("#tr-err-username");
        return false;
    } else {
        hideErrorMsg("#tr-err-username");
    }
    // 校验密码
    let inputPassword = $.trim($("#inputPassword").val()),
        inputPasswordConf = $.trim($("#inputPasswordConf").val());
    if (inputPassword.length === 0) {
        $("#err-password").html("你看不见吗");
        showErrorMsg("#tr-err-password");
        return false;
    } else if (inputPasswordConf.length === 0) {
        hideErrorMsg("#tr-err-password");
        $("#err-passwordConf").html("???");
        showErrorMsg("#tr-err-passwordConf");
        return false;
    } else if (inputPasswordConf !== inputPassword) {
        hideErrorMsg("#tr-err-password");
        $("#err-passwordConf").html("密码不一致");
        showErrorMsg("#tr-err-passwordConf");
        return false;
    } else {
        hideErrorMsg("#tr-err-passwordConf");
    }
    // 校验昵称
    let inputNickname = $.trim($("#inputNickname").val());
    if (inputNickname.length === 0) {
        $("#err-nickname").html("咋称呼");
        showErrorMsg("#tr-err-nickname");
        return false;
    } else if (getByteLen(inputNickname) > 14) {
        $("#err-nickname").html("最多7个字");
        showErrorMsg("#tr-err-nickname");
        return false;
    } else {
        hideErrorMsg("#tr-err-nickname");
    }
    // 校验性别
    let inputGender = $("input:radio:checked").val();
    if (inputGender === "2") {
        $("#err-gender").html("不能保密哟~");
        showErrorMsg("#tr-err-gender");
        return false;
    } else {
        hideErrorMsg("#tr-err-gender");
    }
    // 校验生日
    let inputBirthday = $.trim($("#inputBirthday").val());
    if (inputBirthday.length === 0) {
        $("#err-birthday").html("填了有惊喜");
        showErrorMsg("#tr-err-birthday");
        return false;
    } else {
        hideErrorMsg("#tr-err-birthday");
    }
    // 校验手机
    let inputPhone = $.trim($("#inputPhone").val()),
        regPhone = /^1[34578]\d{9}$/;
    if (inputPhone.length === 0) {
        $("#err-phone").html("留个电话");
        showErrorMsg("#tr-err-phone");
        return false;
    } else if (!regPhone.exec(inputPhone)) {
        $("#err-phone").html("别乱写");
        showErrorMsg("#tr-err-phone");
        return false;
    } else {
        hideErrorMsg("#tr-err-phone");
    }
    // 校验邮箱
    let inputEmail = $.trim($("#inputEmail").val()),
        regEmail = /^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/;
    if (inputEmail.length === 0) {
        $("#err-email").html("给我吧");
        showErrorMsg("#tr-err-email");
        return false;
    } else if (!regEmail.exec(inputEmail)) {
        $("#err-email").html("格式不对头");
        showErrorMsg("#tr-err-email");
        return false;
    } else {
        hideErrorMsg("#tr-err-email");
    }
    // 校验验证码
    let inputCheckCode = $.trim($("#inputCheckCode").val());
    if (inputCheckCode.length === 0) {
        $("#err-checkCode").html("验证一下");
        showErrorMsg("#tr-err-checkCode");
        return false;
    } else {
        hideErrorMsg("#tr-err-checkCode");
    }
    return true;
}

// 获取字符长度
function getByteLen(val) {
    let len = 0;
    for (let i = 0; i < val.length; i++) {
        let a = val.charAt(i);
        if (a.match(/[^\x00-\xff]/ig) != null) {
            len += 2;
        }
        else {
            len += 1;
        }
    }
    return len;
}