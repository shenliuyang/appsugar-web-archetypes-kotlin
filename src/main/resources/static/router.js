const appRoute = {
    path: "/",component:()=> import('./layout/layout.js'),
    children:[
        { path: '', component: { template: '<div>Home</div>' } },
        { path: 'user',component:()=> import('./components/user/user_list.js')},
    ]
}

const routes = [
  appRoute,
  { path: '/login',component:()=> import('./components/login.js')},
]
export default{
	name:"Router",
	router:routes
}