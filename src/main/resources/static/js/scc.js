storage = window.localStorage;
file_name = storage.file_name

function showFilename(file) {
    storage.file_name = file.name;
    $("#filename_label").html(file.name);
}

$("#fileInput").change(function () {
    let file_name = this.files[0].name;
    storage.file_name = file_name;
    $("#filename_label").html(file_name);
})

// 处理单个文档
$("#upload").click(function () {
    var index = layer.load(2);
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
                layer.close(index);
                if (res.code !== 200) {
                    alert(res.msg)
                } else {
                    console.log(res);
                    storage.data = JSON.stringify(res.data);
                    $(location).attr("href", "../result.html");
                }
            },
            complete: function () {
                layer.close(index);
            }
        })
    }
})

// 处理多个文档
$("#upload_zip").click(function () {
    var index = layer.load(2);
    let fileInput = $("#fileInput").val();
    let method = $("input[name='method']:checked").val();
    if (fileInput === "") {
        alert("请选择需要上传的文件！")
    } else {
        let formData = new FormData();
        let file = $("#fileInput")[0].files[0];
        formData.append("file", file);
        formData.append("method", method);
        $.ajax({
            url: "batchExtract",
            type: "post",
            data: formData,
            contentType: false,
            processData: false,
            success: function (res) {
                layer.close(index);
                if (res.code !== 200) {
                    alert(res.msg)
                } else {
                    layer.open({
                        title: "Successfully",
                        content: "Please click the download button to download the identification results!",
                        btn: ["Download"],
                        yes: function (index, layero) {
                            let $downloadForm = $("#downloadForm");
                            $downloadForm.attr("action", "/downloadFile");
                            $("#file_name").val(file_name)
                            $downloadForm.submit();
                        }
                    })
                    console.log(res);
                    storage.data = JSON.stringify(res.data);
                }
            }
        })
    }
})




