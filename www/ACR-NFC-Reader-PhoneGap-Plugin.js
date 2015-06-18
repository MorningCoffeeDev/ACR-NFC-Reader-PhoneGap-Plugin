var exec = require('cordova/exec');

function ACR(){
}

ACR.clearLCD = function (success, failure) {
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "clearLCD", []);
}

ACR.display = function (msg, opts, success, failure) {
  var options = opts || {}
  if(options.bold == undefined){options.bold = false}
  if(options.font == undefined){options.font = 1}
  if(options.x == undefined){options.x = 0}
  if(options.y == undefined){options.y = 0}
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "display", [msg, options.x, options.y, options.bold, options.font]);
}

ACR.addTagIdListener = function (success, failure) {
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "listen", []);
}

ACR.authenticateWithKeyA = function(block,keyA,success,failure){
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "authenticateWithKeyA", [block,keyA]);
}

ACR.authenticateWithKeyB = function(block,keyB,success,failure){
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "authenticateWithKeyB", [block,keyB]);
}

ACR.writeAuthenticate = function(block,keyA, keyB,success,failure){
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "writeAuthenticate", [block,keyA,keyB]);
}

ACR.readData = function(block,success,failure){
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "readData", [block]);
}
ACR.writeData = function(block, data,success,failure){
  cordova.exec(success, failure, "ACRNFCReaderPhoneGapPlugin", "writeData", [block,data]);
}
ACR.onCardAbsent = function () {
}
ACR.onReady = function (reader) {
}
ACR.onAttach = function (device) {
}
ACR.onDetach = function (device) {
}
window.ACR = ACR;
