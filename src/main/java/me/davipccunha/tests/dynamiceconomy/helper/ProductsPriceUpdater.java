package me.davipccunha.tests.dynamiceconomy.helper;

import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import me.davipccunha.tests.dynamiceconomy.model.EconomyGroup;
import me.davipccunha.tests.dynamiceconomy.model.Product;

import java.util.stream.Stream;

public class ProductsPriceUpdater {
    public static void updateProductsPrice(ProductCache cache, EconomyGroup economyGroup) {
        final double totalValue = totalValue(cache, economyGroup);
        final int groupSize = groupSize(cache, economyGroup);

        if (totalValue == 0 || groupSize == 0) return;

        getFilteredProducts(cache, economyGroup)
                .forEach(product -> {
                    final double price = totalValue / (groupSize * (product.getAmountSold() + 1));

                    // Squash the price into the min and max values interval
                    // Disabling the limits during the test since it depends on configuration

                    // final double squashedPrice = Math.min(product.getMaxPrice(), Math.max(product.getMinPrice(), price));
                    product.setBuyPrice(price);
                    cache.add(product.getId(), product.getData(), product);
                });

        resetAmountSold(cache, economyGroup);
    }

    public static void updateAllProductsPrice(ProductCache cache) {
        for (EconomyGroup economyGroup : EconomyGroup.values()) {
            updateProductsPrice(cache, economyGroup);
        }
    }

    private static double totalValue(ProductCache cache, EconomyGroup economyGroup) {
        return getFilteredProducts(cache, economyGroup)
                .mapToDouble(product -> product.getBuyPrice() * product.getAmountSold())
                .sum();
    }

    private static int groupSize(ProductCache cache, EconomyGroup economyGroup) {
        return (int) getFilteredProducts(cache, economyGroup).count();
    }

    private static Stream<Product> getFilteredProducts(ProductCache cache, EconomyGroup economyGroup) {
        return cache.getProducts().stream()
                .filter(product -> product.getEconomyGroup() == economyGroup);
    }

    private static void resetAmountSold(ProductCache cache, EconomyGroup economyGroup) {
        getFilteredProducts(cache, economyGroup).forEach(product -> {
            product.setAmountSold(0);
            cache.add(product.getId(), product.getData(), product);
        });
    }
}
