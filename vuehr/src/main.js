import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import store from './store';
/*通过插件的方法将各种请求方法封装导入，简化开发，不用每次用到都重新导入*/
import {postRequest} from "./utils/api";
import {postKeyValueRequest} from "./utils/api";
import {getRequest} from "./utils/api";
import {putRequest} from "./utils/api";
import {deleteRequest} from "./utils/api";
import {initMenu} from "./utils/menus";
import 'font-awesome/css/font-awesome.min.css'

Vue.prototype.postRequest = postRequest;
Vue.prototype.postKeyValueRequest = postKeyValueRequest;
Vue.prototype.getRequest = getRequest;
Vue.prototype.putRequest = putRequest;
Vue.prototype.deleteRequest = deleteRequest;

Vue.config.productionTip = false
/*全局配置按钮的大小*/
Vue.use(ElementUI, {size:'mini'});

router.beforeEach((to, from, next) => {
    if (to.path == '/') {
        next();
    } else {
        if (window.sessionStorage.getItem(("user"))) {
            initMenu(router, store);
            next();
        } else {
            //如果还没登录就要访问页面的内部，此时会重新跳转到登录页//
            // 同时，登录成功之后也能由to中获取path重定向到之前没有权限时想要访问的路径
            next('/?redirect=' + to.path);
        }

    }
})

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')
