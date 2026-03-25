import util.DatabaseConnection;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        Connection connection = null;

        try {
            connection = DatabaseConnection.openConnection();
            connection.setAutoCommit(false);

            String sql1 = "UPDATE balance SET balance = balance - ? WHERE id = ?";
            CallableStatement callStmt = connection.prepareCall(sql1);
            callStmt.registerOutParameter(1, Types.INTEGER);
            callStmt.setInt(2, 1);
            callStmt.execute();

            String sql2 = "UPDATE balance SET balance = balance + ? WHERE id = ?";
            CallableStatement callStmt2 = connection.prepareCall(sql2);
            callStmt2.setInt(1, 2);
            callStmt2.setInt(2, 1);
            callStmt2.execute();

            connection.commit();
            System.out.println("Chuyển thành công");
        } catch (SQLException e){
            System.err.println(e.getMessage());
             try {
                 if (connection != null) {
                     connection.rollback();
                 }
             } catch (SQLException ex) {
                 System.err.println(ex.getMessage());
             }
        }
    }

    private static boolean checkBalance(Connection connection, String accountId, double balance) throws SQLException {
        String sql = "GET balance FROM balance WHERE account_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, accountId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            double balanceValue = resultSet.getDouble("balance");
            return balanceValue >= balance;
        }
        return false;
    }
}
