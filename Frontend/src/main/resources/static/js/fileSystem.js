function personLoad(){
    var token = sessionStorage.getItem('authToken');

    fetch(domen+'/validate',{
        headers:{
            'Authorization': 'Bearer '+ token
        }
    }).then(function (resp){
        resp.json().then(
            function (json){
                //sessionStorage.setItem('person',json)
                var nick=json.nickname;
                console.log(nick)
                sessionStorage.setItem('nickname',nick)
            }
        )
    })
}

function intoFs(){
    window.location.href='/intofs'
}

document.addEventListener('DOMContentLoaded',()=>{
    personLoad();
})