# LiterMate

LiterMate is a configurable, modular, open-source web-based solution to automatically find and efficiently annotate peer-reviewed publications based on periodic full-text searches across publisher portals.
Without user interactions, LiterMate retrieves and stores article information (full reference, corresponding email contact, and full-text keyword hits) based on pre-set search logic from disparate sources including Wiley, ScienceDirect, Springer/Nature/Frontiers, HighWire, PubMed/PubMedCentral, and GoogleScholar.
Although different portals require different search configurations, the common interface of LiterMate unifies the process from the user perspective. Once saved, all information becomes web accessible, allowing efficient triage of articles based on their actual relevance to the project goals and seamless annotation of suitable metadata dimensions.

The user should read and understand the terms of use of the portals that are using a scraper prior to activte this portion of the tool, we are not responsible of any misuse of it:

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

**Pre-requisites**: Maven to compile and build the code. Download: https://maven.apache.org/download.cgi and install: https://maven.apache.org/install.html and Java 8


### 2.1. Download the code

Download the code from git from the download button or you can use the terminal if git is installed in your system typing the following:<br>
`git clone https://github.com/NeuroMorphoOrg/LiterMate.git`

### 2.2. The properties files

Each of the services contain a properties file with its own configuration in the location:<br>
`./<ServiceName>/src/main/java/application.properties` <br> 

The services that requires download the PDFs needs the key obtained in **1.2** to download Wiley PDFs, if this key is not addd it will download only the open PDFs from CrossRef. Update the CrossRef properties file, the propeoty `token` value. The property `folder` should have write permisions and can be updated to your needs.
Each of the services that requires DB access connects to a database independently, you can have several different databases or one. In this case all of the services are connected to a DB named `nmotest`.
You can update the server ports; just be aware that there are other services that may depend on these connections (the Web Frontend & the LiteratureSearchService) and you should update them accordingly.

`server.port= 8180`<br>
`logging.level.org.springframework.web=ERROR`<br>
`logging.level.o.n.o.drivers.http=ERROR`<br>
`logging.level.org.neuromorpho=DEBUG`<br>
`logging.file=./LiteratureMetadataServiceBoot.log`<br>
`spring.data.mongodb.host=localhost`<br>
`spring.data.mongodb.database=nmotest`<br>
`spring.data.mongodb.port=27017`<br>
`token=`<br>
`folder=/home/services/literature/pdf/`<br>


### 2.3. Compile

From the terminal navigate inside the principal folder LiterMate-master and compile:<br>
`cd LiterMate`<br>
`mvn clean install`<br>

This will compile all the services and you should see the `SUCCESS` for all the services at the end:

` Reactor Summary:`<br>
`[INFO] `<br>
`[INFO] LiteratureCrossRefServiceBoot ...................... SUCCESS [ 21.782 s]`<br>
`[INFO] LiteratureDownloadPDFService ....................... SUCCESS [  9.614 s]`<br>
`[INFO] LiteratureMetadataServiceBoot ...................... SUCCESS [  0.996 s]`<br>
`[INFO] LiteraturePubMedServiceBoot ........................ SUCCESS [  1.003 s]`<br>
`[INFO] LiteratureServiceBoot .............................. SUCCESS [  1.607 s]`<br>
`[INFO] LiteraturePortalServiceBoot ........................ SUCCESS [  1.122 s]`<br>
`[INFO] LiteratureSearchService ............................ SUCCESS [  1.029 s]`<br>
`[INFO] LiterMate .......................................... SUCCESS [  0.021 s]`<br>

### 2.4. Launch
If using Linux or Mac you can launch it typing:
`./launch.sh`<br>

This will launch the rquired services with nohup and java -jar. Any error will be tracd in nohup.out. 
`tail -f nohup.out `  To check everything is working<br> 
` [           main] o.n.literature.<serviceAPP>.Application      : Started Application in 49.649 seconds (JVM running for 54.07)` <br>
_**NOTE: Although the services can be used on your local machine, they are designed to run in a server. If you run them locally and restart your computer this step needs to be executed again. Same happens in a server. Servers are not rebooted that often, but I highly encourage you to create Unix/Linux services following Spring instructions: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html**_ 



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

In your browser type: http://<ipAddress>/NMOLiteratureWeb

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

### 4.1. Launch the crontab services

You can launch it dissociated from the terminal in background

`nohup java -jar ./LiteraturSearchService/target/LiteratureSearchService-1.0.jar &`<br>

Or launch it associated to the terminal in foreground to see how it works and its logs

`java -jar ./LiteraturSearchService/target/LiteratureSearchService-1.0.jar`<br>

Or add it to the crontab. Same applies to LiteratureDownloadPDFService.

### 4.4. Go to the browser & refresh

You will see how the web populates. It is ready to use.




