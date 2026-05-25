package com.example.gestionstock.services.impl;

import com.example.gestionstock.dtos.CommadeDTO;
import com.example.gestionstock.entity.Commande;
import com.example.gestionstock.entity.Product;
import com.example.gestionstock.mapper.ProductMapper;
import com.example.gestionstock.repository.CommandeRepository;
import com.example.gestionstock.repository.ProductRepository;
import com.example.gestionstock.services.CommandeService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RapportService {
    private ProductRepository productRepository;
    private CommandeRepository commandeRepository;
    private CommandeService commandeService;
    private ProductMapper productMapper;


    public RapportService(ProductRepository repository, CommandeRepository commandeRepository, CommandeService commandeService, ProductMapper productMapper) {
        this.productRepository = repository;
        this.commandeRepository = commandeRepository;
        this.commandeService = commandeService;
        this.productMapper = productMapper;
    }

    public byte[] genererRapportGlobal(List<LinkedHashMap<Product, Integer>> list) {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, out);

            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    try {
                        PdfContentByte canvas = writer.getDirectContentUnder();
                        Font watermarkFont = new Font(Font.FontFamily.HELVETICA, 52, Font.BOLD, new GrayColor(0.85f));
                        Phrase watermark = new Phrase("Rapport Global", watermarkFont);
                        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                                watermark, 297.5f, 421, 45);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            document.open();

            // Titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Rapport Global de Gestion de Stock", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Chiffres globaux
            Font font = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph globalStats = new Paragraph();
            globalStats.add("Nombre total de produits : " + productRepository.count() + "\n");
            globalStats.add("Nombre total de commandes : " + commandeRepository.count() + "\n");
            java.util.List<Product> products= productRepository.findAll();
            int totalQuantity=0;
            for (int i=0;i<=products.size()-1;i++){
                totalQuantity+=products.get(i).getQuantiteEnStock()*products.get(i).getPrixUnitaire();
            }

            globalStats.add("Valeur totale du stock : " + totalQuantity + " FCFA\n");
            globalStats.setSpacingAfter(15f);
            document.add(globalStats);

            // Tableau des produits
            Paragraph produitSection = new Paragraph("Produits :", titleFont);
            produitSection.setSpacingBefore(10f);
            document.add(produitSection);

            PdfPTable produitTable = new PdfPTable(4);
            produitTable.setWidthPercentage(100);
            produitTable.setWidths(new float[]{3, 2, 2, 2});
            String[] headers = {"Nom", "Catégorie", "Prix", "Stock"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, font));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                produitTable.addCell(cell);
            }

            java.util.List<Product> produits = productRepository.findAll();
            for (Product p : produits) {
                produitTable.addCell(p.getLibelle());
                produitTable.addCell(p.getCategory().getName());
                produitTable.addCell(String.format("%.2f", p.getPrixUnitaire()));
                produitTable.addCell(String.valueOf(p.getQuantiteEnStock()));
            }

            document.add(produitTable);

            Paragraph commandeSection = new Paragraph("\nCommandes :", titleFont);
            document.add(commandeSection);

            PdfPTable commandeTable = new PdfPTable(3);
            commandeTable.setWidthPercentage(100);
            commandeTable.setWidths(new float[]{2, 3, 3});
            String[] cmdHeaders = {"N°", "Client", "Date"};
            for (String h : cmdHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(h, font));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                commandeTable.addCell(cell);
            }

            List<CommadeDTO> commandes = commandeService.getAllCommande();
            System.out.println("---------------------------------------------");
            System.out.println(commandes);
            for (CommadeDTO c : commandes) {
                commandeTable.addCell(String.valueOf(c.getId()));
                commandeTable.addCell(c.getClientInfo().getClientName());
                commandeTable.addCell(String.valueOf(new Date()));
            }
            document.add(commandeTable);


            Paragraph BestSellingSection = new Paragraph("\nBest Selling :", titleFont);
            document.add(BestSellingSection);

            PdfPTable bestSellingTable = new PdfPTable(3);
            bestSellingTable.setWidthPercentage(100);
            bestSellingTable.setWidths(new float[]{2, 2, 2});
            String[] bestSellingHeaders = {"Meilleur vente", "Nombre vendue", "Chiffre generer"};
            for (String h : bestSellingHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(h, font));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                bestSellingTable.addCell(cell);
            }
            // Classer les produit du mieux vendu au moin vendu
            LinkedHashMap<Product,Integer> bestSellingProducts=new LinkedHashMap<>();
            for (LinkedHashMap<Product, Integer> map : list) {
                for (Map.Entry<Product, Integer> entry : map.entrySet()) {
                    Product product = entry.getKey();
                    int quantity = entry.getValue();
                    if(bestSellingProducts.get(product)==null){
                        bestSellingProducts.put(product,quantity);
                    }else {
                        bestSellingProducts.put(product,bestSellingProducts.get(product)+quantity);
                    }
                }
            }

            LinkedHashMap<Product, Integer> sortedByValue = bestSellingProducts.entrySet()
                    .stream()
                    //S.sorted(Map.Entry.comparingByValue()) // Tri ascendant (20, 25, 30)
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // Tri descendant (30, 25, 20)
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldVal, newVal) -> oldVal,
                                    LinkedHashMap::new
                            )
                    );


            for (Map.Entry<Product, Integer> entry : sortedByValue.entrySet()) {
                Product p = entry.getKey();
                int quantiteVendue = entry.getValue();

                bestSellingTable.addCell(p.getLibelle());
                bestSellingTable.addCell(String.valueOf(quantiteVendue));
                bestSellingTable.addCell(String.format("%.2f", quantiteVendue * p.getPrixUnitaire()));
            }

            document.add(bestSellingTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du rapport PDF", e);
        }
    }

}
