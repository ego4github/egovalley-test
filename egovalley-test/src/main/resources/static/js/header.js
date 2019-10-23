$(function () {
    headerObj.init();
});

let headerObj = {
    init: function () {
        this.initDailyAndTotal();
    },
    initDailyAndTotal: function () {// 页面加载完成时初始化今日访问量与总访问量
        $.ajax({
            url: commonObj.basePath + "/visit/initDailyAndTotal",
            type: "get",
            dataType: "json",
            /*async: false,*/
            success: function (data) {
                // dailyCount
                let daily = data.dailyCount;
                $("#dailyCount").html(daily);
                // totalCount
                let total = data.totalCount;
                $("#totalCount").html(total);
            }
        });
    }
};

$("#header_say").click(function () {
    alert("+1");
});

$("#header_hello").click(function () {
    alert("谢谢关注~");
});