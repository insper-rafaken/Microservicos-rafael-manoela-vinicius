package store.product;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true, fluent = true)
public class Product {

    private UUID id;
    private String name;
    private BigDecimal price;
    private String unit;
}
