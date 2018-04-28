var saas__components = [
	/**
	 * saas-district组件，省市县三级联动
	 * @ex: <saas-district v-model="district"></saas-district>
	 * @v-model: [Object] ex: {name: '山西省', code: 14}
	 */
	{
		name: 'SaasDistrict',
		template: `
			<div class="saas-district-cell">
			    <el-input class="w-150" v-model="currentValue && currentValue.name" readonly></el-input>
			    <el-select
			        class="ml-15 w-150"
			        value-key="code"
			        v-model="districts.province"
			        @change="getDatas(districts.province, 4)"
			        placeholder="请选择省份">
			        <el-option
			            v-for="province in provinceList"
			            :label="province.name"
			            :value="province"
			            :key="province.code">
			        </el-option>
			    </el-select>
			    <el-select
			        class="ml-15 w-150"
			        value-key="code"
			        v-model="districts.city"
			        @change="getDatas(districts.city, 6)"
			        :disabled="!districts.province"
			        placeholder="请选择城市">
			        <el-option
			            v-for="city in cityList"
			            :label="city.name"
			            :value="city"
			            :key="city.code">
			        </el-option>
			    </el-select>
			    <el-select
			        class="ml-15 w-150"
			        value-key="code"
			        v-model="districts.district"
			        @change="getDatas()"
			        :disabled="!districts.city"
			        placeholder="请选择县区">
			        <el-option
			            v-for="district in districtList"
			            :label="district.name"
			            :value="district"
			            :key="district.code">
			        </el-option>
			    </el-select>
			</div>
		`,
		props: {
	        value: [String, Number, Object]
	    },
	    data () {
	        return {
	            currentValue: this.value,
	            districts: {},
	            provinceList: [],
	            cityList: [],
	            districtList: []
	        }
	    },
	    created () {
	        var vm = this
	        // 默认进来先加载省份数据
	        axios.get('/sys/district/getLists', {
	            params: {
	            	code: '',
	                codeLength: 2
	            }
	        }).then(function(res) {
	            if (res.data.length > 0) vm.provinceList = res.data
	        })
	    },
	    methods: {
	        /*
	         * getDatas()获取省市区数据的函数
	         * params: item 上级区域属性
	         * params: length 当前区域的长度值
	         */
	        getDatas (item, length) {
	            var vm = this
	            if (item && item.code && length) {
	                if (length === 4) vm.districts.city = ''
	                vm.districts.district = ''
	                axios.get('/sys/district/getLists', {params: {
	                    code: item.code,
	                    codeLength: length
	                }}).then(function(res) {
	                    if (res.data.length > 0) { // 如果数据不为空，length为4时是城市数据，为6时是县区数据
	                        length === 4 ? vm.cityList = res.data : vm.districtList = res.data
	                    } else { // 如果数据为空，城市跟县区清空
	                        if (length === 4) vm.cityList = []
	                        vm.districtList = []
	                    }
	                })
	            }
	            // 每次地区选择发生变化，更新currentValue的值
	            vm.currentValue = vm.districts.district || vm.districts.city || vm.districts.province || {}
	        }
	    },
	    watch: {
	        'value': function (val, oldValue) {
	            if (val === this.currentValue) return
	            this.currentValue = val
	        },
	        'currentValue': function (val, oldValue) {
	            this.$emit('input', val)
	        }
	    }
	},
	/**
	 * saas-imageupload组件，图片上传
	 * @ex: <saas-imageupload v-model="src" image-limit="2" image-accept="image/png" image-size="500"></saas-imageupload>
	 * @v-model: [String, Array]
	 * @imageLimit: [Number] 限制上传图片的最大数量
	 * @imageAccept: [String] 限制上传图片的类型
	 * @imageSize: [Number] 限制上传图片的大小
	 */
	{
		name: 'SaasImageupload',
		template: `
			<el-upload
				class="saas-imageupload-cell"
                action="/sys/file/upload"
                name="files"
                :data="{'type': 'policy'}"
                :accept="imageAccept"
                :limit="imageLimit"
                :on-exceed="photoExceed"
                :before-upload="beforePhotoUpload"
                :on-progress="handlePhotoProgress"
                :on-success="handlePhotoSuccess"
                :on-error="handlePhotoError">
                	<input type="hidden" :name="name" v-model="currentValue">
                	<span v-if="currentValue.length > 0">
                		<img v-for="img in currentValue" :src="img" v-error-image>
                	</span>
                    <i v-if="imageLimit > 1 || currentValue.length == 0" class="el-icon-upload saas-imageupload-cell-icon"></i>
                    <div slot="tip" class="el-upload__tip">
                    	只能上传 <span style="color:#409EFF;">{{imageAccept}}</span> 文件，
                    	且不超过 <span style="color:#409EFF;">{{imageSize}}</span> KB
                    </div>
            </el-upload>
		`,
		props: {
			value: [String, Array],
			name: String,
			imageLimit: {
				type: Number,
				default: 1
			},
			imageAccept: {
				type: String,
				default: 'image/jpeg'
			},
			imageSize: {
				type: Number,
				default: 500
			}
		},
		data: function () {
			return {
				currentValue: typeof this.value == 'string' ? (this.value ? [this.value] : []) : this.value
			}
		},
		created: function () {
			// console.log(this.options.accept);
		},
		methods: {
			beforePhotoUpload: function (file) {
				var isLt = file.size / 1024 < this.imageSize;
                if (!isLt) {
                	this.$message.error(`上传图片大小不能超过 ${this.imageSize}KB`);
                }
                return isLt;
            },
            handlePhotoProgress: function (event, file, fileList) {
            	// this.fileList = fileList;
            },
            handlePhotoSuccess: function (res, file) {
                this.currentValue = res.data;
            },
            handlePhotoError: function (res, file) {
            },
            photoExceed: function (files, fileList) {
            	this.$message.warning(`最多可以上传 ${this.imageLimit} 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
            }
		},
	    watch: {
	        'value': function (val, oldValue) {
	            if ((typeof val == 'string' ? (val ? [val] : []) : val) === this.currentValue) return
	            this.currentValue = val
	        },
	        'currentValue': function (val, oldValue) {
	            this.$emit('input', val)
	        }
	    }
	},
	/**
	 * saas-list-by-type组件，枚举/字典select
	 * @ex: <saas-list-by-type v-model="district" type="checkbox" sub-type="reject_reason" options="{}"></saas-list-by-type>
	 * @v-model: [String, Array]
	 * @imageLimit: [Number] 限制上传图片的最大数量
	 * @imageAccept: [String] 限制上传图片的类型
	 * @imageSize: [Number] 限制上传图片的大小
	 */
	{
		name: 'SaasListByType',
		template: `
			<div class="saas-List-by-type-cell">
				<input type="hidden" :name="name" v-model="currentValue">
				<el-checkbox-group
					v-if="type == 'checkbox'"
					v-model="currentValue"
				>
					<el-checkbox
						v-for="item in listData"
						:label="item.value"
						:key="item.value"
					>
						{{item.label}}
					</el-checkbox>
				</el-checkbox-group>
				<el-select
					v-else
					class="saas-selector-cell"
					v-model="currentValue"
			        :disabled="options.disabled || loading"
			        :size="options.size"
			        :clearable="options.clearable || false"
			        :collapse-tags="options.collapseTags"
			        :multiple="options.multiple"
			        :multiple-limit="options.multipleLimit"
			        :filterable="options.filterable || true"
			        :placeholder="loading ? '加载中...' : options.placeholder"
			        @change="handleChange"
			    >
			        <el-option
			            v-for="item in listData"
			            :label="typeof item !== 'object' ? item : item.label"
			            :value="typeof item !== 'object' ? item : item.value"
			            :key="typeof item !== 'object' ? item : item.value"
			        ></el-option>
			    </el-select>
		    </div>
		`,
		props: {
			value: [String, Number, Array],
			name: String,
			type: String,
			subType: {
				type: String,
				required: true
			},
	        options: {
	        	type: Object,
	        	default: function () {
	        		return {};
	        	}
	        }
		},
		data: function () {
			return {
	            loading: false,
				currentValue: this.value == 0 ? '' : this.value,
	            listData: this.type == 'enum' ? __enums[this.subType] : []
			}
		},
	    created: function () {
			if (this.type !== 'enum') this.getList(this.subType);
	    },
		methods: {
	        getList: function(sub_type) {
	            var vm = this
	            vm.startGetList()
	            axios.get('/system/dict/listByType/' + (sub_type || 'reject_reason')).then(function(res) {
	                vm.endGetList()
	                vm.listData = res.data
	            }).catch(function(error) {
	                vm.endGetList()
	                return error
	            })
	        },
	        startGetList: function () {
	            this.loading = true
	        },
	        endGetList: function () {
	            this.loading = false
	        },
	        setCurrentValue: function (val) {
	            if (val === this.currentValue) return
	            this.currentValue = val
	        },
	        handleChange: function (val) {
		        this.$emit('change', val);
			}
	    },
	    watch: {
	        value: function (val) {
	            this.setCurrentValue(val == 0 ? '' : val)
	        },
			currentValue: function (val) {
				this.$emit('input', val)
			}
		}
	},
	/**
	 * saas-message组件，反馈提示组件
	 * @ex: <saas-message :message="msg"></saas-message>
	 * @message: [String] 反馈的信息
	 */
	{
		name: 'SaasMessage',
		template: `<div class="saas-message-cell" v-if="false"></div>`,
		props: {
			message: String
		},
		data: function () {
            return {
            }
        },
        mounted: function () {
            if (this.message !== null) {
                this.$message({
                    type: 'info',
                    message: this.message
                });
            }
        }
	}
];

// 遍历所有组件，设置为vue的全局组件
saas__components.map(function(component) {
	Vue.component(component.name, component);
});