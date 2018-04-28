/**
 * saas_mixin全局构造函数
 */
;(function (factory) {
    'use strict';
    if (typeof window !== 'undefined' && window.jQuery) {
        factory(window.jQuery, window);
    } else {
        throw new Error('window/jQuery is undefined');
    }
}(function ($, window) {
    'use strict';
    let saas_mixin = function () {
        this.init();
    }
    saas_mixin.prototype = {
        // saas_mixin构造函数描述
        describe: 'saas系统mixin方法',
        // 版本号
        version: '1.0.0',
        // 初始化，里面是默认需要执行的函数
        init: function () {
            let self = this;
            $.fn.daterangepicker.defaultOptions = {
                singleDatePicker: true,
                showDropdowns: true,
                locale: {
                    format: 'YYYY-MM-DD',
                    applyLabel: '确定',
                    cancelLabel: '取消',
                    fromLabel: '起始时间',
                    toLabel: '结束时间',
                    customRangeLabel: '自定义',
                    daysOfWeek: ['日', '一', '二', '三', '四', '五', '六'],
                    monthNames: ['一月', '二月', '三月', '四月', '五月', '六月',
                        '七月', '八月', '九月', '十月', '十一月', '十二月'],
                    firstDay: 1
                }
            };
            $(function () {
                self.axios();
                self.setVal();
                self.saasFormItem();
                self.saasSingleDaterange();
                self.saasDaterange();
                self.saasDistrict();
                self.isRequired();
                self.saasCheckbox();
                self.saasRadio();
                self.saasSelect();
                self.saasInput();
                self.initValidator();
                self.errorImage();
                self.loadEnd();
            });
        },
        axios: function () {
            if ($.axios) return;
            if (window.axios) {
                // 创建axios实例
                const service = axios.create({
                        // baseURL: process.env.BASE_URL,
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        // 设置允许跨域
                        withCredentials: true,
                        timeout: 30000
                    });
                // request拦截器
                service.interceptors.request.use(function(config) {
                    return config
                }, function(error) {
                    return Promise.reject(error)
                })
                // response响应拦截器
                service.interceptors.response.use(function(response) {
                    return response;
                }, function(error) {
                    if(error && error.response) {
                        switch (error.response.status) {
                            case 504:
                                break
                            case 502:
                                break
                            case 404:
                                break
                            default:
                        }
                    } else {
                        alert('服务器异常');
                    }
                    return Promise.reject(error)
                })
                $.axios = service;
            } else {
                throw new Error('axios is not defined');
            };
        },
        loadEnd: function () {
            $('[saas-cloak]').addClass('load-end');
        },
        // jquery.validator表单验证
        initValidator: function (options) {
            if ($.validator) {
                $.validator.setDefaults({
                    errorClass: 'text-error',
                    onfocusout: function (element) {
                        $(element).valid();
                    },
                    errorPlacement: function(error, element) {
                        element.attr('type') === 'checkbox' ? element.parent().parent().after(error) : element.after(error);
                    }
                });
                // 添加新定义的表单校验规则
                this.addValidatorMethods.map(function (method) {
                    $.validator.addMethod(method.name, method.validator, method.message);
                });
                $('form').each(function () {
                    $(this).validate();
                })
            }
        },
        // 定义自定义表单校验规则
        addValidatorMethods: [
            // 手机号验证
            {
                name: 'isPhone',
                validator: function (value, element, params) {
                    let reg = /^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\d{8}$/;
                    if (reg.test(value)) {
                        return true;
                    } else {
                        return false;
                    }
                },
                message: '手机号格式错误'
            },
            // 身份证号码验证
            {
                name: 'isCardCode',
                validator: function (value, element, params) {
                    let city = {
                        11: "北京",12: "天津",13: "河北",14: "山西",15: "内蒙古",21: "辽宁",22: "吉林",23: "黑龙江 ",31: "上海",32: "江苏",33: "浙江",34: "安徽",35: "福建",36: "江西",37: "山东",41: "河南",42: "湖北 ",43: "湖南",44: "广东",45: "广西",46: "海南",50: "重庆",51: "四川",52: "贵州",53: "云南",54: "西藏 ",61: "陕西",62: "甘肃",63: "青海",64: "宁夏",65: "新疆",71: "台湾",81: "香港",82: "澳门",91: "国外"};
                    let status = true;
                    if (!/^[1-9]\d{5}((1[89]|20)\d{2})(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dx]$/i.test(value)) {
                        status = false;
                    } else if (!city[value.substr(0, 2)]) {
                        status = false;
                    } else {
                        let arrExp = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];//加权因子  
                        let arrValid = [1, 0, "X", 9, 8, 7, 6, 5, 4, 3, 2];//校验码
                        let sum = 0, idx;
                        for (let i = 0; i < value.length - 1; i++) {
                            // 对前17位数字与权值乘积求和  
                            sum += parseInt(value.substr(i, 1), 10) * arrExp[i];
                        }
                        // 计算模（固定算法）  
                        idx = sum % 11;
                        // 检验第18为是否与校验码相等  
                        if (arrValid[idx] == value.substr(17, 1).toUpperCase()) {
                            status = true;
                        } else {
                            status = false;
                        }
                    }
                    return status;
                },
                message: '身份证号码格式错误'
            }
        ],
        // 监听表单的value值变化，这里是监听input事件（可以添加多事件）
        watch: function (elements) {
            if (!elements && typeof elements !== 'object') {
                throw new Error('saas_mixin.watch(element): element is undefined or typeof is not object');
            }
            // 这里的event用change,因为select2是用change监听
            let _event = 'change';
            for (let element in elements) { // 这里注意element作用域的问题，不能用var定义，要用let
                let _element = $(element);
                _element && _element.on(_event, function () {
                    _element.prop('value', function (index, value) {
                        // if(!_element.prop('isFrist')){
                        //     _element.prop('isFrist', true);
                        //     if(_element[0].tagName === 'SELECT' && value === '0') return;
                        // }
                        if (typeof elements[element] !== 'function') return;
                        elements[element](_element, value);
                    });
                }).trigger(
                    // 根据标签是否存在属性undefault判断是否默认触发一次input事件
                    (function(demo){
                        return (demo[0] && 'undefault' in demo[0].attributes) ? '' : _event;
                    }(_element))
                );
            }
        },
        setVal: function (val) {
            if ($.setVal) return;
            $.fn.setVal = function (value) {
                $(this).val(value).trigger('change');
                return $(this);
            }
        },
        getCheckBox: function (el) {
            let checkbox = [];
            el && el.each(function () {
                let _this = $(this);
                if(_this.is(':checked')) checkbox.push(_this.val());
            });
            return checkbox.join(',');
        },
        // 给表单必填项添加*标识
        isRequired: function (element) {
            let self = this;
            let addRequiredFlag = function (demo, isAdd, num) {
                let parent = demo.parent();
                if (parent.hasClass('el-form-item')) {
                    // 根据isAdd值添加/删除*标识（eg: input为disabled时删除*标识）
                    isAdd ? parent.addClass('is-required') : parent.removeClass('is-required');
                } else {
                    if (num++ < 10) { // 最多递归10次，避免找不到元素无限递归报错
                        addRequiredFlag(parent, isAdd, num++);
                    }
                }
            }
            // 这里是做单独的isRequired标识
            if (element) {
                addRequiredFlag(element, !element.prop('disabled') && element.prop('required'), 0);
                return;
            }
            $('*[required]').each(function () {
                if ( self.saasSign($(this), 'required') ) return;
                addRequiredFlag($(this), !$(this).prop('disabled') && $(this).prop('required'), 0);
            });
        },
        isDisabled: function (disabledType, ...elements) {
            let self = this;
            $(elements.join(',')).prop({
                'disabled': function (i, v) {
                    // 表单为disabled时，清除value值
                    if($(this).prop('unDefault')) { // 如果不存在unDefault属性，表明是第一次，不清除value
                        if (disabledType) {
                            $(this).setVal('').removeClass('text-error').next('label.text-error').remove();
                        }
                    }else { // 如果是第一次检测，添加unDefault属性
                        $(this).prop('unDefault', true);
                    }
                    return disabledType;
                },
                'required': function (i, v) {
                    self.isRequired($(this));
                }
            });
        },
        // 多选框美化
        saasCheckbox: function () {
            $('input[type=checkbox]').each(function () {
                let _this = $(this);
                _this.on('change', function () {
                    let checkBox = _this.parent('.checkbox');
                    _this.is(':checked') ?
                        checkBox.addClass('is-checked')
                        :
                        checkBox.removeClass('is-checked')
                }).trigger('change');
            });
        },
        saasRadio: function () {
            $('input[type=radio]').each(function () {
                let _this = $(this);
                _this.on('change', function () {
                    let checkBox = _this.parent('.radio');
                    if(_this.is(':checked')) {
                        checkBox.addClass('is-checked').siblings('.radio').removeClass('is-checked');
                    }
                }).trigger('change');
            });
        },
        saasSelect: function () {
            let self = this;
            $('select').each(function () {
                let _this = $(this);
                if ( self.saasSign(_this, 'select') ) return;
                _this.val(function (i, ov) {
                    return ov == 0 ? '' : ov;
                }).addClass('form-control').select2({
                    language: "zh-CN",
                    placeholder: _this.attr('placeholder') || '请选择',
                    allowClear: _this.attr('multiple') ? false : true
                });
            });
        },
        saasInput: function () {
            let self = this;
            $('input').each(function () {
                let _this = $(this);
                if ( self.saasSign(_this, 'input') ) return;
                let _attr = _this.attr('type');

                switch (_attr) {
                    case 'search':
                        break;
                    case 'hidden':
                        break;
                    case 'checkbox':
                        break;
                    case 'radio':
                        break;
                    case 'button':
                        break;
                    case 'file':
                        break;
                    default:
                        _this.addClass('el-input__inner');
                        creatClear(_this);
                }
            });
            function creatClear (demo) {
                let _clear = $('<i/>', {
                        class: 'saas-input__clear'
                    }).css(
                        ( function(_demo){
                            let _append = _demo.next('.el-input-group__append');
                            if (_append) return {
                                'transform': 'translateX(-'+ _append.outerWidth() +'px)'
                            }
                        } )( demo )
                    ).on('click', function () {
                        demo.setVal('').trigger( (function (d) {
                            // 这里如果是日期表单的话，trigger触发apply.daterangepicker
                            return d.attr('id') === 'saas-daterange__input' ? 'apply.daterangepicker' : 'focusout';
                        })( demo ) );
                    });
        
                if(!demo.parent().hasClass('el-input')) demo.wrap('<div class="el-input"></div>');

                demo.after(_clear);
            }
        },
        saasFormItem: function () {
            let self = this;
            $('div[saas-form-item]').each(function () {
                let _this = $(this);
                if ( self.saasSign(_this, 'form-item') ) return;
                let _label = _this.attr('label') || '';
                let _col = _this.attr('col') || '24';
                let _textarea = _this.find('textarea');
                if(_textarea) _textarea.addClass('form-control');
                _this.children().wrapAll('<div class="el-form-item__content"/>');
                _this.addClass('el-form-item').prepend($('<label class="el-form-item__label"/>').text(_label));
                _this.wrap('<div class="el-col-'+_col+'">');
            });
        },
        saasSingleDaterange: function () {
            let self = this;
            $('input[saas-singleDaterange]').each(function () {
                let _this = $(this);
                if ( self.saasSign(_this, 'singleDaterange') ) return;
                let _options = eval(`(${_this.data('options')})`) || {};
                _this.daterangepicker(_options);
            });
        },
        saasDaterange: function () {
            let self = this;
            $('div[saas-daterange]').each(function () {
                let _this = $(this);
                if ( self.saasSign(_this, 'daterange') ) return;
                let _options = eval(`(${_this.data('options')})`) || {};
                let _startTime = _this.find('input').eq(0);
                let _endTime = _this.find('input').eq(1);
                let _s_v = _startTime.val();
                let _e_v = _endTime.val();
                let _input = $('<input/>', {
                        'id': 'saas-daterange__input',
                        'placeholder': '请选择日期'
                    })
                    .daterangepicker($.extend(true, {
                        singleDatePicker: false,
                        autoUpdateInput: false
                    }, _options))
                    .on('apply.daterangepicker', function(ev, picker) {
                        if(!self.saasSign(_this, 'apply') && _s_v != '' && _e_v != '') {
                            $(this).val(_s_v + ' - ' + _e_v);
                            return;
                        }
                        let s_v = '';
                        let e_v = '';
                        if (picker) {
                            s_v = picker.startDate.format('YYYY-MM-DD');
                            e_v = picker.endDate.format('YYYY-MM-DD');
                            $(this).val(s_v + ' - ' + e_v);
                        }
                        _startTime.setVal(s_v);
                        _endTime.setVal(e_v);
                    })
                    .trigger('apply.daterangepicker')
                    .appendTo(_this);
            });
        },
        // 省市县三级联动
        saasDistrict: function () {
            let self = this;
            let default_attrs = {
                class: 'form-control saas-district',
                placeholder: '请选择',
                disabled: true
            };
            let _districts = $('input[saas-district]');
            _districts.length > 0 && _districts.each(function(index, el) {
                let __el = $(el);
                if ( self.saasSign(__el, 'district') ) return;
                // 生成省份下拉框
                let _province = createdSelect({
                    placeholder: '请选择省份'
                }, {
                    code: '',
                    codeLength: 2
                });
                // 生成城市下拉框
                let _city = createdSelect({
                    placeholder: '请选择城市'
                });
                // 生成县区下拉框
                let _district = createdSelect({
                    placeholder: '请选择县区'
                });
                // 生成.saas-district-group容器
                let _container = $('<div/>').attr({
                    class: 'saas-district-group'
                }).append(_province).append(_city).append(_district);
                // 根标签设置type=hidden隐藏域，并把容器添加到根标签后面
                __el.attr({type: 'hidden'}).after(_container);
                // 省份添加input事件
                onEvent(_province, function (index, value) {
                    setCurrentValue(__el, value);
                    initSelect(_city, _district);
                    createdOptions(_city, {
                        code: value,
                        codeLength: 4
                    });
                })
                // 城市添加input事件
                onEvent(_city, function (index, value) {
                    setCurrentValue(__el, value);
                    initSelect(_district);
                    createdOptions(_district, {
                        code: value,
                        codeLength: 6
                    });
                })
                // 县区添加input事件
                onEvent(_district, function (index, value) {
                    setCurrentValue(__el, value);
                })
            });
            // 生成select
            function createdSelect (attrs, params) {
                let _attrs = $.extend(default_attrs, attrs);
                let _select = $('<select/>')
                                .attr(_attrs)
                                .append(
                                    $('<option>').attr({value: 0}).text(_attrs.placeholder)
                                );
                (params && params.codeLength) && createdOptions (_select, params);
                return _select;
            }
            // 生成select对应的option
            function createdOptions (parentElement, params) {
                axios.get('/sys/district/getLists', {
                    params: params || {}
                }).then(function(res) {
                    let data = res.data;
                    if (data.code === 0 && data.data.length > 0) {
                        data.data.forEach(function (item, index) {
                            let _options = $('<option/>').attr({
                                value: item.code
                            }).text(item.name);
                            parentElement.append(_options)
                        });
                        parentElement.removeAttr('disabled');
                    }
                })
            }
            // 给下拉框绑定input事件
            function onEvent (element, fun) {
                element.on('change', function () {
                    $(this).prop('value', function (index, value) {
                        typeof fun === 'function' && fun(element, value);
                    })
                })
            }
            // 下拉框数据更新前，先初始化对应的下拉框
            function initSelect (...elements) {
                for(let i = 0; i < elements.length; i++) {
                    elements[i].text('').append(
                        $('<option>').attr({value: 0}).text(elements[i][0].attributes.placeholder.value || default_attrs.placeholder)
                    ).attr('disabled', true);
                }
            }
            // 每次选择完更新选中的value值
            function setCurrentValue (element, value) {
                element.val(value);
            }
        },
        // 这里是防止标签被频繁渲染
        saasSign: function (demo, key) {
            if (demo.prop('sign-' + key)) {
                return true;
            } else {
                demo.prop('sign-' + key, true);
                return false;
            }
        },
        // canvas图片预览
        imagePreview: function (file) {
            let helper = {
                support: !!(window.FileReader && window.CanvasRenderingContext2D),
                isFile: function (item) {
                    return typeof item === 'object' && item instanceof window.File;
                },
                isImage: function (file) {
                    let type = '|' + file.type.slice(file.type.lastIndexOf('/') + 1) + '|';
                    return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
                }
            };

            function onLoadFile(event) {
                let img = new Image();
                img.onload = onLoadImage;
                img.src = event.target.result;
            }

            function onLoadImage() {
                let width = 100;
                let height = 100;
                canvas.attr({width: width, height: height});
                canvas[0].getContext('2d').drawImage(this, 0, 0, width, height);
            }

            if (!helper.support) {
                console.log('support');
                return;
            }

            if (!helper.isFile(file)) {
                console.log('isFile');
                return;
            }
            if (!helper.isImage(file)) {
                console.log('isImage');
                return;
            }

            let canvas = $('<canvas/>').text('Your brower does not support HTML5 Canvas!');
            let reader = new FileReader();

            reader.onload = onLoadFile;
            reader.readAsDataURL(file);
            return canvas;
        },
        // 图片加载失败处理
        errorImage: function () {
            let defaultErrorSrc = '/images/errImage.png';
            let images = $('img');
            if (images.length <= 0) return;
            images.each(function () {
                let _this = $(this);
                _this.on('error', function () {
                    _this.attr('src', defaultErrorSrc);
                })
            });
        }
    }
    window.$saas = window.saas_mixin = new saas_mixin();
}));