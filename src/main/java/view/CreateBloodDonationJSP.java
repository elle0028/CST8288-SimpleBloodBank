package view;

import entity.BloodBank;
import entity.BloodDonation;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BloodBankLogic;
import logic.BloodDonationLogic;

import logic.LogicFactory;

/**
 *
 * @author Matthew Ellero
 */
@WebServlet(name = "CreateBloodDonationJSP", urlPatterns = {"/CreateBloodDonationJSP"})
public class CreateBloodDonationJSP extends HttpServlet {

    private void fillTableData(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute("request", toStringMap(req.getParameterMap()));
        req.setAttribute("path", path);
        req.setAttribute("title", path.substring(1));
        req.getRequestDispatcher("/jsp/CreateTable-BloodDonation.jsp").forward(req, resp);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log( "POST" );
        // Dependency logic
        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");

        // Main logic
        BloodDonationLogic bdLogic = LogicFactory.getFor( "BloodDonation" );

        try {
            BloodDonation bloodDonation = bdLogic.createEntity( request.getParameterMap() );
            // Verify Dependency
            String bankName = request.getParameter(BloodBankLogic.NAME);
            BloodBank bloodBank = bbLogic.getBloodBankWithName(bankName);
            if (bloodBank == null) {
                throw new IllegalArgumentException("Selected BloodBank does not exist!");
            }
            bloodDonation.setBloodBank(bloodBank);
            bdLogic.add( bloodDonation );
        } catch( NumberFormatException ex ) {
            log("Error creating BloodDonation: \n", ex);
        }
        
        if( request.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            response.sendRedirect( "BloodDonationTableJSP" );
        } else if( request.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect( "BloodDonationTable" );
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("GET");
        
        // Fetch and populate all the bloodbanks, to select for the creation of the new BloodDonation
        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");
        List<BloodBank> banks = bbLogic.getAll();

        req.setAttribute("banks", banks);
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
