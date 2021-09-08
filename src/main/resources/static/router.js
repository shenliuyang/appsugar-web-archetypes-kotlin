const Home = { template: '<div>Home</div>' }
const About = { template: '<div>About</div>' }

const routes = [
  { path: '/', component: Home },
  { path: '/about', component: About },
  { path: '/user',component:()=> import('./components/user/user_list.js')},
  { path: '/login',component:()=> import('./components/login.js')}
]
export default{
	name:"Router",
	router:routes
}