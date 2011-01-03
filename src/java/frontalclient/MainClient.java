/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package frontalclient;

import fr.alma.client.Client;
import fr.alma.client.ClientBeanRemote;
import fr.alma.dto.catalogue.Categorie;
import fr.alma.dto.central.AProduit;
import fr.alma.dto.central.Item;
import fr.alma.interfaces.CentralRemote;
import fr.alma.order.OrderBeanRemote;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author indy
 */
public class MainClient {

    private static Context context;
    private static ClientBeanRemote clientService;
    private static CentralRemote centralService;
    private static OrderBeanRemote orderService;
    private static BufferedReader in =
            new BufferedReader(new InputStreamReader(System.in));
    private static Client client = null;
    private static Long orderId = null;

    private static int mainMenu() throws IOException {
        System.out.println("*******************************");
        System.out.println("* --- Uber Frontal Client --- *");
        System.out.println("*                             *");
        System.out.println("* 1. Connexion                *");
        System.out.println("* 2. Inscription              *");
        System.out.println("*                             *");
        System.out.println("* 0. Quitter                  *");
        System.out.println("*******************************");
        System.out.print("?: ");
        return in.read() - 48;
    } // int mainMenu()

    private static Categorie orderMenu(Collection<Categorie> categs)
            throws IOException {
        List<Categorie> categsList = new ArrayList<Categorie>();
        categsList.add(null);
        int i = 0;
        System.out.println("--- Veuillez selectionner une categorie ---\n");
        for (Categorie categ : categs) {
            categsList.add(categ);
            System.out.println(++i + ". " + categ.getName());
        } // for
        System.out.println("\n0. Terminer\n");
        System.out.print("?: ");
        return categsList.get(Integer.parseInt(in.readLine()));
    } // Categorie orderMenu(categs)

    private static int scanCategMenu(Categorie categ) throws IOException {
        System.out.println("****************************************");
        System.out.println("*  ---  Uber Categories' Browser  ---  *");
        System.out.println("*                                      *");
        System.out.println("* 1. Afficher tous les produits        *");
        System.out.println("* 2. Recherche par marque              *");
        System.out.println("* 3. Recherche par prix                *");
        System.out.println("* 4. Rechercher par marque et par prix *");
        System.out.println("*                                      *");
        System.out.println("* 0. Terminer                          *");
        System.out.println("****************************************");
        System.out.print("?: ");
        return in.read() - 48;
    } // int scanCategMenu(Categorie)

    private static AProduit pickItemsMenu(Collection<AProduit> items) throws IOException {
        System.out.println("  \tMarque\tModele\tDescription\n");
        int i = 0;
        List<AProduit> itemsList = new ArrayList<AProduit>();
        itemsList.add(null);
        for (AProduit item : items) {
            System.out.println(++i + ".\t" + item.getMarque() + "\t"
                    + item.getModele() + "\t" + item.getDescription());
            itemsList.add(item);
        } // for
        System.out.println("0. Terminer");
        System.out.println("\nSelectionnez le numero d'un produit pour l'ajouter au panier");
        System.out.print("?: ");
        return itemsList.get(Integer.parseInt(in.readLine()));
    }

    private static int addItemMenu(AProduit item) throws IOException {
        System.out.println("Etes-vous sur ? (1 pour oui, 0 pour non)");
        System.out.print("?: ");
        return in.read() - 48;
    }

    private static Item providerMenu(AProduit item) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void order()
            throws NamingException, IOException {
        centralService = (CentralRemote) context.lookup("Central/remote");
        orderService = (OrderBeanRemote) context.lookup("OrderBean/remote");
        Collection<Categorie> categs = centralService.findAllCategories();
        while (true) {
            Categorie choice = orderMenu(categs);
            if (choice != null) {
                scanCateg(choice);
            } else {
                return;
            } // if
        } // while
    } // void order(String[])

    private static void scanCateg(Categorie categ) throws IOException {
        while (true) {
            String brand = null;
            Integer minPrice = null;
            Integer maxPrice = null;
            switch (scanCategMenu(categ)) {
                case 1: // all items
                    break;
                case 2: { // by brand
                    System.out.print("Marque : ");
                    brand = in.readLine();
                    break;
                }
                case 3: { // by price
                    System.out.print("Prix minimum : ");
                    minPrice = Integer.parseInt(in.readLine());
                    System.out.print("Prix maximum : ");
                    maxPrice = Integer.parseInt(in.readLine());
                    break;
                }
                case 4: { // by brand and price
                    System.out.print("Marque : ");
                    brand = in.readLine();
                    System.out.print("Prix minimum : ");
                    minPrice = Integer.parseInt(in.readLine());
                    System.out.print("Prix maximum : ");
                    maxPrice = Integer.parseInt(in.readLine());
                    break;
                }
                default:
                    return;
            } // switch
            scanItems(categ, brand, minPrice, maxPrice);
        } // while
    } // void scanCateg(String[],Categorie)

    private static void scanItems(Categorie categ, String brand, Integer minPrice, Integer maxPrice)
            throws IOException {
        if (brand != null) {
            if (minPrice != null && maxPrice != null) {
                pickItems(centralService.findByCategorieAndMarqueAndPriceRange(categ.getName(), brand, minPrice.doubleValue(), maxPrice.doubleValue()));
            } else {
                pickItems(centralService.findByCategorieAndMarque(categ.getName(), brand));
            } // if
        } else {
            if (minPrice != null && maxPrice != null) {
                pickItems(centralService.findByCategorieAndPriceRange(categ.getName(), minPrice.doubleValue(), maxPrice.doubleValue()));
            } else {
                pickItems(centralService.findProduitsByCategorie(categ.getName()));
            } // if
        } // if
    } // void items(Categorie,String,Integer,Integer)

    private static void pickItems(Collection<AProduit> items) throws IOException {
        while (true) {
            AProduit item = pickItemsMenu(items);
            if (item != null) {
                if (addItemMenu(item) != 0) {
                    if (orderId == null) {
                    }
                    System.out.print("Quantite : ");
                    orderService.addProduct(orderId, providerMenu(item),
                            Integer.parseInt(in.readLine()));
                }
            } else {
                return;
            } // if
        } // while
    } // void scanItemMenu(Collection<AProduit>)

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NamingException, IOException {
        context = new InitialContext();
        clientService = (ClientBeanRemote) context.lookup("ClientBean/remote");
        while (true) {
            if (client == null) {
                switch (mainMenu()) {
                    case 1: { // Connect
                        System.out.print("Nom : ");
                        String name = in.readLine();
                        System.out.print("Mot de passe : ");
                        String pwd = in.readLine();
                        client = clientService.getClient(name, pwd);
                        break;
                    }
                    case 2: { // Register
                        System.out.print("Nom : ");
                        String name = in.readLine();
                        System.out.print("Adress : ");
                        String adress = in.readLine();
                        System.out.print("Mot de passe : ");
                        String pwd = in.readLine();
                        client = clientService.getClient(clientService.createClient(name, adress, pwd));
                        break;
                    }
                    default:
                        return;
                } // switch
            } else {
                order();
                pay();
                return;
            } // if
        } // while
    } // void main(String[])

    private static void pay() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
