data = JSON.parse(storage.data);

$(document).ready(function () {
    $("#article_title").text(data.article.title.title)
    let $article = $("#article_body")
    let paragraphs = data.article.paragraphs
    let text = "";
    $.each(paragraphs, function (index, paragraph) {
        text += "<p>"
        $.each(paragraph, function (index, sentence) {
            text += "<sent sent_id=\"" + sentence.id + "\">" + sentence.textHasRefLabel + "</sent>";
        })
        text += "</p>"
    })
    $article.append(text)
})

$(function () {
    let references = data.article.references;
    let refTags = data.refTags;
    $("ref").click(function () {
        $("sent").removeClass("context");
        $("#reference").empty();
        let $ref = $(this);
        // alert("ref_id:" + $ref.attr("ref_id") + " contexts:" + $ref.attr("contexts") + " reference:" + $ref.attr("reference"))
        let refTag = refTags[$ref.attr("ref_id")];
        let contexts = refTag.contextList_ids;
        $.each(contexts, function (index, value) {
            $("sent[sent_id=" + value + "]").addClass("context")
        })
        let reference = references[refTag.reference_id];
        let text = "";
        $.each(reference, function (key, value) {
            if (value != null) {
                text += "<tr>\n" +
                    "    <td>" + key + "</td>\n" +
                    "    <td>" + value + "</td>\n" +
                    "</tr>"
            }
        })
        $("#reference").append(text);

    })
})

//下载处理结果
$("#download_json").click(function () {
    let $downloadForm = $("#downloadForm");
    $downloadForm.attr("action", "/downloadFile");
    $("#file_name").val(file_name)
    $downloadForm.submit();
})