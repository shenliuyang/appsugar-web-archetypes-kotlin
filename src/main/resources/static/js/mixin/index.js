window.saas__mixin = {
    message: {
        mounted: function () {
            if (this.msg !== null) {
                this.$message({
                    type: 'info',
                    message: this.msg
                });
            }
        }
    },
    /**
     * 表单验证mixin
     * 包含常用的表单自定义验证方法
     */
    validators: {
        methods: {
            /**
             * 手机号校验
             * @param  {[String, Object]} options [校验参数，ex: {required: true, name: '手机号', trigger: 'change'}]
             */
            mixin_validators__phone: function (options) {
                options = this.options_assign(options);
                return this.mixin_validators__common(options, function (rule, value, callback) {
                    if (options.required && !value) return callback(new Error(`${options.name}不能为空`));
                    var reg = /^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\d{8}$/;
                    if (reg.test(value)) {
                        callback();
                    } else {
                        return callback(new Error(`${options.name}格式错误`));
                    }
                });
            },
            /**
             * 身份证号码校验
             * @param  {[String, Object]} options [校验参数，ex: {required: true, name: '身份证号码', trigger: 'change'}]
             */
            mixin_validators__cardCode: function (options) {
                options = this.options_assign(options);
                return this.mixin_validators__common(options, function (rule, value, callback) {
                    if (options.required && !value) return callback(new Error(`${options.name}不能为空`));
                    var city = {11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江 ",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北 ",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏 ",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"};
                    var status = true;
                    if (!/^[1-9]\d{5}((1[89]|20)\d{2})(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dx]$/i.test(value)) {
                        status = false;
                    } else if(!city[value.substr(0,2)]){
                        status = false;
                    } else{
                        var arrExp = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];//加权因子  
                        var arrValid = [1, 0, "X", 9, 8, 7, 6, 5, 4, 3, 2];//校验码
                        var sum = 0, idx;  
                        for(var i = 0; i < value.length - 1; i++){  
                            // 对前17位数字与权值乘积求和  
                            sum += parseInt(value.substr(i, 1), 10) * arrExp[i];  
                        }  
                        // 计算模（固定算法）  
                        idx = sum % 11;  
                        // 检验第18为是否与校验码相等  
                        if(arrValid[idx] == value.substr(17, 1).toUpperCase()){
                            status = true;
                        }else {
                            status = false;
                        }
                    }
                    status ? callback() : callback(new Error(`${options.name}格式错误`));
                });
            },
            /**
             * 必填字段校验
             * @param  {[String, Object]} options [校验参数，ex: {required: true, name: '身份证号码', trigger: 'change'}]
             */
            mixin_validators__required: function (options) {
                options = this.options_assign(options);
                return this.mixin_validators__common(options, function (rule, value, callback) {
                    // if(options.required && (!value || Number(value) == 0)) return callback(new Error(`${options.name}不能为空`));
                    if(options.required && !value) return callback(new Error(`${options.name}不能为空`));
                    callback();
                });
            },
            /**
             * mixin_validators校验共有方法
             * @param  {[String, Object]} options [校验参数，ex: {required: true, name: '姓名', trigger: 'change'}]
             * @param  {[Function]} validatorFun [自定义校验方法，ex: Function(rule, value, callback){...}]
             * @return {[Object]} [返回rules校验规则]
             */
            mixin_validators__common: function (options, validatorFun) {
                return [{
                    required: options.required,
                    trigger: options.trigger,
                    validator: validatorFun || function (rule, value, callback) {
                        callback();
                    }
                }]
            },
            /**
             * 合并options数据
             * @param  {[String, Object]} options [校验参数，ex: {required: true, name: '姓名', trigger: 'change'}]
             * @return {[Object]} [返回合并后的options]
             */
            options_assign: function (options) {
                if ('string' == typeof options) {
                    options = {name: options}
                }
                return Object.assign({
                    required: true,
                    name: '',
                    trigger: 'change'
                }, options || {});
            }
        }
    },
    /**
     * 常用工具方法
     * 里面包含时间格式化、数据部分隐藏等等方法
     */
    utils: {
        methods: {
            /**
             * 日期格式转化YYYY-MM-DD
             * @param  {[Object]} row
             * @param  {[String]} column 
             * @param  {[String]} cellValue 单元格的值
             * @return {[String]} 格式化后的date值
             */
            mixin_utils__dateFormat: function (row, column, cellValue) {
                var date = row[column.property];
                if (!date) {
                    return '无';
                }
                return moment(date).format('YYYY-MM-DD HH:SS');
            }
        }
    },
    /**
     * 详情页面提交跟重置方法
     */
	form: {
		methods: {
            // 表单提交按钮方法
            onSubmit: function (formName, formRef) {
                var formName = formName || 'formName',
                    formRef = formRef || 'formRef';
                this.$refs[formRef].validate(function (valid) {
                    if(valid) {
                        document[formName].submit();
                    }
                })
            },
            // 表单重置按钮方法
            resetForm: function (formRef) {
            	var formRef = formRef || 'formRef';
				this.$refs[formRef].resetFields();
			}
        }
	}
}