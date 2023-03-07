import axios from "axios";
import {Message} from "element-ui";
import da from "element-ui/src/locale/lang/da";
import router from "../router";
axios.interceptors.response.use(success => {
        /*  错误有两种：一种是前端的一种是服务端的
        *  服务端的错误分为两种，一种是查询、删除操作等的失败
                      一种是页面操作不当的失败
         这里服务端的错误统一封装成一种错误
         注意！！！这里的*成功*代表的是html状态码是200，但此时不代表服务端没有错误
                            即前端成功，后端状态码异常
                      *失败*代表前后端都出现异常
               success.status：前端返回的状态码
               success.data.status：后端返回数据的状态码
         */

        if (success.status && success.status == 200 && success.data.status == 500) {
            Message.error({message: success.data.msg})
            return;
        }
        if (success.data.msg) {
            Message.success({message: success.data.msg})
        }
        return success.data;
    },
    error => {
        if (error.response.status == 504 || error.response.status == 404) {
            Message.error({message: '服务器出错了，快去看看☉⌒☉`'})
        } else if (error.response.status == 403) {
            Message.error({message: '权限不足，请联系管理员！'})
        } else if (error.response.status == 401) {
            Message.error({message: '请先登录！^-^'})
            /*回到登录页*/
            router.replace("/");
        } else {
            if (error.response.data.msg) {
                Message.error({message: error.response.data.msg})
            } else {
                Message.error({message: '未知错误！'})
            }
        }
        return;
    })
let base = '';
/*  ${base}${url}:要用着重号引起来，否则在发送requesturl时会找不到路径，出现404
* */
export const postKeyValueRequest = (url, params) => {
    return axios({
        method: 'post',
        url: `${base}${url}`,
        data: params,
        transformRequest: [function (data) {
            let ret = '';
            for (let i in data) {
                ret += encodeURIComponent(i) + '=' + encodeURIComponent(data[i]) + '&'
            }

            return ret;
        }],
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    });
}
export const postRequest = (url, params) => {
    return axios({
        method: 'post',
        url: `${base}${url}`,
        data: params
    })
}
export const getRequest = (url, params) => {
    return axios({
        method: 'get',
        url: `${base}${url}`,
        data: params
    })
}
export const putRequest = (url, params) => {
    return axios({
        method: 'put',
        url: `${base}${url}`,
        data: params
    })
}
export const deleteRequest = (url, params) => {
    return axios({
        method: 'delete',
        url: `${base}${url}`,
        data: params
    })
}