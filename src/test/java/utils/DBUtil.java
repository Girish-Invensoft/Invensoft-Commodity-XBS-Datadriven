package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Central JDBC helper for pulling "ground truth" data straight from the
 * database instead of driving an admin UI. Reads connection details and
 * the group-membership SQL from db.properties so the query can be tuned
 * to your actual schema without touching this class.
 *
 * Expected keys in db.properties:
 *   db.driver=com.mysql.cj.jdbc.Driver           (or org.postgresql.Driver, etc.)
 *   db.url=jdbc:mysql://localhost:3306/yourapp
 *   db.username=readonly_test_user
 *   db.password=********
 *   db.query.groupMembers=SELECT u.username FROM group_members gm \
 *       JOIN users u ON gm.user_id = u.id \
 *       JOIN groups g ON gm.group_id = g.id \
 *       WHERE g.name = ? AND gm.active = true
 *
 * IMPORTANT: use a read-only DB account for test automation. Never point
 * this at a production database, and never commit real credentials —
 * keep db.properties out of source control or load values from env vars.
 */
public class DBUtil {

    private static final String CONFIG_PATH = "src/test/resources/config/db.properties";
    private static Properties props;

    private static synchronized Properties loadProps() throws IOException {
        if (props == null) {
            props = new Properties();
            try (InputStream in = new FileInputStream(CONFIG_PATH)) {
                props.load(in);
            }
        }
        return props;
    }

    private static Connection getConnection() throws SQLException, IOException, ClassNotFoundException {
        Properties p = loadProps();
        Class.forName(p.getProperty("db.driver"));
        return DriverManager.getConnection(
                p.getProperty("db.url"),
                p.getProperty("db.username"),
                p.getProperty("db.password"));
    }

    /**
     * Returns the list of usernames belonging to the given group,
     * queried directly from the database.
     *
     * @param groupName the public/security group name to look up
     * @return list of usernames currently active in that group
     */
    public static List<String> getPublicGroupActiveUsers(String groupName) throws Exception {
        List<String> users = new ArrayList<>();
        Properties p = loadProps();
        String sql = p.getProperty("db.query.groupMembers");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, groupName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(rs.getString(1));
                }
            }
        }
        return users;
    }

    /**
     * Optional: returns any "queue" style entries that should be excluded
     * from a user-vs-user comparison (mirrors activeQueues in the
     * Salesforce-based version). Wire this to a real query if your schema
     * has an equivalent concept; otherwise return an empty list.
     */
    public static List<String> getActiveQueueNames() throws Exception {
        List<String> queues = new ArrayList<>();
        Properties p = loadProps();
        String sql = p.getProperty("db.query.activeQueues");
        if (sql == null || sql.trim().isEmpty()) {
            return queues; // no queue concept in this app — return empty
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                queues.add(rs.getString(1));
            }
        }
        return queues;
    }
}