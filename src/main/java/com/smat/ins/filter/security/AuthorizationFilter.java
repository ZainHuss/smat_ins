package com.smat.ins.filter.security;

import java.io.IOException;
import javax.faces.application.ResourceHandler;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
@WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.REQUEST, DispatcherType.FORWARD })
public class AuthorizationFilter implements Filter {

    private static final String AJAX_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<partial-response><redirect url=\"%s\"></redirect></partial-response>";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        // Check if the request is for the excluded servlet path
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/api/equipment-cert") || requestURI.contains("/api/emp-cert")) {
            chain.doFilter(request, response); // Skip authentication for this path
            return;
        }

        try {
            HttpSession session = request.getSession(false);
            String loginURL = request.getServerName() + ":" + request.getServerPort() + "/smat-ins/auth";
            String prettyLoginURL = "/smat-ins/auth";
            boolean loggedIn = (session != null) && (session.getAttribute("user") != null);
            boolean loginRequest = request.getRequestURI().contains("login");
            boolean resourceRequest = request.getRequestURI()
                    .startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER + "/");
            boolean ajaxRequest = "partial/ajax".equals(request.getHeader("Faces-Request"));

            if (loggedIn || loginRequest || resourceRequest) {
                if (!resourceRequest) {
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
                    response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
                    response.setDateHeader("Expires", 0); // Proxies.
                }

                chain.doFilter(request, response); // So, just continue request.
            } else if (ajaxRequest) {
                response.setContentType("text/xml");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().printf(AJAX_REDIRECT_XML, loginURL); // So, return special XML response instructing JSF ajax to send a redirect.
            }  else {
                response.sendRedirect(prettyLoginURL);
            }
        } catch (Exception e) {
            // TODO: handle exception
            request.getSession().setAttribute("lastException", e);
            request.getSession().setAttribute("lastExceptionUniqueId", e.hashCode());

            if (!isAjax(request)) {
                response.sendRedirect(request.getContextPath() + request.getServletPath() + "/error");
            } else {
                // let's leverage jsf2 partial response
                response.getWriter().print(xmlPartialRedirectToPage(request, "/error"));
                response.flushBuffer();
            }
        }
    }

    private String xmlPartialRedirectToPage(HttpServletRequest request, String page) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<partial-response><redirect url=\"").append(request.getContextPath())
                .append(request.getServletPath()).append(page).append("\"/></partial-response>");
        return sb.toString();
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
    }
}