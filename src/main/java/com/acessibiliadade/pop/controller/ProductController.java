package com.acessibiliadade.pop.controller;

import com.acessibiliadade.pop.dto.ProductDTOs.ProductRequest;
import com.acessibiliadade.pop.dto.ProductDTOs.ProductResponse;
import com.acessibiliadade.pop.model.Product;
import com.acessibiliadade.pop.security.AuthorizationService;
import com.acessibiliadade.pop.service.AccessibilityService;
import com.acessibiliadade.pop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AccessibilityService accessibilityService;
    private final AuthorizationService authorizationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return ProductResponse.from(productService.createProduct(toEntity(request)));
    }

    // Endpoint público — visitante anônimo vê o catálogo sem filtro de acessibilidade;
    // usuário logado continua recebendo a lista filtrada pelo próprio perfil.
    @GetMapping
    public List<Product> listAll() {
        List<Product> products = productService.getAllProducts();
        return accessibilityService.filterProductsByAccessibility(
                products, authorizationService.currentUserIdOrNull());
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ProductResponse.from(productService.updateProduct(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        return productService.updateStock(id, quantity);
    }

    private Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setFranchise(request.franchise());
        product.setRarity(request.rarity());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());
        return product;
    }
}