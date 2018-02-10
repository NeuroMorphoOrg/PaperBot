# LiterMate

LiterMate is a configurable, modular, open-source web-based solution to automatically find and efficiently annotate peer-reviewed publications based on periodic full-text searches across publisher portals.
Without user interactions, LiterMate retrieves and stores article information (full reference, corresponding email contact, and full-text keyword hits) based on pre-set search logic from disparate sources including Wiley, ScienceDirect, Springer/Nature/Frontiers, HighWire, PubMed/PubMedCentral, and GoogleScholar.
Although different portals require different search configurations, the common interface of LiterMate unifies the process from the user perspective. Once saved, all information becomes web accessible, allowing efficient triage of articles based on their actual relevance to the project goals and seamless annotation of suitable metadata dimensions.

The user must understand and respect the terms of use of the portals, we are not responsible of any misuse of this tool:

https://www.google.com/policies/terms/ <br>
http://olabout.wiley.com/WileyCDA/Section/id-826542.html <br>



## 1. DataBase

### 1.1. Install & launch MongoDB
Follow the instructions: https://docs.mongodb.com/manual/administration/install-community/
 
### 1.2. Get an API key for ScienceDiect, SpringerLink and CrossRef (Wiley)

The portals ScienceDiect and SpringerLink require the user to register and obtain an API to use their APIs. You can register and find the key at https://dev.elsevier.com/user/registration and https://dev.springer.com/signup  
CrossRef provides an option to retrieve the pdf urls, some of the portals are completely open, but Wiley for example requies the CrossRef key to download their articles. The key is obtained following the instructions provided in http://olabout.wiley.com/WileyCDA/Section/id-829772.html

### 1.3. Upload the portals configuration to the **Portal Database**

This is needed if you want to use the automated search (Elsevier/ScienceDirect, Springer, Nature, Wiley, PubMed/PubMed Central, and GoogleScholar). The manual PubMed search does not use the **Portal Database**.
* `token` is the api key obtained in **1.2**, once inserted you should replace the `...   "token": "replace with your token"`<br> with your api key.
* `searchPeriod` is defined in months. 
* `active` can be set to true if you want to launch the specific portal or false otherwise. For example, you may want to launch only one of the portal for a given time range and set the others to false.

**a) Insert the data from the terminal copying and pastying the following:**
<br>
`mongo`
<br>
`use portal`
<br>
`db.portal.insertMany([`<br>
`... {`<br>
`...   "name": "PubMed",`<br>
`...   "apiUrl": "https://eutils.ncbi.nlm.nih.gov/entrez/eutils",`<br>
`...   "searchPeriod": 3,`<br>
`...   "active": true,`<br>
`...   "db": "pubmed"`<br>
`... },`<br>
`... {`<br>
`...   "name": "PubMedCentral",`<br>
`...   "apiUrl": "https://eutils.ncbi.nlm.nih.gov/entrez/eutils",`<br>
`...   "searchPeriod": 3,`<br>
`...   "active": true,`<br>
`...   "db": "pmc"`<br>
`... },`<br>
`... {`<br>
`...   "name": "ScienceDirect",`<br>
`...   "apiUrl": "https://api.elsevier.com/content/search/scidir?",`<br>
`...   "searchPeriod": 3,`<br>
`...   "active": true,`<br>
`...   "token": "replace with your token"`<br>
`... },`<br>
`... {`<br>
`...   "name": "Nature",`<br>
`...   "apiUrl": "http://api.nature.com/content/opensearch/request?",`<br>
`...   "searchPeriod": 3,`<br>
`...   "active": true`<br>
`... },`<br>
`... {`<br>
`...   "name": "Wiley",`<br>
`...   "url": "http://onlinelibrary.wiley.com/advanced/search?",`<br>
`...   "searchPeriod": 3,`<br>
`...   "active": false,`<br>
`...   "base": "http://onlinelibrary.wiley.com"`<br>
`... },`<br>
`... {`<br>
`...   "name": "SpringerLink",`<br>
`...   "apiUrl": "http://api.springer.com/metadata/json?",`<br>
`...   "searchPeriod": 3,`<br>
`...   "active": true,`<br>
`...   "token": "replace with your token"`<br>
`... },`<br>
`... {`<br>
`...   "name": "GoogleScholar",`<br>
`...   "url": "https://scholar.google.com/scholar?l=es&",`<br>
`...   "base": "https://scholar.google.com",`<br>
`...   "searchPeriod": 3,`<br>
`...   "active": true`<br>
`... }`<br>
`... ]`<br>
`... );`<br>


