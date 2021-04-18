<%-- 
    Document   : CreateAccountJSP
    Created on : Apr 18, 2021, 11:00 AM
    Author     : Matt Ellero
--%>


<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page import="logic.AccountLogic"%>
<%@page import="logic.LogicFactory"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create Account</title>
        <link rel="stylesheet" href="style/donatebloodstyles.css">
    </head>
    <body>
        <div style="display: flex; text-align: left;flex-direction: column; margin: 0 50px">
            <form action="CreateAccountJSP" method="post">
                <div class="entity">
                    <h3>Account</h3>
                    <fieldset class="field">
                        <label for="${AccountLogic.NAME}" class="lf">Name</label>
                        <input type="text" name="${AccountLogic.NAME}" class="lfi"/>
                        <label for="${AccountLogic.NICKNAME}" class="rf">Nickname</label>
                        <input type="text" name="${AccountLogic.NICKNAME}" class="rfi"/> 
                    </fieldset>
                    <fieldset class="field">
                        <label for="${AccountLogic.USERNAME}" class="lf">Username</label>
                        <input type="text" name="${AccountLogic.USERNAME}" class="lfi"/>
                        <label for="${AccountLogic.PASSWORD}" class="rf">password</label>
                        <input type="password" name="${AccountLogic.PASSWORD}" class="rfi"/> 
                    </fieldset>

                </div>
                <br />
                <input type="submit" name="submit" value="Submit">
                <input type="submit" name="view" value="Submit and View">
            </form>
            <pre>Submitted keys and values:</pre>
        </div>
    </body>
</html>
