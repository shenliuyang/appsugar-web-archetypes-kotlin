import App from './components/App.js';
import Router from './router.js';
import PermissionDirective from './directives/permission.js'
import PermissionUtil from './utils/permission_util.js'
import Api from './api.js'
const router = VueRouter.createRouter({
  history: VueRouter.createWebHashHistory(),
  routes: Router.router
})
const app = Vue.createApp({
	render: () => Vue.h(App),
})
app.directive("can",PermissionDirective)
app.mixin({
	methods:{hasChildPerm(v){return PermissionUtil.anyOfChildPermission(v)} }
})
Api.router = router
app.use(router)
app.use(Element3)
app.mount('#app');