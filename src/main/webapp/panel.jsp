<%-- 
    Document   : panel
    Created on : 5/03/2025, 11:06:07 p. m.
    Author     : usuario
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" 
    import="java.sql.*, java.util.*" %>
<%
    // Verificar sesión
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect("https://react-login-6eit.onrender.com");
        return;
    }
    
    // Recuperar mensaje de resultado (si existe)
    String mensaje = request.getParameter("mensaje");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>JM TUTOS - Panel</title>
        <style>
            /* Paleta de colores */
            :root {
                --azul-oscuro: #11174D;
                --rojo: #C81010;
                --azul-clarito: #04BEFF;
                --amarillo: #FFA101;
                --verde: #1D9522;
            }
            body {
                margin: 0;
                font-family: Arial, sans-serif;
                background-color: #f4f4f4;
            }
            /* Barra lateral */
            .sidebar {
                width: 250px;
                background-color: var(--azul-oscuro);
                color: #fff;
                height: 100vh;
                position: fixed;
                padding: 20px;
            }
            .sidebar nav a {
                display: block;
                color: #fff;
                text-decoration: none;
                padding: 10px;
                margin-bottom: 10px;
                border-radius: 3px;
                transition: background-color 0.3s;
                cursor: pointer;
            }
            .sidebar nav a:hover {
                background-color: var(--rojo);
            }
            /* Área de contenido */
            .content {
                margin-left: 270px;
                padding: 20px;
                min-height: 100vh;
            }
            /* Botón de cerrar sesión */
            .logout {
                position: fixed;
                top: 20px;
                right: 20px;
                background-color: var(--rojo);
                color: #fff;
                border: none;
                padding: 10px 15px;
                border-radius: 3px;
                cursor: pointer;
                transition: background-color 0.3s;
            }
            .logout:hover {
                background-color: var(--amarillo);
            }
            /* Secciones */
            .section {
                background-color: #fff;
                border: 1px solid #ccc;
                border-radius: 5px;
                padding: 15px;
                margin-bottom: 20px;
                display: none;
            }
            .section.active {
                display: block;
            }
            .section h3 {
                color: var(--azul-oscuro);
            }
            /* Grupo de botones */
            .button-group {
                margin-top: 15px;
            }
            .btn {
                padding: 10px 15px;
                margin-right: 10px;
                margin-bottom: 10px;
                border: none;
                border-radius: 3px;
                background-color: var(--azul-clarito);
                color: #fff;
                cursor: pointer;
                transition: background-color 0.3s;
            }
            .btn:hover {
                background-color: var(--verde);
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 15px;
            }
            table, th, td {
                border: 1px solid #ccc;
            }
            th, td {
                padding: 8px;
                text-align: left;
            }
        </style>
    </head>
    <body>
        <%-- Botón para cerrar sesión --%>
        <button class="logout" onclick="location.href='logout.jsp'">Cerrar Sesión</button>
        
        <%-- Mostrar mensaje de operación si existe --%>
        <%
            if(mensaje != null){
        %>
            <p style="color: blue; text-align: center;"><%= mensaje %></p>
        <%
            }
        %>
        <div class="sidebar">
            <nav>
                <a onclick="showSection('inventario')">Inventario</a>
                <a onclick="showSection('soportes')">Soportes</a>
            </nav>
        </div>
        <div class="content">
            <!-- Sección Inventario -->
            <div id="inventario" class="section active">
                <h3>Interfaz de Inventario</h3>
                <p>Gestione su inventario aquí.</p>
                
                <!-- Formulario para agregar ítem -->
                <form action="inventario" method="post">
                    <input type="hidden" name="accion" value="agregar">
                    <input type="text" name="nombre" placeholder="Nombre del ítem" required>
                    <input type="text" name="descripcion" placeholder="Descripción" required>
                    <input type="submit" value="Agregar Ítem">
                </form>
                <br>
                <!-- Formulario para modificar ítem -->
                <form action="inventario" method="post">
                    <input type="hidden" name="accion" value="modificar">
                    <input type="number" name="id" placeholder="ID del ítem" required>
                    <input type="text" name="nombre" placeholder="Nuevo nombre" required>
                    <input type="text" name="descripcion" placeholder="Nueva descripción" required>
                    <input type="submit" value="Modificar Ítem">
                </form>
                <br>
                <!-- Formulario para eliminar ítem -->
                <form action="inventario" method="post">
                    <input type="hidden" name="accion" value="eliminar">
                    <input type="number" name="id" placeholder="ID del ítem" required>
                    <input type="submit" value="Eliminar Ítem">
                </form>
                
                <!-- Listado de Inventario -->
                <%
                    Connection connInv = null;
                    Statement stmtInv = null;
                    ResultSet rsInv = null;
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        String dbUrl = "jdbc:mysql://localhost:10000/servletlogin?useSSL=false&allowPublicKeyRetrieval=true";
                        connInv = DriverManager.getConnection(dbUrl, "root", "admin123");
                        stmtInv = connInv.createStatement();
                        rsInv = stmtInv.executeQuery("SELECT * FROM inventario");
                %>
                        <h4>Listado de Inventario:</h4>
                        <table>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Descripción</th>
                            </tr>
                            <%
                                while(rsInv.next()){
                            %>
                            <tr>
                                <td><%= rsInv.getInt("id") %></td>
                                <td><%= rsInv.getString("nombre") %></td>
                                <td><%= rsInv.getString("descripcion") %></td>
                            </tr>
                            <%
                                }
                            %>
                        </table>
                <%
                    } catch(Exception e){
                        out.println("Error al cargar inventario: " + e.getMessage());
                    } finally {
                        if(rsInv != null) try { rsInv.close(); } catch(Exception e){}
                        if(stmtInv != null) try { stmtInv.close(); } catch(Exception e){}
                        if(connInv != null) try { connInv.close(); } catch(Exception e){}
                    }
                %>
            </div>
            
            <!-- Sección Soportes -->
            <div id="soportes" class="section">
                <h3>Interfaz de Soportes</h3>
                <p>Acceso a soportes técnicos.</p>
                
                <!-- Formulario para registrar soporte -->
                <form action="soportes" method="post">
                    <input type="hidden" name="accion" value="registrar">
                    <input type="text" name="descripcion" placeholder="Descripción del soporte" required>
                    <input type="submit" value="Registrar Soporte">
                </form>
                <br>
                <!-- Formulario para modificar soporte -->
                <form action="soportes" method="post">
                    <input type="hidden" name="accion" value="modificar">
                    <input type="number" name="id" placeholder="ID del soporte" required>
                    <input type="text" name="descripcion" placeholder="Nueva descripción" required>
                    <input type="submit" value="Modificar Soporte">
                </form>
                <br>
                <!-- Formulario para eliminar soporte -->
                <form action="soportes" method="post">
                    <input type="hidden" name="accion" value="eliminar">
                    <input type="number" name="id" placeholder="ID del soporte" required>
                    <input type="submit" value="Eliminar Soporte">
                </form>
                
                <!-- Listado de Soportes -->
                <%
                    Connection connSoporte = null;
                    Statement stmtSoporte = null;
                    ResultSet rsSoporte = null;
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        String dbUrl = "jdbc:mysql://localhost:10000/servletlogin?useSSL=false&allowPublicKeyRetrieval=true";
                        connSoporte = DriverManager.getConnection(dbUrl, "root", "admin123");
                        stmtSoporte = connSoporte.createStatement();
                        rsSoporte = stmtSoporte.executeQuery("SELECT * FROM soportes");
                %>
                        <h4>Listado de Soportes:</h4>
                        <table>
                            <tr>
                                <th>ID</th>
                                <th>Descripción</th>
                            </tr>
                            <%
                                while(rsSoporte.next()){
                            %>
                            <tr>
                                <td><%= rsSoporte.getInt("id") %></td>
                                <td><%= rsSoporte.getString("descripcion") %></td>
                            </tr>
                            <%
                                }
                            %>
                        </table>
                <%
                    } catch(Exception e){
                        out.println("Error al cargar soportes: " + e.getMessage());
                    } finally {
                        if(rsSoporte != null) try { rsSoporte.close(); } catch(Exception e){}
                        if(stmtSoporte != null) try { stmtSoporte.close(); } catch(Exception e){}
                        if(connSoporte != null) try { connSoporte.close(); } catch(Exception e){}
                    }
                %>
            </div>
        </div>
        <script>
            // Función para mostrar la sección seleccionada
            function showSection(sectionId) {
                var sections = document.querySelectorAll(".section");
                sections.forEach(function(section) {
                    section.classList.remove("active");
                });
                document.getElementById(sectionId).classList.add("active");
            }
        </script>
    </body>
</html>
