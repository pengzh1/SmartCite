let storage = window.localStorage;

function showFilename(file) {
    $("#filename_label").html(file.name);
}

$("#upload").click(function () {
    let fileInput = $("#fileInput").val();
    if (fileInput === "") {
        alert("请选择需要上传的文件！")
    } else {
        let formData = new FormData();
        let file = $("#fileInput")[0].files[0];
        formData.append("file", file);
        formData.append("method", "svm")
        $.ajax({
            url: "extract",
            type: "post",
            data: formData,
            contentType: false,
            processData: false,
            success: function (res) {
                if (res.code !== 200) {
                    alert(res.msg)
                } else {
                    console.log(res);
                    storage.data = JSON.stringify(res.data);
                    $(location).attr("href", "../result.html");
                }
            }
        })
    }
})

