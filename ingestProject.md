# Résumé du projet CRM

## Vue d'ensemble
Ce projet est un système de gestion de relation client (CRM) développé en Java, utilisant Hibernate pour la persistance des données et JavaFX pour l'interface utilisateur. Il permet de gérer des contacts, des entreprises et des communications.

## Structure du projet
Le projet est organisé selon le pattern MVC (Modèle-Vue-Contrôleur) avec les packages principaux suivants:
- **model**: Contient les entités et classes de modèle de données
- **controller**: Classes de contrôle pour la gestion de la logique métier
- **dao**: Interfaces et implémentations pour l'accès aux données
- **view**: Composants d'interface utilisateur JavaFX

## Entités principales
1. **Person**: Représente un contact individuel
2. **Company**: Représente une entreprise
3. **Contact**: Classe abstraite dont héritent Person et Company
4. **Communication**: Représente une interaction avec un contact
5. **Address**: Représente l'adresse d'un contact

## Fonctionnalités clés

### Gestion des Contacts

#### Classe `ContactDAO`
Cette classe gère l'accès aux données des contacts (personnes et entreprises).

Méthodes importantes:
- `addContact(Contact contact)`: Ajoute un nouveau contact à la base de données
- `deleteContact(Contact contact)`: Supprime un contact existant
- `updateContact(Contact contact)`: Met à jour les informations d'un contact
- `searchContacts(String searchText)`: Recherche des contacts selon un texte donné

#### Classe `ContactController`
Cette classe fait le lien entre la vue et la couche de données pour les contacts.

Méthodes importantes:
- `handleCreatePerson()`: Gère la création d'une nouvelle personne
- `handleCreateCompany()`: Gère la création d'une nouvelle entreprise
- `handleContactSelection(Contact contact)`: Gère la sélection d'un contact dans la liste
- `handleDeleteContact()`: Gère la suppression d'un contact
- `performSearch(String searchText)`: Effectue une recherche de contacts

### Gestion des Communications

#### Classe `CommunicationDAO`
Gère les opérations CRUD pour les communications.

Méthodes importantes:
- `addCommunication(Communication communication)`: Ajoute une nouvelle communication
- `getCommunicationsForContact(Contact contact)`: Récupère les communications liées à un contact
- `deleteCommunication(Communication communication)`: Supprime une communication

#### Classe `CommunicationController`
Contrôleur pour la gestion des communications.

Méthodes importantes:
- `handleNewCommunication()`: Gère la création d'une nouvelle communication
- `handleDeleteCommunication()`: Gère la suppression d'une communication
- `loadCommunicationsForContact(Contact contact)`: Charge les communications pour un contact spécifique

### Utilitaires et logique spécifique

#### `EntityManagerSingleton`
Classe singleton qui gère l'instanciation et l'accès à l'EntityManager JPA/Hibernate.

```java
public static EntityManager getEntityManager() {
    if (entityManager == null || !entityManager.isOpen()) {
        createEntityManager();
    }
    return entityManager;
}
```

#### `SearchService`
Service permettant d'effectuer des recherches avancées sur les contacts.

```java
public List<Contact> searchContacts(String searchText) {
    Session session = (Session) EntityManagerSingleton.getEntityManager().getDelegate();
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(session);
    
    // Construction de la requête de recherche
    QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
        .buildQueryBuilder()
        .forEntity(Contact.class)
        .get();
    
    // Configuration des champs à rechercher et création de la requête
    org.apache.lucene.search.Query query = queryBuilder
        .keyword()
        .onFields("firstName", "lastName", "name", "email")
        .matching(searchText)
        .createQuery();
    
    // Exécution de la recherche
    javax.persistence.Query persistenceQuery = fullTextEntityManager
        .createFullTextQuery(query, Contact.class);
    
    return persistenceQuery.getResultList();
}
```

#### `ValidationService`
Service qui valide les données des formulaires avant sauvegarde.

```java
public boolean validatePersonForm(String firstName, String lastName, String email) {
    if (firstName == null || firstName.trim().isEmpty()) {
        showValidationError("Le prénom est obligatoire");
        return false;
    }
    if (lastName == null || lastName.trim().isEmpty()) {
        showValidationError("Le nom est obligatoire");
        return false;
    }
    if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
        showValidationError("L'adresse e-mail n'est pas valide");
        return false;
    }
    return true;
}
```

## Interface utilisateur
L'interface utilisateur est construite avec JavaFX et organisée en plusieurs vues:

1. **MainView**: Vue principale qui contient la structure générale de l'application
2. **ContactListView**: Affiche la liste des contacts et permet d'effectuer des recherches
3. **ContactDetailView**: Affiche et permet de modifier les détails d'un contact
4. **CommunicationView**: Gère l'affichage et l'édition des communications

## Flux de travail typique
1. L'utilisateur ouvre l'application et voit la liste des contacts
2. Il peut rechercher un contact spécifique ou en créer un nouveau
3. La sélection d'un contact affiche ses détails et ses communications
4. L'utilisateur peut modifier les informations du contact ou ajouter/supprimer des communications
5. Les modifications sont persistées dans la base de données

## Points d'extension
Le projet est conçu pour être facilement extensible:
- Nouveaux types de contacts via héritage de la classe `Contact`
- Nouveaux types de communications en étendant le modèle `Communication`
- Fonctionnalités de reporting via l'ajout d'un nouveau module

## Aspects techniques importants
- Utilisation d'Hibernate/JPA pour la persistance
- Recherche avancée avec Hibernate Search/Lucene
- Interface utilisateur réactive avec JavaFX
- Pattern Observer pour la synchronisation entre les vues
- Transactions gérées via le pattern Unit of Work
