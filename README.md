# LiterMate

## 1. The DataBase

### 1.1.  Install MongoDB. 
Follow the instructions: https://docs.mongodb.com/manual/administration/install-community/
 
### 1.2. No schema needed.
Thanks to Spring framework no schemas for the database are needed to be created prior to use the tool.

### 1.3. Upload the portals configuration to the **Portal Database**.

This is needed if you want to use the automated search (Elsevier/ScienceDirect, Springer, Nature, Wiley, PubMed/PubMed Central, and GoogleScholar). The manual PubMed search does not use the **Portal Database** to work.

* The `searchPeriod` is defined in days. 
* `active` can be set to true if you want to launch the concrete portal or false otherwise. For example, you may want to launch only one of the portal for a large range and set the other to false.

To create the data from the terminal:
<br>
`$ mongo`
<br>
`$ use portal`
<br>
`$ db.portal.insertMany([`<br>
`... {`<br>
`...   "name": "PubMed",`<br>
`...   "apiUrl": "https://eutils.ncbi.nlm.nih.gov/entrez/eutils",`<br>
`...   "searchPeriod": 384,`<br>
`...   "active": true,`<br>
`...   "db": "pubmed"`<br>
`... },`<br>
`... {`<br>
`...   "name": "PubMedCentral",`<br>
`...   "apiUrl": "https://eutils.ncbi.nlm.nih.gov/entrez/eutils",`<br>
`...   "apiUrl2": "https://www.ncbi.nlm.nih.gov/pmc/utils/idconv/v1.0/?",`<br>
`...   "url": "https://www.ncbi.nlm.nih.gov/pmc/articles/",`<br>
`...   "searchPeriod": 384,`<br>
`...   "active": true,`<br>
`...   "db": "pmc"`<br>
`... },`<br>
`... {`<br>
`...   "name": "ScienceDirect",`<br>
`...   "url": "http://www.sciencedirect.com/science",`<br>
`...   "base": "http://www.sciencedirect.com",`<br>
`...   "searchPeriod": 384,`<br>
`...   "active": true`<br>
`... },`<br>
`... {`<br>
`...   "name": "Nature",`<br>
`...   "url": "http://www.nature.com/search?",`<br>
`...   "base": "http://www.nature.com",`<br>
`...   "searchPeriod": 384,`<br>
`...   "active": true`<br>
`... },`<br>
`... {`<br>
`...   "name": "Wiley",`<br>
`...   "url": "http://onlinelibrary.wiley.com/advanced/search?",`<br>
`...   "searchPeriod": 384,`<br>
`...   "active": true,`<br>
`...   "base": "http://onlinelibrary.wiley.com"`<br>
`... },`<br>
`... {`<br>
`...   "name": "SpringerLink",`<br>
`...   "url": "http://link.springer.com/search?",`<br>
`...   "base": "http://link.springer.com",`<br>
`...   "searchPeriod": 384,`<br>
`...   "active": true`<br>
`... },`<br>
`... {`<br>
`...   "name": "GoogleScholar",`<br>
`...   "url": "https://scholar.google.com/scholar?l=es&",`<br>
`...   "base": "https://scholar.google.com",`<br>
`...   "searchPeriod": 384,`<br>
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

### 1.4. Add some keyWords for the search.
* `name` contains the keywords, where " " around the string is used for exact match if more than one word is used. Only AND operand is supported. In order to perform OR operator add more keywords to the Database.
* `collection` is the current collection where we want the article to be saved. By default `To evaluate`, but you can configure the project to use different collections for other purposes.
* `usage` is a label for the articles found using the keyword. You can add different labels to differentiate search types. 

`$ db.keyword.insert(`<br>
`{`<br>
  `"name": "\"digitally reconstructed neuron\" AND \"filament tracer\"",`<br>
  `"collection": "To evaluate",`<br>
  `"usage": "Describing"`<br>
`}`<br>

## 2. The Boot MicroServices
Microservices run an embedded tomcat using Spring Boot (.jar). All of them are independent and can be launched in any order

**Pre-requisites**: mvn and git installed

### 2.1. Download the code

From the terminal type:<br>
`git clone https://github.com/NeuroMorphoOrg/LiterMate.git`

### 2.2. The properties file

The properties file contains the configuration for the services, each of them connect to a database independently so you can have several different databases or one. In this case all of the are connected to `nmotest`.
You can also update the server ports, just be aware that there are other services that may depend on these connections and you should update them accordingly (the Web Frontend & the LiteratureSearchService).

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

_**NOTE Although the service can be used on your local machine they are designed to run in a server. If you run them locally and restart your computer this step needs to be execute again. Same happens in a server. Servers are not that often rebooted, but I highly encourage you to create Unix/Linux services following Spring instructions: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html**_ 

### 2.5. Test the service is up & running
Go to your browser and type: http://localhost:8180/literature/metadata/test

You should see the following in the browser: `Metadata up & running!`

### 2.6. Launch all of the other services

Repeat the steps **2.2 to 2.5** for the services: LiteraturePubMedServiceBoot, LiteraturePortalServiceBoot, LiteratureServiceBoot

URLs to test they are up & running:
Go to your browser and type: http://localhost:8186/literature/pubmed/test
Go to your browser and type: http://localhost:8189/literature/portal/test
Go to your browser and type: http://localhost:8188/literature/test


## 3. The Fronted

**Pre-requisites:** Apache web server.


## 4. The Crontab Services
