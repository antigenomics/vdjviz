var exampleSocket = new WebSocket("ws://localhost:9000/api/ws");
exampleSocket.onmessage = function(event) {
    console.log(event.data);
};
var msg = {
    type: "message",
    action: "render",
    data : {
        fileName: "migec"
    }
};
exampleSocket.onopen = function() {
    exampleSocket.send(JSON.stringify(msg));
};