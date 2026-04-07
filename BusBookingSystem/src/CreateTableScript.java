import com.busbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class CreateTableScript {
    public static void main(String[] args) {
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS history (" +
                         "history_id INT AUTO_INCREMENT PRIMARY KEY, " +
                         "user_role VARCHAR(255), " +
                         "action_details TEXT, " +
                         "action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                         ")";
            st.executeUpdate(sql);
            System.out.println("History table created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
