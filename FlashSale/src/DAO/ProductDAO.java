package DAO;

import utils.DatabaseConnection;

import java.sql.*;

public class ProductDAO {

    public void addProduct(String name, double price, int stock, String category) throws Exception {
        if (name == null || name.isEmpty()) throw new Exception("Tên không hợp lệ");
        if (price <= 0) throw new Exception("Giá phải > 0");
        if (stock < 0) throw new Exception("Stock không hợp lệ");

        String sql = "INSERT INTO Products(name, price, stock, category) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setString(1, name);
        ps.setDouble(2, price);
        ps.setInt(3, stock);
        ps.setString(4, category);
        ps.executeUpdate();
    }

    public void updateStock(int productId, int quantity) throws Exception {
        String sql = "UPDATE Products SET stock = stock + ? WHERE product_id = ?";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, quantity);
        ps.setInt(2, productId);
        ps.executeUpdate();
    }

    public int getStock(int productId) throws Exception {
        String sql = "SELECT stock FROM Products WHERE product_id = ?";
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, productId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) return rs.getInt("stock");
        throw new Exception("Không tìm thấy sản phẩm");
    }
}
