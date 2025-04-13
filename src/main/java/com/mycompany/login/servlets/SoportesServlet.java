package com.mycompany.login.servlets;

import com.mycompany.login.entidades.Soporte;
import com.mycompany.login.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "SoportesServlet", urlPatterns = {"/soportes"})
public class SoportesServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        String mensaje = "";
        boolean isPostmanRequest = "application/json".equals(request.getHeader("Accept"));
        
        EntityManager em = HibernateUtil.getEntityManager();
        
        try {
            em.getTransaction().begin();
            
            if ("registrar".equals(accion)) {
                String descripcion = request.getParameter("descripcion");
                Soporte soporte = new Soporte(descripcion);
                em.persist(soporte);
                mensaje = "Soporte registrado exitosamente.";
                
            } else if ("modificar".equals(accion)) {
                Integer id = Integer.parseInt(request.getParameter("id"));
                Soporte soporte = em.find(Soporte.class, id);
                if (soporte != null) {
                    soporte.setDescripcion(request.getParameter("descripcion"));
                    em.merge(soporte);
                    mensaje = "Soporte modificado exitosamente.";
                } else {
                    mensaje = "Soporte no encontrado.";
                }
                
            } else if ("eliminar".equals(accion)) {
                Integer id = Integer.parseInt(request.getParameter("id"));
                Soporte soporte = em.find(Soporte.class, id);
                if (soporte != null) {
                    em.remove(soporte);
                    mensaje = "Soporte eliminado exitosamente.";
                } else {
                    mensaje = "Soporte no encontrado.";
                }
            }
            
            em.getTransaction().commit();
            
            if (isPostmanRequest) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"message\":\"" + mensaje + "\"}");
                    out.flush();
                }
            } else {
                response.sendRedirect("panel.jsp?mensaje=" + mensaje);
            }
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            mensaje = "Error en la operación: " + e.getMessage();
            
            if (isPostmanRequest) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.write("{\"message\":\"" + mensaje + "\"}");
                    out.flush();
                }
            } else {
                response.sendRedirect("panel.jsp?mensaje=" + mensaje);
            }
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    // ✅ Método GET para listar soportes desde Postman o navegador
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        boolean isPostmanRequest = "application/json".equals(request.getHeader("Accept"));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        EntityManager em = HibernateUtil.getEntityManager();
        
        try (PrintWriter out = response.getWriter()) {
            List<Soporte> soportes = em.createQuery("SELECT s FROM Soporte s", Soporte.class).getResultList();
            
            StringBuilder json = new StringBuilder();
            json.append("[");
            
            for (int i = 0; i < soportes.size(); i++) {
                Soporte soporte = soportes.get(i);
                json.append("{")
                    .append("\"id\":").append(soporte.getId()).append(",")
                    .append("\"descripcion\":\"").append(soporte.getDescripcion()).append("\"")
                    .append("}");
                
                if (i < soportes.size() - 1) {
                    json.append(",");
                }
            }
            
            json.append("]");
            out.write(json.toString());
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.write("{\"message\":\"Error al recuperar soportes: " + e.getMessage() + "\"}");
                out.flush();
            }
        } finally {
            em.close();
        }
    }
}
