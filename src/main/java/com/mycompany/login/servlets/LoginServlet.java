package com.mycompany.login.servlets;

import com.mycompany.login.entidades.Usuario;
import com.mycompany.login.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
                    //  Usuario encontrado, autenticación exitosa
                    request.getSession().setAttribute("usuario", usuarioParam);
                    
                    response.setStatus(HttpServletResponse.SC_OK);  // Código HTTP 200 OK
                    out.write("{\"message\":\"Autenticación satisfactoria\"}");
                    out.flush();
                } 
            } catch (NoResultException ex) {
                // Usuario no encontrado, autenticación fallida
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // Código HTTP 401 Unauthorized
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