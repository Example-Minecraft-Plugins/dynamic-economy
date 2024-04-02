package me.davipccunha.tests.dynamiceconomy;

import lombok.Getter;
import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import me.davipccunha.tests.dynamiceconomy.command.UpdateCommand;
import me.davipccunha.tests.dynamiceconomy.listener.PlayerInteractListener;
import me.davipccunha.tests.signshop.api.model.SignShopAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class DynamicEconomyPlugin extends JavaPlugin {
    private SignShopAPI signShopAPI;
    private final ProductCache productCache = new ProductCache();

    @Override
    public void onEnable() {
        this.init();
        getLogger().info("Dynamic Economy plugin loaded!");
    }

    public void onDisable() {
        getLogger().info("Dynamic Economy plugin unloaded!");
    }

    private void init() {
        saveDefaultConfig();
        registerListeners(
                new PlayerInteractListener(this)
        );
        registerCommands();

        signShopAPI = Bukkit.getServicesManager().load(SignShopAPI.class);

        productCache.init(this.getConfig());
        productCache.updateShopsPrices(signShopAPI);
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();

        for (Listener listener : listeners) pluginManager.registerEvents(listener, this);
    }

    private void registerCommands() {
        this.getCommand("update").setExecutor(new UpdateCommand(this));
    }
}
