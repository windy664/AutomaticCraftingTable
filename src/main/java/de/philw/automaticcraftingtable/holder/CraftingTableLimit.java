package de.philw.automaticcraftingtable.holder;

import de.philw.automaticcraftingtable.AutomaticCraftingTable;
import de.philw.automaticcraftingtable.database.SQLiteUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class CraftingTableLimit extends PlaceholderExpansion {
    private final AutomaticCraftingTable automaticCraftingTable;
    public CraftingTableLimit(AutomaticCraftingTable automaticCraftingTable) {
        this.automaticCraftingTable = automaticCraftingTable;
    }
    @Override
    public String getAuthor() {
        return "风吟";
    }

    @Override
    public String getIdentifier() {
        return "act"; // 这是占位符的标识符部分
    }

    @Override
    public String getVersion() {
        return automaticCraftingTable.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String islandId = PlaceholderAPI.setPlaceholders(player, "%bskyblock_island_uuid%");
        // 获取当前岛屿的 nowcount 和 maxcount
        int[] counts = SQLiteUtil.getCounts(islandId);
        int nowCount = counts[0];
        int maxCount = counts[1];
        if (params.equalsIgnoreCase("valid")) {
            String valid = String.valueOf(maxCount-nowCount);
            return (valid);
        }else if(params.equalsIgnoreCase("max")){
            return (String.valueOf(maxCount));
        } else if (params.equalsIgnoreCase("now")) {
            return (String.valueOf(nowCount));
        }
        return null;
    }
}
