/**
 * To check what data type something has in javascript is not always the easiest.
 * The language itself provides an operator called typeof for that which returns a string of what a values data type is.
 * For an object ”object” is returned and for a string ”string”.

 However javascripts data types and the typeof operator aren’t exactly perfect.
 For example for arrays and null ”object” is returned and for NaN and Infinity ”number”.
 To check for anything more than just primitive data types
 and to know if something’s actually a number, string, null, an array or a real object a little more logic is required.
 */


// A string is always a string so this one is easy.
// However when a string is called with new it's suddenly an object,
// so to know if the object is a string its constructor can be compared to String.

// Returns if a value is a string
function isString(value) {
    if (value && typeof value === 'object') {
        return value.constructor === String;
    }
    return typeof value === 'string';
}

// From typeof more things than just an ordinary number will return "number" like NaN and Infinity.
// To know if a value really is a number the function isFinite is also required.

// Returns if a value is really a number
function isNumber(value) {
    return typeof value === 'number' && isFinite(value);
}

// In javascript arrays are not true arrays like in java and in other languages.
// They're actually objects so typeof will return "object" for them.
// To know if something's really an array its constructor can be compared to Array.

// Returns if a value is an array
function isArray(value) {
    return value && typeof value === 'object' && value.constructor === Array;
    // Ecmascript 5 has actually has a method for this (ie9+)
    // Array.isArray(value);
}


// Returns if a value is a function
function isFunction(value) {
    return typeof value === 'function';
}

// Many things are objects in javascript.
// To know if a value is really an object that can have properties and be looped through,
// its constructor can be compared to Object.

// Returns if a value is an object
function isObject(value) {
    return value && typeof value === 'object' && value.constructor === Object;
}

// Most times you don't need to check explicitly for null and undefined since they're both false values.
// However to do it below functions can be used.

// Returns if a value is null
function isNull(value) {
    return value === null;
}

// Returns if a value is undefined
function isUndefined(value) {
    return typeof value === 'undefined';
}

function isBoolean(value) {
    return typeof value === 'boolean';
}

// RegExp’s are objects so only thing needed to check is if a values constructor is RegExp.
// Returns if a value is a regexp
function isRegExp (value) {
    return value && typeof value === 'object' && value.constructor === RegExp;
}

// Date is not a real data type in javascript.
// But to know if something's a Date object, instanceof can be used.

// Returns if value is a date object
function isDate (value) {
    return value instanceof Date;
}
