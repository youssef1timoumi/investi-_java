# 📋 PRODUCT BACKLOG - INVESTI Platform

**Plateforme d'Innovation Connectant Innovateurs et Investisseurs**

## 📊 Vue d'Ensemble du Projet

**Équipe :** 6 Développeurs  

**Méthodologie :** Agile Scrum  

## 🏗️ Architecture & Setup Initial

### Prérequis Techniques
- [ ] **SETUP-001** : Configuration de l'environnement de développement
- [ ] **SETUP-002** : Setup de la base de données (PostgreSQL/MongoDB)
- [ ] **SETUP-003** : Configuration des APIs REST/GraphQL
- [ ] **SETUP-004** : Authentification JWT et gestion des sessions
- [ ] **SETUP-005** : Déploiement CI/CD pipeline
- [ ] **SETUP-006** : Configuration des tests automatisés
- [ ] **SETUP-007** : Setup du monitoring et logging

---

## 👥 MODULE 1: GESTION DES UTILISATEURS
**Responsable : Membre 1**

### Epic 1.1: Authentification & Autorisation
- [ ] **US-001** : En tant qu'utilisateur, je veux créer un compte avec email/mot de passe
  - **Critères d'acceptation :**
    - Validation email format
    - Mot de passe sécurisé (8+ caractères, majuscules, chiffres)
    - Email de confirmation
    - Gestion des erreurs
- [ ] **US-002** : En tant qu'utilisateur, je veux me connecter avec mes identifiants
  - **Critères d'acceptation :**
    - Authentification sécurisée
    - Session persistante
    - Redirection selon le rôle
- [ ] **US-003** : En tant qu'utilisateur, je veux réinitialiser mon mot de passe
  - **Critères d'acceptation :**
    - Email de réinitialisation
    - Token sécurisé temporaire
    - Nouveau mot de passe validé
- [ ] **US-004** : En tant qu'admin, je veux gérer les rôles (Admin/Investor/Innovator)
  - **Critères d'acceptation :**
    - Interface d'administration
    - Modification des rôles
    - Permissions par rôle
- [ ] **US-005** : En tant qu'utilisateur, je veux me connecter via OAuth (Google/LinkedIn)
  - **Critères d'acceptation :**
    - Intégration OAuth2
    - Création automatique de profil
    - Synchronisation des données

### Epic 1.2: Profils Utilisateurs
- [ ] **US-006** : En tant qu'utilisateur, je veux compléter mon profil (photo, bio, compétences)
- [ ] **US-007** : En tant qu'investisseur, je veux spécifier mes domaines d'investissement
- [ ] **US-008** : En tant qu'innovateur, je veux présenter mes projets passés
- [ ] **US-009** : En tant qu'utilisateur, je veux voir le profil d'autres utilisateurs
- [ ] **US-010** : En tant qu'admin, je veux modérer les profils utilisateurs

### Epic 1.3: Système de Points & Badges
- [ ] **US-011** : En tant qu'utilisateur, je veux gagner des points pour mes actions
- [ ] **US-012** : En tant qu'utilisateur, je veux débloquer des badges d'accomplissement
- [ ] **US-013** : En tant qu'admin, je veux configurer le système de récompenses

---

## 💬 MODULE 2: GESTION DU FORUM
**Responsable : Membre 2**

### Epic 2.1: Posts & Discussions
- [ ] **US-014** : En tant qu'utilisateur, je veux créer un nouveau post
  - **Critères d'acceptation :**
    - Éditeur de texte riche
    - Catégorisation des posts
    - Upload d'images/fichiers
    - Prévisualisation avant publication
- [ ] **US-015** : En tant qu'utilisateur, je veux commenter les posts
  - **Critères d'acceptation :**
    - Commentaires imbriqués (réponses)
    - Édition/suppression de ses commentaires
    - Notifications aux auteurs
- [ ] **US-016** : En tant qu'utilisateur, je veux voter (upvote/downvote) sur les posts
- [ ] **US-017** : En tant qu'utilisateur, je veux filtrer les posts par catégorie
- [ ] **US-018** : En tant qu'utilisateur, je veux rechercher dans les discussions

### Epic 2.2: Modération & Organisation
- [ ] **US-019** : En tant qu'admin, je veux modérer les posts inappropriés
- [ ] **US-020** : En tant qu'utilisateur, je veux signaler du contenu
- [ ] **US-021** : En tant qu'utilisateur, je veux suivre des discussions
- [ ] **US-022** : En tant qu'utilisateur, je veux voir les posts tendances
- [ ] **US-023** : En tant qu'utilisateur, je veux recevoir des notifications

---

## 📅 MODULE 3: GESTION DES ÉVÉNEMENTS
**Responsable : Membre 3**

### Epic 3.1: Création & Gestion d'Événements
- [ ] **US-024** : En tant qu'admin, je veux créer des événements (pitch nights, workshops)
  - **Critères d'acceptation :**
    - Formulaire de création complet
    - Gestion des dates/heures
    - Capacité et localisation
    - Images et descriptions
