function sentToReg(){
    window.location.href='/registration'
}
function sentToLogin(){
    window.location.href='/login'
}

document.getElementById('signin').addEventListener('submit',(e)=>{
    var header = document.getElementById('_csrf_header').content;
    var token =  document.getElementById('_csrf').content
    var formData = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    }
    fetch("/post", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: [token],
        },
        body: JSON.stringify(formData)
    })
        .then(function(response) {
            response.text().then(
                function (text){
                    sessionStorage.setItem('authToken', text);
                }
            )
        })
        .catch(function(error) {
            console.log('Error:', error);
        });
})
function registartion(){
    var formData = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value,
        nickname: document.getElementById('nickname').value
    }
    console.log(JSON.stringify(formData))
    fetch("http://localhost:8082/register", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
        .then(function(response) {
            response.text().then(
                function (text){
                    sessionStorage.setItem('authToken', text);
                    window.location.href='/home'
                }
            )
        })
        .catch(function(error) {
            console.log('Error:', error);
        });
}