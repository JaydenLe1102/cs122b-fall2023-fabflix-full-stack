package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletUtils {

	public static String getBaseUrl(HttpServletRequest request) {
		String scheme = request.getScheme();
		String host = request.getServerName();
		int port = request.getServerPort();
		String contextPath = request.getContextPath();

		String baseUrl = scheme + "://" + host
				+ ((("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443)) ? "" : ":" + port)
				+ contextPath;
		return baseUrl;
	}

	public static Boolean checkLogin(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (request.getSession().getAttribute("customerId") == null) {
				response.sendRedirect(getBaseUrl(request) + "/loginForm.html");
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Boolean checkLoginEmployee(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (request.getSession().getAttribute("employeeEmail") == null) {
				response.sendRedirect(getBaseUrl(request) + "/loginForm.html");
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
