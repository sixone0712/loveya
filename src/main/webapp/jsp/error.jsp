<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!doctype html>

<html>
<body>
<h1>Error</h1>

<c:if test="${error ne null}">${error}</c:if>
</body>
</html>