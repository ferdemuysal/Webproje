<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SAÜ Mezunları Platformu - Fotoğraf</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="menu.jsp" />
<main class="container mt-4">
    <c:set var="photoId" value="${param.id}" scope="request"/>
    <jsp:include page="/GetPhotoServlet" flush="true">
        <jsp:param name="id" value="${photoId}"/>
    </jsp:include>

    <div class="row">
        <div class="col-md-8">
            <div class="card">
                <img src="${photo.filePath}" class="card-img-top" alt="Fotoğraf">
                <div class="card-body">
                    <p class="card-text">
                        <small class="text-muted">Yükleyen: ${photo.uploaderName}</small>
                    </p>
                </div>
            </div>
        </div>
        <!-- Yorumlar sağ tarafta -->
        <div class="col-md-4">
            <div class="card mt-4">
                <div class="card-header">
                    <h5>Yorumlar</h5>
                </div>
                <div class="card-body">
                    <c:if test="${not empty sessionScope.loggedIn}">
                        <form action="AddCommentServlet" method="post">
                            <input type="hidden" name="photoId" value="${photo.id}">
                            <input type="hidden" name="commentType" value="photo">
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
        <input type="hidden" name="photoId" value="${photo.id}">
        <input type="hidden" name="commentType" value="photo">
        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Yorumu silmek istediğinize emin misiniz?')">Sil</button>
    </form>
</c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
        <!-- Yorumlar sağda sona eriyor -->
    </div>
</main>

<jsp:include page="footer.jsp" />
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>
</body>
</html>
