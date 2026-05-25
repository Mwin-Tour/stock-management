#Gestion de Stock et de Commandes - RootManagement

Une application web développée avec **Spring Boot** et **Thymeleaf** pour gérer les produits, les commandes clients, les factures PDF, et les alertes de stock faible.

## Technologies utilisées

- **Java 17 / Spring Boot 3**
- **Thymeleaf** (Frontend intégré)
- **Bootstrap 5** (Design responsive)
- **MySQL** (Base de données relationnelle)
- **Lombok** (Réduction du boilerplate Java)
- **iText PDF** (Génération de factures)
- **Spring Data JPA / Hibernate** (ORM)
- **Spring Mail** (Alerte email automatique si stock bas)

---

##  Mise en route (localement)

### 1. Prérequis

- Java 17 ou supérieur
- Maven 3.8+ ou Gradle
- MySQL 8.x ou supérieur
- Un IDE (IntelliJ, Eclipse, VS Code...)

---

### 2. Configuration de la base de données

1. Crée une base de données nommée `gestion_stock`.

```sql
CREATE DATABASE gestion_stock;
