# cas-server-support-agimus-cookie
![Esup Portail](https://www.esup-portail.org/sites/esup-portail.org/files/logo-esup%2Baccroche_2.png "Esup Portail")

cas-server-support-agimus-cookie est une extension du serveur CAS pour déposer un cookie de domaine lors d'une authentification. En parallèle cette extension va générer un fichier avec l'id de l'utilisateur authentifié et le valeur du cookie qui lui a été donné.
Si le cookie est déjà présent sur le poste client un nouveau ne sera pas généré.

Le fichier produit est de la forme : 

> USERID:AGIMUS-1-XXXXXXXXXXXXXXX

## Compatibilité

Tester sur 

 - CAS V5.2.2


## Installation

-------------------------

    git clone https://github.com/EsupPortail/cas-server-support-agimus-cookie.git
    cd cas-server-support-agimus-cookie
    mvn clean package install

-------------------------

Ceci va avoir pour effet de compiler le projet et de le mettre dans votre repository local maven.

## Intégration dans CAS

Ajouter la dépendance dans pom.xml de cas-overlay-template (https://github.com/apereo/cas-overlay-template) : 

    <dependencies>
      ...
    	<dependency>
		  <groupId>org.esupportail.cas</groupId>
		  <artifactId>cas-server-support-agimus-cookie</artifactId>
		  <version>${cas.version}</version>
		</dependency>
		...
	</dependencies>

## Configuration dans CAS

Vous devez configurer dans /etc/cas/config/agimus.properties les informations suivantes :

	agimus.cookieName=AGIMUS
	agimus.cookieMaxAge=259200
	agimus.cookiePath=/
	agimus.cookieValueMaxLength=10
	agimus.cookieDomain=univ.fr
	agimus.cookieValuePrefix=TRACEAGIMUS
	agimus.traceFileSeparator=:
	
Sachant que :
 
 - cookieName : est le nom du cookie qui sera déposé sur le poste client
 - cookieMaxAge : délai de vie du cookie sur le poste client (3jours)
 - cookiePath : chemin d'où le cookie sera lisible
 - cookieValueMaxLength : longueur maximum de la valeur du cookie
 - cookieDomain : nom du domain d'où le cookie sera lisible (attention le domaine ne doit pas commencer par un .)
 - cookieValuePrefix : prefix utiliser pour générer la valeur du cookie
 - traceFileSeparator : séparateur utilisé pour la génération du fichier (entre la valeur du cookie et l'identifiant utilisateur)
 
Le cookie déposé sera la forme : 
	{agimus.cookieName} = {agimus.cookieValuePrefix}-XXXXXXXXXX-{cas.host.name} 

## Gestion des logs

Ajouter le logger dans /etc/cas/config/log4j2.xml

    <Appenders>
		...
		<File name="agimusCookieAssociationlogfile" fileName="${sys:cas.log.dir}/agimus-cookie-association.log" append="true" immediateFlush="true">
			<PatternLayout>
				<Pattern>%m%n</Pattern>
			</PatternLayout>
		</File>
    	...
    	...
    	<CasAppender name="casCookieAssociationAgimus">
			<AppenderRef ref="agimusCookieAssociationlogfile" />
		</CasAppender>
    	...
    </Appenders>
    <Loggers>
		...
		<AsyncLogger name="org.esupportail.cas.util.CasAgimusLogger" level="info" additivity="false" includeLocation="true">
			<AppenderRef ref="casCookieAssociationAgimus"/>
		</AsyncLogger>
		...
    </Loggers>
    
Il suffit ensuite de lancer la compilation est le déploiement de CAS.
