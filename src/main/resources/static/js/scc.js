let storage = window.localStorage;

function showFilename(file) {
    $("#filename_label").html(file.name);
}

// 处理单个文档
$("#upload").click(function () {
    let fileInput = $("#fileInput").val();
    let method = $("input[name='method']:checked").val();
    if (fileInput === "") {
        alert("请选择需要上传的文件！")
    } else {
        let formData = new FormData();
        let file = $("#fileInput")[0].files[0];
        formData.append("file", file);
        formData.append("method", method)
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

// 处理多个文档
$("#upload_zip").click(function () {
    let fileInput = $("#fileInput").val();
    if (fileInput === "") {
        alert("请选择需要上传的文件！")
    } else {
        let formData = new FormData();
        let file = $("#fileInput")[0].files[0];
        formData.append("file", file);
        formData.append("method", "svm")
        $.ajax({
            url: "batchExtract",
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



