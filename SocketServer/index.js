var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var bodyParser = require('body-parser');

app.use(bodyParser.json()); //json support
app.use(bodyParser.urlencoded({
	extended: true
})); //support url-encoded bodies


app.post('/emitMessage', function(req, res){

	var headerAuth = req.get('AUTH');
	var body = req.body;


	io.emit(body.eventName, body.message);
	console.log('sent data to event: ' + body.eventName);

	res.send('ok');
});

http.listen(3000, function(){
  console.log('listening on *:3000');
});