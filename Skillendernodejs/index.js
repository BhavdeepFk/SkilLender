var Hapi = require('hapi');
var controllers=require('./config/controllers.js')

var server = new Hapi.Server();
server.connection({ port: 8081 });

server.route(controllers);
server.views({
    path:'./pages/',
    layout:true,
    partialsPath:'./pages/partials/',
    engines:{
        html:require('handlebars')
    }
});



server.start(function () {
    console.log('Server running at:', server.info.uri);
});