<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SAÜ Mezunları Platformu - Kayıt Ol</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="menu.jsp" />
<main class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h2 class="text-center">Kayıt Ol</h2>
                </div>
                <div class="card-body">
                    <form action="RegisterServlet" method="post" enctype="multipart/form-data">
                        <div class="form-group">
                            <label for="firstName">Adınız:</label>
                            <input type="text" class="form-control" id="firstName" name="firstName" required>
                        </div>

                        <div class="form-group">
                            <label for="lastName">Soyadınız:</label>
                            <input type="text" class="form-control" id="lastName" name="lastName" required>
                        </div>

                        <div class="form-group">
                            <label for="email">E-posta Adresiniz:</label>
                            <input type="email" class="form-control" id="email" name="email" required>
                        </div>

                        <div class="form-group">
                            <label for="password">Şifreniz:</label>
                            <input type="password" class="form-control" id="password" name="password" required>
                        </div>

               <jsp:include page="city.jsp" />

                        <div class="form-group">
                            <label>Cinsiyetiniz:</label><br>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" id="male" name="gender" value="Erkek" checked>
                                <label class="form-check-label" for="male">Erkek</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" id="female" name="gender" value="Kadın">
                                <label class="form-check-label" for="female">Kadın</label>
                            </div>
                        </div>

<jsp:include page="hobbies.jsp" />

                        <div class="form-group">
                            <label for="profilePicture">Profil Resminiz:</label>
                            <input type="file" class="form-control-file" id="profilePicture" name="profilePicture" accept="image/*">
                        </div>

                        <div class="form-group">
                            <label for="currentSchool">Okul:</label>
                            <input type="text" class="form-control" id="currentSchool" name="currentSchool">
                        </div>

                        <div class="form-group">
                            <label for="currentJob">İş:</label>
                            <input type="text" class="form-control" id="currentJob" name="currentJob">
                        </div>

                        <div class="form-group">
                            <label for="website">Web Sitesi:</label>
                            <input type="text" class="form-control" id="website" name="website">
                        </div>

                        <div class="form-group">
                            <label for="facebookId">Facebook ID:</label>
                            <input type="text" class="form-control" id="facebookId" name="facebookId">
                        </div>

                        <div class="form-group">
                            <label for="twitterId">Twitter ID:</label>
                            <input type="text" class="form-control" id="twitterId" name="twitterId">
                        </div>

                        <div class="form-group text-center">
                            <button type="submit" class="btn btn-primary">Kayıt Ol</button>
                        </div>
                    </form>
                </div>
            </div>
            <div id="errorMessage" class="mt-3 alert alert-danger" style="display: none;">
                <c:if test="${not empty errorMessage}">
                    ${errorMessage}
                </c:if>
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