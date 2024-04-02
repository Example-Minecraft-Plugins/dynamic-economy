package me.davipccunha.tests.dynamiceconomy.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.dynamiceconomy.DynamicEconomyPlugin;
import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import me.davipccunha.tests.dynamiceconomy.model.Product;
import me.davipccunha.tests.dynamiceconomy.util.InventoryUtil;
import me.davipccunha.tests.signshop.api.model.Shop;
import me.davipccunha.tests.signshop.api.model.ShopLocation;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class PlayerInteractListener implements Listener {
    private final DynamicEconomyPlugin plugin;

    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null || player == null) return;

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (!(block.getState() instanceof Sign)) return;

        Shop shop = plugin.getSignShopAPI().getShop(new ShopLocation(block.getLocation()));
        if (shop == null) return;

        if (!shop.isAdminShop()) return;

        ProductCache cache = plugin.getProductCache();

        Product product = cache.get(shop);
        if (product == null) return;

        if (shop.getBuyAmount() <= 0 || shop.getBuyPrice() <= 0) return;

        final int playerAmount = InventoryUtil.getTotalAmount(player.getInventory(), shop.getItemStack());

        final int shopAmount = shop.getBuyAmount();
        int amount = Math.min(playerAmount, shopAmount);

        if (playerAmount <= 0) return;

        product.addAmountSold(amount);
        cache.add(product.getId(), product.getData(), product);
    }
}
