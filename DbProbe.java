import edu.collaboration.tools.MyConnection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbProbe {
    public static void main(String[] args) {
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery("DESCRIBE investment");
            while (rs.next()) {
                System.out.println(rs.getString("Field") + " - " + rs.getString("Type") + " - Default: "
                        + rs.getString("Default"));
            }
            System.out.println("---- SAMPLE DATA ----");
            rs = st.executeQuery("SELECT investment_id, status FROM investment ORDER BY investment_id DESC LIMIT 5");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("investment_id") + " - Status: '" + rs.getString("status") + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
