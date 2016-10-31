module.exports = {
  'forever': function(init, callback) {
    var eventSource = init();
    if ( !eventSource.reconnectInterval ) {
      eventSource.reconnectInterval = 1000;
    }
    eventSource.onerror = function (err) {
      if ("status" in err && (err.status == 502 || err.status == 503)) {
        eventSource.close();
        setTimeout(function () {
          module.exports.forever(init, callback);
        }, 5000);
      }
    };
    callback(eventSource);
  }
};
