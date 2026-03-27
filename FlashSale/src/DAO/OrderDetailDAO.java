package DAO;

import entity.OrderItem;

import java.sql.*;
import java.util.List;

public class OrderDetailDAO {

    public void insertBatch(Connection conn, int orderId, List<OrderItem> items) throws Exception {
        String sql = "INSERT INTO Order_Details(order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(sql);

        for (OrderItem item : items) {
            ps.setInt(1, orderId);
            ps.setInt(2, item.productId);
            ps.setInt(3, item.quantity);
            ps.setDouble(4, 0); // giả sử lấy giá sau

            ps.addBatch();
        }

        ps.executeBatch(); //  batch processing
    }
}