/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.login.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "InventarioServlet", urlPatterns = {"/inventario"})
public class InventarioServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        String mensaje = "";
        boolean isPostmanRequest = "application/json".equals(request.getHeader("Accept"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(InventarioServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String url = "jdbc:mysql://localhost:10000/servletlogin?useSSL=false&allowPublicKeyRetrieval=true";
        
        try (Connection conexion = DriverManager.getConnection(url, "root", "admin123")) {
            if ("agregar".equals(accion)) {
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                String sql = "INSERT INTO inventario (nombre, descripcion) VALUES (?, ?)";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, nombre);
                ps.setString(2, descripcion);
                int filas = ps.executeUpdate();
                mensaje = filas > 0 ? "Ítem agregado exitosamente." : "Error al agregar ítem.";
                
            } else if ("modificar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                String sql = "UPDATE inventario SET nombre = ?, descripcion = ? WHERE id = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, nombre);
                ps.setString(2, descripcion);
                ps.setInt(3, id);
                int filas = ps.executeUpdate();
                mensaje = filas > 0 ? "Ítem modificado exitosamente." : "Error al modificar ítem.";
                
            } else if ("eliminar".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String sql = "DELETE FROM inventario WHERE id = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setInt(1, id);
                int filas = ps.executeUpdate();
                mensaje = filas > 0 ? "Ítem eliminado exitosamente." : "Error al eliminar ítem.";
            }
            
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
            
        } catch (SQLException ex) {
            Logger.getLogger(InventarioServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
