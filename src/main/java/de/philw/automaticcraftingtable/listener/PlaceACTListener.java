package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.util.ACTBlockUTIL;
import de.philw.automaticcraftingtable.database.SQLiteUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Objects;

import static de.philw.automaticcraftingtable.manager.ConfigManager.getDefaultmaximumlimit;
import static org.apache.logging.log4j.LogManager.getLogger;

public class PlaceACTListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public PlaceACTListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    @EventHandler
    public void onACTPlaced(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block act = e.getBlockPlaced();
        String islandId = PlaceholderAPI.setPlaceholders(player, "%bskyblock_visited_island_uuid%");


        if (act.getType() != Material.CRAFTING_TABLE) return;
        if (!Objects.requireNonNull(e.getItemInHand().getItemMeta()).getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', ConfigManager.getCraftingTableItemDisplay()))) return;
        if (SQLiteUtil.existsIslandId(islandId)) {

            // 获取当前岛屿的 nowcount 和 maxcount
            int[] counts = SQLiteUtil.getCounts(islandId);
            int nowCount = counts[0];
            int maxCount = counts[1];


            // 判断是否超过最大数量
            if (nowCount >= maxCount) {
                e.setCancelled(true);  // 如果超过最大数量，取消事件
                player.sendMessage(ChatColor.RED + "你已经达到了创建自动工作台的最大数量！");
            } else {
                // 如果没有达到最大数量，增加 nowcount
                // To here it is an act
                act.setMetadata(ACTBlockUTIL.metadataKey, ACTBlockUTIL.metadataValue);
                automaticCraftingTable.getCraftingTableManager().addEmptyCraftingTable(e.getBlock().getLocation());
                automaticCraftingTable.getCraftingTableManager().saveCraftingTables();
                SQLiteUtil.updateCounts(islandId, nowCount + 1);
            }
        } else {
            automaticCraftingTable.getCraftingTableManager().addEmptyCraftingTable(e.getBlock().getLocation());
            automaticCraftingTable.getCraftingTableManager().saveCraftingTables();
            SQLiteUtil.insertIsland(islandId, 1, getDefaultmaximumlimit());  // 初始化 nowcount 为 1，maxcount 为 3
            // 如果岛屿记录不存在，插入新记录
            act.setMetadata(ACTBlockUTIL.metadataKey, ACTBlockUTIL.metadataValue);
        }
    }

}
