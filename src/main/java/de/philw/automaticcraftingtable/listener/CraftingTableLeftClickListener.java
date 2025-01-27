package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.database.SQLiteUtil;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.ui.CraftingTableEditUI;
import de.philw.automaticcraftingtable.util.ACTBlockUTIL;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

import static de.philw.automaticcraftingtable.manager.ConfigManager.getDefaultmaximumlimit;

public class CraftingTableLeftClickListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public CraftingTableLeftClickListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    /**
     * This method opens the crafting table edit ui, when a player sneaks and left clicks to a crafting table.
     */

    @EventHandler
    public void onCraftingTableConfigRequested(PlayerInteractEvent playerInteractEvent) {

        Player player = playerInteractEvent.getPlayer();

        if (!player.isSneaking()) {
            return;
        }

        Block craftingTable = playerInteractEvent.getClickedBlock();

        if (craftingTable == null) {
            return;
        }
        if (!craftingTable.getType().equals(Material.CRAFTING_TABLE)) {
            return;
        }

        if (!playerInteractEvent.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        if (ConfigManager.isSeparateFromOtherCraftingTables() && !craftingTable.hasMetadata(ACTBlockUTIL.metadataKey)) {
            return;
        }

        // To here it is requested

        playerInteractEvent.setCancelled(true);

        CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();

/*
        Location location = automaticCraftingTable.getCraftingTableManager().getLocationFromSavedString(
                Objects.requireNonNull(player.getPersistentDataContainer().get(new NamespacedKey(automaticCraftingTable, player.getUniqueId().toString()), PersistentDataType.STRING))
        );

        if (!ConfigManager.isSeparateFromOtherCraftingTables() && !craftingTableManager.isCraftingTableRegistered(location)) {
            String islandId = PlaceholderAPI.setPlaceholders(player, "%bskyblock_visited_island_uuid%");
            // 获取当前岛屿的 nowcount 和 maxcount
            int[] counts = SQLiteUtil.getCounts(islandId);
            int nowCount = counts[0];
            int maxCount = counts[1];
            int valid;

            if (SQLiteUtil.existsIslandId(islandId)) {
                // 判断是否超过最大数量
                if (nowCount >= maxCount) {
                    player.sendMessage("§8[§c✘§8] §c已达到自动合成台限制：" + maxCount + "。无法再创建更多的自动合成台。");
                    return;
                } else {
                    SQLiteUtil.updateCounts(islandId, nowCount + 1);
                    valid = maxCount - nowCount - 1;
                    player.sendMessage("§8[§a✔§8] §a创建了一个自动合成台，你还可以创建" + valid + "个");
                }
            } else {
                SQLiteUtil.insertIsland(islandId, 1, getDefaultmaximumlimit());  // 初始化 nowcount 为 1，maxcount 为 3
                player.sendMessage("§8[§a✔§8] §a创建了一个自动合成台！");
            }
        }else{
            return;
        }

*/



        //这个是注册的逻辑

        new CraftingTableEditUI(player, automaticCraftingTable, craftingTable,
                craftingTableManager.isCraftingTableRegistered(craftingTable.getLocation()));
    }
}