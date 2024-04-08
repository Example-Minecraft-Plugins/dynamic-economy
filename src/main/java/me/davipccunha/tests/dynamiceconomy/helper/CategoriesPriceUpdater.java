package me.davipccunha.tests.dynamiceconomy.helper;

import me.davipccunha.tests.dynamiceconomy.cache.ProductCache;
import me.davipccunha.tests.dynamiceconomy.model.EconomyGroup;

import java.util.Arrays;

public class CategoriesPriceUpdater extends PriceUpdater {
    // This algorithm calculates how the price of all products in a category should change to balance out the category's participation in the total value of the economy
    // If all the categories have the same total value, the price will remain the same (this means their prices are fair)
    // If a category has more value than the average, all its products' prices will decrease proportionally (balances the economy)
    // If a product has less value than the average, all its products' prices will increase proportionally (balances the economy)
    // This is an inter-category price updater, meaning that it multiplies the prices of all products in a category by the same amount
    public static void updateCategoriesPrices(ProductCache cache) {
        final int numberOfCategories = EconomyGroup.values().length;
        final float targetFactor = 1.0f / numberOfCategories;
        final double totalValue = totalValue(cache);

        if (totalValue == 0) return;

        for (EconomyGroup economyGroup : EconomyGroup.values()) {
            final double categoryTotalValue = categoryTotalValue(cache, economyGroup);

            final double smallestParticipationFactor = getSmallestValueCategory(cache) / totalValue;
            final double zeroFactor = smallestParticipationFactor / numberOfCategories;

            final double currentParticipationFactor = categoryTotalValue == 0 ?
                    zeroFactor :
                    categoryTotalValue / totalValue;

            final double factor = targetFactor / currentParticipationFactor;

            multiplyPrices(cache, economyGroup, factor);
        }
    }

    private static double totalValue(ProductCache cache) {
        return Arrays.stream(EconomyGroup.values())
                .mapToDouble(economyGroup -> categoryTotalValue(cache, economyGroup))
                .sum();
    }

    private static void multiplyPrices(ProductCache cache, EconomyGroup economyGroup, double factor) {
        getFilteredProducts(cache, economyGroup).forEach(product -> {
            product.setBuyPrice(product.getBuyPrice() * factor);
            cache.add(product.getId(), product.getData(), product);
        });
    }

    private static double getSmallestValueCategory(ProductCache cache) {
        return Arrays.stream(EconomyGroup.values())
                .filter(economyGroup -> categoryTotalValue(cache, economyGroup) > 0)
                .mapToDouble(economyGroup -> categoryTotalValue(cache, economyGroup))
                .min().orElse(1);
    }
}
