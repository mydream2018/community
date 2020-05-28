function post() {
    var id = $("#question-id").val();
    var comment = $("#comment_content").val();
    $.ajax({
        type: "POST",
        url:"/comment",
        contentType:"application/json",
        data: JSON.stringify({
            "parentId":id,
            "content": comment,
            "type":1
        }),
        success: function (response) {
            if(response.code == 200){
                $("#comment_window").hide();
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