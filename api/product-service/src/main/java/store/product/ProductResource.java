package store.product;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductResource implements ProductController {

    @Autowired
    private ProductService service;

    @Override
    public ResponseEntity<ProductOut> create(ProductIn in) {
        Product product = service.create(ProductParser.to(in));
        return ResponseEntity.status(201).body(ProductParser.to(product));
    }

    @Override
    public ResponseEntity<List<ProductOut>> findAll() {
        return ResponseEntity.ok(ProductParser.to(service.findAll()));
    }

    @Override
    public ResponseEntity<ProductOut> findById(UUID id) {
        Product product = service.findById(id);
        if (product == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ProductParser.to(product));
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
