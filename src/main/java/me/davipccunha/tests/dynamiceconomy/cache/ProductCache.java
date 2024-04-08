package me.davipccunha.tests.dynamiceconomy.cache;

import lombok.NoArgsConstructor;
import me.davipccunha.tests.dynamiceconomy.factory.ProductFactory;
import me.davipccunha.tests.dynamiceconomy.helper.ProductsPriceUpdater;
import me.davipccunha.tests.dynamiceconomy.model.EconomyGroup;
import me.davipccunha.tests.dynamiceconomy.model.Product;
import me.davipccunha.tests.dynamiceconomy.util.ObjectSerializer;
import me.davipccunha.tests.signshop.api.model.Shop;
import me.davipccunha.tests.signshop.api.model.ShopLocation;
import me.davipccunha.tests.signshop.api.model.SignShopAPI;
import org.bukkit.configuration.file.FileConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ProductCache {
    private final RedisConnector redisConnector = new RedisConnector("localhost", 6379, "davi123");

    public void add(int id, short data, Product product) {
        try (Jedis jedis = redisConnector.getJedis()) {
            final Pipeline pipeline = jedis.pipelined();
            pipeline.hset("products", id + ":" + data, ObjectSerializer.serialize(product));
            pipeline.sync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(int id, short data) {
        if (this.has(id, data)) {
            try (Jedis jedis = redisConnector.getJedis()) {
                final Pipeline pipeline = jedis.pipelined();
                pipeline.hdel("products", id + ":" + data);
                pipeline.sync();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean has(int id, short data) {
        try (Jedis jedis = redisConnector.getJedis()) {
            Pipeline pipeline = jedis.pipelined();
            Response<Boolean> response = pipeline.hexists("products", id + ":" + data);
            pipeline.sync();

            if (response == null || response.get() == null) return false;

            return response.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Product get(int id, short data) {
        try (Jedis jedis = redisConnector.getJedis()) {
            Pipeline pipeline = jedis.pipelined();
            Response<String> response = pipeline.hget("products", id + ":" + data);
            pipeline.sync();

            if (response == null || response.get() == null) return null;

            return ObjectSerializer.deserialize(response.get(), Product.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Product get(Shop shop) {
        return this.get(shop.getItemID(), shop.getItemData());
    }

    public Collection<Product> getProducts() {
        try (Jedis jedis = redisConnector.getJedis()) {
            Pipeline pipeline = jedis.pipelined();
            Response<Map<String, String>> response = pipeline.hgetAll("products");
            pipeline.sync();

            if (response == null || response.get() == null) return null;

            return response.get().values().stream()
                    .map(product -> ObjectSerializer.deserialize(product, Product.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Collection<ShopLocation> getShopsFromProduct(SignShopAPI api, Product product) {
        return api.getAdminShops().stream()
                .filter(shop -> shop.getItemID() == product.getId()
                        && shop.getItemData() == product.getData())
                .map(Shop::getLocation)
                .collect(Collectors.toList());
    }

    public void init(FileConfiguration config) {
        config.getConfigurationSection("products").getKeys(false).forEach(key -> {
            String[] split = key.split(":");
            int id = Integer.parseInt(split[0]);
            short data = split.length > 1 ? Short.parseShort(split[1]) : 0;


            if (this.has(id, data)) return;

            Product product = ProductFactory.createProduct(config, id, data);
            this.add(id, data, product);
        });
    }

    public void updateShopsPrices(SignShopAPI api) {
        for (Shop shop : api.getAdminShops()) {
            Product product = this.get(shop.getItemID(), shop.getItemData());

            if (product == null) continue;

            double fullPrice = product.getBuyPrice() * shop.getBuyAmount();
            api.setBuyPrice(shop.getLocation(), fullPrice);
            api.setSellPrice(shop.getLocation(), fullPrice * 1.3);
        }

        api.updateAdminShops();
    }

    public void updateAllProductsPrices() {
        ProductsPriceUpdater.updateAllProductsPrice(this);
    }

    public void updateProductsPrice(EconomyGroup economyGroup) {
        ProductsPriceUpdater.updateProductsPrice(this, economyGroup, true);
    }
}
