<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SAÜ Mezunları Platformu - Kişi Profili</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css"> 
</head>
<body>
<jsp:include page="menu.jsp" />
<main class="container mt-4">
    <c:set var="personId" value="${param.id}" scope="request"/>
    <jsp:include page="/GetPersonServlet">
        <jsp:param name="id" value="${personId}"/>
    </jsp:include>

    <div class="row">
        <div class="col-md-4">
            <img src="${empty person.profilePicture ? 'images/default.jpg' : person.profilePicture}"
                 class="img-fluid rounded-circle" alt="${person.firstName} ${person.lastName}">
        </div>
        <div class="col-md-8">
            <h2>${person.firstName} ${person.lastName}</h2>
            <p><strong>Şehir:</strong> ${person.city}</p>
            <p><strong>İş:</strong> ${person.currentJob}</p>
            <p><strong>E-posta:</strong> ${person.email}</p>
            <c:if test="${not empty person.website}">
                <p><strong>Web Sitesi:</strong> <a href="${person.website}" target="_blank">${person.website}</a></p>
            </c:if>
            <c:if test="${not empty person.facebookId}">
                <p><strong>Facebook:</strong> <a href="https://www.facebook.com/${person.facebookId}"
                                              target="_blank">${person.facebookId}</a></p>
            </c:if>
            <c:if test="${not empty person.twitterId}">
                <p><strong>Twitter:</strong> <a href="https://twitter.com/${person.twitterId}"
                                             target="_blank">${person.twitterId}</a></p>
            </c:if>
        </div>
    </div>

<div class="mt-4">
    <h3>Yorumlar</h3>
    <c:if test="${not empty sessionScope.loggedIn}">
        <form action="AddCommentServlet" method="post">
            <input type="hidden" name="personId" value="${person.id}">
            <input type="hidden" name="commentType" value="person">
            <div class="form-group">
                <label for="comment">Yorumunuz:</label>
                <textarea class="form-control" id="comment" name="comment" rows="3" required></textarea>
            </div>
            <button type="submit" class="btn btn-primary">Yorum Ekle</button>
        </form>
    </c:if>

    <c:if test="${empty sessionScope.loggedIn}">
        <p class="alert alert-info">Yorum yazmak için lütfen <a href="login.jsp" class="alert-link">giriş yapınız</a></p>
    </c:if>

   <div class="mt-3">
    <c:forEach var="comment" items="${comments}">
        <div class="card mb-2">
            <div class="card-body">
                <p class="card-text">${comment.commentText}</p>
                <p class="card-text">
                    <small class="text-muted">${comment.formattedDate} -
                        <strong>${comment.commenterName}</strong></small>
                </p>
                <c:if test="${sessionScope.userRole eq 'admin'}">
                    <form action="DeleteCommentServlet" method="post" style="display: inline;">
    <input type="hidden" name="commentId" value="${comment.id}">
    <input type="hidden" name="personId" value="${person.id}">
    <input type="hidden" name="commentType" value="person">
    <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Yorumu silmek istediğinize emin misiniz?')">Sil</button>
                </form>
                </c:if>
            </div>
        </div>
    </c:forEach>
</div>
</div>
</main>

<jsp:include page="footer.jsp" />

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>
</body>
</html>
