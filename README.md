# smartCite

#### 介绍
基于语义相关性的引文上下文抽取工具

#### 软件架构
软件架构说明


#### 安装教程

1. xxxx
2. xxxx
3. xxxx

#### 接口说明

##### 抽取
请求方式：POST

请求地址：/extract

请求参数：

|字段|说明|类型|长度|是否必填|备注|
|---|---|---|---|---|---|
|file|文件|MultipleFile|	|是||

返回参数

|code|message|data|动作|
|---|---|---|---|
|0|成功|JsonObject|显示收到的data|
|100|文件错误，请上传争取文件|null|提示错误信息|
|500|服务器错我|null|提示错误信息|

返回示例
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

##### 批量抽取
请求方式：POST

请求地址：/batchExtract

请求参数：

|字段|说明|类型|长度|是否必填|备注|
|---|---|---|---|---|---|
|file|文件|MultipleFile|	|是||

返回参数

|code|message|data|动作|
|---|---|---|---|
|0|成功|JsonArray|显示收到的data|
|100|文件错误，请上传争取文件|null|提示错误信息|
|500|服务器错我|null|提示错误信息|

返回示例
```json

```
#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request


#### 码云特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. 码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5. 码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. 码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)