export default {
  name: 'Header',
  components: {
    
  },
  data() {
    return {
     
    };
  },
  template: `
   <el-header style="text-align: right; font-size: 12px">
      <el-dropdown>
        <i class="el-icon-setting" style="margin-right: 15px"/>
          <el-dropdown-menu>
            <el-dropdown-item>查看</el-dropdown-item>
            <el-dropdown-item>新增</el-dropdown-item>
            <el-dropdown-item>删除</el-dropdown-item>
          </el-dropdown-menu>
	  </el-dropdown>
      <span>王小虎</span>
    </el-header>
  `,
};