<%-- 
    Document   : ShowTable-Account
    Created on : April 17,2020
    Author     : Fargol Azimi
--%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    </head>
    <body>
        <div style="margin-left: 400px  ">

            <form  method="post">
                First Name:<br>
                <input type="text" name= "first_name" value=""><br>
                <br>
                Last Name:
                <br>
                <input type="text" name="last_name" value=""><br>
                <br>
                Phone:
                <br>
                <input type="number" name="phone" value=""><br>
                <br>
                Address:
                <br>
                <input type="text" name="address" value=""><br>
                <br>
                Birth Date:<br>
                <input type="datetime-local" step="1" name="birth" value="">
                <br>
                <br>
                <input type="submit" name="view" value="Add and View">
                <input type="submit" name="add" value="Add">
            </form>
        </div>
    </body>
</html>