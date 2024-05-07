package me.davipccunha.tests.dynamiceconomy;

import lombok.Getter;
import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import me.davipccunha.tests.dynamiceconomy.command.DynamicEconomyCommand;
import me.davipccunha.tests.dynamiceconomy.listener.AdminShopBuyListener;
import me.davipccunha.tests.signshop.api.model.SignShopAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class DynamicEconomyPlugin extends JavaPlugin {
    private SignShopAPI signShopAPI;
    private ProductCache productCache;

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
                new AdminShopBuyListener(this)
        );
        registerCommands();
        loadCaches();

        signShopAPI = Bukkit.getServicesManager().load(SignShopAPI.class);

        productCache.init(this.getConfig());
        productCache.updateShopsPrices(signShopAPI);
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();

        for (Listener listener : listeners) pluginManager.registerEvents(listener, this);
    }

    private void registerCommands() {
        this.getCommand("dynamiceconomy").setExecutor(new DynamicEconomyCommand(this));
    }

    private void loadCaches() {
        this.productCache = new ProductCache();
    }
}
