package edu.connexion3a8.tools;

import org.junit.jupiter.api.*;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour MyConnection.
 * Verifie la connexion a la base de donnees MySQL.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyConnectionTest {

    @Test
    @Order(1)
    void testConnexionNonNull() {
        MyConnection mc = new MyConnection();
        Connection cnx = mc.getCnx();
        // Si MySQL est demarre, la connexion ne doit pas etre null
        // Si MySQL n'est pas demarre, ce test echouera (comportement attendu)
        assertNotNull(cnx, "La connexion ne doit pas etre null (MySQL doit etre demarre)");
    }

    @Test
    @Order(2)
    void testConnexionOuverte() throws Exception {
        MyConnection mc = new MyConnection();
        Connection cnx = mc.getCnx();
        assertNotNull(cnx);
        assertFalse(cnx.isClosed(), "La connexion doit etre ouverte");
    }

    @Test
    @Order(3)
    void testConnexionValide() throws Exception {
        MyConnection mc = new MyConnection();
        Connection cnx = mc.getCnx();
        assertNotNull(cnx);
        assertTrue(cnx.isValid(5), "La connexion doit etre valide (timeout 5s)");
    }

    @Test
    @Order(4)
    void testPlusieursInstances() {
        MyConnection mc1 = new MyConnection();
        MyConnection mc2 = new MyConnection();
        // Chaque instance cree sa propre connexion
        assertNotNull(mc1.getCnx());
        assertNotNull(mc2.getCnx());
        // Ce sont des connexions differentes
        assertNotSame(mc1.getCnx(), mc2.getCnx());
    }
}
