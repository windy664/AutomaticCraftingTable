package de.philw.automaticcraftingtable.commands;

import de.philw.automaticcraftingtable.database.SQLiteUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ACTCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;

    // 构造函数，接收插件实例
    public ACTCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 权限检查
        if (sender instanceof Player && !sender.hasPermission("act.change")) {
            sender.sendMessage("§8[§c✘§8] §c你没有权限执行此命令！");
            return false;
        }

        // 参数检查
        if (args.length != 2) {
            sender.sendMessage("§8[§c✘§8] §c用法: /act <玩家> <数值>");
            return false;
        }

        // 获取玩家和数值
        String playerName = args[0];
        String valueString = args[1];

        // 检查数值是否有效
        int value;
        try {
            value = Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            sender.sendMessage("§8[§c✘§8] §c请输入有效的数值！");
            return false;
        }

        // 执行数据库插入

        String islandId = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(playerName), "%bskyblock_island_uuid%");
        SQLiteUtil.updateMax(islandId, value);
        // 命令执行成功
        sender.sendMessage("§8[§a✔§8] §a岛屿数据已更新，玩家" + playerName + "的岛屿最大限值：" + value);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // 只在第二个参数为空时，提供玩家名的补全
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    suggestions.add(player.getName());
                }
            }
        }

        return suggestions;
    }
}
