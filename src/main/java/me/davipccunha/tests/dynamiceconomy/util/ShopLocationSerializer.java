package me.davipccunha.tests.dynamiceconomy.util;

import me.davipccunha.tests.signshop.api.model.ShopLocation;

public class ShopLocationSerializer {
    public static String serialize(ShopLocation location) {

        return location.getWorldName() + ";" +
                location.getX() + ";" +
                location.getY() + ";" +
                location.getZ();
    }
}
