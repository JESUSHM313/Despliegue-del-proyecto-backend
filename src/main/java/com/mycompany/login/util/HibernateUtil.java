package com.mycompany.login.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateUtil {

    private static final EntityManagerFactory emf;

    static {
        try {
            // Crea el EntityManagerFactory usando el nombre de la unidad de persistencia definida en persistence.xml
            emf = Persistence.createEntityManagerFactory("MiUnidadPersistencia");
        } catch (Exception e) {
            // Si falla la inicialización, se lanza un error en la inicialización de la clase
            throw new ExceptionInInitializerError(e);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public static void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
