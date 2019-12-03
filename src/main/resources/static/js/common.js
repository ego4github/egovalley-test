"use strict";
let commonObj = {
    basePath: window.location.protocol + "//" + window.location.host + "/egovalley"
};

function toJoke1() {
    location.href = "joke-1";
}

function toJoke2() {
    location.href = "joke-2";
}

function _uuid() {
    let d = Date.now();
    if (typeof performance !== 'undefined' && typeof performance.now === 'function') {
        d += performance.now();
    }
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        let r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
}