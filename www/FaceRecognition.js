var exec = require('cordova/exec');

exports.startFaceRecognition = function (arg0, success, error) {
    exec(success, error, 'FaceRecognition', 'startFaceRecognition', [arg0]);
};
