**SPRINT BACKLOG GLOBAL - PROJET INVESTI**

Sprint 1 - Tous les modules combinés

1. **Résumé Global du Sprint 1![ref1]**

*Ce tableau résume les 6 modules du projet INVESTI avec leurs story points et responsables.*



|**Module**|**Responsable**|**Story Points**|**Durée (jours)**|**Nb Tâches**|**Statut**|
| - | - | - | - | - | - |
|Gestion des Utilisateurs|Youssef Timoumi|34|5|8|✅ DONE|
|Gestion de Collaboration|Seif Eddine Ben Abdallah|60|14|28|✅ DONE|
|Gestion des Événements + Inscriptions|Sassi Fatma|57|14|12|✅ DONE|
|Gestion des Produits + Paiement|Moez Touil|22 (heures)|10|13|✅ DONE|
|Gestion du Forum / Blog|Mohamed Taha Frihida|20|8|9|✅ DONE|
|Gestion Courses / Gamification|Dhia Eddine Djebbi|91|13|13|✅ DONE|
|**TOTAL PROJET**|**6 membres**|**284 pts**|**14 jours (sprint)**|**83 tâches**|✅ **100%**|

2. **Sprint Backlog Détaillé par Module![ref1]**

**Module 1 : Gestion des Utilisateurs (34 pts - 5 jours) - Youssef Timoumi**



|**ID**|**Tâche**|**Story Points**|**Priorité**|**Statut**|
| - | - | - | - | - |
|U1|Créer l'entité User et le service CRUD|3|Haute|✅ DONE|
|U2|Interface Login/Register (JavaFX, validation, toggle password)|5|Haute|✅ DONE|
|U3|Vérification email SMTP avec OTP 6 chiffres|5|Haute|✅ DONE|
|U4|Flux KYC : upload pièce d'identité + validation admin|5|Haute|✅ DONE|
|U5|Dashboard Admin : CRUD utilisateurs + recherche|5|Haute|✅ DONE|
|U6|Externaliser les credentials (config.properties)|2|Moyenne|✅ DONE|
|U7|Dashboard amélioré : filtres, stats, export PDF|8|Haute|✅ DONE|
|U8|Nettoyage du code (suppression module Transport)|1|Basse|✅ DONE|
|**Total**|**34 pts**||||
**Module 2 : Gestion de Collaboration (60 pts - 14 jours) - Seif Eddine Ben Abdallah**



|**ID**|**Tâche**|**Priorité**|**Statut**|||||
| - | - | - | - | :- | :- | :- | :- |
|**Module Entrepreneur**||||||||
|E1|CRUD Projet (Créer, Modifier, Supprimer, Consulter)|Haute|✅ DONE|||||
|E2|Restreindre Edit/Delete quand statut = FUNDED ou CLOSED|Haute|✅ DONE|||||
|E3|Afficher les cartes projet avec catégorie, statut et objectif|Haute|✅ DONE|||||
|E4|TableView des offres par projet|Haute|✅ DONE|||||
|E5|Filtrer et trier les offres (Montant / Equity / Statut)|Moyenne|✅ DONE|||||
|E6|Accepter une offre (rejet auto des autres)|Haute|✅ DONE|||||
|E7|Notification email via EmailService|Haute|✅ DONE|||||
|E8|Évaluation AI Mentor pour l'offre sélectionnée|Haute|✅ DONE|||||
|E9|Page Collaboration (Slider progression 0-100%)|Haute|✅ DONE|||||
|E10|Sauvegarder journal de mise à jour + progression en BDD|Haute|✅ DONE|||||
|E11|Suivi financier (Total Funding & Paid Months)|Moyenne|✅ DONE|||||
|**Module Investisseur**||||||||
|I1|Parcourir les projets OPEN|Haute|✅ DONE|||||
|I2|Recherche avancée et filtrage (Catégorie, Mot-clé)|Haute|✅ DONE|||||
|I3|Tri (Montant / Equity)|Moyenne|✅ DONE|||||
|I4|Intégration conversion de devises|Haute|✅ DONE|||||
|I5|Ajouter une offre d'investissement|Haute|✅ DONE|||||
|I6|Générer rapport PDF via AI|Moyenne|✅ DONE|||||
|I7|Page Portfolio (historique investissements)|Haute|✅ DONE|||||
|I8|Annuler les offres en attente|Moyenne|✅ DONE|||||
|I9|AI Portfolio Advisor|Haute|✅ DONE|||||
|I10|Page suivi de collaboration|Haute|✅ DONE|||||
|I11|Système de journalisation des paiements|Haute|✅ DONE|||||
|**Services Externes**||||||||
|S1-S6|CurrencyService, EmailService, PDF, AI Advisor, Background Threads, Loading States|Haute|✅ DONE|||||
|**Total**|**60 pts - 28 tâches**|||||||

**Module 3 : Gestion des Événements + Inscriptions (57 pts - 14 jours) - Sassi Fatma**



