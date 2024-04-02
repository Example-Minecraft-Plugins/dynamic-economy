package me.davipccunha.tests.dynamiceconomy.model;

import lombok.Getter;
import lombok.Setter;
import me.davipccunha.tests.dynamiceconomy.factory.ProductFactory;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@Setter
public class Product {
    private final int id;
    private final short data;
    private final double minPrice, maxPrice;
    private double buyPrice;
    private final EconomyGroup economyGroup;
    private int amountSold;

    public Product(Product product) {
        this.id = product.id;
        this.data = product.data;
        this.minPrice = product.minPrice;
        this.maxPrice = product.maxPrice;
        this.buyPrice = product.buyPrice;
        this.economyGroup = product.economyGroup;
        this.amountSold = product.amountSold;
    }

    public Product(FileConfiguration config, int id, short data) {
        this(ProductFactory.createProduct(config, id, data));
    }

    public Product(int id, short data, double minPrice, double maxPrice, EconomyGroup economyGroup) {
        this.id = id;
        this.data = data;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.economyGroup = economyGroup;
        this.buyPrice = (minPrice + maxPrice) / 2;
    }

    public void addAmountSold(int amount) {
        this.amountSold += amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Product)) return false;
        Product product = (Product) obj;
        return product.minPrice == this.minPrice && product.maxPrice == this.maxPrice && product.economyGroup == this.economyGroup;
    }
}
