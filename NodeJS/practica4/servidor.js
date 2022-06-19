var http = require("http");
var url = require("url");
var fs = require("fs");
var path = require("path");
var socketio = require("socket.io");

var MongoClient = require('mongodb').MongoClient;
var MongoServer = require('mongodb').Server;
var mimeTypes = { "html": "text/html", "jpeg": "image/jpeg", "jpg": "image/jpeg", "png": "image/png", "js": "text/javascript", "css": "text/css", "swf": "application/x-shockwave-flash" };

var httpServer = http.createServer(
    function (request, response) {
        var uri = url.parse(request.url).pathname;
        if (uri == "/") uri = "/usuario.html";
        var fname = path.join(process.cwd(), uri);
        fs.exists(fname, function (exists) {
            if (exists) {
                fs.readFile(fname, function (err, data) {
                    if (!err) {
                        var extension = path.extname(fname).split(".")[1];
                        var mimeType = mimeTypes[extension];
                        response.writeHead(200, mimeType);
                        response.write(data);
                        response.end();
                    }
                    else {
                        response.writeHead(200, { "Content-Type": "text/plain" });
                        response.write('Error de lectura en el fichero: ' + uri);
                        response.end();
                    }
                });
            }
            else {
                console.log("Peticion invalida: " + uri);
                response.writeHead(200, { "Content-Type": "text/plain" });
                response.write('404 Not Found\n');
                response.end();
            }
        });
    }
);

MongoClient.connect("mongodb://localhost:27017/", { useUnifiedTopology: true }, function (err, db) {
    httpServer.listen(8080);
    var io = socketio(httpServer);
    // const io = require('socket.io')(httpServer);
    const dbo = db.db("domotica");
    var allClients = new Array();
    var info = "";
    var estadoAC = false;
    var estadoPersiana = false;

    dbo.createCollection("medidas", function (err, collection) {
        if (err){
            var collection = dbo.collection("medidas");
        }
        io.sockets.on('connection',
            function(client) {
                allClients.push({ address: client.request.connection.remoteAddress, port: client.request.connection.remotePort});
                console.log('New connection from ' + client.request.connection.remoteAddress + ':' + client.request.connection.remotePort);
                io.sockets.emit('all-connections', allClients);
                io.sockets.emit('actualizarActuadores', {persiana: estadoPersiana, aire: estadoAC});
                client.on('informacionSensores', function (data){
                    info = data;
                    console.log(info);
                    collection.insertOne(info, {safe:true}, function(err, result){});
                    console.log(collection.find().toArray());
                    io.sockets.emit('actualizarSensores', info);
                });

                client.on('actualizarSensores', function(){
                    io.sockets.emit('actualizarSensores', info);
                });

                client.on('alertaTemperatura', function(data){
                    if (data.limite == "max") {
                        if (estadoAC == false) {
                            estadoAC = true;
                        }
                        if (estadoPersiana == true) {
                            estadoPersiana = false;
                        }

                    }
                    if (data.limite == "min") {
                        if (estadoAC == false) {
                            estadoAC = true;
                        }
                    }
                    io.sockets.emit('actualizarActuadores', { persiana: estadoPersiana, aire: estadoAC });
                });

                client.on('alertaLuminosidad', function (data) {
                    if (data.limite == "max") {
                        if (estadoPersiana == true) {
                            estadoPersiana = false;
                        }
                    }
                    if (data.limite == "min") {
                        if (estadoPersiana == false) {
                            estadoPersiana = true;
                        }
                    }
                    io.sockets.emit('actualizarActuadores', { persiana: estadoPersiana, aire: estadoAC });
                });


                client.on('aireAcondicionado', function(){
                    estadoAC = !estadoAC;
                    console.log('Aire ' + estadoAC);
                    io.sockets.emit('actualizarActuadores', {persiana:estadoPersiana, aire:estadoAC});
                });

                client.on('persiana', function () {
                    estadoPersiana = !estadoPersiana;
                    console.log('Persiana ' + estadoPersiana);
                    io.sockets.emit('actualizarActuadores', { persiana: estadoPersiana, aire: estadoAC });
                });

                client.on('alarmas', function(data){
                    io.sockets.emit('alarmas', data);
                });

                client.on('estadisticas', function(){
                    const func = collection.find().toArray().then(function(datos){
                        var total = datos.length;
                        var minT = datos[0].temperatura;
                        var maxT = datos[0].temperatura;
                        var minL = datos[0].luminosidad;
                        var maxL = datos[0].luminosidad;
                        
                        datos.forEach(element => {
                            if (element.temperatura >= maxT){
                                maxT = element.temperatura;
                            }
                            if (element.temperatura <= minT){
                                minT = element.temperatura;
                            }
                            if(element.luminosidad >= maxL){
                                maxL = element.luminosidad;
                            }
                            if (element.luminosidad <= minL){
                                minL = element.luminosidad;
                            }                                
                        });

                        console.log(minT);
                        stats = {total: total, max_l: maxL, max_t: maxT, min_l: minL, min_t: minT};
                        io.sockets.emit('estadisticas', stats);

                    });
                });

                client.on('disconnect', function () {
                    console.log("El cliente " + client.request.connection.remoteAddress + " se va a desconectar");
                    console.log(allClients);

                    var index = -1;
                    for (var i = 0; i < allClients.length; i++) {
                        //console.log("Hay "+allClients[i].port);
                        if (allClients[i].address == client.request.connection.remoteAddress
                            && allClients[i].port == client.request.connection.remotePort) {
                            index = i;
                        }
                    }

                    if (index != -1) {
                        allClients.splice(index, 1);
                        io.sockets.emit('all-connections', allClients);
                    } else {
                        console.log("EL USUARIO NO SE HA ENCONTRADO!")
                    }
                    console.log('El usuario ' + client.request.connection.remoteAddress + ' se ha desconectado');
                });

            }
        )
    });
});

console.log("Servicio Socket.io iniciado");
