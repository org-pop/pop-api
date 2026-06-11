package com.acessibiliadade.pop.service;

import com.acessibiliadade.pop.model.Product;
import com.acessibiliadade.pop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setImageUrl(productDetails.getImageUrl());
        product.setFranchise(productDetails.getFranchise());
        product.setRarity(productDetails.getRarity());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public List<Product> getProductsByFranchise(String franchise) {
        return productRepository.findByFranchiseIgnoreCase(franchise);
    }

    public List<Product> getProductsByRarity(String rarity) {
        return productRepository.findByRarityIgnoreCase(rarity);
    }

    public List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }

    public List<Product> getLowStockProducts(Integer stock) {
        return productRepository.findByStockLessThan(stock);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product updateStock(Long id, Integer quantity) {
        Product product = getProductById(id);
        int newStock = product.getStock() + quantity;

        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock! Current stock: " + product.getStock());
        }

        product.setStock(newStock);
        return productRepository.save(product);
    }

    public boolean checkStock(Long id, Integer requestedQuantity) {
        Product product = getProductById(id);
        return product.getStock() >= requestedQuantity;
    }
}