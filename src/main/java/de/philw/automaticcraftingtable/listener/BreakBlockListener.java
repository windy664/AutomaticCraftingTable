package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.util.ACTBlockUTIL;
import de.philw.automaticcraftingtable.database.SQLiteUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import static de.philw.automaticcraftingtable.manager.ConfigManager.getDefaultmaximumlimit;

public class BreakBlockListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public BreakBlockListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    /**
     * This method checks if a registered crafting table block is broken.
     * If so, it removes all info from it of off the craftingTales.yml and gives the items in it back to the player.
     */

    @EventHandler
    public void onRegisteredCraftingTableBlockBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        Block craftingTable = blockBreakEvent.getBlock();
        if (craftingTable.getType() != Material.CRAFTING_TABLE) {
            return;
        }
        CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();
        if (blockBreakEvent.isCancelled()) {
            return;
        }
        if (craftingTable.hasMetadata(ACTBlockUTIL.metadataKey)) {
            blockBreakEvent.setDropItems(false);
            craftingTable.getWorld().dropItemNaturally(craftingTable.getLocation(), ACTBlockUTIL.getACT());
        }
        if (!craftingTableManager.isCraftingTableRegistered(craftingTable.getLocation())) {
            return;
        }
        for (ItemStack itemStack : craftingTableManager.getItemsInCraftingTable(craftingTable.getLocation())) {
            craftingTable.getWorld().dropItemNaturally(craftingTable.getLocation(), itemStack);
        }

        // 获取岛屿ID
        String islandId = PlaceholderAPI.setPlaceholders(player, "%bskyblock_visited_island_uuid%");

        int[] counts = SQLiteUtil.getCounts(islandId);
        int nowCount = counts[0];

        // 如果未找到记录（nowCount == -1），则插入新记录
        if (nowCount == -1) {
            // 假设 maxCount 默认是 3，nowCount 默认是 0
            SQLiteUtil.insertIsland(islandId, 0, getDefaultmaximumlimit());  // 插入新记录
        } else if (nowCount > 0) {
            SQLiteUtil.updateCounts(islandId, nowCount - 1);
        }else if(nowCount == 0){
            return;
        }

        craftingTableManager.removeCraftingTable(craftingTable.getLocation());
        craftingTableManager.saveCraftingTables();


    }

}