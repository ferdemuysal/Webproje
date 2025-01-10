<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SAÜ Mezunları Platformu - Harita</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/styles.css"> <!-- Harici CSS dosyasını ekledik -->
</head>
<body>
<jsp:include page="menu.jsp" />
<main>
    <div id="map-container">
        <img id="map-image" src="https://www.kgm.gov.tr/SiteCollectionImages/KGMimages/Haritalar/turistik.jpg" alt="Türkiye Haritası">
    </div>
    </main>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>
<script>
    const mapImage = document.getElementById('map-image');
    let scale = 0.8; // Başlangıç yakınlaştırma seviyesi (küçültülmüş)
    let translateX = 0;
    let translateY = 0;

    function zoomMap(direction) {
        const zoomStep = 0.1; // Yakınlaştırma adımı
        if (direction === 'in') {
            scale = Math.min(scale + zoomStep, 3); // Maksimum zoom seviyesi: 3
        } else if (direction === 'out') {
            scale = Math.max(scale - zoomStep, 0.5); // Minimum zoom seviyesi: 0.5 (küçültüldü)
        }
        updateMapTransform();
    }

    function moveMap(direction) {
        const moveStep = 100; // Hareket adımı (px) artırıldı
        if (direction === 'up') {
            translateY -= moveStep;
        } else if (direction === 'down') {
            translateY += moveStep;
        } else if (direction === 'left') {
            translateX -= moveStep;
        } else if (.direction === 'right') {
            translateX += moveStep;
        }
        updateMapTransform();
    }

    function updateMapTransform() {
        mapImage.style.transform = `translate(${translateX}px, ${translateY}px) scale(${scale})`;
    }

    // Panning işlemi için mouse olaylarını ekleyelim
    let isPanning = false;
    let startX, startY;

    mapImage.addEventListener('mousedown', (e) => {
        isPanning = true;
        startX = e.clientX - translateX;
        startY = e.clientY - translateY;
        mapImage.style.cursor = 'grabbing';
    });

    mapImage.addEventListener('mouseup', () => {
        isPanning = false;
        mapImage.style.cursor = 'grab';
    });

    mapImage.addEventListener('mousemove', (e) => {
        if (!isPanning) return;
        translateX = e.clientX - startX;
        translateY = e.clientY - startY;
        updateMapTransform();
    });

    mapImage.addEventListener('mouseleave', () => {
        isPanning = false;
        mapImage.style.cursor = 'grab';
    });
</script>
<jsp:include page="footer.jsp" />

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>
<script src="js/script.js"></script>
</body>
</html>
