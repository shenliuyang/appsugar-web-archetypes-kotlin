export default {
  name: 'Menu',
  components: {
   
  },
  data() {
	const menus = [
		{name:"系统管理",icon:"el-icon-message",children:[
			{name:"主页",path:"/",},{name:"介绍",path:"/About"}
		]},
		{name:"用户角色管理",icon:"el-icon-user-solid",children:[
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
      <el-submenu :index="pIndex+''"  v-if="hasChildPerm(menu.children)">
        <template v-slot:title><i :class="menu.icon"></i>{{menu.name}}</template>
        <el-menu-item-group>
          <el-menu-item :index="pIndex + '-' + cIndex" v-for="(child,cIndex) in menu.children" >
			<router-link :to="child.path" v-can="child.permission">{{child.name}}</router-link>
		  </el-menu-item>
        </el-menu-item-group>
      </el-submenu>
	 </template>
    </el-menu>
  </el-aside>
  `,
};