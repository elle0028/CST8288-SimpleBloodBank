package view;

import entity.BloodBank;
import entity.BloodDonation;
import entity.DonationRecord;
import entity.Person;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BloodBankLogic;
import logic.BloodDonationLogic;
import logic.DonationRecordLogic;

import logic.LogicFactory;
import logic.PersonLogic;

/**
 *
 * @author Matthew Ellero
 */
@WebServlet(name = "CreateDonationRecordJSP", urlPatterns = {"/CreateDonationRecordJSP"})
public class CreateDonationRecordJSP extends HttpServlet {

    private void fillTableData(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute("request", toStringMap(req.getParameterMap()));
        req.setAttribute("path", path);
        req.setAttribute("title", path.substring(1));
        req.getRequestDispatcher("/jsp/CreateTable-DonationRecord.jsp").forward(req, resp);
    }

    private String toStringMap(Map<String, String[]> m) {
        StringBuilder builder = new StringBuilder();
        m.keySet().forEach((k) -> {
            builder.append("Key=").append(k)
                    .append(", ")
                    .append("Value/s=").append(Arrays.toString(m.get(k)))
                    .append(System.lineSeparator());
        });
        return builder.toString();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");

        // Dependency Logic
        BloodDonationLogic bdLogic = LogicFactory.getFor("BloodDonation");
        PersonLogic personLogic = LogicFactory.getFor("Person");

        // Main logic
        DonationRecordLogic drLogic = LogicFactory.getFor("DonationRecord");
        
        try {
            DonationRecord donation_record = drLogic.createEntity(request.getParameterMap());
            
            int personId = Integer.parseInt(request.getParameterMap().get(DonationRecordLogic.PERSON_ID)[0]);
            Person person = personLogic.getWithId(personId);
            donation_record.setPerson(person);

            int donationId = Integer.parseInt(request.getParameterMap().get(DonationRecordLogic.DONATION_ID)[0]);
            BloodDonation blood_donation = bdLogic.getWithId(donationId);
            donation_record.setBloodDonation(blood_donation);
            
            drLogic.add(donation_record);
        } catch (IllegalArgumentException ex) {
            log("Error creating DonationRecord: \n", ex);
        }

        if (request.getParameter("add") != null) {
            //if add button is pressed return the same page
            response.sendRedirect("DonationRecordTableJSP");
        } else if (request.getParameter("view") != null) {
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect("DonationRecordTable");
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("GET");

        // Fetch Dependency Data
        BloodDonationLogic bdLogic = LogicFactory.getFor("BloodDonation");
        PersonLogic personLogic = LogicFactory.getFor("Person");

        List<BloodDonation> bloodDonations = bdLogic.getAll();
        List<Person> persons = personLogic.getAll();

        req.setAttribute("bloodDonations", bloodDonations);
        req.setAttribute("persons", persons);
        fillTableData(req, resp);
    }

    /**
     * Handles the HTTP <code>PUT</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("PUT");
        doPost(req, resp);
    }

    /**
     * Handles the HTTP <code>DELETE</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("DELETE");
        doPost(req, resp);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Smaple of Person Table using JSP";
    }

    private static final boolean DEBUG = true;

    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }
}
