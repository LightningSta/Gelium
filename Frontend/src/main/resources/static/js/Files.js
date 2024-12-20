var pages=0;
var filess;
document.addEventListener('DOMContentLoaded', ()=>{
    function onFileload(){
        fetch(domen+'/api/files/viewFs',{
            method: 'GET',
            headers: {
                'Authorization': 'Bearer '+ sessionStorage.getItem('authToken')
            }
        }).then(function (resp){
            resp.json().then(
                function (json){
                    filess=json;
                    showFsItems(json)
                }
            )
        })
    }
    function onLoad(){
        const tilesCountSelect = document.getElementById('tilesCount');
        const tilesNumberSelect = document.getElementById('tilesNumber');

        function updateTilesNumber() {
            const tilesCount = parseInt(tilesCountSelect.value);

            tilesNumberSelect.innerHTML = '';

            for (let i =1; i <= tilesCount; i++) {
                const option = document.createElement('option');
                option.value = i;
                option.textContent = i;
                tilesNumberSelect.appendChild(option);
            }
        }

        tilesCountSelect.addEventListener('change', updateTilesNumber);

        updateTilesNumber();
    }
    function bundle(){
        document.getElementById('folder_plus').addEventListener('mousedown',()=>{
            addpopupFolder();
        })
        document.getElementById('upload').addEventListener('mousedown',()=>{
            uploadImage();
        })
    }
    onFileload()
    onLoad()
    overlay()
    bundle()
})
function showFsItems(json){
    const files = json.files;
    for (let i = pages; i < 7+pages; i++) {
        try {
            const file = files[i]
            console.log(file)
            addItem(file)
        }catch (e){
            return;
        }
    }
    pages+=7;

}
function nextfiles(){
    document.getElementById('content-fs').innerHTML=''
    showFsItems(filess);
}
function backfiles(){
    if(pages>=7){
        pages-=(pages%7)+7
        document.getElementById('content-fs').innerHTML='';
        showFsItems(filess);
    }
}
function addItem(file){
    const fsbody= document.getElementById('content-fs')
    const divFile = document.createElement('div')
    const iconFile = document.createElement('img')
    const nameFile = document.createElement('a')
    const sizeFile = document.createElement('a')
    const date = document.createElement('a')

    var tiles = null;
    iconFile.src='/images/'+file.fileIcon+'.svg'
    iconFile.classList.add('icons')

    nameFile.text=file.fileName
    if(file.fileName.includes('.')){
        tiles = document.createElement('img')
        tiles.classList.add('icons')
        tiles.id=file.fileName;
        tiles.src='/images/book.svg';
        tiles.addEventListener('mousedown',()=>{
            titleGet(sessionStorage.getItem('nickname')+'?fileName='+ file.fileName,
                tiles.id);
        })
    }
    sizeFile.text=file.fileSize

    date.text=file.Date

    divFile.appendChild(iconFile)
    divFile.appendChild(nameFile)
    divFile.appendChild(sizeFile)
    divFile.appendChild(date)
    divFile.appendChild(document.createElement('br'))
    if(tiles!==null){
        divFile.appendChild(tiles)
    }
    fsbody.appendChild(divFile)



}
function overlay(){
    const overlay = document.createElement('div');
    overlay.id = 'overlay';
    document.body.appendChild(overlay);
}

function addpopupFolder(){
    const popup = document.getElementById('createFolder');

    const openPopupBtn = document.querySelector('.icons[src="/images/folder-plus.svg"]');
    const cancelBtn = document.getElementById('cancel2');
    const submitBtn = document.getElementById('sub2');
    const overlay = document.getElementById('overlay')
    function openPopup() {
        popup.style.display = 'block';
        overlay.style.display = 'block';
    }

    function closePopup() {
        popup.style.display = 'none';
        overlay.style.display = 'none';
    }

    openPopupBtn.addEventListener('click', openPopup);

    cancelBtn.addEventListener('click', closePopup);


    overlay.addEventListener('click', closePopup);


    submitBtn.addEventListener('click', () => {
        const folderName = document.getElementById('naming').value;
        const nickname = sessionStorage.getItem('nickname')
        fetch(domen+'/api/files/createFolder', {
            method: 'POST',
            headers:{
                'Content-type': 'application/json',
                'Authorization': 'Bearer '+ sessionStorage.getItem('authToken')
            },
            body: JSON.stringify({
                nickname: nickname,
                folderName: folderName
            })
        })
        closePopup();
    });
}
function uploadImage(){
    const popup = document.getElementById('addFile');
    const overlay = document.getElementById('overlay')

    const openPopupBtn = document.querySelector('.icons[src="/images/upload.svg"]');
    const cancelBtn = document.getElementById('cancel3');
    const submitBtn = document.getElementById('sub3');


    function openPopup() {
        popup.style.display = 'block';
        overlay.style.display = 'block';
    }


    function closePopup() {
        popup.style.display = 'none';
        overlay.style.display = 'none';
    }

    openPopupBtn.addEventListener('click', openPopup);


    cancelBtn.addEventListener('click', closePopup);


    overlay.addEventListener('click', closePopup);


    submitBtn.addEventListener('click', async () => {
        const input = document.getElementById('imageInput');
        const formData = new FormData();
        formData.append('file', input.files[0]);
        formData.append('namefolder',sessionStorage.getItem('nickname'))
        try {
            const response = await fetch(domen + '/api/files/upload', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + sessionStorage.getItem('authToken')
                },
                body: formData
            });
        } catch (error) {
            console.error('Error:', error);
            alert('Error uploading image');
        }
        closePopup();
    });
}
function titleGet(tile,id){
    const popup = document.getElementById('tiles-get');
    const overlay = document.getElementById('overlay')

    const openPopupBtn = document.getElementById(id);
    const cancelBtn = document.getElementById('cancel4');
    const submitBtn = document.getElementById('sub4');


    function openPopup() {
        popup.style.display = 'block';
        overlay.style.display = 'block';
    }


    function closePopup() {
        popup.style.display = 'none';
        overlay.style.display = 'none';
    }


    openPopupBtn.addEventListener('click', openPopup);


    cancelBtn.addEventListener('click', closePopup);


    overlay.addEventListener('click', closePopup);

    submitBtn.addEventListener('click', async () => {
        const tilesCount = document.getElementById('tilesCount').value;
        const tilesNumber = document.getElementById('tilesNumber').value;
        window.location.href='/imageview_tile/'+tile+'&tileCount='+tilesCount + '&tileNumber='+tilesNumber
    });
}