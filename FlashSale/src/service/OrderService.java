package service;

import DAO.OrderDAO;
import DAO.OrderDetailDAO;
import entity.OrderItem;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.List;

public class OrderService {

    public void placeOrder(int userId, List<OrderItem> items) {
        Connection conn = null;

        // 👉 validate input
        if (items == null || items.isEmpty()) {
            System.out.println("❌ Danh sách sản phẩm rỗng");
            return;
        }

        try {
            conn = DatabaseConnection.getConnection();

            //  bắt buộc
            conn.setAutoCommit(false);

            //  chống race condition
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            OrderDAO orderDAO = new OrderDAO();
            OrderDetailDAO detailDAO = new OrderDetailDAO();

            // =========================
            // 1. CHECK STOCK (CÓ LOCK)
            // =========================
            String checkSql = "SELECT stock FROM Products WHERE product_id = ? FOR UPDATE";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);

            for (OrderItem item : items) {

                if (item.quantity <= 0) {
                    throw new Exception("Số lượng không hợp lệ: product_id=" + item.productId);
                }

                checkStmt.setInt(1, item.productId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int stock = rs.getInt("stock");

                    if (stock < item.quantity) {
                        throw new Exception("Hết hàng: product_id=" + item.productId);
                    }

                } else {
                    throw new Exception("Không tìm thấy sản phẩm: product_id=" + item.productId);
                }
            }

            // =========================
            // 2. UPDATE STOCK (BATCH)
            // =========================
            String updateSql = "UPDATE Products SET stock = stock - ? WHERE product_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);

            for (OrderItem item : items) {
                updateStmt.setInt(1, item.quantity);
                updateStmt.setInt(2, item.productId);
                updateStmt.addBatch();
            }

            updateStmt.executeBatch();

            // =========================
            // 3. TẠO ORDER
            // =========================
            int orderId = orderDAO.createOrder(conn, userId);

            // =========================
            // 4. INSERT ORDER DETAILS
            // =========================
            detailDAO.insertBatch(conn, orderId, items);

            // =========================
            // 5. COMMIT
            // =========================
            conn.commit();

            System.out.println("✅ User " + userId + " đặt hàng thành công!");

        } catch (Exception e) {

            // =========================
            // ROLLBACK
            // =========================
            try {
                if (conn != null) {
                    conn.rollback();
                }
                System.out.println("❌ User " + userId + " thất bại: " + e.getMessage());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // reset lại
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}