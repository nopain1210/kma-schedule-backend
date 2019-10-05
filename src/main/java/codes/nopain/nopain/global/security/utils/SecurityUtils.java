package codes.nopain.nopain.global.security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class SecurityUtils {
    public static boolean requiresAdministratorPermission(HttpServletRequest req) {
        String pattern = ".*/admin/.*";

        return req.getRequestURI().matches(pattern);
    }

    public static boolean isAdministrator(HttpServletRequest req) {
        final String ADMIN_GROUP = "Administrator";
        try {
            String accessToken = req.getHeader("Authorization").replace("Bearer ", "").trim();
            DecodedJWT jwt = JWT.decode(accessToken);
            Claim groups = jwt.getClaim("groups");
            List<String> groupList = groups.asList(String.class);

            for (String group : groupList) {
                if (group.equals(ADMIN_GROUP)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
