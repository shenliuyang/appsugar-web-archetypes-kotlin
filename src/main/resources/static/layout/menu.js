export default {
  name: 'Menu',
  components: {
   
  },
  data() {
	const menus = [
		{name:"系统管理",icon:"el-icon-user-solid",children:[
			{name:"用户管理",path:"/user",permission:"user:list"}
		]}
	];
    return {
       menuGroup : menus
    };
  },
  template: `
    <el-aside width="250px" style="background-color: rgb(238, 241, 246)">
    <el-menu >
	 <template v-for="(menu,pIndex) in menuGroup">
      <el-sub-menu :index="pIndex+''"  v-if="hasChildPerm(menu.children)">
        <template v-slot:title><i :class="menu.icon"></i>{{menu.name}}</template>
        <el-menu-item-group>
          <el-menu-item :index="pIndex + '-' + cIndex" v-for="(child,cIndex) in menu.children" >
			<router-link :to="child.path" v-can="child.permission">{{child.name}}</router-link>
		  </el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
	 </template>
    </el-menu>
  </el-aside>
  `,
  style: `
       .el-aside {
         color: var(--el-text-color-primary);
       }
  `
};