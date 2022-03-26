
function setInputFilter(textbox, inputFilter) {

    ["input", "keydown", "keyup", "mousedown", "mouseup", "select", "contextmenu", "drop"].forEach(function (event) {

        textbox.addEventListener(event, function () {

            if (inputFilter(this.value)) {
                this.oldValue = this.value;
                this.oldSelectionStart = this.selectionStart;
                this.oldSelectionEnd = this.selectionEnd;
            } else if (this.hasOwnProperty("oldValue")) {
                this.value = this.oldValue;
                this.setSelectionRange(this.oldSelectionStart, this.oldSelectionEnd);
            } else {
                this.value = "";
            }
        });
    });
}

setInputFilter(document.getElementById('dataBox'), function (value) {

    return /^[0-9a-f\s]*$/i.test(value) &&
        (value.length === 0 || /^(?!.*([\s])\1+).+$/.test(value)) &&
        value.split(/\s/).every(function (period) {
            return period.length < 8 || period.length === 8
        })
});

let socket;

if (!window.WebSocket) {
    window.WebSocket = window.MozWebSocket;
}

if (window.WebSocket) {

    socket = new WebSocket("wss://localhost:8090/web-socket");
    socket.binaryType = "arraybuffer"
    socket.onmessage = function (event) {
        var ta = document.getElementById('responseText');
        ta.value += event.data + "\r\n";
    };

    socket.onopen = function (event) {

        var ta = document.getElementById('responseText');
        ta.value = "Successfully connected to server.\r\n";
    };

    socket.onclose = function (event) {

        var ta = document.getElementById('responseText');
        ta.value = "Server is disconnected.\r\n";
    };
} else {
    alert("Your browser is not supported websocket！");
}

function parseArrayBuffer(str) {

    return new Uint8Array(str.split(" ").map(function (h) {

        return parseInt(h, 16);
    })).buffer;
}


function send(message) {

    if (!window.WebSocket) {
        return;
    }

    if (socket.readyState === WebSocket.OPEN) {

        socket.send(message);

    } else {

        alert("Connection failure." + socket.readyState);
    }
}
