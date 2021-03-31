let data = JSON.parse(storage.data);
$(document).ready(function () {
    $("#article_title").text(data.article.title.title)
    let $article = $("#article_body")
    let paragraphs = data.article.paragraphs
    let text = "";
    $.each(paragraphs, function (index, paragraph) {
        text += "<p>"
        $.each(paragraph, function (index, sentence) {
            let refTags = sentence.refTags;
            let sentText = sentence.text;
            if (refTags != null && refTags.length !== 0) {
                $.each(refTags, function (index, refTag) {
                    sentText = sentText.replace(refTag.text, "<ref ref_id='" + refTag.id + "' contexts='" + JSON.stringify(refTag.contextList_ids) + "' reference='" + refTag.reference_id + "' onclick='showContext(this)'>" + refTag.text + "</ref>")
                })
            }
            text += "<sent sent_id=\"" + sentence.id + "\">" + sentText + "</sent>";
        })
        text += "</p>"
    })
    $article.append(text)
})


function showContext(obj) {
    let references = data.article.references;
    $("sent").removeClass("context");
    $("#reference").empty();

    let $ref = $(obj);
    // alert("ref_id:" + $ref.attr("ref_id") + " contexts:" + $ref.attr("contexts") + " reference:" + $ref.attr("reference"))
    let contexts = JSON.parse($ref.attr("contexts"));
    $.each(contexts, function (index, value) {
        $("sent[sent_id=" + value + "]").addClass("context")
    })
    let reference = references[$ref.attr("reference")];
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
}