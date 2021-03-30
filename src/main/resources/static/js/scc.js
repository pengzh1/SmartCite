function showFilename(file){
    $("#filename_label").html(file.name);
}
$("#upload").click(function () {
    var fileInput = $("#fileInput").val();
    if (fileInput === "") {
        alert("请选择需要上传的文件！")
    } else {
        var formData = new FormData();
        var file = $("#fileInput")[0].files[0];
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
                    alert("成功")
                    console.log(res)
                }
            }
        })
    }
})