var amqp = require('amqplib');
        var FUNCTION_NAME = "mqttconsume";
        function send_feedback(msg){
            var q = 'iot/logs';
            amqp.connect('amqp://guest:guest@192.168.137.94:5672').then(function(conn) {
                return conn.createChannel().then(function(ch) {
                    var ok = ch.assertQueue(q, {durable: false});
                    return ok.then(function(_qok) {
                    ch.sendToQueue(q, Buffer.from(msg));
                    console.log(" [x] Sent '%s'", msg);
                    return ch.close();
                    });
                }).finally(function() { 
                        conn.close();
                    });
            }).catch(console.warn);
        }
        function bin2string(array){
          var result = "";
          for(var i = 0; i < array.length; ++i){
            result+= (String.fromCharCode(array[i]));
          }
          return result;
        }


 var request = require('request');
        exports.handler = function(context, event) {
           
            var _event = JSON.parse(JSON.stringify(event));
            var _data = bin2string(_event.body.data);
            var reqUrl = 'https://maker.ifttt.com/trigger/facerecognition/with/key/cr2zacfrJUv8WnKnD2r-3FLJctYlRovdnryzRr5Ie8N';
      var json = JSON.parse(_data);

		var parameters = {};
        var GeneralAspect= "<br><b>General aspect</b><br> Age: "+json.faces[0].attributes.age.value+"<br>Ethnicity: "+json.faces[0].attributes.ethnicity.value+"<br>Gender: "+json.faces[0].attributes.gender.value;
var Beauty="<br><b>Beauty:</b><br>Male Score: "+json.faces[0].attributes.beauty.male_score+"<br>Female score: "+json.faces[0].attributes.beauty.female_score+"<br>";
var SkinStatus="<br><b>Skin Status:</b></br>Acne: "+json.faces[0].attributes.skinstatus.acne+"<br>Health: "+json.faces[0].attributes.skinstatus.health;
var Emotion="<br><b>Emotion:</b></br> Sadness: "+json.faces[0].attributes.emotion.sadness+"<br>Neutral: "+ json.faces[0].attributes.emotion.neutral+"<br>Disgust: "+json.faces[0].attributes.emotion.disgust+"<br>Anger: "+json.faces[0].attributes.emotion.anger+"<br>Surprise: "+ json.faces[0].attributes.emotion.surprise+"<br>Fear: "+json.faces[0].attributes.emotion.fear+"<br> Happiness: "+ json.faces[0].attributes.emotion.happiness;
parameters['value1'] = GeneralAspect+SkinStatus+Emotion+Beauty;
request.post({
         headers: {'content-type' : 'application/json'},
         url:reqUrl,
         body: JSON.stringify(parameters) 
}, function(error , response , body){
    console.log(body);
   
            send_feedback("Invoked Function MQTT: "+FUNCTION_NAME+" received "+body);
});


            console.log("TRIGGER "+_data);
  send_feedback("Invoked Function MQTT: "+FUNCTION_NAME+" received "+_data);
          };
