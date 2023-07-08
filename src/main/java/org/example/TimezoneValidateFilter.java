package org.example;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req,
                            HttpServletResponse resp,
                            FilterChain chain) throws IOException, ServletException {

        String timeZone = req.getParameter("timezone");

        if (timeZone == null) {
            timeZone = "UTC";
        }

        try {
            ZoneId.of(timeZone.replace(" ", "+"));
            chain.doFilter(req, resp);
        } catch (DateTimeException e) {

            resp.setStatus(400);
            resp.setContentType("application/json");
            resp.getWriter().write("Invalid timezone");
            resp.getWriter().close();
        }
    }
}