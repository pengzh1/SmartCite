#  **SmartCiteCon** 

##  **介绍** 
一个基于语义的学术文献的引文上下文抽取工具。可提供PDF格式及符合Plosone数据集标准的XML格式的学术文件引文上下文抽取工作

##  **安装教程** 

1. 安装Java8
2. 如果需要处理PDF论文，请安装并运行gorbid。[点击查看grobid官方安装说明文档](https://grobid.readthedocs.io/en/latest/Install-Grobid/) 
3. 下载下面的压缩包，并解压
    
    链接: https://pan.baidu.com/s/1PPARTNc0NipZ7bKaNmsupg 

    提取码: ixj4
   
4. 在包含jar包文件夹下打开在控制台，并运行命令: 
    
    java -jar smart_cite-1.0.0-SNAPSHOT.jar  
        
##  **接口说明** 

注意：上传的文件必须是学术论文且必须符合支持的文件格式

SCC支持的文件格式有：
Plosone数据库的XML、TEI格式的XML、符合GORC data format的Json数据

### /extract
功能：抽取单篇论文的引文上下文

请求方式：POST

请求地址：/extract

请求参数：

| method | request type | response type | parameters | reqirement | description|
|---|---|---|---|---|---|
|post|multipart/form-data|application/json|file|required|待处理的文件|



请求示例：
```ftl
curl --location --request POST 'http://localhost:8080/extract' --form 'file=@/E://FileStorage/File/2020-05/pdf/CO13_1p014.pdf'
```

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

#### /batchExtract
功能：批量抽取多篇论文的引文上下文

注意：

1. 目前仅支持上传zip类型的压缩文件
2. zip中的文件的格式必须是SCC支持的
3. zip文件大小不可超过10M，request大小不可超过100M。

请求方式：POST

请求地址：/batchExtract

请求参数：

| method | request type | response type | parameters | reqirement | description|
|---|---|---|---|---|---|
|post|multipart/form-data|application/json|file|required|待处理的zip文件|


请求示例：
```ftl
curl --location --request POST 'http://localhost:8080/batchExtract' --form 'file=@/E:/temp/plos/computer_science/3(done).zip'
```

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
#### /localExtract
功能: 抽取文件夹下所有论文的引文上下文

注意：
1. 文件夹必须是在本地服务器上
2. 文件夹下的文件必须都是SCC支持的格式
3. 处理的结果存储在与jar包同级目录的output文件夹下
4. 在没有完成处理前，请不要关停服务

请求方式：GET

请求地址：/localExtract

请求参数：

| method | request type | response type | parameters | reqirement | description|
|---|---|---|---|---|---|
|get| | |path|required|含有待处理文件的文件夹路径|

请求示例：

```ftl
curl --location --request GET 'http://localhost:8080/localExtract?path=/home/guochenrui/smart_cite/3000'
```
返回参数：无。只需发出请求即可，无需等待返回参数。

#### /count/contextNum
功能: 统计结果文件中ICC和ECC的数量

请求方式：GET

请求地址：/count/contextNum

请求参数：

| method | request type | response type | parameters | reqirement | description|
|---|---|---|---|---|---|
|get| | |outputFolder|unrequired|含有结果的文件夹路径，默认为output文件夹|

请求示例：

```ftl
curl --location --request GET 'http://localhost:8080/count/contextNum?outputFolder=E:\code\smart_cite\output\json'
```

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
    "msg": "extract successfully",
    "data": {
        "ECC": 1605695,
        "ICC": 10215848,
        "FileNum": 33319
    }
}
```

##  **其他说明** 

1. 默认请求端口：8080

2. 上传文件大小 ≤ 10M

3. 配置文件位于与jar包同级目录的resource文件夹下。可以根据您的情况修改默认端口等参数。


