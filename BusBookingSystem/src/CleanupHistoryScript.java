import com.busbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class CleanupHistoryScript {
    public static void main(String[] args) {
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            String sql = "DELETE FROM history WHERE action_details LIKE 'Logged in' OR action_details LIKE 'Created an account%'";
            int rows = st.executeUpdate(sql);
            System.out.println("Removed " + rows + " login/account creation entries from history.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
