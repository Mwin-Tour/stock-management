package com.example.gestionstock.controller;

import com.example.gestionstock.dtos.CommadeDTO;
import com.example.gestionstock.dtos.ProductDTO;
import com.example.gestionstock.entity.Category;
import com.example.gestionstock.entity.Product;
import com.example.gestionstock.repository.CategoryRepository;
import com.example.gestionstock.services.CommandeService;
import com.example.gestionstock.services.ProductService;
import com.example.gestionstock.services.impl.ProductServiceImpl;
import com.example.gestionstock.services.impl.RapportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    private final CategoryRepository categoryRepository;
    private ProductService productService;
    private CommandeService commandeService;
    private RapportService rapportService;

    public DashboardController(ProductService productService, CommandeService commandeService, CategoryRepository categoryRepository, RapportService rapportService) {
        this.productService = productService;
        this.commandeService = commandeService;
        this.categoryRepository = categoryRepository;
        this.rapportService = rapportService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        long totalItems = productService.countAllProducts();
        double inventoryValue = commandeService.getTotalPriceOfAllCommandes();
        long lowStockItems = productService.nombreProduitParEnStockFaible();
        long totalCategories = categoryRepository.count();

        List<ProductDTO> products= productService.getAllProducts();
        Map<Category, Long> itemsByCategory = products.stream()
                .filter(Objects::nonNull)
                .filter(product -> product.getCategory() != null)
                .collect(Collectors.groupingBy(
                        ProductDTO::getCategory,
                        Collectors.counting()
                ));

        List<ProductDTO> lowStockProducts = products.stream().filter(p -> p.getQuantiteEnStock() <= p.getSeuilAlerte()).collect(Collectors.toList());


        model.addAttribute("lowStockProductList", lowStockProducts); // List<Product>
        model.addAttribute("itemsByCategory", itemsByCategory); // Map<String, Integer>
        model.addAttribute("totalItems", totalItems); // Pour le pourcentage


        model.addAttribute("totalItems", totalItems);
        model.addAttribute("inventoryValue", inventoryValue);
        model.addAttribute("lowStockItems", lowStockItems);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("itemsByCategory", itemsByCategory);

        return "dashboard";
    }

    @GetMapping("/rapport/generer")
    public ResponseEntity<byte[]> genererRapport() {
        List<CommadeDTO> commadeDTOS=commandeService.getAllCommande();
        List<LinkedHashMap<Product, Integer>> collect = commadeDTOS.stream().map(p -> commandeService.matchProductsToCommande(p.getProductIds(), p.getQuantities())).collect(Collectors.toList());

        byte[] pdf = rapportService.genererRapportGlobal(collect);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "rapport_global.pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }


}
