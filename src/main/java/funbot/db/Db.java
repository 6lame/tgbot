package funbot.db;


import java.sql.*;

abstract class Db {
    private final String url;
    private final String userName;
    private final String password;
    private final String select;
    public Db(String url, String userName, String password, String sqlCreate, String select) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.select = select;
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement();) {
            statement.execute(sqlCreate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, userName, password);
    }
    protected void extractDataFromDb(){
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(select)) {
            while (resultSet.next()) {
                extractDataFromRs(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    abstract void extractDataFromRs(ResultSet resultSet) throws SQLException;

}