- [ ] **US-025** : En tant qu'utilisateur, je veux voir la liste des événements
- [ ] **US-026** : En tant qu'utilisateur, je veux m'inscrire à un événement
- [ ] **US-027** : En tant qu'utilisateur, je veux voir les détails d'un événement
- [ ] **US-028** : En tant qu'organisateur, je veux gérer les inscriptions

### Epic 3.2: Cours & Formation
- [ ] **US-029** : En tant qu'admin, je veux créer des cours en ligne
- [ ] **US-030** : En tant qu'utilisateur, je veux m'inscrire à des cours
- [ ] **US-031** : En tant qu'utilisateur, je veux suivre ma progression
- [ ] **US-032** : En tant qu'utilisateur, je veux évaluer les cours
- [ ] **US-033** : En tant qu'instructeur, je veux gérer le contenu de mes cours

---

## 📊 MODULE 4: GESTION DES PROJETS
**Responsable : Membre 4**

### Epic 4.1: Cycle de Vie des Projets
- [ ] **US-034** : En tant qu'innovateur, je veux créer une idée de projet
  - **Critères d'acceptation :**
    - Formulaire détaillé (titre, description, tags)
    - Upload de documents/images
    - Statut initial "Ouvert"
    - Visibilité publique
- [ ] **US-035** : En tant qu'investisseur, je veux parcourir les idées
- [ ] **US-036** : En tant qu'utilisateur, je veux voir les détails d'un projet
- [ ] **US-037** : En tant qu'équipe, je veux transformer une idée en projet actif
- [ ] **US-038** : En tant qu'utilisateur, je veux suivre le progrès des projets

### Epic 4.2: Gestion de Projet
- [ ] **US-039** : En tant qu'équipe projet, je veux définir des jalons
- [ ] **US-040** : En tant qu'équipe projet, je veux mettre à jour le statut
- [ ] **US-041** : En tant qu'investisseur, je veux voir les métriques de performance
- [ ] **US-042** : En tant qu'utilisateur, je veux voir l'historique du projet
- [ ] **US-043** : En tant qu'équipe, je veux partager des updates publiques

---

## 🤝 MODULE 5: GESTION DE LA COLLABORATION
**Responsable : Membre 5**

### Epic 5.1: Matching & Propositions
- [ ] **US-044** : En tant qu'investisseur, je veux exprimer mon intérêt pour une idée
  - **Critères d'acceptation :**
    - Bouton "Intéressé" sur chaque idée
    - Message personnalisé optionnel
    - Notification à l'innovateur
    - Historique des intérêts
- [ ] **US-045** : En tant qu'innovateur, je veux voir qui s'intéresse à mon idée
- [ ] **US-046** : En tant qu'utilisateur, je veux créer des propositions de collaboration
- [ ] **US-047** : En tant qu'utilisateur, je veux accepter/refuser des propositions
- [ ] **US-048** : En tant qu'utilisateur, je veux voir mes collaborations actives

### Epic 5.2: Communication & Chat
- [ ] **US-049** : En tant qu'utilisateur, je veux chatter en temps réel avec mes collaborateurs
- [ ] **US-050** : En tant qu'utilisateur, je veux partager des fichiers dans le chat
- [ ] **US-051** : En tant qu'utilisateur, je veux créer des groupes de discussion
- [ ] **US-052** : En tant qu'utilisateur, je veux voir l'historique des conversations
- [ ] **US-053** : En tant qu'utilisateur, je veux recevoir des notifications de messages

---

## 🏆 MODULE 6: GESTION D'AVANCEMENT
**Responsable : Membre 6**

### Epic 6.1: Métriques & Analytics
- [ ] **US-054** : En tant qu'admin, je veux voir les statistiques de la plateforme
  - **Critères d'acceptation :**
    - Dashboard avec KPIs principaux
    - Graphiques d'évolution
    - Métriques d'engagement
    - Export des données
- [ ] **US-055** : En tant qu'utilisateur, je veux voir mon tableau de bord personnel
- [ ] **US-056** : En tant qu'investisseur, je veux voir mes investissements
- [ ] **US-057** : En tant qu'innovateur, je veux voir mes projets et leur statut
- [ ] **US-058** : En tant qu'admin, je veux générer des rapports

### Epic 6.2: Quizzes & Évaluations
- [ ] **US-059** : En tant qu'admin, je veux créer des quizzes éducatifs
- [ ] **US-060** : En tant qu'utilisateur, je veux passer des quizzes
- [ ] **US-061** : En tant qu'utilisateur, je veux voir mes résultats
- [ ] **US-062** : En tant qu'utilisateur, je veux gagner des points via les quizzes
- [ ] **US-063** : En tant qu'admin, je veux analyser les performances des quizzes

