<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SAÜ Mezunları Platformu - Anasayfa</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css"> 
    <style>
        .hero-section {
            background-color: #233b77;
            color: #fff;
            padding-top: 50px;
            padding-bottom: 50px;
        }
        .icon-box {
            padding: 25px;
            background-color: #f8f9fa;
            border: 1px solid #e9ecef;
            text-align: justify;
        } .icon-box h4 { text-align: center;
        }
            </style>
</head>
<body>
<jsp:include page="menu.jsp" />
<c:if test="${not empty sessionScope.loggedIn && sessionScope.loggedIn}">
<main>
    <section class="hero-section">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <h1>Mezunlar Platformuna Hoşgeldiniz!</h1>
                    <p>Burada tüm mezunlarımız için haberler, etkinlikler ve pek çok içeriklere ulaşabileceksiniz.</p>
                  
                </div>
                <div class="col-md-6">
                    <img src="images/sau.jpg" alt="anasayfa" class="img-fluid">
                </div>
            </div>
        </div>
    </section>

    <section class="container mt-5">
        <div class="row">
            <div class="col-md-3">
                <div class="icon-box">
                    <i class="fas fa-chart-line fa-3x"></i> 
                     <img src="images/gorsel1.jpg" alt="gorsel1" class="img-fluid mx-auto d-block">
                    <h4 class="mt-3">Mezun Kart</h4>
                    <a href="https://mezun.sakarya.edu.tr/?page_id=95" target="_blank" rel="noreferrer noopener">Dijital mezun kart ile ayrıcalıkları keşfet</a>
                </div>
            </div>
            <div class="col-md-3">
                <div class="icon-box">
                    <i class="fas fa-gem fa-3x"></i>
                    <img src="images/gorsel2.jpg" alt="gorsel2" class="img-fluid mx-auto d-block"> 
                    <h4 class="mt-3">Davetlisiniz!</h4>
                    <a href="https://mezun.sakarya.edu.tr/?p=1151" target="_blank" rel="noreferrer noopener">Endüstri Mühendisliği mezunlar buluşmasına sizi de bekliyoruz.</a>
                </div>
            </div>
            <div class="col-md-3">
                <div class="icon-box">
                    <img src="images/gorsel3.jpg" alt="gorsel3" class="img-fluid mx-auto d-block"> 
                    <h4 class="mt-3">SAÜ Aktüel</h4>
                    <a href="https://mezun.sakarya.edu.tr/?page_id=526" target="_blank" rel="noreferrer noopener">SAÜ Aktüel dergisinin yeni sayısı çıktı.</a>
                </div>
            </div>
            <div class="col-md-3">
                <div class="icon-box">
                <img src="images/gorsel4.jpg" alt="gorsel4" class="img-fluid mx-auto d-block"> 
                    <i class="fas fa-anchor fa-3x"></i> 
                    <h4 class="mt-3">Tanıtım Filmi</h4>
                    <a href="https://www.youtube.com/watch?v=qYMSXMKGtKg" target="_blank" rel="noreferrer noopener">Sakarya Üniversitesi tanıtım filmini izlemek için tıklayın.</a>
                </div>
            </div>
        </div>
    </section>

    <section class="container mt-5">
    <img src="images/banner1.jpg" alt="banner1">
    </section>
</main>
</c:if>
<c:if test="${empty sessionScope.loggedIn || !sessionScope.loggedIn}">
    <c:redirect url="login.jsp"/>
</c:if>

<jsp:include page="footer.jsp" />

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>
<script src="https://kit.fontawesome.com/your-fontawesome-kit.js"></script>
</body>
</html>