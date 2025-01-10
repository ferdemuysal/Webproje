<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <a class="navbar-brand" href="index.jsp">
    <img src="images/logo.png" alt="Logo" height="70">
</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item"><a class="nav-link" href="index.jsp">Anasayfa</a></li>
                <li class="nav-item"><a class="nav-link" href="forum.jsp">Forum</a></li>
                <li class="nav-item"><a class="nav-link" href="people.jsp">Kişiler</a></li>
                <li class="nav-item"><a class="nav-link" href="photos.jsp">Fotoğraflar</a></li>
                <li class="nav-item"><a class="nav-link" href="videos.jsp">Videolar</a></li>
                <li class="nav-item"><a class="nav-link" href="whoswhere.jsp">Kim, Nerede, Ne Yapıyor?</a></li>
                <li class="nav-item"><a class="nav-link" href="map.jsp">Harita</a></li>
             <c:if test="${empty sessionScope.loggedIn}">
                    <li class="nav-item"><a class="nav-link" href="register.jsp">Kayıt Ol</a></li>
                    <li class="nav-item"><a class="nav-link" href="login.jsp">Giriş Yap</a></li>
                </c:if>
                <c:if test="${not empty sessionScope.loggedIn}">
                    <li class="nav-item">
                        <a class="nav-link" href="#">Hoşgeldin, ${sessionScope.userFirstName}</a>
                    </li>
                    <li class="nav-item"><a class="nav-link" href="LogoutServlet">Çıkış Yap</a></li>
                </c:if>
            </ul>
        </div>
    </nav>
</header>