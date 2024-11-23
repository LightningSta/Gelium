var domen='http://localhost:8082';
function test(){
    var token = localStorage.getItem('authToken');

    fetch(domen+'/api/files/test',{
        headers:{
            'Authorization': 'Bearer '+ token
        }
    })
}
function profile(){
    window.location.href='/profile'
}
function home(){
    window.location.href='/home'
}