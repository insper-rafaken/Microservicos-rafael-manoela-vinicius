package store.product;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Product create(Product product) {
        ProductModel saved = repository.save(ProductParser.toModel(product));
        return ProductParser.to(saved);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return repository.findAll().stream()
            .map(ProductParser::to)
            .toList();
    }

    @Transactional(readOnly = true)
    public Product findById(UUID id) {
        return ProductParser.to(repository.findById(id).orElse(null));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
