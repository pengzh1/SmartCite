# SmartCiteCon

##  **Description** 
A semantic citation context extraction tool for academic literature. Provide citation context extraction of academic documents in PDF format and XML format in accordance with Plosone data set standards


##  **Installation** 

1. Install Java8
2. Download the following compressed package and unzip the link: https://pan.baidu.com/s/1XR0GRRWME5UDNgPT1eguZQ ， extraction code: vcfk
3. Go to the grobid-0.5.6 folder, open the console under this folder and run the command gradlew run
4. Open the console under the folder containing the jar package and run the command java -jar smart_cite-1.0.0-SNAPSHOT.jar

##  **Interface Description** 

###  Extract

Request method: POST

Request address: / extract

Request parameters:

Field|Description|Type|Length|IsRequired|Remarks
---|---|---|---|---|---
file|file|MultipleFile||True|

Return parameter:

code|message|data|action
---|---|---|---
0|Success|JsonObject|displays the received data
100|File error, please upload the correct file|null|prompt error message
500|server error|null|prompt error message




Return result



