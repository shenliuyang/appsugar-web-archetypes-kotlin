export default {
  name: 'Main',
  components: {
   
  },
  data() {
    return {
      
    };
  },
  template: `
	<el-main>
		  <router-view v-slot="{ Component }">
			  <keep-alive>
				<component :is="Component" />
			  </keep-alive>
		  </router-view>
    </el-main>
    
  `,
};