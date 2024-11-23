function overlay(){
    const overlay = document.createElement('div');
    overlay.id = 'overlay';
    document.body.appendChild(overlay);
}

function addActions(){
    const popup = document.getElementById('actions-in');

    const openPopupBtn = document.querySelector('.icons[src="/images/book.svg"]');
    const cancelBtn = document.getElementById('cancel2');
    const submitBtn = document.getElementById('sub2');
    const overlay = document.getElementById('overlay')
    // Функция для открытия окна
    function openPopup() {
        popup.style.display = 'block';
        overlay.style.display = 'block';
    }

    // Функция для закрытия окна
    function closePopup() {
        popup.style.display = 'none';
        overlay.style.display = 'none';
    }

    // Открытие окна при клике на иконку "Создать папку"
    openPopupBtn.addEventListener('click', openPopup);

    // Закрытие окна при клике на "Отменить"
    cancelBtn.addEventListener('click', closePopup);

    // Закрытие окна при клике на подложку
    overlay.addEventListener('click', closePopup);

    // Действие при клике на "Создать"
    submitBtn.addEventListener('click', () => {
        const val = document.getElementById('act').value
        if(val=='dpichange'){
            changeDpi();
        }else{
            compress();
        }
        closePopup();
    });
}
function changeDpi(){
    var dpi = document.getElementById('dpi').value
    const src = document.getElementById('dynamicImage').src;
    const url = new URL(src); // Преобразуем строку в объект URL
    const fileName = url.searchParams.get('fileName'); // Получаем значение параметра 'fileName'
    fetch(domen+'/api/files/images/dpichange',{
        method: 'POST',
        headers: {
            'Authorization': 'Bearer '+ sessionStorage.getItem('authToken')
        },
        body: JSON.stringify({
            filename: fileName,
            nickname: sessionStorage.getItem('nickname'),
            additionalPath: "",
            dpi: dpi
        })
    })
}
function compress(){
    const src = document.getElementById('dynamicImage').src;
    const url = new URL(src); // Преобразуем строку в объект URL
    const fileName = url.searchParams.get('fileName'); // Получаем значение параметра 'fileName'
    fetch(domen+'/api/files/images/compress',{
        method: 'POST',
        headers: {
            'Authorization': 'Bearer '+ sessionStorage.getItem('authToken')
        },
        body: JSON.stringify({
            filename: fileName,
            nickname: sessionStorage.getItem('nickname'),
            additionalPath: "",
            compress: document.getElementById('sel2').value
        })
    })
}
document.addEventListener('DOMContentLoaded', ()=>{
    overlay();
    document.getElementById('action-book').addEventListener('mousedown',()=>{
        addActions();
    })
})