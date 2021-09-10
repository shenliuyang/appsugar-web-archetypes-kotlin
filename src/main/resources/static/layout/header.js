export default {
  name: 'Header',
  components: {
    
  },
  data() {
    return {
        headerStyle:{
            'background-color':'#b3c0d1',
            'color': 'var(--el-text-color-primary)',
            'line-height':'60px'
        }
    };
  },
  template: `
    <el-header style="text-align: right; font-size: 12px;" :style="headerStyle">
         <el-dropdown>
           <i class="el-icon-setting" style="margin-right: 15px"></i>
           <template #dropdown>
             <el-dropdown-menu>
               <el-dropdown-item>View</el-dropdown-item>
               <el-dropdown-item>Add</el-dropdown-item>
               <el-dropdown-item>Delete</el-dropdown-item>
             </el-dropdown-menu>
           </template>
         </el-dropdown>
         <span>娟露华</span>
    </el-header>
  `,
};