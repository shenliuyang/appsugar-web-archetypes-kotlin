import {postJson} from '../../api.js'
export default {
  name: 'UserList',
  components: {
    
  },
  data() {
    return {
       tableData: [

        ],
        c:{
            name: "",
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
		postJson({
			url:"user/list/"+(this.page.currentPage-1)+"/"+this.page.pageSize,
			data: this.c,
			success:data => {
			    this.page.pageSize = data.pageable.pageSize
			    this.page.currentPage = data.pageable.pageNumber + 1
			    this.total = data.totalElements
			    this.tableData = data.content
			}
		});
	  },
  },
  mounted() {
    this.pageChange()
  },
  template: `
        <el-form :inline="true" :model="c">
          <el-form-item label="名称">
            <el-input v-model="c.name" placeholder="前置匹配"></el-input>
          </el-form-item>
          <el-form-item label="账号">
            <el-input v-model="c.loginName" placeholder="全文匹配"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="pageChange">查询</el-button>
          </el-form-item>
        </el-form>

     <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="编号" width="180"> </el-table-column>
        <el-table-column prop="name" label="姓名" width="180"> </el-table-column>
        <el-table-column prop="loginName" label="账号"> </el-table-column>
      </el-table>

	<el-pagination background layout="prev, pager, next, sizes" :total="page.total" 
		v-model:pageSize="page.pageSize" v-model:currentPage="page.currentPage"  
		@size-change="pageChange" @current-change="pageChange">
	</el-pagination>

  `,
};