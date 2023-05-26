package funbot.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class ImgDb extends Db{
    private final ArrayList<String> imgNames = new ArrayList<>();
    private final String dir;
    private final long ownerId;
    public String getDir() {
        return dir;
    }

    public ImgDb(String url, String userName, String password, String sqlCreate, String select, String dir, long ownerId) {
        super(url, userName, password, sqlCreate, select);
        this.dir = dir;
        this.ownerId = ownerId;
        extractDataFromDb();

    }

    @Override
    void extractDataFromRs(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString(1);
        if (!imgNames.contains(name)) {
            imgNames.add(name);
        }
    }
    public synchronized Boolean addImgToDb(String fileName, long userId) {
        String add = "INSERT INTO `images` (`name`, `userid`) VALUES (?, ?);";
        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(add)) {
            preparedStatement.setString(1, fileName);
            preparedStatement.setInt(2, (int) userId);
            preparedStatement.executeUpdate();
            imgNames.add(fileName);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateFromDir(String dir) {
        try {
            Files.list(Paths.get(dir))
                    .filter(path -> !Files.isDirectory(path))
                    .map(path -> path.getFileName().toString())
                    .filter(name -> !imgNames.contains(name))
                    .forEach(name -> this.addImgToDb(name, ownerId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getImgNames() {
        return imgNames;
    }
    public String getRandomName(){
        Random random = new Random();
        int randomIndex = random.nextInt(imgNames.size());
        return imgNames.get(randomIndex);
    }
}
