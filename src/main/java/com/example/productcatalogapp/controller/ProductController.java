package com.example.productcatalogapp.controller;

import com.example.productcatalogapp.core.Product;
import com.example.productcatalogapp.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/products") // Tüm URL'lerin başında /products olacak
public class ProductController {

    private final ProductService productService;

    // ProductService'ı enjekte ediyoruz (Dependency Injection)
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Ana sayfa için zaten oluşturduğumuz metot, ancak şimdi /product altında değil
    // @GetMapping("/") metodunu ana uygulama sınıfından kaldıralım, çünkü ProductController'da @RequestMapping("/products") var
    // Veya daha basit olması için, sadece ProductController'daki "/" mapping'ini "/" olarak bırakıp, ana dizini buraya yönlendirebiliriz.
    // Şimdilik ProductController'ı sadece ürünle ilgili URL'ler için kullanacağız.
    // Ana dizin ("/") hala eski controller metodumuzda kalacak.

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products"; // src/main/resources/templates/products.html dosyasını render edecek
    }

    @GetMapping("/new")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product()); // Boş bir Product objesi gönderiyoruz
        return "add-product"; // src/main/resources/templates/add-product.html dosyasını render edecek
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") Product product) {
        productService.addProduct(product);
        return "redirect:/products"; // Ürün eklendikten sonra ürün listesi sayfasına yönlendir
    }

    @GetMapping("/{id}")
    public String viewProductDetails(@PathVariable String id, Model model) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product-detail"; // src/main/resources/templates/product-detail.html
        } else {
            return "redirect:/products"; // Bulunamazsa listeye geri dön
        }
    }
}