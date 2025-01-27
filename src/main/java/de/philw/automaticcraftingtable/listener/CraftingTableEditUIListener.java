package de.philw.automaticcraftingtable.listener;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.database.SQLiteUtil;
import de.philw.automaticcraftingtable.manager.ConfigManager;
import de.philw.automaticcraftingtable.manager.CraftingTableManager;
import de.philw.automaticcraftingtable.ui.CraftingTableEditUI;
import de.philw.automaticcraftingtable.util.ACTBlockUTIL;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static de.philw.automaticcraftingtable.manager.ConfigManager.getDefaultmaximumlimit;

public class CraftingTableEditUIListener implements Listener {

    private final AutomaticCraftingTable automaticCraftingTable;

    public CraftingTableEditUIListener(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }

    /**
     * This method saves all the inventory changes a player made on a crafting table in craftingTables.yml.
     */

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {

        if (!inventoryCloseEvent.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
                ConfigManager.getCraftingTableInventoryDisplay()))) {
            return;
        }

        CraftingTableManager craftingTableManager = automaticCraftingTable.getCraftingTableManager();
        Player player = (Player) inventoryCloseEvent.getPlayer();

        Location location = automaticCraftingTable.getCraftingTableManager().getLocationFromSavedString(
                Objects.requireNonNull(player.getPersistentDataContainer().get(new NamespacedKey(automaticCraftingTable, player.getUniqueId().toString()), PersistentDataType.STRING))
        );

        boolean empty = true;

        // 存储所有非空物品
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int index = 0; index < 9; index++) {
            ItemStack itemStack = inventoryCloseEvent.getInventory().getItem(index);
            if (itemStack != null) {
                empty = false;
                itemStacks.add(itemStack); // 添加非空物品到列表
            }
        }

        String islandId = PlaceholderAPI.setPlaceholders(player, "%bskyblock_visited_island_uuid%");
        // 获取当前岛屿的 nowcount 和 maxcount
        int[] counts = SQLiteUtil.getCounts(islandId);
        int nowCount = counts[0];
        int maxCount = counts[1];
        int valid;

        if (!empty && !ConfigManager.isSeparateFromOtherCraftingTables() && !craftingTableManager.isCraftingTableRegistered(location)) {

            if (SQLiteUtil.existsIslandId(islandId)) {
                // 判断是否超过最大数量
                if (nowCount >= maxCount) {
                    player.sendMessage("§8[§c✘§8] §c已达到自动合成台限制：" + maxCount + "。无法再创建更多的自动合成台。");
                    // 将物品还给玩家，检查是否有足够空间
                    for (ItemStack itemStack : itemStacks) {
                        if (itemStack != null) {
                            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(itemStack);
                            if (!leftover.isEmpty()) {
                                for (ItemStack remaining : leftover.values()) {
                                    player.getWorld().dropItem(player.getLocation(), remaining);
                                }
                            }
                        }
                    }
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
        }

        // 将物品保存到 CraftingTableManager
        for (int index = 0; index < 9; index++) {
            ItemStack itemStack = inventoryCloseEvent.getInventory().getItem(index);
            craftingTableManager.setItemToIndex(location, index, itemStack);
        }

        if (!craftingTableManager.isCraftingTableRegistered(location)) {
            craftingTableManager.addEmptyCraftingTable(location);
        }

        if (empty && !ConfigManager.isSeparateFromOtherCraftingTables()) {
            return;
        }

        // 存储物品到 craftingTable.yml
        craftingTableManager.saveCraftingTables();
    }

}