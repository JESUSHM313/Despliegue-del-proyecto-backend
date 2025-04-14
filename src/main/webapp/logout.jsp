<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="jakarta.servlet.http.Cookie"%>
<%
    // Invalidar sesión actual
    session.invalidate();

    // Eliminar la cookie JSESSIONID manualmente
    Cookie cookie = new Cookie("JSESSIONID", "");
    cookie.setPath("/");
    cookie.setMaxAge(0); // Expira inmediatamente
    cookie.setSecure(true);
    cookie.setHttpOnly(true);

    // 👉 Agrega SameSite=None (si estás en Servlet 6 o superior)
    response.setHeader("Set-Cookie", "JSESSIONID=; Path=/; Max-Age=0; Secure; HttpOnly; SameSite=None");

    response.addCookie(cookie);

    // Redirigir de vuelta al frontend React
    response.sendRedirect("https://react-login-6eit.onrender.com/");
%>
