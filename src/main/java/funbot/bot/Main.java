package funbot.bot;


import com.fasterxml.jackson.databind.ObjectMapper;
import funbot.db.ImgDb;
import funbot.db.UserDb;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.Console;
import java.io.File;
import java.io.IOException;

public class Main {
    private static final String sqlCreateUsers = "CREATE TABLE IF NOT EXISTS `users` (\n" +
            "  `id` int NOT NULL,\n" +
            "  `role` varchar(100) NULL,\n" +
            "  `name` varchar(100) NULL,\n" +
            "  PRIMARY KEY (`id`))";
    private static final String sqlCreateImg = "CREATE TABLE IF NOT EXISTS `images` (\n" +
            "  `name` VARCHAR(100) NOT NULL,\n" +
            "  `userid` INT NULL,\n" +
            "  PRIMARY KEY (`name`),\n" +
            "  INDEX `id_idx` (`userid` ASC) VISIBLE,\n" +
            "  CONSTRAINT `id`\n" +
            "    FOREIGN KEY (`userid`)\n" +
            "    REFERENCES `users` (`id`)\n" +
            "    ON DELETE NO ACTION\n" +
            "    ON UPDATE NO ACTION);\n";
    private static final String selectUser =  "SELECT * FROM users";
    private static final String selectImg = "SELECT * FROM images";
    public static void main(String[] args) throws TelegramApiException, IOException {
        File file = new File("botConfig.json");
        BotConfig cnfg;
        ObjectMapper objectMapper = new ObjectMapper();
        if (file.exists() && file.isFile()){
            cnfg = objectMapper.readValue(file, BotConfig.class);
        } else {
            Console console = System.console();
            cnfg = new BotConfig();
            cnfg.setSqlUrl(console.readLine("Enter sqlUrl: "));
            cnfg.setUserName(console.readLine("Enter userName: "));
            cnfg.setPassword(console.readLine("Enter password: "));
            cnfg.setDir(console.readLine("Enter dir: "));
            cnfg.setBotName(console.readLine("Enter botName: "));
            cnfg.setToken(console.readLine("Enter token: "));
            cnfg.setOwnerId(Long.parseLong(console.readLine("Enter ownerId: ")));
            objectMapper.writeValue(file, cnfg);
        }
        UserDb userDb  = new UserDb(cnfg.getSqlUrl(), cnfg.getUserName(), cnfg.getPassword(), sqlCreateUsers, selectUser);
        userDb.addUser(cnfg.getOwnerId(), "OWNER", "Alex");
        ImgDb imgDb = new ImgDb(cnfg.getSqlUrl(), cnfg.getUserName(), cnfg.getPassword(), sqlCreateImg, selectImg,
                cnfg.getDir(), cnfg.getOwnerId());
        imgDb.updateFromDir(cnfg.getDir());

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot(cnfg.getBotName(), cnfg.getToken(), cnfg.getOwnerId(), imgDb, userDb);
        botsApi.registerBot(bot);
        
    }
}

class BotConfig{
    private String sqlUrl;
    private String userName;
    private String password;
    private String dir;
    private String botName;
    private String token;
    private long ownerId;

    public BotConfig() {
    }

    public BotConfig(String sqlUrl, String userName, String password, String dir, String botName, String token, long ownerId) {
        this.sqlUrl = sqlUrl;
        this.userName = userName;
        this.password = password;
        this.dir = dir;
        this.botName = botName;
        this.token = token;
        this.ownerId = ownerId;
    }

    public String getSqlUrl() {
        return sqlUrl;
    }

    public void setSqlUrl(String sqlUrl) {
        this.sqlUrl = sqlUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
