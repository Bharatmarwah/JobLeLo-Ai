package in.joblelo.JobAgentBackend.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import in.joblelo.JobAgentBackend.exceptionhandling.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${cookie.secure:false}")
    private boolean secure;

    public void setCookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie("refresh-token",token);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setAttribute("SameSite", secure ? "None" : "Lax");

        response.addCookie(cookie);
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            throw new ApiException("No cookies found", HttpStatus.BAD_REQUEST);
        }

        for (Cookie cookie : cookies) {

            if ("refresh-token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new ApiException("Refresh token cookie not found", HttpStatus.BAD_REQUEST);
    }



}
