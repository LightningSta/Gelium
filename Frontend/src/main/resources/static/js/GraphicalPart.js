let isDragging = false;
var img;
var sel1 = document.querySelector('#sel1');
var sel2 = document.querySelector('#sel2');
var options2 = sel2.querySelectorAll('option');
function giveSelection(selValue) {
    sel2.innerHTML = '';
    for(var i = 0; i < options2.length; i++) {
        if(options2[i].dataset.option === selValue) {
            sel2.appendChild(options2[i]);
        }
    }
}
document.addEventListener('DOMContentLoaded', function() {
    const zoomInButton = document.getElementById('imgPlus');
    const one = document.getElementById('OneAtOne');
    const zoomonButton = document.getElementById('imgMinus');
    const zoomImage = document.getElementById('dynamicImage');
    img = document.getElementById('dynamicImage');
    const button = document.getElementById('getImage7');
    let zoomLevel = 1;
    let zoomingIn = false;
    function startZoomingIn() {
        console.log('work')
        zoomingIn = true;
        zoomIn();
    }
    function startZoomingOn() {
        zoomingIn = true;
        zoomOn();
    }
    function ogr() {
        zoomImage.style.objectFit = 'contain';
    }
    function startReset() {
        zoomingIn = true;
        realSize();
    }

    function stopZoomingIn() {
        zoomingIn = false;
    }

    function zoomIn() {
        if (zoomingIn) {
            zoomLevel += 0.01;
            zoomImage.style.transform = `scale(${zoomLevel})`;
            requestAnimationFrame(zoomIn);
        }
    }
    function zoomOn(){
        if(zoomingIn){
            zoomLevel -= 0.01;
            zoomImage.style.transform = `scale(${zoomLevel})`;
            requestAnimationFrame(zoomOn);
        }
    }
    function realSize() {
        if(zoomingIn){
            zoomingIn = false;
            zoomLevel = 1;
            zoomImage.style.objectFit = 'none';
            zoomImage.style.transform = `scale(${zoomLevel})`;
            requestAnimationFrame(realSize);
        }
    }
    function preStart(){
        giveSelection(sel1.value);
    }
    preStart()
    overlay();
    // Add event listeners for drag functionality
    button.addEventListener('click',imageZoomOnClick)
    zoomInButton.addEventListener('mousedown', startZoomingIn);
    zoomInButton.addEventListener('mouseup', stopZoomingIn);
    zoomInButton.addEventListener('mouseleave', stopZoomingIn);
    one.addEventListener('mousedown', startReset)
    one.addEventListener('mouseup', stopZoomingIn);
    one.addEventListener('mouseleave', stopZoomingIn);
    zoomonButton.addEventListener('mousedown', startZoomingOn)
    zoomonButton.addEventListener('mouseup', stopZoomingIn);
    zoomonButton.addEventListener('mouseleave', stopZoomingIn);


    function imageZoomOnClick() {
        isDragging=!isDragging
        if(isDragging){
            var img, result, zoomedImage, scale = 6;
            img = document.getElementById('dynamicImage');
            // Обработчик клика для увеличения при клике по изображению
            img.addEventListener("click", function(e) {
                var pos = getCursorPos(e);
                console.log(pos)
                // Устанавливаем позиции для увеличенного изображения
                img.style.transformOrigin = `${pos.x}px ${pos.y}px`;
            });

            // Функция для вычисления координат клика относительно изображения
            function getCursorPos(e) {
                var rect = img.getBoundingClientRect();
                var x = e.clientX - rect.left;
                var y = e.clientY - rect.top;
                return { x: x, y: y };
            }
        }
    }
});
function MM_swapImgRestore() { //v3.0
    var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_replaceObj(n, d) { //v4.01
    var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
        d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
    if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
    for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_replaceObj(n,d.layers[i].document);
    if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
    var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
        if ((x=MM_replaceObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}

function imagefun7() {
    var Image_Id = document.getElementById('getImage7');
    if (Image_Id.src.match("/images/hand.png")) {
        Image_Id.src = "/images/hand-active2.png";
    }
    else {
        Image_Id.src = "/images/hand.png";
    }
}