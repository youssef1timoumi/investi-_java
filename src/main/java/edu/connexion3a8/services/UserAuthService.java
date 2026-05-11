package edu.connexion3a8.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import edu.connexion3a8.entities.User;
import edu.connexion3a8.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Service for user authentication and authorization checks.
 *
 * <p>Password hashing is bcrypt (PHP's {@code password_hash(..., PASSWORD_BCRYPT)}
 * format, prefix {@code $2y$} or {@code $2a$}) so hashes round-trip between
 * Symfony and Java without re-encoding. The {@link at.favre.lib.crypto.bcrypt.BCrypt}
 * library is used because it tolerates the {@code $2y$} prefix PHP emits,
 * unlike the older jbcrypt.
 */
public class UserAuthService {

    /** Cost factor matches the range PHP's PASSWORD_DEFAULT emits (10-12 typical). */
    private static final int BCRYPT_COST = 12;

    /**
     * Verify a plaintext password against a stored hash.
     *
     * <p>Supports bcrypt hashes produced by PHP {@code password_hash()} (prefixes
     * {@code $2y$}, {@code $2a$}, {@code $2b$}) and falls back to plaintext
     * equality for legacy records where the stored value is the raw password
     * (to be rehashed on login via {@link #upgradeIfLegacy(String, String, String)}).
     *
     * @param plain      the user-supplied password
     * @param storedHash the value currently stored in {@code users.password_hash}
     * @return true if the password matches the stored hash
     */
    public static boolean verifyPassword(String plain, String storedHash) {
        if (plain == null || storedHash == null) {
            return false;
        }
        if (storedHash.length() >= 4 && storedHash.charAt(0) == '$' && storedHash.charAt(1) == '2') {
            // bcrypt hash — use the modern verifier which handles $2y$ / $2a$ / $2b$
            return BCrypt.verifyer().verify(plain.toCharArray(), storedHash).verified;
        }
        // Legacy plaintext record — compare directly. Caller SHOULD rehash on success.
        return plain.equals(storedHash);
    }

    /**
     * Produce a fresh bcrypt hash at the canonical cost (cost 12, {@code $2a$} prefix).
     * PHP's {@code password_verify()} accepts both {@code $2a$} and {@code $2y$}, so
     * hashes produced here are interoperable with the Symfony side.
     */
    public static String hashPassword(String plain) {
        if (plain == null) {
            throw new IllegalArgumentException("plain password must not be null");
        }
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plain.toCharArray());
    }

    /**
     * If the stored hash was a legacy plaintext record and the password just
     * verified successfully, upgrade the DB column to a bcrypt hash. Safe to
     * call on every login; does nothing when the stored value is already bcrypt.
     *
     * @param userId     the user id to update
     * @param plain      the plaintext password that just verified
     * @param storedHash the current stored value (to decide whether upgrade is needed)
     * @return true if a rehash was applied
     */
    public static boolean upgradeIfLegacy(String userId, String plain, String storedHash) throws SQLException {
        if (storedHash == null || storedHash.length() < 4 || storedHash.charAt(0) != '$' || storedHash.charAt(1) != '2') {
            String newHash = hashPassword(plain);
            String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
            try (Connection conn = MyConnection.getInstance().getCnx();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newHash);
                ps.setString(2, userId);
                ps.executeUpdate();
            }
            return true;
        }
        return false;
    }

    /**
     * Replace the stored password hash for the given user. Used by the
     * profile page when the user changes their password from inside the app.
     * Expects {@code newHash} to already be a bcrypt hash — callers should
     * pass the result of {@link #hashPassword(String)}.
     */
    public static void updateStoredHash(String userId, String newHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, userId);
            ps.executeUpdate();
        }
    }

    /**
     * Check if a user is verified (active and email verified)
     * @param userId The user ID to check
     * @return true if user is verified, false otherwise
     */
    public static boolean isUserVerified(String userId) throws SQLException {
        String query = "SELECT is_active, email_verified FROM users WHERE id = ?";
        
        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                boolean isActive = rs.getBoolean("is_active");
                boolean emailVerified = rs.getBoolean("email_verified");
                return isActive && emailVerified;
            }
            
            return false;
        }
    }

    /**
     * Check if a user can access the forum (investor or innovator role)
     * @param userId The user ID to check
     * @return true if user can access forum, false otherwise
     */
    public static boolean canAccessForum(String userId) throws SQLException {
        String query = "SELECT role FROM users WHERE id = ?";
        
        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                return "investor".equalsIgnoreCase(role) || 
                       "innovator".equalsIgnoreCase(role) ||
                       "admin".equalsIgnoreCase(role);
            }
            
            return false;
        }
    }

    /**
     * Check if a user can perform write operations (post, comment, vote)
     * User must be verified AND have appropriate role
     * @param userId The user ID to check
     * @return true if user can perform write operations, false otherwise
     */
    public static boolean canPerformWriteOperations(String userId) throws SQLException {
        String query = "SELECT role, is_active, email_verified FROM users WHERE id = ?";
        
        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");
                boolean emailVerified = rs.getBoolean("email_verified");
                
                boolean hasValidRole = "investor".equalsIgnoreCase(role) || 
                                      "innovator".equalsIgnoreCase(role) ||
                                      "admin".equalsIgnoreCase(role);
                
                return hasValidRole && isActive && emailVerified;
            }
            
            return false;
        }
    }

    /**
     * Get user verification status with detailed information
     * @param userId The user ID to check
     * @return UserVerificationStatus object with detailed status
     */
    public static UserVerificationStatus getUserVerificationStatus(String userId) throws SQLException {
        String query = "SELECT role, is_active, email_verified, name FROM users WHERE id = ?";
        
        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");
                boolean emailVerified = rs.getBoolean("email_verified");
                String name = rs.getString("name");
                
                return new UserVerificationStatus(userId, name, role, isActive, emailVerified);
            }
            
            return null;
        }
    }

    /**
     * Inner class to hold user verification status details
     */
    public static class UserVerificationStatus {
        private final String userId;
        private final String name;
        private final String role;
        private final boolean isActive;
        private final boolean emailVerified;

        public UserVerificationStatus(String userId, String name, String role, boolean isActive, boolean emailVerified) {
            this.userId = userId;
            this.name = name;
            this.role = role;
            this.isActive = isActive;
            this.emailVerified = emailVerified;
        }

        public String getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        public boolean isActive() {
            return isActive;
        }

        public boolean isEmailVerified() {
            return emailVerified;
        }

        public boolean isVerified() {
            return isActive && emailVerified;
        }

        public boolean canAccessForum() {
            return "investor".equalsIgnoreCase(role) || 
                   "innovator".equalsIgnoreCase(role) ||
                   "admin".equalsIgnoreCase(role);
        }

        public boolean canPerformWriteOperations() {
            return canAccessForum() && isVerified();
        }

        public String getStatusMessage() {
            if (!canAccessForum()) {
                return "Your account role (" + role + ") does not have access to the forum.";
            }
            if (!isActive) {
                return "Your account is not active. Please contact support.";
            }
            if (!emailVerified) {
                return "Please verify your email address to post, comment, or vote.";
            }
            return "Verified";
        }
    }
}
