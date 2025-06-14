package com.example.productcatalogapp.service;

import com.example.productcatalogapp.core.Product;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // Benzersiz ID oluşturmak için

@Service // Bu sınıfın bir Spring servisi olduğunu belirtir
public class ProductService {

    // Ürünleri bellekte tutan geçici liste
    private final List<Product> products = new ArrayList<>();

    public ProductService() {
        // Başlangıçta bazı örnek ürünler ekleyelim
        products.add(new Product(UUID.randomUUID().toString(), "Laptop", "Güçlü işlemcili dizüstü bilgisayar", 1200.00));
        products.add(new Product(UUID.randomUUID().toString(), "Mouse", "Ergonomik kablosuz fare", 25.50));
        products.add(new Product(UUID.randomUUID().toString(), "Keyboard", "Mekanik oyuncu klavyesi", 75.00));
    }

    public List<Product> findAll() {
        return new ArrayList<>(products); // Listeyi kopyalayarak dışarı veriyoruz
    }

    public Optional<Product> findById(String id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public void addProduct(Product product) {
        // Yeni ürün eklerken benzersiz bir ID atayalım
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(UUID.randomUUID().toString());
        }
        products.add(product);
    }

    // İsteğe bağlı: Ürün silme veya güncelleme metotları da eklenebilir
}