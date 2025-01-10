<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SAÜ Mezunları Platformu - Giriş Yap</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
<style> body { background-image: url('images/login.jpg'); background-size: cover; background-position: center; background-repeat: no-repeat; } </style>
</head>
<body>
<jsp:include page="menu.jsp" />

<main class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h5 class="text-center">İçeriklere erişebilmek için lütfen giriş yapınız.</h5>
                </div>
                <div class="card-body">
                    <form action="LoginServlet" method="post">
                        <div class="form-group">
                            <label for="email">E-posta Adresiniz:</label>
                            <input type="email" class="form-control" id="email" name="email" required>
                        </div>

                        <div class="form-group">
                            <label for="password">Şifreniz:</label>
                            <input type="password" class="form-control" id="password" name="password" required>
                        </div>

                        <div class="form-group text-center">
                            <button type="submit" class="btn btn-primary">Giriş Yap</button>
                        </div>
                    </form>

                    <c:if test="${not empty errorMessage}">
                        <div class="mt-3 alert alert-danger">${errorMessage}</div>
                    </c:if>

                    <div class="mt-3 text-center">
                        <p>Henüz platform üyesi değilseniz <a href="register.jsp" class="btn btn-link">kayıt olun!</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp" />

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>
</body>
</html>