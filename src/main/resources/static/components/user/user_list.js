import Api from '../../api.js'
export default {
  name: 'UserList',
  components: {
    
  },
  data() {
    return {
       tableData: [

        ],
        condition:{
            userName: "",
            loginName: ""
        },
		page:{
			pageSize : 25,
			total: 0,
			currentPage : 1
		}
    };
  },
  methods: {
	  pageChange(){
		Api.postJson({
			url:"user/list/"+(this.page.currentPage-1)+"/"+this.page.pageSize,
			data: this.condition,
			success:data => {
			    this.page.pageSize = data.pageable.pageSize
			    this.page.currentPage = data.pageable.pageNumber + 1
			    this.total = data.totalElements
			    this.tableData = data.content
			},
		});
	  },
  },
  mounted() {
    this.pageChange()
  },
  template: `
    <table>
        <tr>
            <th>编号</th>
            <th>姓名</th>
            <th>登录账号</th>
            <th>密码</th>
        </tr>
        <tr v-for="item in tableData">
            <td>{{item.id}}</td>
            <td>{{item.name}}</td>
            <td>{{item.loginName}}</td>
            <td>{{item.password}}</td>
        </tr>
    </table>

	<el-pagination background layout="prev, pager, next, sizes" :total="page.total" 
		v-model:pageSize="page.pageSize" v-model:currentPage="page.currentPage"  
		@size-change="pageChange" @current-change="pageChange">
	</el-pagination>
	 <el-button type="primary" @click="pageChange">刷新</el-button>
  `,
};