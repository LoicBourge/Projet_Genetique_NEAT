# Projet_Genetique_NEAT

## Introduction
Le projet NEAT est une implémentation Java de l'algorithme NEAT (NeuroEvolution of Augmenting Topologies), une méthode d'évolution artificielle utilisée pour l'entraînement de réseaux de neurones.

## Classes principales

### Neat
La classe `Neat` représente l'algorithme NEAT lui-même. Voici une description des principaux attributs et méthodes :

- **populationSize**: La taille de la population de réseaux de neurones.
- **maxIter**: Le nombre maximum d'itérations de l'algorithme.
- **fitnessCalculator**: La méthode de calcul de la fitness.
- **solutionFitness**: La fitness minimale nécessaire pour considérer le problème comme résolu.
- **bestToKeep**: Le nombre de meilleurs génomes à conserver après chaque itération.
- **tournamentSize**: La taille du tournoi utilisée lors de la sélection par tournoi.
- **crossoverRate**: Le taux de crossover.
- **weightsMutationRate**: Le taux de mutation des poids.
- **connectionMutationRate**: Le taux de mutation des connexions.
- **nodeMutationRate**: Le taux de mutation des nœuds.
- **results**: Un ensemble de résultats tels que le meilleur génome, le nombre d'itérations, le temps d'exécution, etc.
- **fitnessGenomePairs**: Une liste de paires fitness-génome représentant la population actuelle.
- **temp**: Une liste temporaire utilisée lors de l'évolution.

Méthodes importantes :

- **run()**: Exécute l'algorithme NEAT.
- **evolve()**: Évolue la population de génomes.
- **mutate()**: Applique les mutations sur un génome donné.
- **getBestFitnessGenomePair()**: Retourne la meilleure paire fitness-génome.

### NeatNetwork
La classe `NeatNetwork` représente un réseau de neurones dans le contexte de NEAT. Voici quelques points clés :

- **connections**: Une liste de connexions entre les neurones.
- **inputLayer**: La couche d'entrée du réseau.
- **hiddenLayer**: La couche cachée du réseau.
- **outputLayer**: La couche de sortie du réseau.
- **age**: L'âge du réseau.

Méthodes importantes :

- **crossover(parent1, parent2)**: Réalise un crossover entre deux parents et génère un enfant.
- **mutateWeights(numberOfLinks)**: Fais muter les poids des connexions.
- **mutateInsertion(numberOfInsertions)**: Insère de nouveaux neurones dans le réseau.
- **mutateConnection(numberOfConnections)**: Ajoute de nouvelles connexions au réseau.
- **computeOutput()**: Calcule la sortie du réseau.

### FitnessGenomePair
La classe `FitnessGenomePair` représente une paire fitness-génome. Elle contient un génome (représenté par un réseau de neurones) et sa fitness associée.

### Node
La classe `Node` représente un neurone dans un réseau de neurones. Quelques points importants :

- **output**: La sortie calculée du neurone.
- **id**: L'identifiant du neurone.
- **edge**: Un indicateur indiquant si le neurone est un neurone d'entrée/sortie ou caché.
- **inputConnections**: Liste des connexions entrantes vers le neurone.

Méthodes importantes :

- **calculate()**: Calcule la sortie du neurone.
- **mutateInsertion()**: Insère de nouveaux neurones dans le réseau.
- **mutateConnection()**: Ajoute de nouvelles connexions au réseau.
- **computeOutput()**: Calcule la sortie du réseau.

## Auteur
Bourge Loïc
