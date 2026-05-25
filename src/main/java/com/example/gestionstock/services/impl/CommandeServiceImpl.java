package com.example.gestionstock.services.impl;

import com.example.gestionstock.dtos.CommadeDTO;
import com.example.gestionstock.dtos.ProductDTO;
import com.example.gestionstock.entity.Commande;
import com.example.gestionstock.entity.Product;
import com.example.gestionstock.mapper.CommandeMapper;
import com.example.gestionstock.mapper.ProductMapper;
import com.example.gestionstock.repository.CommandeRepository;
import com.example.gestionstock.services.CommandeService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.transaction.Transactional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Date;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class CommandeServiceImpl implements CommandeService {
    private final CommandeRepository commandeRepository;
    private final ProductServiceImpl productService;

    public CommandeServiceImpl(CommandeRepository commandeRepository, ProductServiceImpl productService) {
        this.commandeRepository = commandeRepository;
        this.productService = productService;
    }

    @Override
    public CommadeDTO addCommande(CommadeDTO commandeDTO, List<Long> productIds,LinkedHashMap<Product, Integer> productIntegerLinkedHashMap) {

        System.out.println("Apres la recuperation des produits dans le addCommande");
        //System.out.println("productIds" + productIds);
        System.out.println("Products map quantities" + productIntegerLinkedHashMap);
        Commande commande = CommandeMapper.toCommandeSansProduits(commandeDTO);

        List<Product> products=new ArrayList<>();

        // Decrementer le stock des produits de la commande
        for (Map.Entry<Product, Integer> entry : productIntegerLinkedHashMap.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            product.setQuantiteEnStock(product.getQuantiteEnStock() - quantity);
            ProductDTO productDTO = productService.updateProduct(ProductMapper.toProductDTO(product));
            products.add(ProductMapper.toProduct(productDTO));
        }
        commande.setProducts(products);
        commande.setCreatAt(new Date(System.currentTimeMillis()));
        Commande saved = commandeRepository.save(commande);
        return CommandeMapper.toCommadeDTO(saved);
    }

    @Override
    public CommadeDTO getCommandeById(Long id) {
        Commande commande = commandeRepository.findById(id).orElse(null);
        if (commande == null) {
            throw new RuntimeException("Commande introuvable");
        }
        return CommandeMapper.toCommadeDTO(commande);
    }

    @Override
    public List<CommadeDTO> getAllCommande() {
        List<Commande> commandes = commandeRepository.findAll();
        return commandes.stream()
                .map(CommandeMapper::toCommadeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommadeDTO updateCommade(CommadeDTO commandeDTO) {
        if (commandeDTO.getId() == null) {
            throw new RuntimeException("L'ID de la commande est requis pour la mise à jour.");
        }

        Optional<Commande> optional = commandeRepository.findById(commandeDTO.getId());
        if (optional.isEmpty()) {
            throw new RuntimeException("Commande introuvable pour mise à jour.");
        }

        Commande existingCommande = optional.get();
        existingCommande.setClientInfo(commandeDTO.getClientInfo());
        existingCommande.setPrixTotal(commandeDTO.getPrixTotal());

        List<Product> validatedProducts = new ArrayList<>();
        /*for (Product p : CommandeMapper.toCommadeDTO(commandeDTO).getProducts()) {
            Product productFromDB = ProductMapper.toProduct(productService.getProductById(p.getId()));
            if (productFromDB != null) {
                validatedProducts.add(productFromDB);
            }
        }*/

        existingCommande.setProducts(validatedProducts);
        Commande updated = commandeRepository.save(existingCommande);
        return CommandeMapper.toCommadeDTO(updated);
    }

    @Override
    public void deleteCommade(Long id) {
        commandeRepository.deleteById(id);
    }

    @Override
    public Double getTotalPriceOfAllCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        Double totalPrice = 0.0;
        List<Double> list = commandes.stream().map(c -> c.getPrixTotal()).toList();
        for (Double d : list) {
            totalPrice += d;
        }

        return totalPrice;
    }

    @Override
    public Double getTotalPriceOfCommande(Long id) {
        Commande commande = commandeRepository.findById(id).orElse(null);
        if(commande==null) return null;
        return commande.getPrixTotal();
    }

    @Override
    public byte[] genererFacturePourCommande(Long id, LinkedHashMap<Product,Integer> map) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Ajout d'un filigrane
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        PdfContentByte canvas = writer.getDirectContentUnder();
                        Font watermarkFont = new Font(Font.FontFamily.HELVETICA, 52, Font.BOLD, new GrayColor(0.75f));
                        Phrase watermark = new Phrase("RootManagement", watermarkFont);
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                                watermark, 297.5f, 421, 45); // Centré et incliné
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            document.open();

            // Logo
            InputStream logoStream = new ClassPathResource("static/images/logo.png").getInputStream();
            Image logo = Image.getInstance(logoStream.readAllBytes());
            logo.scaleToFit(120, 60);
            logo.setAlignment(Image.ALIGN_RIGHT);
            document.add(logo);

            // Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("FACTURE - RootManagement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Infos client
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph infos = new Paragraph();
            infos.add("Commande N°: " + commande.getId() + "\n");
            infos.add("Client : " + commande.getClientInfo().getClientName() + "\n");
            infos.add("Adresse : " + commande.getClientInfo().getClientAddress() + "\n");
            infos.add("Téléphone : " + commande.getClientInfo().getClientPhoneNumber() + "\n");
            infos.setSpacingAfter(15f);
            document.add(infos);

            // Tableau des produits
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setWidths(new float[]{3, 2, 2, 2});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            String[] headers = {"Produit", "Prix unitaire", "Quantité", "Total"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            double totalCommande = 0.0;

            for (Map.Entry<Product,Integer> ligne : map.entrySet()) {
                Product produit = ligne.getKey();
                int quantite = ligne.getValue();
                double prixUnitaire = produit.getPrixUnitaire();
                double totalProduit = prixUnitaire * quantite;

                table.addCell(produit.getLibelle());
                table.addCell(String.format("%.2f", prixUnitaire));
                table.addCell(String.valueOf(quantite));
                table.addCell(String.format("%.2f", totalProduit));

                totalCommande += totalProduit;
            }

            document.add(table);

            // Total global
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Paragraph total = new Paragraph("\nPrix total : " + String.format("%.2f", totalCommande) + " FCFA", boldFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur PDF : " + e.getMessage(), e);
        }
    }



    @Override
    public LinkedHashMap<Product,Integer> matchProductsToCommande(List<Long> productIds,List<Integer> quantities) {
        LinkedHashMap<Product,Integer> map = new LinkedHashMap<>();
        for (int i=0;i<=productIds.size()-1;i++){
            ProductDTO byId = productService.getProductById(productIds.get(i));
            Product product = ProductMapper.toProduct(byId);
            map.put(product,quantities.get(i));
        }
        return map;
    }

    @Override
    public Double calculCommandePriceWithProduct(LinkedHashMap<Product, Integer> list) {
        Double montant=0.0;
        for (Map.Entry<Product,Integer> entry : list.entrySet()){
            montant+=entry.getKey().getPrixUnitaire()*entry.getValue();
        }
        return montant;
    }

    @Override
    public Boolean verifierQuantiteEnStock(LinkedHashMap<Product, Integer> map) {
        for (Map.Entry<Product, Integer> entry : map.entrySet()) {
            if (entry.getKey().getQuantiteEnStock() < entry.getValue()) {
                throw new RuntimeException("Quantité en stock insuffisante pour " + entry.getKey().getLibelle());
            }
        }
        return true;
    }

}