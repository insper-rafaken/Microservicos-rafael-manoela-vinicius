package store.product;

import java.util.List;

public class ProductParser {

    public static Product to(ProductIn in) {
        if (in == null) return null;
        return Product.builder()
            .name(in.name())
            .price(in.price())
            .unit(in.unit())
            .build();
    }

    public static ProductOut to(Product p) {
        if (p == null) return null;
        return ProductOut.builder()
            .id(p.id())
            .name(p.name())
            .price(p.price())
            .unit(p.unit())
            .build();
    }

    public static List<ProductOut> to(List<Product> products) {
        return products.stream().map(ProductParser::to).toList();
    }

    public static Product to(ProductModel m) {
        if (m == null) return null;
        return Product.builder()
            .id(m.id())
            .name(m.name())
            .price(m.price())
            .unit(m.unit())
            .build();
    }

    public static ProductModel toModel(Product p) {
        if (p == null) return null;
        ProductModel model = new ProductModel();
        model.name(p.name());
        model.price(p.price());
        model.unit(p.unit());
        return model;
    }
}