If everything works well you should see the following response. Of course the `ids` will be different:

`{`<br>
	`"acknowledged" : true,`<br>
	`"insertedIds" : [`<br>
		`ObjectId("57c709dcf139a309cc559a81"),`<br>
		`ObjectId("57c709dcf139a309cc559a82"),`<br>
		`ObjectId("57c709dcf139a309cc559a83"),`<br>
		`ObjectId("57c709dcf139a309cc559a84"),`<br>
		`ObjectId("57c709dcf139a309cc559a85"),`<br>
		`ObjectId("57ceca1e14896407206e3d82"),`<br>
		`ObjectId("59272282f139a31a3a033501")`<br>
	`]`<br>
`}`<br>

**b) Replace `...   "token": "replace with your token"` with your api keys:**

For example, if your api Key for SpringerLink is 111 execute:
<br><br>
`db.portal.update({"name": "SpringerLink"},{$set: {"token": "111"}});`

If everything works well you should see the following response:
<br><br>
`WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })`

Same for ScienceDirect and an api Key 222:
<br><br>
`db.portal.update({"name": "ScienceDirect"},{$set: {"token": "222"}});`


### 1.4. Add keywords for the search
* `name` contains the keywords, where " " around the string is used for exact match if inputting more than one word and to avoid approximate string matching. Only AND operand is supported. In order to perform OR operation add more keywords to the Database.
* `collection` is the group in wich the article will be saved. By default this is set to the `To evaluate` group, but you can configure the project to use different groups for other purposes.
* `usage` is a label for the articles found using the keyword. You can add different labels to differentiate search types. 

`db.keyword.insert(`<br>
`{`<br>
  `"name": "\"reconstructed neuron\" AND neurolucida",`<br>
  `"collection": "To evaluate",`<br>
  `"usage": "Describing"`<br>
`});`<br>


Close mongo console:
<br>
`exit`
<br>
## 2. Boot MicroServices
Microservices run an embedded tomcat using Spring Boot (.jar). All of them are independent and can be launched in any order

**Pre-requisites**: Maven to compile and build the code: https://maven.apache.org/install.html and Java 8


### 2.1. Download the code

Download the code from git from the download button or you can use the terminal if git is installed in your system typing the following:<br>
`git clone https://github.com/NeuroMorphoOrg/LiterMate.git`

### 2.2. The properties files

Each of the services contain a properties file with its own configuration. The location of the properties files:<br>
`./LiteratureMetadataServiceBoot/src/main/java/application.properties` <br> `./LiteraturePortalServiceBoot/src/main/java/application.properties` <br> `./LiteraturePubMedServiceBoot/src/main/java/application.properties` <br> `./LiteratureSearchService/src/main/java/application.properties` <br>
`./LiteratureServiceBoot/src/main/java/application.properties` <br>

Each of the services connects to a database independently so you can have several different databases or one. In this case all of the services are connected to `nmotest`.
You can update the server ports; just be aware that there are other services that may depend on these connections (the Web Frontend & the LiteratureSearchService) and you should update them accordingly.

`server.port= 8180`<br>
`logging.level.org.springframework.web=ERROR`<br>
`logging.level.o.n.o.drivers.http=ERROR`<br>
`logging.level.org.neuromorpho=DEBUG`<br>
`logging.file=./LiteratureMetadataServiceBoot.log`<br>
`spring.data.mongodb.host=localhost`<br>
`spring.data.mongodb.database=nmotest`<br>
`spring.data.mongodb.port=27017`<br>

### 2.3. Compile

From the terminal type:<br>
`cd LiteratureMetadataServiceBoot`<br>
`mvn clean install`<br>

You should see the following at the end:

`[INFO] ------------------------------------------------------------------------`<br>
`[INFO] BUILD SUCCESS`<br>
`[INFO] ------------------------------------------------------------------------`<br>

### 2.4. Launch
`cd target`<br>
`nohup java -jar LiteratureMetadataServiceBoot-1.0.jar &`<br>

