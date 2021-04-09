package view;

import entity.BloodBank;
import entity.BloodDonation;
import entity.BloodGroup;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
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
 * @author Shariar (Shawn) Emami
 */
@WebServlet( name = "DonateBloodForm", urlPatterns = { "/DonateBloodForm" } )
public class DonateBloodForm extends HttpServlet {
    private String errorMessage = null;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        
        request.getRequestDispatcher( "/jsp/donateblood.jsp" ).forward( request, response );
    }

    private String toStringMap( Map<String, String[]> values ) {
        StringBuilder builder = new StringBuilder();
        values.forEach( ( k, v ) -> builder.append( "Key=" ).append( k )
                .append( ", " )
                .append( "Value/s=" ).append( Arrays.toString( v ) )
                .append( System.lineSeparator() ) );
        return builder.toString();
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
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "GET" );
        processRequest( request, response );
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
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "POST" );
        //you can add your logic here
        Map<String, String[]> map = request.getParameterMap();
        
        if (map.containsKey(BloodDonationLogic.BLOOD_GROUP)) {
            log("Blood group: " + map.get(BloodDonationLogic.BLOOD_GROUP)[0]);
        }
        
        if (map.containsKey(BloodDonationLogic.RHESUS_FACTOR)) {
            log("RHESUS_FACTOR: " + map.get(BloodDonationLogic.RHESUS_FACTOR)[0]);
        }
        
        if (map.containsKey(BloodDonationLogic.MILLILITERS)) {
            log("Milliliters: " + map.get(BloodDonationLogic.MILLILITERS)[0]);
        }
        
//        createBloodDonation(request);
        
//        if( map.containsKey( "view" ) ){
//            response.sendRedirect( "UsernameTableViewNormal" );
//        } else if( map.containsKey( "submit" ) ){
//            processRequest( request, response );
//        }
    }
    
    private void createBloodDonation(HttpServletRequest request) {
        
        // Dependency logic
        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");
        // Main logic
        BloodDonationLogic bdLogic = LogicFactory.getFor( "BloodDonation" );

        try {
            BloodDonation bloodDonation = bdLogic.createEntity( request.getParameterMap() );
            // Use given BankId to get the blood bank associated
            int bankId = Integer.parseInt(request.getParameterMap().get(BloodDonationLogic.BANK_ID)[0]);
            BloodBank bloodBank = bbLogic.getWithId(bankId);
            if (bloodBank == null) {
                throw new IllegalArgumentException("Bank ID not found");
            }
            bloodDonation.setBloodBank(bloodBank);
            bdLogic.add( bloodDonation );
        } catch( NumberFormatException ex ) {
            errorMessage = ex.getMessage(); // TODO: what to do with error
        }
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

    public void log( String msg ) {
        if( DEBUG ){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
            getServletContext().log( message );
        }
    }

    public void log( String msg, Throwable t ) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
        getServletContext().log( message, t );
    }
}
