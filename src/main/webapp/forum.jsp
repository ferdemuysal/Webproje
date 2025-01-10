<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Yıllık Platformu - Forum</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        th {
            background-color: #f0f0f0;
        }

        .pagination {
            margin-top: 20px;
        }

        .pagination a, .pagination span {
            padding: 5px 10px;
            margin-right: 5px;
            border: 1px solid #ccc;
            text-decoration: none;
            border-radius: 3px;
        }

        .pagination span.current {
            background-color: #007bff;
            color: white;
        }

        .pagination a:hover {
            background-color: #f0f0f0;
        }
    </style>
</head>
<body>
<jsp:include page="menu.jsp" />

<main class="container mt-4">
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert">
                ${errorMessage}
        </div>
    </c:if>
    <%-- Sayfa numarasını ve ListThreadsServlet'i dahil et --%>
    <c:set var="currentPage" value="${param.page ne null ? param.page : 1}" scope="request"/>
    <jsp:include page="/ListThreadsServlet" flush="true">
        <jsp:param name="page" value="${currentPage}"/>
    </jsp:include>

    <h2 class="mb-3">Forum</h2>

    <c:if test="${not empty sessionScope.loggedIn}">
        <div class="card mb-3">
            <div class="card-header">
                <h5>Yeni Konu Ekle</h5>
            </div>
            <div class="card-body">
                <form action="AddThreadServlet" method="post">
                    <div class="form-group">
                        <label for="title">Başlık:</label>
                        <input type="text" class="form-control" id="title" name="title" required>
                    </div>
                    <div class="form-group">
                        <label for="content">İçerik:</label>
                        <textarea class="form-control" id="content" name="content" rows="3" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Konu Oluştur</button>
                </form>
            </div>
        </div>
    </c:if>

    <c:if test="${empty sessionScope.loggedIn}">
        <div class="alert alert-info">
            Konu açmak için lütfen <a href="login.jsp" class="alert-link">giriş yapınız</a>
        </div>
    </c:if>

    <h3 class="mb-3">Konular</h3>
    <c:if test="${not empty threads}">
        <table class="table table-striped table-bordered">
            <thead class="thead-light">
            <tr>
                <th>Tarih</th>
                <th>Başlık</th>
                <th>Yazar</th>
                <c:if test="${sessionScope.userRole eq 'admin'}">
                    <th>İşlemler</th>
                </c:if>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="thread" items="${threads}">
                <tr>
                    <td>${thread.formattedDate}</td>
                    <td><a href="thread.jsp?id=${thread.id}">${thread.title}</a></td>
                    <td>${thread.authorName}</td>
                    <c:if test="${sessionScope.userRole eq 'admin'}">
                        <td>
                            <form action="DeleteThreadServlet" method="post" style="display: inline;">
                                <input type="hidden" name="threadId" value="${thread.id}">
                                <button type="submit" class="btn btn-danger btn-sm"
                                        onclick="return confirm('Konuyu ve tüm yanıtlarını silmek istediğinize emin misiniz?')">
                                    Konuyu Sil
                                </button>
                            </form>
                            <c:choose>
                                <c:when test="${not empty threadToEdit && threadToEdit.id eq thread.id}">
                                    <form action="EditThreadServlet" method="post" style="display: inline-block;">
                                        <input type="hidden" name="threadId" value="${thread.id}">
                                        <div class="form-group">
                                            <label for="editTitle">Başlık:</label>
                                            <input type="text" class="form-control" id="editTitle" name="title"
                                                   value="${threadToEdit.title}" required>
                                        </div>
                                        <div class="form-group">
                                            <label for="editContent">İçerik:</label>
                                            <textarea class="form-control" id="editContent" name="content" rows="3"
                                                      required>${threadToEdit.content}</textarea>
                                        </div>
                                        <button type="submit" class="btn btn-primary btn-sm">Kaydet</button>
                                        <a href="forum.jsp" class="btn btn-secondary btn-sm">İptal</a>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <a href="EditThreadServlet?action=edit&threadId=${thread.id}"
                                       class="btn btn-warning btn-sm">Düzenle</a>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <c:if test="${empty threads}">
        <p>Henüz konu yok.</p>
    </c:if>

    <%-- Sayfalama --%>
    <nav aria-label="Forum Sayfalama">
        <ul class="pagination justify-content-center">
            <c:if test="${currentPage > 1}">
                <li class="page-item"><a class="page-link" href="forum.jsp?page=${currentPage - 1}">Önceki</a></li>
            </c:if>

            <c:forEach begin="1" end="${totalPages}" var="i">
                <li class="page-item ${currentPage eq i ? 'active' : ''}">
                    <a class="page-link" href="forum.jsp?page=${i}">${i}</a>
                </li>
            </c:forEach>

            <c:if test="${currentPage < totalPages}">
                <li class="page-item"><a class="page-link" href="forum.jsp?page=${currentPage + 1}">Sonraki</a></li>
            </c:if>
        </ul>
    </nav>
</main>

<jsp:include page="footer.jsp" />

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>
</body>
</html>