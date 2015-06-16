var exec = require('cordova/exec');

function ACR(){
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
