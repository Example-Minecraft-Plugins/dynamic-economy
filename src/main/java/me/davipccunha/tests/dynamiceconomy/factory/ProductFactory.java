package me.davipccunha.tests.dynamiceconomy.factory;

import me.davipccunha.tests.dynamiceconomy.model.EconomyGroup;
import me.davipccunha.tests.dynamiceconomy.model.Product;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class ProductFactory {
    public static Product createProduct(FileConfiguration config, int id, short data) {
        String key = String.format("products.%d:%s", id, data);
        double minPrice = config.getDouble(key + ".min-price");
        double maxPrice = config.getDouble(key + ".max-price");
        String category = config.getString(key + ".category");

        if (minPrice == 0 || maxPrice == 0 || category == null) {
            Bukkit.getLogger().warning("Invalid product configuration for item ID " + id + ":" + data);
            throw new IllegalArgumentException("Invalid product configuration for " + key);
        }

        EconomyGroup economyGroup = EconomyGroup.valueOf(category.toUpperCase());

        return new Product(id, data, minPrice, maxPrice, economyGroup);
    }
}
