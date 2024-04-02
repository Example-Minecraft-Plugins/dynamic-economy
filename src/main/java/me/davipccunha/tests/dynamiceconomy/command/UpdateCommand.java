package me.davipccunha.tests.dynamiceconomy.command;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.dynamiceconomy.DynamicEconomyPlugin;
import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class UpdateCommand implements CommandExecutor {
    private final DynamicEconomyPlugin plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        ProductCache cache = plugin.getProductCache();

        cache.updateAllProductsPrices();
        cache.updateShopsPrices(plugin.getSignShopAPI());

        return false;
    }
}
