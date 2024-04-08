package me.davipccunha.tests.dynamiceconomy.helper;

import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import me.davipccunha.tests.dynamiceconomy.model.EconomyGroup;
import me.davipccunha.tests.dynamiceconomy.model.Product;

import java.util.Collection;
import java.util.stream.Collectors;

public class PriceUpdater {
    protected static Collection<Product> getFilteredProducts(ProductCache cache, EconomyGroup economyGroup) {
        return cache.getProducts().stream()
                .filter(product -> product.getEconomyGroup() == economyGroup).collect(Collectors.toList());
    }

    protected static double categoryTotalValue(ProductCache cache, EconomyGroup economyGroup) {
        return getFilteredProducts(cache, economyGroup).stream()
                .mapToDouble(product -> product.getBuyPrice() * product.getAmountSold())
                .sum();
    }
}