|**ID**|**Tâche**|**Story Points**|**Priorité**|**Statut**|||||||
| - | - | - | - | - | :- | :- | :- | :- | :- | :- |
|**Gestion des Événements**|||||||||||
|EV1|Créer un événement (formulaire + validation + sauvegarde BDD)|8|Haute|✅ DONE|||||||
|EV2|Afficher la liste des événements (TableView, colonnes, tri par date)|5|Haute|✅ DONE|||||||
|EV3|Modifier un événement (remplir formulaire + mettre à jour BDD)|5|Moyenne|✅ DONE|||||||
|EV4|Supprimer un événement (confirmation + suppression BDD)|3|Moyenne|✅ DONE|||||||
|EV5|Rechercher des événements en temps réel (FilteredList)|5|Basse|✅ DONE|||||||
|EV6|Exporter les événements en PDF (tableaux formatés, FileChooser)|8|Basse|✅ DONE|||||||
|**Gestion des Inscriptions**|||||||||||
|IN1|Créer une inscription (formulaire + validation doublon + sauvegarde)|5|Haute|✅ DONE|||||||
|IN2|Afficher toutes les inscriptions (TableView, colonnes, tri/recherche)|5|Haute|✅ DONE|||||||
|IN3|Filtrer les inscriptions par statut (ComboBox, compteur)|3|Moyenne|✅ DONE|||||||
|IN4|Modifier le statut d'une inscription (Confirmée/En attente)|3|Moyenne|✅ DONE|||||||
|IN5|Supprimer une inscription (confirmation + refresh)|2|Basse|✅ DONE|||||||
|IN6|Exporter les inscriptions en PDF (statuts colorés)|5|Basse|✅ DONE|||||||
|**Total**|**57 pts**||||||||||
**Module 4 : Gestion des Produits + Paiement (22h - 10 jours) - Moez Touil**



|**ID**|**Tâche**|**Estimation**|**Priorité**|**Statut**|
| - | - | - | - | - |
|P1|Intégrer le module de scan QR Code|2h|Haute|✅ DONE|
|P2|Recherche automatique de produit via QR|1\.5h|Haute|✅ DONE|
|P3|Page détail produit (nom, description, prix, image)|2h|Haute|✅ DONE|
|P4|Ajout au panier (gestion état, persistance)|1\.5h|Haute|✅ DONE|
|P5|Intégration paiement en ligne (Stripe/PayPal)|3h|Haute|✅ DONE|
|P6|Tables Commande + Historique d'achat (BDD + CRUD)|2h|Haute|✅ DONE|
|P7|Tests flux complet (QR → paiement → commande)|1h|Moyenne|✅ DONE|
|P8|Ajouter budget et intérêts au profil utilisateur|1h|Moyenne|✅ DONE|
|P9|Filtrer les produits par budget|1\.5h|Moyenne|✅ DONE|
|P10|Recommandation par catégorie/intérêt|2h|Haute|✅ DONE|
|P11|Afficher les produits recommandés|1\.5h|Moyenne|✅ DONE|
|P12|Optimiser recommandations via historique d'achat|2h|Haute|✅ DONE|
|P13|Tests recommandation (budget, catégorie, historique)|1h|Moyenne|✅ DONE|
|**Total**|**22h**||||
**Module 5 : Gestion du Forum / Blog (20 pts - 8 jours) - Mohamed Taha Frihida**



|**ID**|**Tâche**|**Story Points**|**Priorité**|**Statut**|
| - | - | - | - | - |
|F1|CRUD Posts (créer, lire, modifier, supprimer avec images)|5|Haute|✅ DONE|
|F2|Système de votes upvote/downvote sur les posts|2|Haute|✅ DONE|
|F3|Filtrage par catégorie et recherche de posts|2|Moyenne|✅ DONE|
|F4|CRUD Commentaires (ajout, modification, suppression)|3|Haute|✅ DONE|
|F5|Réponses imbriquées (nested comments avec parent\_comment\_id)|2|Moyenne|✅ DONE|
|F6|Votes sur les commentaires|1|Moyenne|✅ DONE|
|F7|Bad Words Filter (détection + censure mots inappropriés)|2|Haute|✅ DONE|
|F8|Suivi d'activité utilisateur (Mes Posts, Mon Activité)|1|Basse|✅ DONE|
|F9|Tests unitaires (services, entités, BadWordsFilter)|2|Moyenne|✅ DONE|
|**Total**|**20 pts**||||
**Module 6 : Gestion Courses / Gamification (91 pts - 13 jours) - Dhia Eddine Djebbi**



