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
	  xxx(){
	    console.log("do nothing")
	    this.tableData = [{}]
	  },
  },
  mounted() {
    this.pageChange()
  },
  template: `
    <el-table :data="tableData" style="width: 100%">
	  <el-table-column prop="id" label="编号" width="180"> </el-table-column>
	  <el-table-column prop="name" label="姓名" width="180"> </el-table-column>
	  <el-table-column prop="loginName" label="登录账号" width="180"> </el-table-column>
	  <el-table-column prop="password" label="密码"> </el-table-column>
	</el-table>
	<el-pagination background layout="prev, pager, next, sizes" :total="page.total" 
		v-model:pageSize="page.pageSize" v-model:currentPage="page.currentPage"  
		@size-change="xxx" @current-change="pageChange">
	</el-pagination>
	 <el-button type="primary" @click="pageChange">刷新</el-button>
  `,
};