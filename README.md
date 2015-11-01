#### INSTALLATION 
http://benja135.free.fr

Pour compiler et lancer les programmes, il faut installer le logiciel
NXJ leJOS : http://www.lejos.org/nxt/nxj/tutorial/Preliminaries/GettingStarted.htm

Puis installer le plugin Eclipse :
http://www.lejos.org/nxt/nxj/tutorial/Preliminaries/UsingEclipse.htm
(sur Windows, utiliser la version 32 bits d'Eclipse, la 64 bits peut poser problème)

Vous pouvez ensuite importer les programmes du dossier courant dans Eclipse.

Le sigle [PC] signifie que le programme fonctionne du coté PC.
Le sigle [NXT] signifie que le programme fonctionne du coté robot.

Les projets principaux sont : 
  - Scanner Runner [NXT]
  - Bluetooth Mapper [PC]

(une mini Javadoc est disponible dans leur dossier doc)

L'adresse du robot dans main doit être changé par l'adresse de votre robot.
  
#### FORME DU ROBOT

Forme du robot pour "Scanner" : Moteur droit  / port A
				Moteur gauche / port B
				Moteur tete   / port C
				Capteur distance sur tete / port 1
				Capteur lumiére gauche 	  / port 2
				capteur lumiére droit 	  / port 3
				
Forme du robot pour "FastAndFurious" :	Moteur droit  / port A
					Moteur gauche / port B
					Capteur distance droit  / port 1
					Capteur distance gauche / port 4
					Capteur lumiére gauche  / port 2
					capteur lumiére droit   / port 3
					
Peut être modifié dans "Constantes".


#### BLUETOOTH

Vous devez disposez d'un dongle bluetooth sur votre ordinateur !

Si vous avez des problèmes pour trouver vos NXT en Bluetooth, la commande suivante peut être très utile :
nxjbrowse -b -n "*"
Elle va chercher tous les NXT présent et les ajouter dans le cache de NXJ, ça résout en général les problèmes.
Le programme nxjbrowse se lancera également, il peut vous être utile si vous voulez uploader un fichier quelconque sur le robot.

La première fois que votre PC se connecte à un NXT, il lui sera demander le PIN (coté PC), celui-ci est 1234
si vous le l'avez pas changé sur le NXT.

ATTENTION : Le Bluetooth ne fonctionne pas sur Mac.


#### PROBLEMES COURANTS /////

- La tête tourne n'importe comment ! : il faut la positionner coté face avant de lancer le programme !

- Le robot fonce dans un mur, se trompe de chemin : problème de détection d'un mur, les murs sont-ils bien droit ? pas trop loin ni trop serré ?
						    les cables du robot ne passent pas devant ses capteurs ?
						  
- Le robot fonce sans s'arrêter : problème de détection d'une ligne noire, la piéce est peut-être trop éclairé, valeur seuil à changer

- Exception lors d'une connexion en Bluetooth : fermer bien tous les programmes coté PC et NXT et relancer dans le bonne ordre

- Le robot dérape bizarrement : les cables ne géne pas les roues ? le pneu n'est pas en train de sortir de la jante ? le sol est bien lisse ?
