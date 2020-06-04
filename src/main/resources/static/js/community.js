/*
提交评论
 */
function post() {
    var id = $("#question-id").val();
    var comment = $("#comment_content").val();
    comment2target(id,1, comment);
}

/*
展开二级评论
 */
function collapseComments(e) {
var id = e.getAttribute('data-id');
// console.log(id);
var comments = $('#comment-' + id);

//获取二级评论的展开状态
var collapse = e.getAttribute("data-collapse");
    if(collapse){//如果状态为真，则关闭评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active")
    }else{

        $.getJSON( "/comment/" + id, function( data ) {
            // console.log(data);
            var items = [];
            var commentBody = $("comment-body-" + id);

            $.each( data.data, function( comment) {
                var cont = $("<div/>",{
                    "class":"col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    html: comment.content
                });
                items.push(cont)
            });

            $("<div/>", {
                "class":"col-lg-12 col-md-12 col-sm-12 col-xs-12 comment-sp collapse sub-comments",
                "id":"comment-" + id,
                html:items.join("")
            }).appendTo(commentBody);


            //否则展开评论，设置展开属性
            comments.addClass("in");
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        });
    }


// console.log(id);
}

function comment2target(targetId, type, content){
    if(!content){
        alert("您输入的评论内容不能为空！");
        return;
    }
    $.ajax({
        type: "POST",
        url:"/comment",
        contentType:"application/json",
        data: JSON.stringify({
            "parentId":targetId,
            "content": content,
            "type":type
        }),
        success: function (response) {
            if(response.code == 200){
                window.location.reload();
            }else{
                if(response.code == 2003){
                    var isAccept = confirm(response.message);
                    if(isAccept){
                        window.open("https://github.com/login/oauth/authorize?client_id=5a7d4b587a6053a37b9c&redirect_uri=http://localhost:8090/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", "true");
                    }
                }else{
                    alert(response.message);
                }

            }
            console.log(response);
        }
    });
}


function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content);
}