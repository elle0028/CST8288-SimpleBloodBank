package view;

import entity.Person;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import logic.LogicFactory;
import logic.PersonLogic;

/**
 *
 * @author Fargol Azimi
 */
@WebServlet(name = "PersoneInsertJSP", urlPatterns = {"/CreatePersonJSP"})

public class CreatePersonJSP extends HttpServlet {

    private void fillTableData(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute("request", toStringMap(req.getParameterMap()));
        req.setAttribute("path", path);
        req.setAttribute("title", path.substring(1));
        req.getRequestDispatcher("/jsp/CreateTable-Person.jsp").forward(req, resp);

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
        
        PersonLogic pLogic = LogicFactory.getFor( "Person" );
        String firstName = request.getParameter( PersonLogic.LAST_NAME );
     //   if( pLogic.getPersonWithLastName( firstName ).isEmpty()){
            try {
                Person person = pLogic.createEntity( request.getParameterMap() );
                pLogic.add( person );
            } catch( Exception ex ) {
                ex.printStackTrace();
            }
//        } else {
//            //if duplicate print the error message
//            errorMessage = "First Name: \"" + firstName + "\" already exists";
//        }
        if( request.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            fillTableData( request, response );
        } else if( request.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect( "PersonTable" );
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
