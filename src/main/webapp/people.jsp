<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>SAÜ Mezunları Platformu - Kişiler</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<jsp:include page="menu.jsp" />
<main class="container mt-4">
    <h2 class="mb-3">Kişiler</h2>

    <c:set var="currentPage" value="${param.page ne null ? param.page : 1}" scope="request"/>
    <jsp:include page="/ListPeopleServlet" flush="true">
        <jsp:param name="page" value="${currentPage}"/>
    </jsp:include>

    <div class="row">
        <c:forEach var="person" items="${people}">
            <div class="col-md-4 mb-3">
                <div class="card">
                    <img src="${empty person.profilePicture ? 'images/default.jpg' : person.profilePicture}"
     class="card-img-top" alt="${person.firstName} ${person.lastName}" 
     style="width: 200px; height: 200px; object-fit: cover; object-position: top;">
                        <div class="card-body">
                        <h5 class="card-title">
                            <a href="person.jsp?id=${person.id}" class="card-link">
                                    ${person.firstName} ${person.lastName}
                            </a>
                        </h5>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <nav aria-label="Kişiler Sayfalama">
        <ul class="pagination justify-content-center">
            <c:if test="${currentPage > 1}">
                <li class="page-item"><a class="page-link" href="people.jsp?page=${currentPage - 1}">Önceki</a></li>
            </c:if>
            <c:forEach begin="1" end="${totalPages}" var="i">
                <li class="page-item ${currentPage eq i ? 'active' : ''}">
                    <a class="page-link" href="people.jsp?page=${i}">${i}</a>
                </li>
            </c:forEach>
            <c:if test="${currentPage < totalPages}">
                <li class="page-item"><a class="page-link" href="people.jsp?page=${currentPage + 1}">Sonraki</a></li>
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