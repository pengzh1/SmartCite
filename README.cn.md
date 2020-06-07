# SmartCiteCon

##  **Description** 
A semantic citation context extraction tool for academic literature. Provide citation context extraction of academic documents in PDF format and XML format in accordance with Plosone data set standards

##  **Installation** 

1. Install Java8
2. Download the following compressed package and unzip 

    the link: https://pan.baidu.com/s/1XODDiJmO9wYIPh38EJ37bQ ， 

    extraction code: ndi4

3. Go to the grobid-0.5.6 folder, open the console under this folder and run the command ：gradlew run
4. Open the console under the folder containing the jar package and run the command ：java -jar smart_cite-1.0.0-SNAPSHOT.jar

##  **Interface Description** 

###  Extract

Request method: POST

Request address: /extract

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


Return result：
```json
{
    "code": 0,
    "msg": "extract successfully",
    "data": {
        "fileName": "asset_id=10.1371%2Fjournal.pone.0000039.XML",
        "refTags": [
            {
                "reference": {
                    "volume": "67",
                    "article_title": "Behavioral study of obedience.",
                    "issue": "null",
                    "fpage": "371",
                    "year": "1963",
                    "id": "pone.0000039-Milgram1",
                    "label": "null",
                    "source": "Journal of Abnormal and Social Psychology",
                    "lpage": "378",
                    "authors": [
                        {
                            "surName": "Milgram",
                            "givenName": "null"
                        }
                    ]
                },
                "sentence": {
                    "id": 2,
                    "text": "He showed that in a social structure with recognised lines of authority, ordinary people could be relatively easily persuaded to give what seemed to be even lethal electric shocks to another randomly chosen person , ."
                },
                "contextList": [
                    {
                        "id": 1,
                        "text": "In an attempt to understand events in which people carry out horrific acts against their fellows Stanley Milgram carried out a series of experiments in the 1960s at Yale University that directly attempted to investigate whether ordinary people might obey the orders of an authority figure to cause pain to a stranger."
                    },
                    {
                        "id": 3,
                        "text": "His results are often cited today, for example, recently in helping to explain how people become embroiled in organised prisoner abuse  and even suicide bombings ."
                    },
                    {
                        "id": 4,
                        "text": "However, his study also ignited a far-reaching debate about the ethics of deception and of putting subjects in a highly distressing situation in the course of research , , and as a result this line of research is no longer amenable to direct experimental studies."
                    },
                    {
                        "id": 5,
                        "text": "Milgram's paradigm was an experiment that subjects were led to believe was a study of the effects of punishment on learning."
                    },
                    {
                        "id": 6,
                        "text": "The subjects, referred to as Teachers, were asked to administer electric shocks of increasing voltages to another subject (the Learner) whenever he gave a wrong answer in a word-memory experiment."
                    }
                ],
                "id": 1,
                "text": "[1]"
            },
            {
                "reference": {
                    "volume": "null",
                    "article_title": "null",
                    "issue": "null",
                    "fpage": "null",
                    "year": "1974",
                    "id": "pone.0000039-Milgram2",
                    "label": "null",
                    "source": "Obedience to Authority:",
                    "lpage": "null",
                    "authors": [
                        {
                            "surName": "Milgram",
                            "givenName": "null"
                        }
                    ]
                },
                "sentence": {
                    "id": 2,
                    "text": "He showed that in a social structure with recognised lines of authority, ordinary people could be relatively easily persuaded to give what seemed to be even lethal electric shocks to another randomly chosen person , ."
                },
                "contextList": [
                    {
                        "id": 1,
                        "text": "In an attempt to understand events in which people carry out horrific acts against their fellows Stanley Milgram carried out a series of experiments in the 1960s at Yale University that directly attempted to investigate whether ordinary people might obey the orders of an authority figure to cause pain to a stranger."
                    },
                    {
                        "id": 3,
                        "text": "His results are often cited today, for example, recently in helping to explain how people become embroiled in organised prisoner abuse  and even suicide bombings ."
                    },
                    {
                        "id": 4,
                        "text": "However, his study also ignited a far-reaching debate about the ethics of deception and of putting subjects in a highly distressing situation in the course of research , , and as a result this line of research is no longer amenable to direct experimental studies."
                    },
                    {
                        "id": 5,
                        "text": "Milgram's paradigm was an experiment that subjects were led to believe was a study of the effects of punishment on learning."
                    },
                    {
                        "id": 6,
                        "text": "The subjects, referred to as Teachers, were asked to administer electric shocks of increasing voltages to another subject (the Learner) whenever he gave a wrong answer in a word-memory experiment."
                    }
                ],
                "id": 2,
                "text": "[2]"
            }
        ]
    }
}
```

###  Batch Extraction

Request method: POST

Request address: /batchExtract

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


Return result：
```json
{
    "code": 0,
    "msg": "Successful extraction",
    "data": [
        {
            "fileName": "asset_id=10.1371%2Fjournal.pone.0000039.XML",
            "refTags": [
                {
                    "reference": {
                        "volume": "67",
                        "article_title": "Behavioral study of obedience.",
                        "issue": "null",
                        "fpage": "371",
                        "year": "1963",
                        "id": "pone.0000039-Milgram1",
                        "label": "null",
                        "source": "Journal of Abnormal and Social Psychology",
                        "lpage": "378",
                        "authors": [
                            {
                                "surName": "Milgram",
                                "givenName": "null"
                            }
                        ]
                    },
                    "sentence": {
                        "id": 2,
                        "text": "He showed that in a social structure with recognised lines of authority, ordinary people could be relatively easily persuaded to give what seemed to be even lethal electric shocks to another randomly chosen person , ."
                    },
                    "contextList": [
                        {
                            "id": 1,
                            "text": "In an attempt to understand events in which people carry out horrific acts against their fellows Stanley Milgram carried out a series of experiments in the 1960s at Yale University that directly attempted to investigate whether ordinary people might obey the orders of an authority figure to cause pain to a stranger."
                        },
                        {
                            "id": 3,
                            "text": "His results are often cited today, for example, recently in helping to explain how people become embroiled in organised prisoner abuse  and even suicide bombings ."
                        },
                        {
                            "id": 4,
                            "text": "However, his study also ignited a far-reaching debate about the ethics of deception and of putting subjects in a highly distressing situation in the course of research , , and as a result this line of research is no longer amenable to direct experimental studies."
                        },
                        {
                            "id": 5,
                            "text": "Milgram's paradigm was an experiment that subjects were led to believe was a study of the effects of punishment on learning."
                        },
                        {
                            "id": 6,
                            "text": "The subjects, referred to as Teachers, were asked to administer electric shocks of increasing voltages to another subject (the Learner) whenever he gave a wrong answer in a word-memory experiment."
                        }
                    ],
                    "id": 1,
                    "text": "[1]"
                },...
            ]
      },...
    ]
}
                
```

##  **Other Instructions** 
1. Default port: 8080
2. Upload file size ≤ 10M