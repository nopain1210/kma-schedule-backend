package codes.nopain.nopain.global.security.filter;

import codes.nopain.nopain.global.security.utils.SecurityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AdministratorRoleFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        if (SecurityUtils.requiresAdministratorPermission(req) && (!SecurityUtils.isAdministrator(req))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        } else {
            filterChain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {

    }
}
