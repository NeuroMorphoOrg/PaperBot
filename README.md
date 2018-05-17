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
`...   "active": true,`<br>
`...   "db": "pubmed"`<br>
`... },`<br>
`... {`<br>
`...   "name": "PubMedCentral",`<br>
`...   "apiUrl": "https://eutils.ncbi.nlm.nih.gov/entrez/eutils",`<br>
`...   "active": true,`<br>
`...   "db": "pmc"`<br>
`... },`<br>
`... {`<br>
`...   "name": "ScienceDirect",`<br>
`...   "apiUrl": "https://api.elsevier.com/content/search/scidir?",`<br>
`...   "active": true,`<br>
`... },`<br>
`... {`<br>
`...   "name": "Nature",`<br>
`...   "apiUrl": "http://api.nature.com/content/opensearch/request?",`<br>
`...   "searchPeriod": 3,`<br>
`...   "active": true`<br>
`... },`<br>
`... {`<br>
`...   "name": "Wiley",`<br>
`...   "url": "https://onlinelibrary.wiley.com/action/doSearch",`<br>
`...   "active": false,`<br>
`...   "base": "http://onlinelibrary.wiley.com"`<br>
`... },`<br>
`... {`<br>
`...   "name": "SpringerLink",`<br>
`...   "apiUrl": "http://api.springer.com/metadata/json?",`<br>
`...   "active": true,`<br>
`... },`<br>
`... {`<br>
`...   "name": "GoogleScholar",`<br>
`...   "url": "https://scholar.google.com/scholar?l=es&",`<br>
`...   "base": "https://scholar.google.com",`<br>
`...   "active": false`<br>
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

Close mongo console:
<br>
`exit`
<br>
## 2. Boot MicroServices
Microservices run an embedded tomcat using Spring Boot (.jar). All of them are independent and can be launched in any order

**Pre-requisites**: Maven to compile and build the code. Download: https://maven.apache.org/download.cgi and install: https://maven.apache.org/install.html and Java 8


### 2.1. Download the code

Download the code from git from the download button or you can use the terminal if git is installed in your system typing the following:<br>
`git clone https://github.com/NeuroMorphoOrg/PaperBot.git`


### 2.2. Compile

From the terminal navigate inside the principal folder LiterMate-master and compile:<br>
`cd LiterMate`<br>
`mvn clean install`<br>

This will compile all the services and you should see the `SUCCESS` for all the services at the end:

` Reactor Summary:`<br>
`[INFO] `<br>
`[INFO] CrossRef ...................... SUCCESS [ 21.782 s]`<br>
`[INFO] Metadata ...................... SUCCESS [  0.996 s]`<br>
`[INFO] PubMed ........................ SUCCESS [  1.003 s]`<br>
`[INFO] Literature .................... SUCCESS [  1.607 s]`<br>
`[INFO] Search ........................ SUCCESS [  1.029 s]`<br>

### 2.3. Launch
If using Linux or Mac you can launch it typing:
`./launch.sh`<br>

This will launch the required services with nohup and java -jar. Any error will be traced in the correspondnt log. 

_**NOTE: Although the services can be used on your local machine, they are designed to run in a server. If you run them locally and restart your computer this step needs to be executed again. Same happens in a server. Servers are not rebooted that often, but I highly encourage you to create Unix/Linux services following Spring instructions: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html described in detail in https://springjavatricks.blogspot.com/2017/11/installing-spring-boot-services-in.html**_ 



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

`sudo mkdir /Library/WebServer/DocumentsPaperBot` <br>
`sudo cp -r NMOLiteratureWeb/app/ /Library/WebServer/Documents/PperBot` <br>

In your browser type: http://[ipAddress]/PaperBot

### 3.2. If runing on a server and not your localhost remember to update the ip in the browser

Update NMOLiteratureWeb/communications/articlesCommunicationService.js 

`var url_literature = 'http://<serverIP>:8443/literature';`<br>
`var url_metadata = 'http://<serverIP>:8443/metadata';`<br>
`var url_pubmed = 'http://<serverIP>:8443/pubmed';`<br>
`...`


### 3.3. Update metadata html to your desired metadata properties 

Edit PaperBot/article/metadata.html. Any kind of object is supported since the metadataService receives type Object in java, so you can add Strings, Booleans, and Lists. If you want to use Lists you have to update the frontend controller accordingly.

Lets update a name for a given tag. For example:

 `<tr>`<br>
    `<td><strong>Category 1:</strong></td>`<br>
    `<td><span e-style="width:600px;" editable-text="metadata.category1">{{metadata.category1}}</span></td>`<br>
 `</tr>`<br>
 
 Update Category 1 for your desired name, also category1 if you want the name of the DataBase to match (not needed). You can add as many `<tr>` groups as you want.
 
 The `metadataFinished` is a nice feature that allows you to remember if you had finished reviewing a paper. If it is set to false, when you navigate to the Positive group of articles a red flag will remind you that there is pending work.

### 3.4. Go to the Wiki to learn how to update the keywords, the portals configuration, launch your first search, and add an article manually


### 3.5. Go to the browser & refresh

You will see how the web populates. It is ready to use.




