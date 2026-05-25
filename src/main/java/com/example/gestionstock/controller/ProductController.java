package com.example.gestionstock.controller;

import com.example.gestionstock.dtos.ProductDTO;
import com.example.gestionstock.services.CategoryService;
import com.example.gestionstock.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // Ajout de l'image du produit
    @GetMapping("/products")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute ProductDTO productDTO,@RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) throws IOException {
        if (!imageFile.isEmpty()) {
            // Crée le dossier s’il n’existe pas
            String uploadDir = "uploads/images";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Sauvegarde le chemin relatif dans la BDD
            productDTO.setPathImage("/" + uploadDir + "/" + fileName);
        }

        productService.addProduct(productDTO);
        redirectAttributes.addFlashAttribute("success", "Produit ajouter avec succès.");
        return "redirect:/products";
    }

    @PostMapping("/products/update")
    public String updateProduct(@ModelAttribute ProductDTO productDTO) {
        productService.updateProduct(productDTO);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé avec succès.");
            return "redirect:/products";
        }catch (RuntimeException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }
}
