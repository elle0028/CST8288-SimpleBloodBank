<%-- 
    Document   : CreateBloodBankJSP
    Created on : Apr 17, 2021, 3:27:57 PM
    Author     : Matt Ellero
--%>


<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page import="entity.BloodBank"%>
<%@page import="entity.BloodGroup"%>
<%@page import="entity.RhesusFactor"%>
<%@page import="logic.BloodDonationLogic"%>
<%@page import="logic.BloodBankLogic"%>
<%@page import="logic.LogicFactory"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create Blood Donation</title>
        <link rel="stylesheet" href="style/donatebloodstyles.css">
    </head>
    <body>
        <div style="display: flex; text-align: left;flex-direction: column; margin: 0 50px">
            <form action="CreateBloodDonationJSP" method="post">
                <div class="entity">
                    <h3>Blood Donation</h3>
                    <fieldset class="field">
                        <label for="${BloodDonationLogic.BLOOD_GROUP}" class="lf">Blood Group</label>
                        <select name="${BloodDonationLogic.BLOOD_GROUP}" class="lfi">
                            <c:forEach var="bg" items="${BloodGroup.values()}">
                                <option>${bg.toString()}</option>
                            </c:forEach>
                        </select>
                        <label for="${BloodDonationLogic.RHESUS_FACTOR}" class="rf">RHD</label>
                        <select name="${BloodDonationLogic.RHESUS_FACTOR}" class="lfi">
                            <c:forEach var="rhd" items="${RhesusFactor.values()}">
                                <option>${rhd.toString()}</option>
                            </c:forEach>
                        </select>
                    </fieldset>

                    <fieldset class="field">
                        <label for="${BloodDonationLogic.MILLILITERS}" class="lf">Milliliters</label>
                        <input type="number" name="${BloodDonationLogic.MILLILITERS}" class="lfi"/>
                        <label for="bloodbank" class="rf">Blood Bank</label>
                        <select name="${BloodBankLogic.NAME}" class="rfi">
                            <!--For each bloodbank get all we create an option with the name? -->
                            <c:forEach var="bb" items="${banks}">                                
                                <option>${bb.getName()}</option>
                            </c:forEach>

                        </select>
                    </fieldset>

                    <fieldset class="field">
                        <label for="${BloodDonationLogic.CREATED}" class="lf">Date</label>
                        <input type="datetime-local" step="1" name="${BloodDonationLogic.CREATED}" min="1900-01-01" class="lfi">
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
