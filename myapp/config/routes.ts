export default [
  {
    path: '/user',
    layout: false,
    routes: [{ name: '登录', path: '/user/login', component: './User/Login' },
    { name: '注册', path: '/user/register', component: './User/Register' }],
  },
  { path: '/welcome', name: '欢迎', icon: 'smile', component: './Welcome' },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin', 
    routes: [
      { path: '/admin', component: './Admin' },
      { path: '/admin/user-manage', name: '用户管理页', component: './Admin/UserManage' },
    ],
  },
  {path: '/center', name: '个人设置', icon: 'smile', component: './Center' },
  { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
