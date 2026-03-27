package DAO;

import utils.DatabaseConnection;

import java.sql.*;

public class ReportDAO {


    public void getTopBuyers() {
        try (Connection conn = DatabaseConnection.getConnection()) {

            CallableStatement cs = conn.prepareCall("{CALL GetTopBuyers()}");

            ResultSet rs = cs.executeQuery();

            System.out.println("\n🔥 TOP 5 KHÁCH HÀNG:");

            while (rs.next()) {
                System.out.println(
                        "UserID: " + rs.getInt("user_id") +
                                " | Name: " + rs.getString("name") +
                                " | Total: " + rs.getInt("total_items")
                );
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi report: " + e.getMessage());
        }
    }

    public void getRevenueByCategory() {
        try (Connection conn = DatabaseConnection.getConnection()) {

            CallableStatement cs = conn.prepareCall("{CALL GetRevenueByCategory()}");

            ResultSet rs = cs.executeQuery();

            System.out.println("\n💰 DOANH THU THEO DANH MỤC:");

            while (rs.next()) {
                System.out.println(
                        "Category: " + rs.getString("category") +
                                " | Revenue: " + rs.getDouble("total_revenue")
                );
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi report: " + e.getMessage());
        }
    }
}
