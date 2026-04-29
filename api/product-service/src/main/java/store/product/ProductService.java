package store.product;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Product create(Product product) {
        ProductModel saved = repository.save(ProductParser.toModel(product));
        return ProductParser.to(saved);
    }

    public List<Product> findAll() {
        return repository.findAll().stream()
            .map(ProductParser::to)
            .toList();
    }

    public Product findById(UUID id) {
        return ProductParser.to(repository.findById(id).orElse(null));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
