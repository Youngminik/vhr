/*定义代理对象proxyObj*/
let proxyObj = {};
/*webscoket的代理配置*/
proxyObj['/ws'] = {
    ws: true,
    target: "ws://localhost:8081"
};
/*http请求*/
proxyObj['/'] = {
    ws: false,
    target:'http://localhost:8081',/*后端端口域*/
    changeOrigin: true,
    pathRewrite:{
            '^/':''
    }
}
/*开发环境的配置*/
module.exports={
    devServer:{
        host:'localhost',
        port:8080,
        proxy:proxyObj
    }
}