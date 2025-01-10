<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SAÜ Mezunları Platformu - Videolar</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css"> <%-- Kendi stillerinizi ekleyebilirsiniz --%>
    <style>
        .video-card {
            display: flex;
            flex-direction: column;
            height: 100%; /* Make the cards take up full height */
        }

        .video-card img {
            width: 100%;
            height: 200px; /* Fixed height for images */
            object-fit: cover;
            border-bottom: 1px solid #ccc;
        }

        .card-body {
            flex-grow: 1; /* Allow the card body to grow */
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        .card-title{
            font-size: 1rem;
        }

    </style>
</head>
<body>
<jsp:include page="menu.jsp" />
<main class="container mt-4">
    <h2>Videolar</h2>

    <c:set var="currentPage" value="${param.page ne null ? param.page : 1}" scope="request"/>
    <jsp:include page="/ListVideosServlet" flush="true">
        <jsp:param name="page" value="${currentPage}"/>
    </jsp:include>

    <div class="row">
        <c:forEach var="video" items="${videos}">
            <div class="col-md-4 mb-3">
                <div class="card video-card">
                    <img src="${video.thumbnailUrl}" class="card-img-top" alt="${video.title}">
                    <div class="card-body">
                        <h5 class="card-title">
                            <a href="video.jsp?id=${video.id}" class="card-link">${video.title}</a>
                        </h5>
                        <p class="card-text">
                            <small class="text-muted">Yükleyen: ${video.uploaderName}</small>
                        </p>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <nav aria-label="Video Sayfalama">
        <ul class="pagination justify-content-center">
            <c:if test="${currentPage > 1}">
                <li class="page-item"><a class="page-link" href="videos.jsp?page=${currentPage - 1}">Önceki</a>
                </li>
            </c:if>
            <c:forEach begin="1" end="${totalPages}" var="i">
                <li class="page-item ${currentPage eq i ? 'active' : ''}">
                    <a class="page-link" href="videos.jsp?page=${i}">${i}</a>
                </li>
            </c:forEach>
            <c:if test="${currentPage < totalPages}">
                <li class="page-item"><a class="page-link" href="videos.jsp?page=${currentPage + 1}">Sonraki</a>
                </li>
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