package view;

import entity.BloodBank;
import entity.BloodDonation;
import entity.DonationRecord;
import entity.Person;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
 * @author Shariar (Shawn) Emami, Matthew Ellero
 */
@WebServlet(name = "DonateBloodForm", urlPatterns = {"/DonateBloodForm"})
public class DonateBloodForm extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/jsp/donateblood.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");

        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");
        List<BloodBank> banks = bbLogic.getAll();

        request.setAttribute("banks", banks);
        request.getRequestDispatcher("/jsp/donateblood.jsp").forward(request, response);
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        Map<String, String[]> map = request.getParameterMap();

        try {
            Person person = createPerson(request);
            BloodBank bloodBank = getBloodBank(request);
            BloodDonation bloodDonation = createBloodDonation(request, bloodBank);
            createDonationRecord(request, person, bloodDonation);
        } catch (IllegalArgumentException e) {
            log("Error Creating Donation: \n", e);
        }

        if (map.containsKey("view")) {
            response.sendRedirect("PersonTable");
        } else if (map.containsKey("submit")) {
            processRequest(request, response);
        }
    }

    private void createDonationRecord(HttpServletRequest request, Person person, BloodDonation bloodDonation) {
        // Main logic
        DonationRecordLogic drLogic = LogicFactory.getFor("DonationRecord");
        try {
            DonationRecord donation_record = drLogic.createEntity(request.getParameterMap());
            donation_record.setPerson(person);
            donation_record.setBloodDonation(bloodDonation);
            drLogic.add(donation_record);
        } catch (IllegalArgumentException ex) {
            log("Error Creating Donation Record: \n", ex);
        }
    }

    private Person createPerson(HttpServletRequest request) {
        PersonLogic pLogic = LogicFactory.getFor("Person");
        Person person = null;
        try {
            person = pLogic.createEntity(request.getParameterMap());
            pLogic.add(person);
        } catch (IllegalArgumentException ex ) {
            log("Error Creating Person: \n", ex);
        }
        return person;
    }

    private BloodDonation createBloodDonation(HttpServletRequest request, BloodBank bloodBank) {
        // Main logic
        BloodDonationLogic bdLogic = LogicFactory.getFor("BloodDonation");
        BloodDonation bloodDonation = null;
        try {
            bloodDonation = bdLogic.createEntity(request.getParameterMap());
            bloodDonation.setBloodBank(bloodBank);
            bdLogic.add(bloodDonation);
        } catch (NumberFormatException ex) {
            log("Error Creating Blood Donation \n", ex);
        }
        return bloodDonation;
    }

    private BloodBank getBloodBank(HttpServletRequest request) {
        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");
        String name = request.getParameter(BloodBankLogic.NAME);

        BloodBank bloodbank = bbLogic.getBloodBankWithName(name);
        if (bloodbank == null) {
            throw new IllegalArgumentException("Selected BloodBank does not exist!");
        }
        return bloodbank;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Sample page of multiple Eelements";
    }

    private static final boolean DEBUG = true;

    @Override
    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    @Override
    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }
}
