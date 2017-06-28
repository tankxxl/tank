$(document).ready(function () {
    console.log("ready");
    $.ajax({
        // http://10.10.2.213:8081/pms/api/user/.xml
        url: "http://10.10.2.213:8081/pms/api/greeting/greeting?name=rgz"
    }).then(function (data) {
        console.log(data);
        $('.greeting-id').append(data.id);
        $('.greeting-content').append(data.content);
    });
});