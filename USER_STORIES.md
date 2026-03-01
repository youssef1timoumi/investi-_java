# User Stories - Gamification System

## TABLE 1

**Id_US: 13**

**User Story:**
En tant qu'administrateur, je souhaite gérer les cours (créer, modifier, télécharger des médias et des miniatures) afin d'enrichir le contenu éducatif de la plateforme.

| Id_Us | Tâches | Estimation (h) | Responsable |
|-------|--------|----------------|-------------|
| 13.1 | Créer le formulaire de gestion des cours (FXML + CSS) | 2 | [Votre nom] |
| 13.2 | Implémenter l'upload de fichiers média (vidéo/PDF) avec FileChooser | 2.5 | [Votre nom] |
| 13.3 | Implémenter l'upload de miniatures (PNG/JPG) avec validation | 2 | [Votre nom] |
| 13.4 | Créer les méthodes CRUD dans CouseService (add, update, delete, getAll) | 3 | [Votre nom] |
| 13.5 | Implémenter la recherche et le tri des cours (titre, points, durée) | 2 | [Votre nom] |
| 13.6 | Créer la page statistiques des cours (par catégorie, difficulté, statut) | 2.5 | [Votre nom] |
| 13.7 | Tester le flux complet (Création → Upload → Modification → Suppression) | 1.5 | [Votre nom] |

**Total: 15.5 heures**

---

## TABLE 2

**Id_US: 14**

**User Story:**
En tant qu'utilisateur, je souhaite consulter le catalogue des cours, voir le contenu (vidéo/PDF), interagir (like/dislike/signaler) et passer les quiz associés afin d'apprendre et gagner des points.

| Id_Us | Tâches | Estimation (h) | Responsable |
|-------|--------|----------------|-------------|
| 14.1 | Créer la page catalogue avec grille de cours et miniatures | 2 | [Votre nom] |
| 14.2 | Implémenter les filtres (recherche, catégorie, difficulté) | 1.5 | [Votre nom] |
| 14.3 | Créer la page de contenu du cours avec lecteur vidéo (JavaFX Media) | 3 | [Votre nom] |
| 14.4 | Implémenter l'affichage des PDF (ouverture externe) | 1.5 | [Votre nom] |
| 14.5 | Implémenter le système d'interactions (like/dislike/report) avec table course_interactions | 2.5 | [Votre nom] |
| 14.6 | Afficher les quiz liés au cours avec bouton "Passer le quiz" | 2 | [Votre nom] |
| 14.7 | Créer la liaison cours-quiz dans la base de données (table course_quizzes) | 1.5 | [Votre nom] |
| 14.8 | Tester le flux complet (Catalogue → Contenu → Interactions → Quiz) | 1.5 | [Votre nom] |

**Total: 15.5 heures**

---

## Résumé des User Stories

| Id_US | Description | Total Heures | Statut |
|-------|-------------|--------------|--------|
| 13 | Gestion des cours (Admin) | 15.5h | ✅ Complété |
| 14 | Consultation et interaction avec les cours (User) | 15.5h | ✅ Complété |

**Total général: 31 heures**

---

## Fonctionnalités Implémentées

### User Story 13 - Gestion des Cours (Admin)
- ✅ Formulaire de création/modification de cours
- ✅ Upload de vidéos et PDFs (stockage dans `media/courses/`)
- ✅ Upload de miniatures PNG/JPG (stockage dans `media/thumbnails/`)
- ✅ CRUD complet (Create, Read, Update, Delete)
- ✅ Recherche par titre, catégorie, difficulté
- ✅ Tri par titre, points, durée
- ✅ Statistiques détaillées (par catégorie, difficulté, statut, langue)
- ✅ Animations et thème dark/light

### User Story 14 - Catalogue et Contenu (User)
- ✅ Catalogue avec grille 3 colonnes
- ✅ Affichage des miniatures (240x140px)
- ✅ Filtres en temps réel (recherche, catégorie, difficulté)
- ✅ Page de contenu dédiée avec:
  - Lecteur vidéo intégré (play, pause, stop, rewind, forward)
  - Visualisation PDF (ouverture externe)
  - Liste des quiz liés
- ✅ Système d'interactions (like/dislike/report)
- ✅ Compteurs d'interactions en temps réel
- ✅ Boutons "View Details" et "Course Content"
- ✅ Navigation fluide entre les pages

---

## Base de Données

### Tables Créées/Utilisées

1. **course** - Stockage des cours
2. **course_interactions** - Likes, dislikes, reports
3. **course_quizzes** - Liaison cours-quiz
4. **quiz** - Quiz liés aux cours
5. **questions** - Questions des quiz
6. **question_options** - Options de réponse

---

## Technologies Utilisées

- **JavaFX 21** - Interface utilisateur
- **MySQL** - Base de données
- **Maven** - Gestion des dépendances
- **JavaFX Media** - Lecteur vidéo
- **JavaFX Web** - Composants web
- **JUnit 5** - Tests unitaires

---

## Fichiers Principaux

### Controllers
- `CourseController.java` - Gestion des cours (admin)
- `CourseCatalogController.java` - Catalogue (user)
- `CourseContentController.java` - Contenu du cours (user)

### FXML
- `CourseForm.fxml` - Formulaire de gestion
- `CourseCatalogView.fxml` - Vue catalogue
- `CourseContentView.fxml` - Vue contenu

### CSS
- `coursesForm.css` - Style formulaire admin
- `courseCatalog.css` - Style catalogue
- `courseContent.css` - Style page contenu

### Services
- `CouseService.java` - Logique métier cours
- `GamificationService.java` - Logique gamification

### Entities
- `Course.java` - Entité cours
- `CourseInteraction.java` - Entité interaction
- `Quiz.java` - Entité quiz

---

## Documentation Complète

Voir les fichiers de documentation détaillée:
- `COURSE_CATALOG_IMPLEMENTATION_GUIDE.md`
- `COURSE_MEDIA_UPLOAD_FEATURE.md`
- `THUMBNAIL_UPLOAD_FEATURE.md`
- `COURSE_CONTENT_PAGE_FEATURE.md`
- `QUIZ_COURSE_LINKING_FEATURE.md`

---

**Date de création:** 26 février 2026  
**Statut:** ✅ Toutes les fonctionnalités implémentées et testées
