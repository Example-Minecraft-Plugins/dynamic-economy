package me.davipccunha.tests.dynamiceconomy.listener;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.dynamiceconomy.DynamicEconomyPlugin;
import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import me.davipccunha.tests.dynamiceconomy.model.Product;
import me.davipccunha.tests.signshop.api.event.AdminShopBuyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class AdminShopBuyListener implements Listener {
    private final DynamicEconomyPlugin plugin;

    @EventHandler(priority = EventPriority.NORMAL)
    private void onAdminShopBuy(AdminShopBuyEvent event) {
        final ProductCache cache = plugin.getProductCache();

        Product product = cache.get(event.getShop());
        if (product == null) return;

        product.addAmountSold(event.getAmount());
        cache.add(product.getId(), product.getData(), product);
    }
}
