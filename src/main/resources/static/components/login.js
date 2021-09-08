import Api from '../api.js'
import PermissionUtil from '../utils/permission_util.js'
export default {
  name: 'Login',
  components: {
    
  },
  data() {
    return {
      username : localStorage.username,
	  password :''
    };
  },
  methods:{
    login(){
        localStorage.username = this.username
        var router = this.$router
        Api.postJson({
            url:'api/public/login',
            data:{
                username: this.username,
                password: this.password
            },
            success(res){
                Element3.Message.error('登录成功')
                PermissionUtil.storePermission(res.permissions)
                router.go(-1);
            }
        });
    }
  },
  template: `
	 <el-input v-model="username" placeholder="请输入账号"></el-input>
	 <el-input v-model="password" placeholder="请输入密码" show-password></el-input>
	 <el-button type="primary" @click="login">登录</el-button>

  `,
};