# cas-server-support-agimus-cookie
![Esup Portail](https://www.esup-portail.org/sites/esup-portail.org/files/logo-esup%2Baccroche_2.png "Esup Portail")

cas-server-support-agimus-cookie est une extension du serveur CAS pour d�poser un cookie de domaine lors d'une authentification. En parall�le cette extension va g�n�rer un fichier avec l'id de l'utilisateur authentifi� et le valeur du cookie qui lui a �t� donn�.
Si le cookie est d�j� pr�sent sur le poste client un nouveau ne sera pas g�n�r�.

Le fichier produit est de la forme : 

> USERID:AGIMUS-1-XXXXXXXXXXXXXXX

## Compatibilit�

Tester sur 

 - CAS V6.4.0-RC5


## Installation

-------------------------

    git clone https://github.com/EsupPortail/cas-server-support-agimus-cookie.git
    cd cas-server-support-agimus-cookie
    mvn clean package install

-------------------------

Ceci va avoir pour effet de compiler le projet et de le mettre dans votre repository local maven.

## Int�gration dans CAS

Ajouter la d�pendance dans pom.xml de cas-overlay-template (https://github.com/apereo/cas-overlay-template) : 

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
 
 - cookieName : est le nom du cookie qui sera d�pos� sur le poste client
 - cookieMaxAge : d�lai de vie du cookie sur le poste client (3jours)
 - cookiePath : chemin d'o� le cookie sera lisible
 - cookieValueMaxLength : longueur maximum de la valeur du cookie
 - cookieDomain : nom du domain d'o� le cookie sera lisible (attention le domaine ne doit pas commencer par un .)
 - cookieValuePrefix : prefix utiliser pour g�n�rer la valeur du cookie
 - traceFileSeparator : s�parateur utilis� pour la g�n�ration du fichier (entre la valeur du cookie et l'identifiant utilisateur)
 
Le cookie d�pos� sera la forme : 
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
    
Il suffit ensuite de lancer la compilation est le d�ploiement de CAS.
