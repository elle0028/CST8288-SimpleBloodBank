<%-- 
    Document   : CreateDonationRecordJSP
    Created on : Apr 17, 2021, 3:27:57 PM
    Author     : Matt Ellero
--%>


<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page import="entity.Person"%>
<%@page import="entity.BloodDonation"%>
<%@page import="entity.DonationRecord"%>
<%@page import="logic.PersonLogic"%>
<%@page import="logic.BloodDonationLogic"%>
<%@page import="logic.DonationRecordLogic"%>
<%@page import="logic.LogicFactory"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create Donation Record</title>
        <link rel="stylesheet" href="style/donatebloodstyles.css">
    </head>
    <body>
        <div style="display: flex; text-align: left;flex-direction: column; margin: 0 50px">
            <form action="CreateDonationRecordJSP" method="post">
                <div class="entity">
                    <h3>Donation Record</h3>
                    <fieldset class="field">
                        <label for="${DonationRecordLogic.PERSON_ID}" class="lf">Person ID</label>
                        <select name="${DonationRecordLogic.PERSON_ID}" class="lfi">
                            <c:forEach var="person" items="${persons}">                                
                                <option>${person.getId()}</option>
                            </c:forEach>
                        </select>
                        <label for="${DonationRecordLogic.ADMINISTRATOR}" class="rf">Administrator</label>
                        <input type="text" name="${DonationRecordLogic.ADMINISTRATOR}" class="rfi"/>
                    </fieldset>

                    <fieldset class="field">
                        <label for="${DonationRecordLogic.HOSPITAL}" class="lf">Hospital</label>
                        <input type="text" name="${DonationRecordLogic.HOSPITAL}" class="lfi" />
                        <label for="${DonationRecordLogic.DONATION_ID}" class="rf">Blood Donation ID</label>
                        <select name="${DonationRecordLogic.DONATION_ID}" class="rfi">
                            <c:forEach var="bd" items="${bloodDonations}">                                
                                <option>${bd.getId()}</option>
                            </c:forEach>
                        </select>
                    </fieldset>

                    <fieldset class="field">
                        <label for="${DonationRecordLogic.CREATED}" class="lf">Date</label>
                        <input type="datetime-local" step="1" name="${DonationRecordLogic.CREATED}" min="1900-01-01" class="lfi">
                        <label for="${DonationRecordLogic.TESTED}" class="rf">Tested</label>
                        <select name="${DonationRecordLogic.TESTED}" class="rfi">
                            <option>Positive</option>
                            <option>Negative</option>
                        </select>
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
