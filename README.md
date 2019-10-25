## Face Recognition via MQTT and IFTTT using [Nuclio](https://nuclio.io/)

Let's talk about my idea, we need analyse data from resources like surveillance camera or some camera. It will be useful for some use cases, for example, recognization about people that are committing a crime or something similar. The IoT project is composed about a client, in our case a simulator of a "reader from surveillance camera", a simple interface were you upload photo of a person, it detects the face and recognize some characteristics about the face (for example age, gender, ethnicity, emotions etc.), maybe this analyses is accomplished in real-world by a fog node. After the client sends via MQTT the data of the analysis. When a message arrives on MQTT a function in Nuclio is triggered, this function takes the raw data and accomplishes some filter and enrichment of data and in the second moment sends it to IFTTT webhook, that is triggered and send a message to telegram, below the result:

![telegram bot IFTTT](http://ferrara.link/img/iotFaceRecognition2019/iftttTelegramBot.jpg)

