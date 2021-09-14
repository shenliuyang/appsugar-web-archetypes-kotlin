import {postJson,pageUtil} from '../../api.js'
export default {
  name: 'UserList',
  components: {
    
  },
  data() {
    return {
        data: pageUtil.initPage(),
        c:{
            name: "",
            loginName: ""
        }
    };
  },
  methods: {
	  pageChange(){
		postJson({
			url: this.data.pageUrl("user/list"),
			data: this.c,
			success:data => pageUtil.setPageData(this.data,data)
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

     <el-table :data="data.content" style="width: 100%">
        <el-table-column prop="id" label="编号" width="180"> </el-table-column>
        <el-table-column prop="name" label="姓名" width="180"> </el-table-column>
        <el-table-column prop="loginName" label="账号"> </el-table-column>
      </el-table>

	<el-pagination background layout="prev, pager, next, sizes" :total="data.totalElements"
		v-model:pageSize="data.pageable.pageSize" v-model:currentPage="data.pageable.pageNumber"
		@size-change="pageChange" @current-change="pageChange">
	</el-pagination>

  `,
};