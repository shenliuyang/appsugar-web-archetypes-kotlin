import Header from '../layout/header.js'
import Menu from '../layout/menu.js'
import Main from '../layout/main.js'
export default {
  name: 'Layout',
  components: {
    Header,
	Menu,
	Main
  },
  data() {
    return {
      
    };
  },
  template: `
    <el-container style="height: 730px; border: 1px solid #eee;">
		<Menu></Menu>
		<el-container class="is-vertical">
			<Header></Header>
			<Main></Main>
		</el-container>
	</el-container>
  `,
};