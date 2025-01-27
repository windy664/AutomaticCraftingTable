package de.philw.automaticcraftingtable.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteUtil {
    private static final String DB_NAME = "island_data.db"; // 数据库文件名
    private static String dbPath;

    // 设置数据库路径
    public static void initialize(JavaPlugin plugin) {
        // 获取插件数据目录
        File dataFolder = plugin.getDataFolder();

        // 如果插件的文件夹不存在，则创建
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        // 设置数据库路径
        dbPath = dataFolder + File.separator + DB_NAME;

        // 确保数据库文件所在的文件夹存在
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            try {
                // 如果数据库文件不存在，尝试创建
                dbFile.createNewFile();
                System.out.println("SQLite数据库文件未找到，已创建新文件：" + dbFile.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    // 检查数据库中是否已存在该 islandid 的记录
    public static boolean existsIslandId(String islandId) {
        String query = "SELECT COUNT(*) FROM island_data WHERE islandid = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, islandId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取当前岛屿的数据 (nowcount 和 maxcount)
    public static int[] getCounts(String islandId) {
        String query = "SELECT nowcount, maxcount FROM island_data WHERE islandid = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, islandId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new int[]{rs.getInt("nowcount"), rs.getInt("maxcount")};
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[]{-1, -1};  // 如果没有找到返回 -1
    }

    // 插入新记录
    public static void insertIsland(String islandId, int nowCount, int maxCount) {
        String query = "INSERT INTO island_data (islandid, nowcount, maxcount) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, islandId);
            stmt.setInt(2, nowCount);
            stmt.setInt(3, maxCount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 更新 islandid 的记录
    public static void updateMax(String islandId, int maxcount) {
        String query = "UPDATE island_data SET maxcount = ? WHERE islandid = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, maxcount);
            stmt.setString(2, islandId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updateCounts(String islandId, int nowCount) {
        String query = "UPDATE island_data SET nowcount = ? WHERE islandid = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, nowCount);
            stmt.setString(2, islandId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 创建表格，如果表格不存在
    public static void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS island_data ("
                + "islandid TEXT PRIMARY KEY, "
                + "nowcount INTEGER, "
                + "maxcount INTEGER);";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