|**ID**|**Tâche**|**Story Points**|**Priorité**|**Statut**|
| - | - | - | - | - |
|G1|Appliquer la palette de couleurs du template|3|Moyenne|✅ DONE|
|G2|Convertir multi-fenêtre en navigation single-window|5|Haute|✅ DONE|
|G3|Ajouter framework de tests JUnit 5|8|Haute|✅ DONE|
|G4|Créer tests unitaires des entités|5|Moyenne|✅ DONE|
|G5|Fonctionnalité Edit/Delete (Courses, Quizzes, Badges)|8|Haute|✅ DONE|
|G6|Design moderne + recherche, tri, dark mode|13|Haute|✅ DONE|
|G7|Redesign page Badges avec animations GSAP-like|13|Haute|✅ DONE|
|G8|Ajustement layout et styling page Badges|5|Moyenne|✅ DONE|
|G9|Améliorer les éléments animés (shimmer, sparkle)|3|Basse|✅ DONE|
|G10|Appliquer le design Badges aux pages Courses et Quizzes|13|Haute|✅ DONE|
|G11|Animations d'entrée page Courses|5|Moyenne|✅ DONE|
|G12|Toggle liste Courses + améliorations statut|5|Moyenne|✅ DONE|
|G13|Polish final et cohérence globale|3|Basse|✅ DONE|
|**Total**|**91 pts**||||
3. **Burndown Chart Global - Sprint 1![](Aspose.Words.c2e7998b-2d8f-430a-8dee-f12b29ff44ae.002.png)**

*Ce burndown chart combine la progression de tous les 6 modules sur la durée du sprint (14 jours). Les story points sont normalisés : le module Produits (22h) est converti en ~22 story points pour cohérence. Total global : 284 story points.*



|**Jour**|**Date**|**Points Début**|**Points Terminés**|**Points Restants (Réel)**|**Points Restants (Idéal)**|**Modules actifs**|
| - | - | - | - | - | - | - |
|J0|10/02|284|0|284|284|Sprint Planning|
|J1|11/02|284|14|270|264|Users (8), Collab (3), Events (3)|
|J2|12/02|270|22|248|244|Users (10), Collab (5), Gamif (3), Forum (2), Events (4)|
|J3|13/02|248|20|228|223|Users (7), Collab (4), Gamif (5), Produits (2), Events (2)|
|J4|14/02|228|22|206|203|Users (8), Collab (4), Gamif (5), Produits (2), Forum (3)|
|J5|15/02|206|23|183|183|Users (1), Collab (5), Gamif (8), Produits (1.5), Events (3), Forum (2)|
|J6|16/02|183|24|159|162|Collab (4), Gamif (13), Produits (3), Events (4)|
|J7|17/02|159|22|137|142|Collab (5), Gamif (13), Produits (2), Forum (2)|
|J8|18/02|137|20|117|122|Collab (5), Gamif (5), Produits (2.5), Events (5), Forum (2)|
|J9|19/02|117|22|95|101|Collab (5), Gamif (3), Produits (3.5), Events (5), Forum (2)|
|J10|20/02|95|23|72|81|Collab (5), Gamif (13), Produits (3), Events (5)|
|J11|21/02|72|22|50|61|Collab (5), Gamif (5), Events (5), Forum (2), Produits (2)|
|J12|22/02|50|22|28|41|Collab (6), Gamif (5), Events (5), Forum (3), Produits (3)|
|J13|23/02|28|20|8|20|Collab (6), Gamif (3), Events (5), Forum (2), Produits (2)|
|J14|24/02|8|8|0|0|Finalisation tous modules|

**Burndown Chart Visuel**

Points Restants![](Aspose.Words.c2e7998b-2d8f-430a-8dee-f12b29ff44ae.003.png)

`     `|

` `284 |●

`     `|  ╲

` `260 |    ●╲

`     `|      ╲

` `240 |        ●╲

`     `|          ╲

` `220 |            ●╲

`     `|              ╲

` `200 |                ●╲

`     `|                  ╲

` `180 |                    ●╲

`     `|                      ╲

` `160 |                    ●   ╲

`     `|                      ╲

` `140 |                        ●╲

`     `|                          ╲

` `120 |                        ●   ╲

`     `|                          ╲

` `100 |                            ●╲

`     `|                              ╲

`  `80 |                            ●   ╲

`     `|                              ╲

`  `60 |                                ●╲

`     `|                                  ╲

`  `40 |                                ●   ╲

`     `|                                  ╲

`  `20 |                                    ╲

`     `|                                  ●   ╲

`   `0 |\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_●

`     `J0  J1  J2  J3  J4  J5  J6  J7  J8  J9 J10 J11 J12 J13 J14

`     `--- Ligne idéale (planifiée)      ●── Progression réelle

**Analyse du Burndown**



|**Métrique**|**Valeur**|
| - | - |
|Total Story Points|284 points|
|Points terminés|284 (100%)|
|Durée du Sprint|14 jours|
|Vélocité moyenne|20\.3 pts/jour|
|Nombre total de tâches|83 tâches|
|Nombre de modules|6 modules|
|Taux de complétion|100%|
|Pic de productivité|Jours 6-12 (en avance sur l'idéal)|
|Aucune user story reportée|Sprint 2 commence sans dette|

**Observations**

- Les premiers jours (J1-J5) suivent la ligne idéale de près
- À partir de J6, l'équipe prend de l'avance grâce au travail parallèle sur plusieurs modules
- Le pic de productivité se situe entre J6 et J12 où l'équipe est en avance sur le planning
- Le sprint se termine à J14 avec 0 points restants - objectif atteint à 100%
- Progression régulière sans blocages majeurs

[ref1]: Aspose.Words.c2e7998b-2d8f-430a-8dee-f12b29ff44ae.001.png
