<%-- 
    Document   : CreateBloodBankJSP
    Created on : Apr 18, 2021, 9:35 AM
    Author     : Andrew O'Hara  adapted from code written by Matt Ellero
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
            <form action="CreateBloodBankJSP" method="post">
                <div class="entity">
                    <h3>Blood Bank</h3>
                    <fieldset class="field">
                        <label for="${BloodBankLogic.NAME}" class="lf">Name</label>
                        <input type="text" name="${BloodBankLogic.NAME}" class="lfi"/>                        
                    </fieldset>

                    <fieldset class="field">
                        <label for="${BloodBankLogic.EMPLOYEE_COUNT}" class="lf">Employee Count</label>
                        <input type="number" name="${BloodBankLogic.EMPLOYEE_COUNT}" class="lfi"/>                                               
                    </fieldset>

                    <fieldset class="field">
                        <label for="${BloodBankLogic.PRIVATELY_OWNED}" class="lf">Ownership</label>
                        <select name="${BloodBankLogic.PRIVATELY_OWNED}" class="rfi">
                        	<option>Private</option>
                        	<option>Public</option>
                        </select>
                    </fieldset>
                                
                    <fieldset class="field">
                        <label for="${BloodBankLogic.OWNER_ID}" class="lf">Owner ID</label>
                        <input type="number" name="${BloodBankLogic.OWNER_ID}" class="lfi"/>                        
                    </fieldset>    

                    <fieldset class="field">
                        <label for="${BloodBankLogic.ESTABLISHED}" class="lf">Date Established</label>
                        <input type="datetime-local" step="1" name="${BloodBankLogic.ESTABLISHED}" min="1900-01-01" class="lfi">
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
