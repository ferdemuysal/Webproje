<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SAÜ Mezunları Platformu - Kim Nerede Ne Yapıyor?</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="menu.jsp" />
<main class="container mt-4">
    <h2>Kim Nerede Ne Yapıyor?</h2>

    <jsp:include page="/ListPeopleInfoServlet" flush="true"/>

    <table class="table table-striped table-bordered">
        <thead class="thead-light">
        <tr>
            <th>Ad</th>
            <th>Soyad</th>
            <th>Şehir</th>
            <th>İş</th>
            <th>E-posta</th>
            <th>Web Sitesi</th>
            <th>Facebook</th>
            <th>Twitter</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="user" items="${users}">
            <tr>
                <td>${user.firstName}</td>
                <td>${user.lastName}</td>
                <td>${user.city}</td>
                <td>${user.currentJob}</td>
                <td>${user.email}</td>
                <td>
                    <c:if test="${not empty user.website}">
                        <a href="${user.website}" target="_blank">${user.website}</a>
                    </c:if>
                </td>
                <td>
                    <c:if test="${not empty user.facebookId}">
                        <a href="https://www.facebook.com/${user.facebookId}" target="_blank">${user.facebookId}</a>
                    </c:if>
                </td>
                <td>
                    <c:if test="${not empty user.twitterId}">
                        <a href="https://twitter.com/${user.twitterId}" target="_blank">${user.twitterId}</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</main>

<jsp:include page="footer.jsp" />

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>
</body>
</html>