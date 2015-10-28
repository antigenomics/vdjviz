/*
    Created by bvdmitri<https://github.com/bvdmitri><bvdmitri@gmail.com>
 */

//Parameters
//  placeType:
//      -id
//      -class
//      -native
//      -child
//  place:
//      'based on placeType'
//  height
//      - value
//      - +value
//      - -value
//      - parent value
//  width
//      - same as height
//  offset
//      - .left
//      - .top
//  generateInlineStyles (experimental feature)
//      - true/false
//  method
//      -canvas
//      -direct
//  replaceElementType:
//      -same as place type
//  replaceElement:
//      'based on replaceElementType'
//  action
//      -download
//      -replace
//  force
//

function saveSvgAsPng(el, name, skaleFactor, type) {
    var svgConverter = new SVGConverter({
        place: el,
        action: 'download',
        imageName: name + '.png',
        force: true,
        generateInlineStyles: false,
        width: '+100',
        offset: {
            left: -50
        },
        method: 'canvas'
    });
}

function SVGConverter(parameters) {
    //Constant values
    var xmlns = "http://www.w3.org/2000/xmlns/";

    //Parameters
    var _placeType = 'native', _place,
        _height = 100, _width = 100,
        _offset = {
            _left: 0,
            _top: 0
        },
        _image, _canvas, _context, _data,
        _force = false,
        _replaceElementType = 'native', _replaceElement,
        _action = 'noop', _imageName = 'image.png',
        _browser, _inlineCss, _method = 'direct';


    detectBrowser();

    if (parameters) {
        setPlaceType(parameters.placeType);
        setPlace(parameters.place);
        if (parameters.height)
            setHeight(parameters.height);
        if (parameters.width)
            setWidth(parameters.width);
        if (parameters.replaceElementType)
            setReplaceElementType(parameters.replaceElementType);
        if (parameters.replaceElement)
            setReplaceElement(parameters.replaceElement);
        if (parameters.imageName)
            setImageName(parameters.imageName);
        if (parameters.offset)
            setOffset(parameters.offset);
        if (parameters.generateInlineStyles)
            _inlineCss = generateInlineStyles();
        if (parameters.method)
            setMethod(parameters.method);
        if (parameters.action)
            setAction(parameters.action);
        if (parameters.force) {
            _force = true;
            action();
        }

    }

    function setHeight(height) {
        if (height === 'parent') {
            var _oldPlace = _place;
            _place = _place.parentNode;
            setHeight();
            _place = _oldPlace;
            return;
        }
        if (!height || height[0] == '+') {
            _height = _place.offsetHeight || _place.height.baseVal.value || 100;
            if (height && height[0] == '+') {
                _height += parseInt(height.substring(1, height.length))
            }
        } else {
            _height = height;
        }
    }

    function setWidth(width) {
        if (width === 'parent') {
            var _oldPlace = _place;
            _place = _place.parentNode;
            setWidth();
            _place = _oldPlace;
            return;
        }
        if (!width || width[0] == '+') {
            _width = _place.offsetWidth || _place.width.baseVal.value || 100;
            if (width && width[0] == '+') {
                _width += parseInt(width.substring(1, width.length));
            }
        } else {
            _width = width;
        }
    }

    function setImageName(imageName) {
        _imageName = imageName || 'image.png';
    }

    function setOffset(offset) {
        _offset._left =  offset.left ? offset.left : 0;
        _offset._top = offset.top ? offset.top: 0;
    }

    function setMethod(method) {
        _method = method || 'direct';
    }

    function setPlaceType(placeType) {
        _placeType = placeType || 'native';
    }

    function setPlace(place) {
        if (!_placeType) _placeType = 'native';
        switch (_placeType) {
            case 'id':
                if (typeof place !== 'string') throw new Error('SVGConverter error\nIllegal place: it should be id of the element');
                _place = document.getElementById(place);
                break;
            case 'class':
                if (typeof _place !== 'string') throw new Error('SVGConverter error\nIllegal place: it should be class of the element');
                _place = document.getElementByClass(place);
                break;
            case 'native':
                _place = place;
                break;
            case 'child':
                _place = place.children[parameters.childCount];
                break;
            default:
                throw new Error("SVGConverter error\nIllegal place type: " + _placeType + "\n Possible values:\n-id\n-class\n-native\n");
        }
        if (!_place || _place.nodeName !== 'svg') throw new Error('SVGConverter error\nIllegal place: it should be svg element');
        setHeight();
        setWidth();
    }

    function setReplaceElementType(replaceElementType) {
        _replaceElementType = replaceElementType || 'native';
    }

    function setReplaceElement(element) {
        if (!_replaceElementType) _replaceElementType = 'native';
        switch (_replaceElementType) {
            case 'id':
                if (typeof element !== 'string') throw new Error('SVGConverter error\nIllegal replace element: it should be id of the element');
                _replaceElement = document.getElementById(element);
                break;
            case 'class':
                if (typeof element !== 'string') throw new Error('SVGConverter error\nIllegal replace element: it should be class of the element');
                _replaceElement = document.getElementByClass(element);
                break;
            case 'native':
                _replaceElement = element;
                break;
            default:
                throw new Error('SVGConverter error\nIllegal place type: ' + _placeType + '\n Possible values:\n-id\n-class\n-native\n');
        }
        if (!_replaceElement) throw new Error('SVGConverter error\nIllegal replace element');
    }

    function setAction(action) {
        _action = action || 'noop';
    }

    function action() {
        if (!_action) _action = 'noop';
        switch (_action) {
            case 'noop':
                break;
            case 'replace':
                generateImage();
                if (_method != 'canvas') replaceElement();
                break;
            case 'download':
                generateImage();
                if (_method != 'canvas') downloadImage();
                break;
            default:
                throw new Error('SVGConverter error\nIllegal action\nPossible values:\n-noop\n-replace\n');
        }
    }

    function generateStyles(element) {
        var place = element ? element : _place;
        if (!place || !(place instanceof Element)) throw new Error('SVGConverter error\nIllegal place: it should be svg element');
        var cssString = '',
            stylesheets = document.styleSheets;
        Array.prototype.forEach.call(stylesheets, function(stylesheet) {
            var rules = stylesheet.cssRules;
            if (rules) {
                try {
                    Array.prototype.forEach.call(rules, function (rule) {
                        if (rule && rule.style) {
                            if (place.querySelector(rule.selectorText) || place.parentNode.querySelector(rule.selectorText)) {
                                cssString += (rule.selectorText + ' { ' + rule.style.cssText + ' } \n');
                            } else if (rule.cssText.match(/^@font-face/)) {
                                cssString += rule.cssText + '\n';
                            }
                        }
                    })
                } catch (error) {
                    console.log('Warning generating styles: ' + error);
                }
            }
        });
        return cssString;
    }

    function generateInlineStyles(element) {
        var place = element ? element : _place;
        var cssString = '';
        if (!place || !(place instanceof Element)) throw new Error('SVGConverter error\nIllegal place: it should be svg element');
        var childNodes = place.childNodes;
        Array.prototype.forEach.call(childNodes, function(childNode) {
            if (!(childNode instanceof Element)) return;
            var css = window.getComputedStyle(childNode, null);
            if (css) {
                childNode.style.cssText = css.cssText;
            }
            if (childNode.id) {
                cssString += '#' + childNode.id + ' { ' + css.cssText  + ' } \n';
            }
            cssString += generateInlineStyles(childNode);
        });
        return cssString;
    }

    function generateImage() {
        var svgDataString,
            cssRules = generateStyles();
        var _clone = _place.cloneNode(true);

        _clone.setAttribute("version", "1.1");
        _clone.setAttributeNS(xmlns, "xmlns", "http://www.w3.org/2000/svg");
        _clone.setAttributeNS(xmlns, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        _clone.setAttribute("width", _width);
        _clone.setAttribute("height", _height);
        _clone.setAttribute("viewBox", _offset._left + " " +  _offset._top + " " + _width + " " + _height);

        var styles = document.createElement('style');
        styles.setAttribute('type', 'text/css');
        if (_inlineCss) cssRules += _inlineCss;
        styles.innerHTML = cssRules;
        _clone.insertBefore(styles, _clone.firstChild);


        switch (_method) {
            case 'direct':
                svgDataString = (new XMLSerializer()).serializeToString(_clone);
                _data = "data:image/svg+xml;base64," + btoa(svgDataString);
                break;
            case 'canvas':
                svgDataString = (new XMLSerializer()).serializeToString(_clone);
                _canvas = document.createElement('canvas');
                _canvas.width = _width;
                _canvas.height = _height;
                _context = _canvas.getContext('2d');
                _image = new Image();
                _image.src = "data:image/svg+xml;base64," + btoa(svgDataString);
                _image.onload = function () {
                    _context.drawImage(_image, -_offset._left, -_offset._top);
                    _data = _canvas.toDataURL('image/png');
                    if (_force && _action == 'replace') replaceElement();
                    if (_force && _action == 'download') downloadImage();
                };
                break;
            default:
                throw new Error('SVGConverter error\nIllegal method\nPossible values:\n-direct\n-canvas\n');
        }

    }

    function replaceElement(element) {
        var place = element ? element : _replaceElement;
        var image = new Image();
        image.src = _data;
        place.parentNode.replaceChild(image, place);
    }

    function downloadImage(name) {
        if (!_data) generateImage();
        var a = document.createElement('a');
        a.download = name ? name : _imageName ? _imageName : 'image.png';
        a.href = _data;
        document.body.appendChild(a);
        a.addEventListener("click", function() {
            a.parentNode.removeChild(a);
        });
        a.click();
    }

    function detectBrowser() {
        var ua = navigator.userAgent;
        if (ua.search(/Chrome/) > 0) _browser =  'chrome';
        else if (ua.search(/Firefox/) > 0) _browser = 'firefox';
        else if (ua.search(/Opera/) > 0) _browser = 'opera';
        else if (ua.search(/Safari/) > 0) _browser = 'safari';
        else if (ua.search(/MSIE/) > 0) _browser =  'ie';
    }


    //Public methods
    this.setHeight = setHeight;
    this.setWidth = setWidth;
    this.setOffset = setOffset;
    this.setAction = setAction;
    this.setMethod = setMethod;
    this.setPlaceType = setPlaceType;
    this.setPlace = setPlace;
    this.setReplaceElementType = setReplaceElementType;
    this.setReplaceElement = setReplaceElement;
    this.generateImage = generateImage;
    this.setImageName = setImageName;
    this.downloadImage = downloadImage;
    this.replaceElement = replaceElement;

}