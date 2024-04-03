package me.davipccunha.tests.dynamiceconomy.helper;

import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import me.davipccunha.tests.dynamiceconomy.model.EconomyGroup;
import me.davipccunha.tests.dynamiceconomy.model.Product;

import java.util.Collection;
import java.util.stream.Collectors;

public class ProductsPriceUpdater {
    public static void updateProductsPrice(ProductCache cache, EconomyGroup economyGroup) {
        final double totalValue = totalValue(cache, economyGroup);
        final int groupSize = groupSize(cache, economyGroup);

        if (totalValue == 0 || groupSize == 0) return;

        Collection<Product> filteredProducts = getFilteredProducts(cache, economyGroup);

        for (Product product : filteredProducts) {
            final double price = totalValue / (groupSize * (product.getAmountSold() + 1));
            final double average = filteredProducts.stream().mapToDouble(Product::getBuyPrice).average().orElse(0);

            // Normalize the price into the min and max values interval
            // TODO: Test different methods to normalize the price -> https://en.wikipedia.org/wiki/Feature_scaling
            final double squashedPrice = price * (product.getMinPrice() + product.getMaxPrice()) / 2 / average;
            product.setBuyPrice(squashedPrice);
            cache.add(product.getId(), product.getData(), product);
        }

        resetAmountSold(cache, economyGroup);
    }

    public static void updateAllProductsPrice(ProductCache cache) {
        for (EconomyGroup economyGroup : EconomyGroup.values()) {
            updateProductsPrice(cache, economyGroup);
        }
    }

    private static double totalValue(ProductCache cache, EconomyGroup economyGroup) {
        return getFilteredProducts(cache, economyGroup).stream()
                .mapToDouble(product -> product.getBuyPrice() * product.getAmountSold())
                .sum();
    }

    private static int groupSize(ProductCache cache, EconomyGroup economyGroup) {
        return getFilteredProducts(cache, economyGroup).size();
    }

    private static Collection<Product> getFilteredProducts(ProductCache cache, EconomyGroup economyGroup) {
        return cache.getProducts().stream()
                .filter(product -> product.getEconomyGroup() == economyGroup).collect(Collectors.toList());
    }

    private static void resetAmountSold(ProductCache cache, EconomyGroup economyGroup) {
        getFilteredProducts(cache, economyGroup).forEach(product -> {
            product.setAmountSold(0);
            cache.add(product.getId(), product.getData(), product);
        });
    }
}
