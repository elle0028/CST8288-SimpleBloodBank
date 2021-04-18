package view;

import entity.BloodBank;
import entity.Person;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BloodBankLogic;
import logic.LogicFactory;
import logic.PersonLogic;

/**
 * CreateBloodBankJSP
 * @author Andrew O'Hara  adapted from code written by Matt Ellero
 */
@WebServlet(name = "CreateBloodBankJSP", urlPatterns = {"/CreateBloodBankJSP"})
public class CreateBloodBankJSP extends HttpServlet {

    private void fillTableData(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute("request", toStringMap(req.getParameterMap()));
        req.setAttribute("path", path);
        req.setAttribute("title", path.substring(1));
        req.getRequestDispatcher("/jsp/CreateTable-BloodBank.jsp").forward(req, resp);
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
        log( "POST" );
   
        BloodBankLogic bbLogic = LogicFactory.getFor("BloodBank");
        PersonLogic pLogic = LogicFactory.getFor( "Person" );

        // we cannot change the original map but we need to change the OWNER_ID 
        // value to be true or false, instead of Private or Public
        Map<String, String[]> copiedMap = new HashMap(request.getParameterMap());
        // change ownership parameter to proper true or false
        String ownershipInput = request.getParameterMap().get(BloodBankLogic.PRIVATELY_OWNED)[0];
        if (ownershipInput.equals("Private")) {
            copiedMap.put(BloodBankLogic.PRIVATELY_OWNED, new String[]{"true"});
        }
        else {
            copiedMap.put(BloodBankLogic.PRIVATELY_OWNED, new String[]{"false"});
        }            
        
        try {
            // send the copiedMap with the true or false value for OWNER_ID
            BloodBank bloodbank = bbLogic.createEntity( copiedMap );
            
            // attach owner if necessary
            if(bloodbank.getPrivatelyOwned() == true) {
                int personId = Integer.parseInt(request.getParameterMap().get(BloodBankLogic.OWNER_ID)[0]); 
                Person owner = pLogic.getWithId(personId);
                if (owner != null) {
                    bloodbank.setOwner(owner);
                }
                else {
                    // TODO: maybe just log and dont create instead of throwing exception? 
                    throw new IllegalArgumentException("Ownership true but owner id is invalid");
                }
            }
            
            bbLogic.add( bloodbank );
        } catch( NumberFormatException ex ) {
            log("Error creating BloodBank: \n", ex);        }
        
        if( request.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            response.sendRedirect( "BloodBankTableJSP" );
        } else if( request.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect( "BloodBankTable" );
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("GET");        
        
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