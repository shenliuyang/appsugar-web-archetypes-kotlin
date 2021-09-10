import {postJson} from '../api.js'
import PermissionUtil from '../utils/permission_util.js'
export default {
  name: 'Login',
  components: {
    
  },
  data() {
    return {
      c: {
          username : localStorage.username,
          password :''
	  }
    };
  },
  methods:{
    login(){
        localStorage.username = this.c.username
        postJson({
            url:'api/public/login',
            data:this.c,
            success:res=>{
                this.$message.success('登录成功')
                PermissionUtil.storePermission(res.permissions)
                this.$router.go(-1);
            }
        });
    }
  },
  template: `
      <el-card class="box-card" style="width:480px;margin:auto; position:absolute; left:50%; top:50%; margin-left:-17%; margin-top:-15%">
          <p style="text-align:center">系统登录</p>
          <el-form ref="form"  status-icon  label-position="left" label-width="auto">
              <el-form-item label="账号" prop="c.username">
                  <el-input v-model="c.username" prefix-icon="el-icon-user"></el-input>
              </el-form-item>
              <el-form-item label="密码" prop="c.password" @keyup.enter="login">
                  <el-input v-model="c.password" prefix-icon="el-icon-lock" show-password></el-input>
              </el-form-item>

              <el-form-item style="text-align:center">
                  <el-button type="primary" @click="login">登录</el-button>
              </el-form-item>
          </el-form>
      </el-card>

  `,
};