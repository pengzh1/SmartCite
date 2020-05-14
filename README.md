#  **smartCiteCon** 

##  **介绍** 
一个基于语义的学术文献的引文上下文抽取工具。可提供PDF格式及符合Plosone数据集标准的XML格式的学术文件引文上下文抽取工作

##  **安装教程** 

1. 安装Java8
2. 下载下面的压缩包，并解压
    链接：https://pan.baidu.com/s/1XR0GRRWME5UDNgPT1eguZQ 
    提取码：vcfk
3. 进入grobid-0.5.6文件夹，在该文件夹下打开控制台并运行命令 gradlew run 
4. 在包含jar包文件夹下打开在控制台，并运行命令 java -jar smart_cite-1.0.0-SNAPSHOT.jar

##  **接口说明** 

### 抽取
请求方式：POST

请求地址：/extract

请求参数：

|字段|说明|类型|长度|是否必填|备注|
|---|---|---|---|---|---|
|file|文件|MultipleFile|	|是||

返回参数：

|code|message|data|动作|
|---|---|---|---|
|0|成功|JsonObject|显示收到的data|
|100|文件错误，请上传正确文件|null|提示错误信息|
|500|服务器错误|null|提示错误信息|

返回示例：
```json
{
    "code": 0,
    "msg": "抽取成功",
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

#### 批量抽取
请求方式：POST

请求地址：/batchExtract

请求参数：

|字段|说明|类型|长度|是否必填|备注|
|---|---|---|---|---|---|
|file|文件|MultipleFile|	|是||

返回参数：

|code|message|data|动作|
|---|---|---|---|
|0|成功|JsonArray|显示收到的data|
|100|文件错误，请上传正确文件|null|提示错误信息|
|500|服务器错误|null|提示错误信息|

返回示例：
```json
{
    "code": 0,
    "msg": "抽取成功",
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

##  **其他说明** 
1.默认端口：8080
2.上传文件大小 ≤ 10M