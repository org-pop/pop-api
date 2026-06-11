package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.model.Product;
import com.acessibiliadade.pop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping
    public List<Product> listAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/franchise/{franchise}")
    public List<Product> getByFranchise(@PathVariable String franchise) {
        return productService.getProductsByFranchise(franchise);
    }

    @GetMapping("/rarity/{rarity}")
    public List<Product> getByRarity(@PathVariable String rarity) {
        return productService.getProductsByRarity(rarity);
    }

    @GetMapping("/price-range")
    public List<Product> getByPriceRange(@RequestParam BigDecimal min,
                                         @RequestParam BigDecimal max) {
        return productService.getProductsByPriceRange(min, max);
    }

    @GetMapping("/low-stock")
    public List<Product> getLowStock(@RequestParam(defaultValue = "10") Integer threshold) {
        return productService.getLowStockProducts(threshold);
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam String name) {
        return productService.searchProductsByName(name);
    }

    @PatchMapping("/{id}/stock")
    public Product updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        return productService.updateStock(id, quantity);
    }
}