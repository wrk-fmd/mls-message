const server = require('net').createServer();

server.on('connection', function (c) {
  let receivedData = false;

  c.on('data', function (chunk) {
    if (!receivedData) {
      receivedData = true;
      console.log('==============================================================================\n');

      c.write('HTTP/1.1 200 OK\r\n');
      c.write('Date: ' + (new Date()).toString() + '\r\n');
      c.write('Connection: close\r\n');
      c.write('Content-Type: text/plain\r\n');
      c.write('Access-Control-Allow-Origin: *\r\n');
      c.write('\r\n');
      setTimeout(function () {
        c.end();
      }, 2000);
    }

    console.log(chunk.toString());
  });
});

const port = process.argv[2] || process.env.PORT;
server.listen(port);
