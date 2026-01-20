# Système de détection de visage en Java

## Description

Ce projet consiste à développer une application Java capable de détecter des visages en temps réel à partir du flux vidéo d'une webcam.
Le programme affiche les visages détectés en encadrant chaque visage et fournit un compteur du nombre de visages présents.
L'objectif est de découvrir la vision par ordinateur, le traitement d'images et l'utilisation de Java avec OpenCV.

## Fonctionnalités

- Accès à la caméra de l'ordinateur
- Affichage en temps réel du flux vidéo
- Détection automatique des visages
- Encadrement graphique des visages détectés
- Compteur de visages présents

## Prérequis

- Java 17 ou supérieur
- OpenCV (Java bindings)
- IDE compatible Java (IntelliJ, Eclipse, VSCode, etc.)
- Webcam fonctionnelle

## Installation

1. Cloner le projet :
```bash
git clone <URL_DU_PROJET>
```

2. Importer le projet dans votre IDE Java.

3. Ajouter la bibliothèque OpenCV au projet :
   - Télécharger OpenCV : https://opencv.org/releases/
   - Configurer le chemin des librairies natives (`.dll` sur Windows, `.so` sur Linux, `.dylib` sur macOS)

4. Compiler le projet :
```bash
javac -cp <chemin_opencv_jar> src/**/*.java
```

## Utilisation

1. Lancer l'application :
```bash
java -cp <chemin_opencv_jar> src.main.App
```

2. L'interface s'ouvre et affiche le flux vidéo de la caméra.

3. Les visages détectés sont encadrés automatiquement et le compteur est mis à jour en temps réel.

4. Pour arrêter l'application, fermer simplement la fenêtre.

## Organisation du projet
```
src/
 ├── main/
 │   └── App.java
 ├── camera/
 │   └── CameraService.java
 ├── detection/
 │   └── FaceDetector.java
 ├── ui/
 │   └── DisplayWindow.java
 └── utils/
     └── Logger.java
```

## Notes

- La détection est basée sur les classifieurs Haar Cascade d'OpenCV.
- L'application ne stocke aucune image et ne réalise aucune reconnaissance faciale.
- La détection est uniquement en mémoire, à des fins pédagogiques.

## Auteur

**Simon Maigrot** – Projet réalisé dans le cadre de la formation Epitech.