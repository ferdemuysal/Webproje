$(document).ready(function () {
    let scale = 1; // Başlangıç yakınlaştırma oranı
    let isDragging = false;
    let startX, startY, currentX, currentY, translateX = 0, translateY = 0;
    const mapContainer = $('#map-container');
    const mapImage = $('#map-image');

    mapImage.on('mousedown', function (e) {
        isDragging = true;
        startX = e.pageX - translateX;
        startY = e.pageY - translateY;
        mapImage.css('cursor', 'grabbing');
    });

    $(document).on('mouseup', function () {
        isDragging = false;
        mapImage.css('cursor', 'grab');
    });

    $(document).on('mousemove', function (e) {
        if (!isDragging) return;
        e.preventDefault();
        currentX = e.pageX;
        currentY = e.pageY;
        translateX = currentX - startX;
        translateY = currentY - startY;
        setTransform();
    });

    $('#zoom-in').on('click', function () {
        scale += 0.1;
        updateZoom();
    });

    $('#zoom-out').on('click', function () {
        scale -= 0.1;
        if (scale < 0.2) scale = 0.2;
        updateZoom();
    });

    function updateZoom() {
        // Resmin genişliğini ve yüksekliğini yakınlaştırma oranına göre ayarla
        const newImageWidth = 1500 * scale;
        const newImageHeight = mapImage.get(0).naturalHeight * (newImageWidth / mapImage.get(0).naturalWidth);

        mapImage.css('width', newImageWidth + 'px');
        mapImage.css('height', newImageHeight + 'px');

        // Kaydırma pozisyonunu güncelle
        const containerWidth = mapContainer.width();
        const containerHeight = mapContainer.height();

        // Eğer resim container'dan küçükse ortala
        if (newImageWidth < containerWidth) {
            translateX = (containerWidth - newImageWidth) / 2;
        } else {
            // Resmi kaydırma sınırları içinde tut
            translateX = Math.max(translateX, containerWidth - newImageWidth);
            translateX = Math.min(translateX, 0);
        }

        if (newImageHeight < containerHeight) {
            translateY = (containerHeight - newImageHeight) / 2;
        } else {
            // Resmi kaydırma sınırları içinde tut
            translateY = Math.max(translateY, containerHeight - newImageHeight);
            translateY = Math.min(translateY, 0);
        }

        setTransform();
    }

    function setTransform() {
        mapImage.css('transform', `translate(${translateX}px, ${translateY}px) scale(${scale})`);
    }

    mapContainer.on('wheel', function (e) {
        e.preventDefault();
        if (e.originalEvent.deltaY < 0) {
            scale += 0.1;
        } else {
            scale -= 0.1;
        }
        if (scale < 0.2) scale = 0.2;
        updateZoom();
    });

    function fitImageToContainer() {
        const containerWidth = mapContainer.width();
        const containerHeight = mapContainer.height();

        // Resmi, map-container genişliğinin %75'ine göre ayarla
        const newImageWidth = containerWidth;
        
        // Yüksekliği otomatik ayarla, böylece en-boy oranı korunur
        const newImageHeight = mapImage.get(0).naturalHeight * (newImageWidth / mapImage.get(0).naturalWidth);

        mapImage.css('width', newImageWidth + 'px');
        mapImage.css('height', newImageHeight + 'px');

        // Resmi ortalamak için
        translateX = 0;
        translateY = 0;

        scale = 1; // Ölçeği sıfırla
        setTransform();
    }

    $(window).on('load resize', fitImageToContainer); // Sayfa yüklendiğinde ve pencere boyutu değiştiğinde çalıştır
    fitImageToContainer(); // İlk yüklemede resmi sığdır
});