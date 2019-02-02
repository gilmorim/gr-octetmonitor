import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class InterfacesDataServlet extends HttpServlet {
    private final ArrayList<InterfaceInformation> interfaces;

    public InterfacesDataServlet(ArrayList<InterfaceInformation> interfaces){ this.interfaces = interfaces;}
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder information = new StringBuilder();
        for(InterfaceInformation intf : interfaces)
            information.append(intf.toString());

        resp.setContentType("text/plain");
        resp.getWriter().println(information.toString());
    }
}