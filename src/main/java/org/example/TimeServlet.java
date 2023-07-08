package org.example;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine engine;
    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("templates\\");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String timezoneParameterName = "timezone";
        final String defaultTimezone = "UTC";

        Cookie[] cookies = request.getCookies();

        Cookie requestCookie = null;

        if(cookies != null){
            requestCookie = Arrays.stream(cookies)
                    .filter(cookie->cookie.getName().equals(timezoneParameterName))
                    .findFirst()
                    .get();
        }

        String requestTimezoneParameter = request.getParameter(timezoneParameterName);

        String outputTimezone = "";

        if(requestCookie == null && requestTimezoneParameter == null){
            outputTimezone = defaultTimezone;
        }
        else if(requestCookie == null && requestTimezoneParameter != null){
            outputTimezone = requestTimezoneParameter;
        }
        else if(requestCookie != null && requestTimezoneParameter == null){
            outputTimezone = requestCookie.getValue();
        }
        else{
            outputTimezone = requestTimezoneParameter;
        }

        outputTimezone = outputTimezone.replace(" ", "+");

        response.addCookie(new Cookie(timezoneParameterName, outputTimezone));

        response.setContentType("text/html");

        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(outputTimezone));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        String formattedTime = currentTime.format(formatter);

        Context simpleContext = new Context(request.getLocale(), Map.of("time", formattedTime));

        engine.process("time", simpleContext, response.getWriter());
        response.getWriter().close();
    }
}
