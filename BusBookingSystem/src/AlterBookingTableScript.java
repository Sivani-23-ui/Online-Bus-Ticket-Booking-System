import com.busbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class AlterBookingTableScript {
    public static void main(String[] args) {
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            
            // Add columns if they don't exist
            String sql1 = "ALTER TABLE booking ADD COLUMN gender VARCHAR(10)";
            String sql2 = "ALTER TABLE booking ADD COLUMN seat_number VARCHAR(10)";
            String sql3 = "ALTER TABLE booking ADD COLUMN payment_method VARCHAR(20)";
            
            try { st.executeUpdate(sql1); System.out.println("Added column: gender"); } catch(Exception e) { System.out.println("gender column may already exist."); }
            try { st.executeUpdate(sql2); System.out.println("Added column: seat_number"); } catch(Exception e) { System.out.println("seat_number column may already exist."); }
            try { st.executeUpdate(sql3); System.out.println("Added column: payment_method"); } catch(Exception e) { System.out.println("payment_method column may already exist."); }
            
            System.out.println("Table booking altered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
