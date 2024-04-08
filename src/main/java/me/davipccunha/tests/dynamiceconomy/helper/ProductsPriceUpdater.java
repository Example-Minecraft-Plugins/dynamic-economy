package me.davipccunha.tests.dynamiceconomy.helper;

import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import me.davipccunha.tests.dynamiceconomy.model.EconomyGroup;
import me.davipccunha.tests.dynamiceconomy.model.Product;

import java.util.Collection;

public class ProductsPriceUpdater extends PriceUpdater {
    // This algorithm calculates how the price of a product should change inversely proportional to the amount sold
    // If all the products in a category are sold the same amount of times, the price will remain the same (this means their prices are fair)
    // If a product is sold more than the average, its price will decrease (balances the economy)
    // If a product is sold less than the average, its price will increase (balances the economy)
    // This is an intra-category price updater, meaning that it updates the price of all products in a category
    public static void updateProductsPrice(ProductCache cache, EconomyGroup economyGroup, boolean resetAmounts) {
        final double totalValue = categoryTotalValue(cache, economyGroup);
        final int groupSize = groupSize(cache, economyGroup);

        if (totalValue == 0 || groupSize == 0) return;

        Collection<Product> filteredProducts = getFilteredProducts(cache, economyGroup);
        final double averagePrice = totalValue / totalQuantity(cache, economyGroup);
        final int lessSold = filteredProducts.stream()
                .filter(p -> p.getAmountSold() > 0)
                .mapToInt(Product::getAmountSold)
                .min().orElse(1);

        for (Product product : filteredProducts) {
            if (product.getAmountSold() == 0) product.setAmountSold(lessSold / (groupSize + 1));

            final double price = totalValue / (groupSize * product.getAmountSold());

            // Normalize the price
            final double normalizedPrice = price / averagePrice * product.getBuyPrice();
            product.setBuyPrice(normalizedPrice);
            cache.add(product.getId(), product.getData(), product);
        }

        if (resetAmounts) resetAmountSold(cache, economyGroup);
    }

    public static void updateAllProductsPrice(ProductCache cache) {
        for (EconomyGroup economyGroup : EconomyGroup.values()) {
            updateProductsPrice(cache, economyGroup, false);
        }

        CategoriesPriceUpdater.updateCategoriesPrices(cache);
        resetAllAmountSold(cache);
    }

    private static int totalQuantity(ProductCache cache, EconomyGroup economyGroup) {
        return getFilteredProducts(cache, economyGroup).stream()
                .mapToInt(Product::getAmountSold)
                .sum();
    }

    private static int groupSize(ProductCache cache, EconomyGroup economyGroup) {
        return getFilteredProducts(cache, economyGroup).size();
    }

    private static void resetAmountSold(ProductCache cache, EconomyGroup economyGroup) {
        getFilteredProducts(cache, economyGroup).forEach(product -> {
            product.setAmountSold(0);
            cache.add(product.getId(), product.getData(), product);
        });
    }

    private static void resetAllAmountSold(ProductCache cache) {
        for (EconomyGroup economyGroup : EconomyGroup.values()) {
            resetAmountSold(cache, economyGroup);
        }
    }
}