_**NOTE: Although the services can be used on your local machine, they are designed to run in a server. If you run them locally and restart your computer this step needs to be executed again. Same happens in a server. Servers are not rebooted that often, but I highly encourage you to create Unix/Linux services following Spring instructions: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html**_ 

### 2.5. Test the service is up & running
Go to your browser and type: http://localhost:8180/literature/metadata/test

You should see the following in the browser: `Metadata up & running!`

### 2.6. Launch all of the other services

Repeat the steps **2.2 to 2.5** for the services: LiteraturePubMedServiceBoot, LiteraturePortalServiceBoot, LiteratureServiceBoot

URLs to test they are up & running: <br>
Go to your browser and type: http://localhost:8186/literature/pubmed/test <br>
Go to your browser and type: http://localhost:8189/literature/portals/test <br>
Go to your browser and type: http://localhost:8188/literature/test <br>


## 3. Fronted

**Pre-requisites:** Apache web server installed & running: https://httpd.apache.org

### 3.1. Copy the frontend to apache folder & launch

Apache default directory is: <br>
	- MacOS: `/Library/WebServer/Documents/` <br>
	- Linux: `/var/www/html` <br>
	- Windows v2.2 and up (replace 2.2 with the version you had installed):
	`C:\Program Files\Apache Software Foundation\Apache2.2\htdocs` <br>
	- Windows v2:
	`C:\Program Files\Apache Group\Apache2\htdocs` <br>
	

Replace from the following commands `/Library/WebServer/Documents/` with your apache folder in the following commands:

`sudo mkdir /Library/WebServer/Documents/NMOLiteratureWeb` <br>
`sudo cp -r NMOLiteratureWeb/app/ /Library/WebServer/Documents/NMOLiteratureWeb` <br>

In your browser type: http://localhost/NMOLiteratureWeb/index.html

If you decide to change the name or the url project you will have to update the html links

### 3.2. Update metadata html to your desired metadata properties

Edit NMOLiteratureWeb/article/metadata.html. Any kind of object is supported since the metadataService receives type Object in java, so you can add Strings, Booleans, and Lists. If you want to use Lists you have to update the frontend controller accordingly.

Lets update a name for a given tag. For example:

 `<tr>`<br>
    `<td><strong>Cell Type:</strong></td>`<br>
    `<td><span editable-text="metadata.cellType">{{metadata.cellType}}</span></td>`<br>
 `</tr>`<br>
 
 Update Cell Type for your desired name and cellType too. Your new metadata tag will be saved in the DB with that name. You can add as many `<tr>` groups as you want.
 
 The `metadataFinished` is a nice feature that allows you to remember if you had finished reviewing a paper. If it is set to false, when you navigate to the Positive group of articles a red flag will remind you that there is pending work.

### 3.3. Go to the Wiki to learn how to add an article manually


## 4. Crontab Services

Now that everything is set, it is fun to see how the Database populates from the Web page.

### 4.1. Configuration file

Because it is a microservice architecture, the services can run in different servers with different IPs and dataBases. The article search connects to 3 services: LiteratureServiceBoot, LiteraturePubMedServiceBoot, and LiteraturePortalServiceBoot. Remember to update the URLs localhost to the desired server IP if you are not running them locally.

`endpoints.shutdown.enabled=true` <br>
`server.port= 8087`<br>
`logging.level.org.springframework.web=ERROR`<br>
`logging.level.org.neuromorpho=DEBUG`<br>
`logging.file=./LiteratureSearch.log`<br>
`uriLiteratureService=http://localhost:8188/literature`<br>
`uriPortalService=http://localhost:8189/literature`<br>
`uriPubMedService=http://localhost:8186/literature/pubmed`<br>

### 4.2. Clean and build the search service

`cd LiteratureSearchService`<br>
`mvn clean install`<br>

You should see the following at the end:

`[INFO] ------------------------------------------------------------------------`<br>
`[INFO] BUILD SUCCESS`<br>
`[INFO] ------------------------------------------------------------------------`<br>

### 4.3. Launch
`cd target`<br>

You can launch it dissociated from the terminal in background

`nohup java -jar LiteratureSearchService-1.0.jar &`<br>

Or launch it associated to the terminal in foreground to see how it works and its logs

`java -jar LiteratureSearchService-1.0.jar`<br>

Or add to the crontab

### 4.4. Go to the browser & refresh

You will see how the web populates. It is ready to use.




