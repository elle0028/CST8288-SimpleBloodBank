<%-- 
    Document   : donateblood
    Created on : Apr 7, 2021, 3:27:57 PM
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
<%@page import="entity.DonationRecord"%>
<%@page import="entity.Person"%>
<%@page import="logic.PersonLogic"%>
<%@page import="logic.BloodDonationLogic"%>
<%@page import="logic.DonationRecordLogic"%>
<%@page import="logic.BloodBankLogic"%>
<%@page import="logic.LogicFactory"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Donate Blood Form</title>
        <link rel="stylesheet" href="style/donatebloodstyles.css">
    </head>
    <body>
        <div style="display: flex; text-align: left;flex-direction: column; margin: 0 50px">
            <form action="DonateBloodForm" method="post">
                <div class="entity">
                    <h3>Person</h3>
                    <fieldset class="field">
                        <label for="${PersonLogic.FIRST_NAME}" class="lf">First name</label>
                        <input type="text" name="${PersonLogic.FIRST_NAME}" value="Mickey" class="lfi" />
                        <label for="${PersonLogic.LAST_NAME}" class="rf">Last name</label>
                        <input type="text" name="${PersonLogic.LAST_NAME}" value="Mouse" class="rfi"/>
                    </fieldset>

                    <fieldset class="field">
                        <label for="${PersonLogic.PHONE}" class="lf">Phone</label>
                        <input type="tel" name="${PersonLogic.PHONE}" placeholder="8888888888" class="lfi"/>
                        <label for="${PersonLogic.ADDRESS}" class="rf">Address</label>
                        <input type="text" name="${PersonLogic.ADDRESS}" placeholder="123 John st." class="rfi"/>
                    </fieldset>

                    <fieldset class="field">
                        <label for="${PersonLogic.BIRTH}" class="lf">Date of Birth</label>
                        <input type="date" name="${PersonLogic.BIRTH}" min="1900-01-01" class="lfi">
                    </fieldset>

                </div>
                <br />
                <div class="entity">
                    <h3>Blood</h3>
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
                        <label for="${BloodDonationLogic.MILLILITERS}" class="lf">Amount</label>
                        <input type="number" name="${BloodDonationLogic.MILLILITERS}" class="lfi"/>
                        <label for="${DonationRecordLogic.TESTED}" class="rf">Tested</label>
                        <select name="${DonationRecordLogic.TESTED}" class="rfi">
                            <option>Positive</option>
                            <option>Negative</option>
                        </select>
                    </fieldset>
                </div>
                <br />
                <div class="entity">
                    <h3>Administration</h3>
                    <fieldset class="field">
                        <label for="${DonationRecordLogic.HOSPITAL}" class="lf">Hospital</label>
                        <input type="text" name="${DonationRecordLogic.HOSPITAL}" class="lfi" />
                        <label for="${DonationRecordLogic.ADMINISTRATOR}" class="rf">Administrator</label>
                        <input type="text" name="${DonationRecordLogic.ADMINISTRATOR}" class="rfi"/>
                    </fieldset>

                    <fieldset class="field">
                        <label for="${BloodDonationLogic.CREATED}" class="lf">Date</label>
                        <input type="datetime-local" step="1" name="${BloodDonationLogic.CREATED}" min="1900-01-01" class="lfi">
                        <label for="bloodbank" class="lf">Blood Bank</label>
                        <select name="${BloodBankLogic.NAME}" class="lfi">
                            <!--For each bloodbank get all we create an option with the name? -->
                            <c:forEach var="bb" items="${banks}">                                
                                <option>${bb.getName()}</option>
                            </c:forEach>
                            
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
