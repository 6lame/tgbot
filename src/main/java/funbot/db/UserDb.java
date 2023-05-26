package funbot.db;

import java.sql.*;
import java.util.HashMap;

public class UserDb extends Db{
    private final HashMap<Long, UserInDb> userList;
    public UserDb(String url, String userName, String password, String sqlCreate, String select) {
        super(url, userName, password, sqlCreate, select);
        userList = new HashMap<>();
        extractDataFromDb();
    }

    @Override
    void extractDataFromRs(ResultSet resultSet) throws SQLException {
        Long id = (long) resultSet.getInt(1);
        String role = resultSet.getString(2);
        String name = resultSet.getString(3);
        UserInDb user = new UserInDb(id,role,name);
        userList.put(id, user);
    }

    public boolean addUser(Long id, String role, String name){
        if (userList.containsKey(id)){
            return false;
        }
        String add = "INSERT INTO `users` (`id`, `role`, `name`) VALUES (?, ?, ?);";
        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(add)) {
            preparedStatement.setInt(1, Math.toIntExact(id));
            preparedStatement.setString(2, role);
            preparedStatement.setString(3, name);
            preparedStatement.executeUpdate();
            userList.put(id, new UserInDb(id, role, name));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean isAdmin(Long id){
        return userList.containsKey(id) && userList.get(id).getRole().equals("ADMIN") && isOwner(id);
    }
    public boolean isOwner(Long id){
        return userList.containsKey(id) && userList.get(id).getRole().equals("OWNER");
    }
}

class UserInDb {
    private Long id;
    private String role;
    private String name;

    public UserInDb(Long id, String role, String name) {
        this.id = id;
        this.role = role;
        this.name = name;
    }
    public UserInDb(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}