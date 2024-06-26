package com.example.servicecatalogue.services;

import com.example.servicecatalogue.dtos.ProduitDTO;
import com.example.servicecatalogue.dtos.ProduitUpdateDTO;
import com.example.servicecatalogue.dtos.out.ProduitOutPaginateDTO;
import com.example.servicecatalogue.dtos.pagination.Paginate;
import com.example.servicecatalogue.dtos.pagination.PaginateRequestDTO;
import com.example.servicecatalogue.dtos.pagination.Pagination;
import com.example.servicecatalogue.dtos.rabbits.*;
import com.example.servicecatalogue.enums.Couleurs;
import com.example.servicecatalogue.exceptions.InvalideCommandeException;
import com.example.servicecatalogue.exceptions.QuantiteIndisponibleCommandeException;
import com.example.servicecatalogue.exceptions.StockExisteDejaException;
import com.example.servicecatalogue.exceptions.StockNotFoundException;
import com.example.servicecatalogue.modele.Categorie;
import com.example.servicecatalogue.modele.Produit;
import com.example.servicecatalogue.modele.Stocks;
import com.example.servicecatalogue.repositories.CategorieRepository;
import com.example.servicecatalogue.repositories.StocksRepository;
import com.example.servicecatalogue.utils.PageableUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.servicecatalogue.repositories.ProduitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceProduit {
    @Autowired
    private final ProduitRepository produitRepository;
    @Autowired
    private final CategorieRepository categorieRepository;
    @Autowired
    private final StocksRepository stockRepository;

    private final ServiceRabbitMQSender serviceRabbitMQSender;

    public ServiceProduit(ProduitRepository produitRepository, CategorieRepository categorieRepository, StocksRepository stockRepository, @Autowired ServiceRabbitMQSender serviceRabbitMQSender) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
        this.stockRepository = stockRepository;
        this.serviceRabbitMQSender = serviceRabbitMQSender;
    }

    @Transactional
    public Produit saveProduit(ProduitDTO produitDTO) {
        if (produitDTO.prix_unitaire() <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être positif");
        }
        Categorie categorie = categorieRepository.findById(produitDTO.categorie_id())
                .orElseThrow(() -> new EntityNotFoundException("La catégorie spécifiée n'existe pas"));

        if (produitDTO.sexe() == null || produitDTO.taille() == null || produitDTO.libelle() == null) {
            throw new IllegalArgumentException("Les champs 'sexe', 'taille' et 'libelle' ne doivent pas être nuls");
        }
        String libelleMajuscules = produitDTO.libelle().toUpperCase();

        Produit produit = new Produit();
        produit.setPrixUnitaire(produitDTO.prix_unitaire());
        produit.setSexe(produitDTO.sexe());
        produit.setTaille(produitDTO.taille());
        produit.setLibelle(libelleMajuscules);
        produit.setDescription(produitDTO.description());
        produit.setImage(produitDTO.image_url());
        produit.setCategory(categorie);

        produitRepository.save(produit);

        if (produitDTO.couleurs() == null || produitDTO.couleurs().isEmpty()) {
            throw new IllegalArgumentException("La liste des couleurs ne doit pas être nulle ou vide");
        }

        List<Stocks> stocks = new ArrayList<>();
        for (String couleur : produitDTO.couleurs()) {
            if (couleur == null || Couleurs.valueOf(couleur) == null) {
                throw new IllegalArgumentException("La couleur '" + couleur + "' n'est pas valide");
            }
            Stocks stock = new Stocks();
            stock.setQuantite(0);
            stock.setCouleurs(Couleurs.valueOf(couleur));
            stock.setProduit(produit);
            stockRepository.save(stock);
            stocks.add(stock);
        }

        stocks.forEach(s -> this.serviceRabbitMQSender.stockManquant(Produit.toRabbitMqDTO(produit, s.getCouleurs().toString(), 5)));

        produit.setStocks(stocks);
        return produit;
    }

    /**
     * * @param paginateRequestDTO
     * @return
     */
    public Paginate<ProduitOutPaginateDTO> getAllProduits(PaginateRequestDTO paginateRequestDTO){
        Pageable pageable = PageableUtils.convert(paginateRequestDTO);
        Page<Produit> paginated = this.produitRepository.findAll(pageable);

        // Convertir les objets BlogDAO en BlogDTO en utilisant la fabrique
        List<ProduitOutPaginateDTO> dtos = paginated.stream()
                .map(Produit::toDTO)
                .toList();

        // Créer un objet Paginate contenant les blogs paginés
        return new Paginate<>(dtos, new Pagination(Math.toIntExact(paginated.getTotalElements()),
                paginateRequestDTO.limit(), paginateRequestDTO.page()));
    }

    public Produit getProduitById(int id) {
        Optional<Produit> produitOptional = produitRepository.findById(id);

        if (produitOptional.isEmpty()) {
            throw new EntityNotFoundException("Produit non trouvé pour l'ID :" +id);
        }
        return produitOptional.get();
    }

    @Transactional
    public void deleteProduit(int id) {
        Optional<Produit> produit = this.produitRepository.findById(id);
        if (produit.isEmpty()) {
            throw new EntityNotFoundException("Produit non trouvé pour l'ID :" +id);
        }
        produitRepository.deleteById(id);
        produit.get().getStocks().stream().forEach(s ->
            this.serviceRabbitMQSender.supprimerStock(new SupprimerStockDTO(s.getCouleurs().name(), id))
        );
    }

    @Transactional
    public Produit updateProduit(int id , ProduitUpdateDTO produitDTO) {
        Optional<Produit> opProduit = produitRepository.findById(id);
        if(opProduit.isEmpty()) {
            throw new EntityNotFoundException("Produit not found");
        }
        Produit produit = opProduit.get();

        if(produitDTO.prix_unitaire() >= 0f)
            produit.setPrixUnitaire(produitDTO.prix_unitaire());

        if(produitDTO.sexe() != null)
            produit.setSexe(produitDTO.sexe());

        if(produitDTO.taille() != null)
            produit.setTaille(produitDTO.taille());

        if(produitDTO.libelle() != null)
            produit.setLibelle(produitDTO.libelle());

        if(produitDTO.image_url() != null)
            produit.setImage(produitDTO.image_url());

        if(produitDTO.description() != null)
            produit.setDescription(produitDTO.description());

        if(produitDTO.categorie_id() != 0) {
            Optional<Categorie> categorie = categorieRepository.findById(produitDTO.categorie_id());
            if(categorie.isEmpty()) {
                throw new EntityNotFoundException("Categorie not found");
            }
            produit.setCategory(categorie.get());
        }
        return produitRepository.save(produit);
    }

    public Stocks addStock(int id, Couleurs couleur) throws StockExisteDejaException {
        Optional<Produit> opProduit = produitRepository.findById(id);
        if(opProduit.isEmpty()) {
            throw new EntityNotFoundException("Produit not found");
        }
        Produit produit = opProduit.get();

        Stocks stock = stockRepository.findByProduitIdAndCouleurs(id, couleur);
        if(stock != null) {
            throw new StockExisteDejaException("Le stock existe déjà");
        }

        Stocks s = new Stocks();
        s.setQuantite(0);
        s.setProduit(produit);
        s.setCouleurs(couleur);
        s = stockRepository.save(s);
        this.serviceRabbitMQSender.stockManquant(Produit.toRabbitMqDTO(produit, s.getCouleurs().toString(), 5));
        return s;
    }

    @Transactional
    public void deleteStock(int id, Couleurs couleur) {
        Stocks stock = stockRepository.findByProduitIdAndCouleurs(id, couleur);
        if(stock == null) {
            throw new EntityNotFoundException("Le stock existe déjà");
        }
        stockRepository.delete(stock);
        this.serviceRabbitMQSender.supprimerStock(new SupprimerStockDTO(couleur.name(), stock.getProduit().getId()));
    }

    /**
     * A la réception d'une commande valider, envoie le détail des produits au service catalogue pour traitement.
     * Met à jours le stock en conséquence.
     * @param validerCommandeDTO
     */
    @Transactional
    public void genereCommande(ValiderCommandeDTO validerCommandeDTO) throws InvalideCommandeException, QuantiteIndisponibleCommandeException {
        List<ProduitCatalogueDTO> produits = new ArrayList<>();
        // Liste des produits qui ont un stock inférieur ou égale à 5 : besoin d'être réapprovisionner
        List<ProduitCatalogueDTO> produitsStockManquant = new ArrayList<>();
        for (ProduitCommandeDTO p : validerCommandeDTO.produits()) {
            Optional<Produit> opt = this.produitRepository.findById(p.id_produit());
            if (opt.isEmpty())
                throw new InvalideCommandeException("Le produit " + p.id_produit() + "n'existe plus dans la base." + validerCommandeDTO);

            Produit produit = opt.get();
            Stocks stock = null;
            for (Stocks s : produit.getStocks()) {
                if (s.getCouleurs().equals(Couleurs.valueOf(p.couleur())))
                    stock = s;
            }
            if(stock == null)
                throw new QuantiteIndisponibleCommandeException("La couleur n'est plus disponible : "+p.couleur());

            if(stock.getQuantite() < p.quantite())
                throw new QuantiteIndisponibleCommandeException("La quantité est indisponible : "+p.couleur() + "; "+p.id_produit());

            // Mise à jour du stock
            stock.setQuantite(stock.getQuantite() - p.quantite());
            produits.add(Produit.toRabbitMqDTO(produit, p.couleur(), p.quantite()));
            if(stock.getQuantite() <= 5)
                produitsStockManquant.add(Produit.toRabbitMqDTO(produit, p.couleur(), p.quantite()));
        }
        this.serviceRabbitMQSender.genererCommande(
                new GenererCommandeDTO(
                        produits,
                        validerCommandeDTO.id_commande()
                )
        );
        produitsStockManquant.stream().forEach(this.serviceRabbitMQSender::stockManquant);
    }

    @Transactional
    public void stockReappro(ProduitCommandeDTO produitCommandeDTO) throws StockNotFoundException {
        Stocks stock = this.stockRepository.findByProduitIdAndCouleurs(produitCommandeDTO.id_produit(), Couleurs.valueOf(produitCommandeDTO.couleur()));
        if(stock == null)
            throw new StockNotFoundException("Le stock n'existe pas.");
        stock.setQuantite(stock.getQuantite() + produitCommandeDTO.quantite());
        this.stockRepository.save(stock);
    }
}
