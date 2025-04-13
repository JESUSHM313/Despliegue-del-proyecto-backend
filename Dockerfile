# Usa Tomcat oficial con JDK 17
FROM tomcat:10.1.36-jdk17

# Elimina las apps por defecto de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia tu archivo WAR dentro de la carpeta webapps
COPY target/login-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Exponer puerto para Tomcat
EXPOSE 8080
