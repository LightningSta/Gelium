document.addEventListener('DOMContentLoaded',()=>{
    function groupsGet(){
        document.getElementById('name-user').textContent=sessionStorage.getItem('nickname');
    }
    groupsGet()
})