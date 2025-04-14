package com.mycompany.login.servlets;

import com.mycompany.login.entidades.Usuario;
import com.mycompany.login.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configuración de respuesta JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String usuarioParam = request.getParameter("usuario");
        String contrasenaParam = request.getParameter("contraseña");

        EntityManager em = HibernateUtil.getEntityManager();

        try (PrintWriter out = response.getWriter()) {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.usuario = :usuario AND u.contrasena = :contrasena",
                    Usuario.class);
            query.setParameter("usuario", usuarioParam);
            query.setParameter("contrasena", contrasenaParam);

            try {
                Usuario usuario = query.getSingleResult();

                if (usuario != null) {
                    // Crear sesión
                    HttpSession session = request.getSession(true);
                    session.setAttribute("usuario", usuarioParam);

                    // ✅ Crear cookie con atributos compatibles para cross-site
                    String sessionId = session.getId();
                    String cookieString = "JSESSIONID=" + sessionId +
                            "; Path=/; Secure; HttpOnly; SameSite=None";
                    response.setHeader("Set-Cookie", cookieString);

                    // Respuesta exitosa
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"message\":\"Autenticación satisfactoria\"}");
                    out.flush();
                }
            } catch (NoResultException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\":\"Error en la autenticación\"}");
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
