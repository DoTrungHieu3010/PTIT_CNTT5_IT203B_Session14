package DAO;

import java.sql.*;

public class OrderDAO {

    public int createOrder(Connection conn, int userId) throws Exception {
        String sql = "INSERT INTO Orders(user_id) VALUES (?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, userId);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) return rs.getInt(1);

        throw new Exception("Không tạo được order");
    }
}
