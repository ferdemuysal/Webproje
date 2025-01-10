<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Yıllık Platformu - Konu</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="menu.jsp" />

<main class="container mt-4">
    <c:set var="threadId" value="${param.id}" scope="request" />
    <jsp:include page="/ViewThreadServlet" flush="true">
        <jsp:param name="id" value="${threadId}" />
    </jsp:include>

    <c:if test="${not empty thread}">
        <div class="card mb-3">
            <div class="card-header">
                <h4>${thread.title}</h4>
                <p class="text-muted">Yazar: ${thread.authorName}, Tarih: ${thread.formattedDate}</p>
            </div>
            <div class="card-body">
                <p>${thread.content}</p>
            </div>
        </div>

        <c:if test="${not empty replies}">
            <h5 class="mb-3">Yanıtlar</h5>
            <c:forEach var="reply" items="${replies}">
                <div class="card mb-2">
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${reply.editing && sessionScope.userRole eq 'admin'}">
                                <form action="EditReplyServlet" method="post">
                                    <input type="hidden" name="replyId" value="${reply.id}">
                                    <input type="hidden" name="threadId" value="${thread.id}">
                                    <div class="form-group">
                                        <textarea class="form-control" name="content">${reply.content}</textarea>
                                    </div>
                                    <button type="submit" class="btn btn-primary btn-sm">Kaydet</button>
                                    <a href="thread.jsp?id=${thread.id}" class="btn btn-secondary btn-sm">İptal</a>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <p class="card-text">${reply.content}</p>
                                <p class="card-text">
                                    <small class="text-muted">${reply.formattedDate} - <strong>${reply.authorName}</strong></small>
                                </p>
                                <c:if test="${sessionScope.userRole eq 'admin'}">
                                    <form action="DeleteReplyServlet" method="post" style="display: inline;">
                                        <input type="hidden" name="replyId" value="${reply.id}">
                                        <input type="hidden" name="threadId" value="${thread.id}">
                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Yanıtı silmek istediğinize emin misiniz?')">Yanıtı Sil</button>
                                    </form>
                                    <a href="EditReplyServlet?action=edit&replyId=${reply.id}&threadId=${thread.id}" class="btn btn-warning btn-sm">Yanıtı Düzenle</a>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </c:if>

        <c:if test="${not empty sessionScope.loggedIn}">
            <div class="card mb-3">
                <div class="card-header">
                    <h5>Yanıt Ekle</h5>
                </div>
                <div class="card-body">
                    <form action="AddReplyServlet" method="post">
                        <input type="hidden" name="threadId" value="${thread.id}">
                        <div class="form-group">
                            <label for="replyContent">Yanıtınız:</label>
                            <textarea class="form-control" id="replyContent" name="content" rows="3" required></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">Yanıtla</button>
                    </form>
                </div>
            </div>
        </c:if>
        <c:if test="${empty sessionScope.loggedIn}">
            <div class="alert alert-info">
                Yanıt yazmak için lütfen <a href="login.jsp" class="alert-link">giriş yapınız</a>
            </div>
        </c:if>
    </c:if>

    <c:if test="${empty thread}">
        <div class="alert alert-warning">
            Konu bulunamadı.
        </div>
    </c:if>

</main>

<footer class="bg-light py-3 mt-5">
    <div class="container">
        <p class="text-center">&copy; 2023 Yıllık Platformu</p>
    </div>
</footer>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>
</body>
</html>