package me.davipccunha.tests.dynamiceconomy.command;

import lombok.RequiredArgsConstructor;
import me.davipccunha.tests.dynamiceconomy.DynamicEconomyPlugin;
import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class DynamicEconomyCommand implements CommandExecutor {
    private final DynamicEconomyPlugin plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("dynamiceconomy.forceupdate")) {
            commandSender.sendMessage("§cVocê não tem permissão para executar este comando.");
            return true;
        }

        if (args.length == 0 || !args[0].equals("update")) {
            commandSender.sendMessage("§cUso: /de update");
            return false;
        }

        final ProductCache cache = plugin.getProductCache();

        cache.updateAllProductsPrices();
        cache.updateShopsPrices(plugin.getSignShopAPI());

        commandSender.sendMessage("§aPreços atualizados com sucesso!");

        return false;
    }
}
