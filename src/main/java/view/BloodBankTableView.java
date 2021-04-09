package view;

import entity.BloodBank;
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
import logic.Logic;
import logic.LogicFactory;

/**
 *
 * @author dynonomous
 */
@WebServlet(name = "BloodBankTableView", urlPatterns = {"/BloodBankTable"})
public class BloodBankTableView extends HttpServlet {

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
        response.setContentType( "text/html;charset=UTF-8" );
        try( PrintWriter out = response.getWriter() ) {
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>BloodBankViewNormal</title>" );
            out.println( "</head>" );
            out.println( "<body>" );

            out.println( "<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">" );
            out.println( "<caption>BloodBank</caption>" );
            //this is an example, for your other tables use getColumnNames from
            //logic to create headers in a loop.
            
            

//            AccountLogic logic = LogicFactory.getFor( "BloodBank" );
            Logic<BloodBank> logic = LogicFactory.getFor( "BloodBank" );
            out.println( "<tr>" );
            logic.getColumnNames().forEach( c -> out.printf( "<th>%s</th>", c ) );
//            out.println( "<th>EmployeeCount</th>" );
//            out.println( "<th>Established</th>" );
//            out.println( "<th>PrivatelyOwned</th>" );
//            out.println( "<th>Name</th>" );
//            out.println( "<th>Owner</th>" );
            out.println( "</tr>" );

//            List<Account> entities = logic.getAll();
//            for( Account e: entities ) {
//                //for other tables replace the code bellow with
//                //extractDataAsList in a loop to fill the data.
//                out.printf( "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
//                        logic.extractDataAsList( e ).toArray() );
//            }
            logic.getAll().forEach( e -> out.printf( "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                    logic.extractDataAsList( e ).toArray() ) );

            out.println( "<tr>" );
            //this is an example, for your other tables use getColumnNames from
            //logic to create headers in a loop.
            logic.getColumnNames().forEach( c -> out.printf( "<th>%s</th>", c ) );
//            out.println( "<th>EmployeeCount</th>" );
//            out.println( "<th>Established</th>" );
//            out.println( "<th>PrivatelyOwned</th>" );
//            out.println( "<th>Name</th>" );
//            out.println( "<th>Owner</th>" );
            out.println( "</tr>" );
            out.println( "</table>" );
            out.printf( "<div style=\"text-align: center;\"><pre>%s</pre></div>", toStringMap( request.getParameterMap() ) );
            out.println( "</body>" );
            out.println( "</html>" );
        }
    }
    
    private String toStringMap( Map<String, String[]> m ) {
        StringBuilder builder = new StringBuilder();
        for( String k: m.keySet() ) {
            builder.append( "Key=" ).append( k )
                    .append( ", " )
                    .append( "Value/s=" ).append( Arrays.toString( m.get( k ) ) )
                    .append( System.lineSeparator() );
        }
        return builder.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        log( "GET" );
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
        BloodBankLogic logic = LogicFactory.getFor( "BloodBank" );
        BloodBank bloodbank = logic.updateEntity( request.getParameterMap() );
        logic.update( bloodbank );       
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "BloodBank Table View";
    }// </editor-fold>
    
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
