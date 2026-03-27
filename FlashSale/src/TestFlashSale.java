import DAO.ReportDAO;
import entity.OrderItem;
import service.OrderService;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class TestFlashSale {

    public static void main(String[] args) {

        try {
            initData();
        } catch (Exception e) {
            System.out.println("❌ Lỗi init data: " + e.getMessage());
        }

        ExecutorService ex = Executors.newFixedThreadPool(50);

        for (int i = 1; i <= 50; i++) {

            ex.submit(() -> {
                try {
                    int userId = new Random().nextInt(10) + 1; // 🔥 fix
                    int productId = randomProductId();

                    List<OrderItem> items = new ArrayList<>();
                    items.add(new OrderItem(productId, 1));

                    System.out.println("User " + userId + " mua product " + productId);

                    new OrderService().placeOrder(userId, items);

                } catch (Exception e) {
                    System.out.println("❌ Lỗi thread: " + e.getMessage());
                }
            });
        }

        //  CHỜ THREAD XONG
        try {
            ex.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //  REPORT SAU KHI CÓ DATA
        ReportDAO report = new ReportDAO();
        report.getTopBuyers();
        report.getRevenueByCategory();
    }

    // =============================
    // RANDOM PRODUCT ID (CHUẨN)
    // =============================
    public static int randomProductId() {
        List<Integer> ids = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT product_id FROM Products")) {

            while (rs.next()) {
                ids.add(rs.getInt("product_id"));
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi lấy product_id: " + e.getMessage());
        }

        if (ids.isEmpty()) {
            throw new RuntimeException("Không có sản phẩm trong DB");
        }

        Random rand = new Random();
        return ids.get(rand.nextInt(ids.size()));
    }

    // =============================
    // INIT DATA
    // =============================
    public static void initData() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate("DELETE FROM Order_Details");
            st.executeUpdate("DELETE FROM Orders");
            st.executeUpdate("DELETE FROM Users");
            st.executeUpdate("DELETE FROM Products");

            // 🔥 reset auto increment
            st.executeUpdate("ALTER TABLE Products AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Users AUTO_INCREMENT = 1");

            System.out.println(" Tạo dữ liệu...");

            // 10 users
            for (int i = 1; i <= 10; i++) {
                st.executeUpdate(
                        "INSERT INTO Users(name, email) VALUES ('User" + i + "', 'user" + i + "@gmail.com')"
                );
            }

            // 5 products (stock = 1)
            for (int i = 1; i <= 5; i++) {
                st.executeUpdate(
                        "INSERT INTO Products(name, price, stock, category) " +
                                "VALUES ('Product" + i + "', 100, 2, 'Test')"
                );
            }

            System.out.println("✅ Init data thành công!");
        }
    }
}