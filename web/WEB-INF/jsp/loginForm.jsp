<%--
  Created by IntelliJ IDEA.
  User: 李浩然
  Date: 2017/4/8
  Time: 16:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<form:form commandName="user" action="login_validate" method="post">
    <form:hidden path="id" value="0" id="id" name="id"/>
    <form:hidden path="authority" value="0" id="authority" name="authority" />
    <fieldset>
        <legend><spring:message code="login.form.title" /></legend>
        <p>
            <label for="username"><spring:message code="label.username" /></label>
            <form:input path="username" id="username" cssErrorClass="error" />
            <form:errors path="username" />
        </p>
        <p>
            <label for="password"><spring:message code="label.password" /></label>
            <form:input path="password" id="password" cssErrorClass="error" />
            <form:errors path="password" />
        </p>
        <p>
            <input id="submit" type="submit" value="<spring:message code="button.login" /> ">
        </p>
    </fieldset>
</form:form>
</body>
</html>