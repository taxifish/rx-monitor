package org.resha.rx.rxmonitor;

import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

public class ConfigServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/javascript");
        resp.setStatus(HttpStatus.OK_200);
        resp.getWriter().println(scriptContent());
    }

    private String scriptContent() {
        StringBuilder builder = new StringBuilder();
        builder.append("var App = {};\n\n");

        Formatter f = new Formatter(builder, Locale.US);

        for (String item : System.getProperties().stringPropertyNames()) {
            if (item.startsWith("App.")) {
                f.format("%s = '%s';\n", item, System.getProperty(item));
            }
        }

        return builder.toString();
    }
}
